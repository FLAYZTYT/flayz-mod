package com.antigravity.tiktokmod.hooks;

import android.net.Uri;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import com.antigravity.tiktokmod.PreferencesHelper;

public class NetworkHooks {

    public static void init(LoadPackageParam lpparam, XSharedPreferences prefs) {
        // Подмена параметров в построителе URI
        XposedHelpers.findAndHookMethod(Uri.class, "getQueryParameter", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                String key = (String) param.args[0];
                if (key == null) return;

                prefs.reload();
                String region = prefs.getString(PreferencesHelper.KEY_REGION_ISO, "US");
                String mccMnc = prefs.getString(PreferencesHelper.KEY_MCC_MNC, "310260");

                if ("carrier_region".equalsIgnoreCase(key) ||
                    "sys_region".equalsIgnoreCase(key) ||
                    "account_region".equalsIgnoreCase(key) ||
                    "region".equalsIgnoreCase(key)) {
                    param.setResult(region);
                } else if ("mcc_mnc".equalsIgnoreCase(key) || "sim_op".equalsIgnoreCase(key)) {
                    param.setResult(mccMnc);
                }
            }
        });
    }
}
