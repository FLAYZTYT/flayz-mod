package com.antigravity.tiktokmod.hooks;

import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import com.antigravity.tiktokmod.PreferencesHelper;

public class TelephonyHooks {

    public static void init(LoadPackageParam lpparam, XSharedPreferences prefs) {
        hookTelephonyManager(lpparam, prefs);
        hookSubscriptionManager(lpparam, prefs);
    }

    private static void hookTelephonyManager(LoadPackageParam lpparam, XSharedPreferences prefs) {
        Class<?> tmClass = TelephonyManager.class;

        XC_MethodHook countryIsoHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                prefs.reload();
                String region = prefs.getString(PreferencesHelper.KEY_REGION_ISO, "US").toLowerCase();
                param.setResult(region);
            }
        };

        XC_MethodHook simOperatorHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                prefs.reload();
                String mccMnc = prefs.getString(PreferencesHelper.KEY_MCC_MNC, "310260");
                param.setResult(mccMnc);
            }
        };

        // Хуки TelephonyManager
        XposedHelpers.findAndHookMethod(tmClass, "getSimCountryIso", countryIsoHook);
        XposedHelpers.findAndHookMethod(tmClass, "getNetworkCountryIso", countryIsoHook);
        XposedHelpers.findAndHookMethod(tmClass, "getSimOperator", simOperatorHook);
        XposedHelpers.findAndHookMethod(tmClass, "getNetworkOperator", simOperatorHook);
    }

    private static void hookSubscriptionManager(LoadPackageParam lpparam, XSharedPreferences prefs) {
        try {
            Class<?> subInfoClass = SubscriptionInfo.class;

            XposedHelpers.findAndHookMethod(subInfoClass, "getCountryIso", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    prefs.reload();
                    String region = prefs.getString(PreferencesHelper.KEY_REGION_ISO, "US").toLowerCase();
                    param.setResult(region);
                }
            });

            XposedHelpers.findAndHookMethod(subInfoClass, "getMccString", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    prefs.reload();
                    String mccMnc = prefs.getString(PreferencesHelper.KEY_MCC_MNC, "310260");
                    if (mccMnc != null && mccMnc.length() >= 3) {
                        param.setResult(mccMnc.substring(0, 3));
                    }
                }
            });

            XposedHelpers.findAndHookMethod(subInfoClass, "getMncString", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    prefs.reload();
                    String mccMnc = prefs.getString(PreferencesHelper.KEY_MCC_MNC, "310260");
                    if (mccMnc != null && mccMnc.length() > 3) {
                        param.setResult(mccMnc.substring(3));
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("[TikTokMod] SubscriptionManager hook skipped: " + t.getMessage());
        }
    }
}
