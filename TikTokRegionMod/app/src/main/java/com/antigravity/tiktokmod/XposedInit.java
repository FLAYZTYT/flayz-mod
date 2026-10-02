package com.antigravity.tiktokmod;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import com.antigravity.tiktokmod.hooks.MediaHooks;
import com.antigravity.tiktokmod.hooks.NetworkHooks;
import com.antigravity.tiktokmod.hooks.TelephonyHooks;

public class XposedInit implements IXposedHookLoadPackage {

    private static final String PKG_MUSICAL_LY = "com.zhiliaoapp.musically";
    private static final String PKG_TRILL = "com.ss.android.ugc.trill";
    private static final String PKG_MUSICAL_LY_GO = "com.zhiliaoapp.musically.go";

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        String packageName = lpparam.packageName;

        if (!PKG_MUSICAL_LY.equals(packageName) &&
            !PKG_TRILL.equals(packageName) &&
            !PKG_MUSICAL_LY_GO.equals(packageName)) {
            return;
        }

        XposedBridge.log("[TikTokMod] Активация хуков для: " + packageName);

        XSharedPreferences prefs = PreferencesHelper.getXposedPreferences();

        // 1. Инициализация хуков TelephonyManager и SIM
        TelephonyHooks.init(lpparam, prefs);

        // 2. Инициализация хуков сетевых запросов
        NetworkHooks.init(lpparam, prefs);

        // 3. Инициализация хуков медиа и снятия ограничений
        MediaHooks.init(lpparam, prefs);
    }
}
