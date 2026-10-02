package com.antigravity.tiktokmod.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import com.antigravity.tiktokmod.PreferencesHelper;

public class MediaHooks {

    public static void init(LoadPackageParam lpparam, XSharedPreferences prefs) {
        prefs.reload();
        boolean allowBypass = prefs.getBoolean(PreferencesHelper.KEY_WATERMARK_BYPASS, true);
        if (!allowBypass) return;

        try {
            // Поиск модели элемента ленты (Aweme)
            Class<?> awemeClass = XposedHelpers.findClassIfExists("com.ss.android.ugc.aweme.feed.model.Aweme", lpparam.classLoader);
            if (awemeClass != null) {
                // Разрешаем скачивание
                XposedHelpers.findAndHookMethod(awemeClass, "isPreventDownload", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        param.setResult(false);
                    }
                });

                XposedHelpers.findAndHookMethod(awemeClass, "isAllowDownload", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        param.setResult(true);
                    }
                });
            }
        } catch (Throwable t) {
            XposedBridge.log("[TikTokMod] Media hook error: " + t.getMessage());
        }
    }
}
