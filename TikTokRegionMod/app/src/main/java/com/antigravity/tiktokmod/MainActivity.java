package com.antigravity.tiktokmod;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    public static final String TG_CHANNEL_URL = "https://t.me/+H135_eOHfuY4OWEy";
    private static final String KEY_TG_VISITED = "tg_channel_visited";

    private Spinner spinnerRegions;
    private SwitchMaterial switchWatermark;
    private Button btnTelegram;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerRegions = findViewById(R.id.spinner_regions);
        switchWatermark = findViewById(R.id.switch_watermark);
        btnTelegram = findViewById(R.id.btn_telegram);
        btnSave = findViewById(R.id.btn_save);

        String[] regions = getResources().getStringArray(R.array.available_regions);
        String[] isoCodes = getResources().getStringArray(R.array.region_iso_codes);
        String[] mccMncs = getResources().getStringArray(R.array.region_mcc_mnc);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, regions);
        spinnerRegions.setAdapter(adapter);

        // Загрузка сохраненных настроек
        SharedPreferences prefs = PreferencesHelper.getAppPreferences(this);
        String savedIso = prefs.getString(PreferencesHelper.KEY_REGION_ISO, "US");
        boolean savedWatermark = prefs.getBoolean(PreferencesHelper.KEY_WATERMARK_BYPASS, true);

        for (int i = 0; i < isoCodes.length; i++) {
            if (isoCodes[i].equalsIgnoreCase(savedIso)) {
                spinnerRegions.setSelection(i);
                break;
            }
        }
        switchWatermark.setChecked(savedWatermark);

        // Обработчик кнопки Telegram
        btnTelegram.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TG_CHANNEL_URL));
                startActivity(intent);
                prefs.edit().putBoolean(KEY_TG_VISITED, true).apply();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Не удалось открыть браузер/Telegram", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик кнопки сохранения настроек
        btnSave.setOnClickListener(v -> {
            boolean hasVisitedTg = prefs.getBoolean(KEY_TG_VISITED, false);
            if (!hasVisitedTg) {
                Toast.makeText(MainActivity.this, R.string.tg_required_toast, Toast.LENGTH_LONG).show();
                return;
            }

            int selectedIndex = spinnerRegions.getSelectedItemPosition();
            String selectedIso = isoCodes[selectedIndex];
            String selectedMccMnc = mccMncs[selectedIndex];
            boolean watermarkBypass = switchWatermark.isChecked();

            prefs.edit()
                .putString(PreferencesHelper.KEY_REGION_ISO, selectedIso)
                .putString(PreferencesHelper.KEY_MCC_MNC, selectedMccMnc)
                .putBoolean(PreferencesHelper.KEY_WATERMARK_BYPASS, watermarkBypass)
                .apply();

            Toast.makeText(MainActivity.this, R.string.settings_saved, Toast.LENGTH_LONG).show();
        });
    }
}
