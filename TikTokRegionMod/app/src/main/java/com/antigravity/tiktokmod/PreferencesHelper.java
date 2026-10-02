package com.antigravity.tiktokmod;

import android.content.Context;
import android.content.SharedPreferences;

import de.robv.android.xposed.XSharedPreferences;

public class PreferencesHelper {

    public static final String PACKAGE_NAME = "com.antigravity.tiktokmod";
    public static final String PREFS_NAME = "tiktok_mod_prefs";

    public static final String KEY_REGION_ISO = "region_iso";
    public static final String KEY_MCC_MNC = "mcc_mnc";
    public static final String KEY_WATERMARK_BYPASS = "watermark_bypass";

    // Получение настроек внутри UI приложения
    public static SharedPreferences getAppPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Получение настроек внутри хуков Xposed/LSPosed
    public static XSharedPreferences getXposedPreferences() {
        XSharedPreferences prefs = new XSharedPreferences(PACKAGE_NAME, PREFS_NAME);
        prefs.makeWorldReadable();
        prefs.reload();
        return prefs;
    }
}
