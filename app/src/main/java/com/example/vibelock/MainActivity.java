package com.example.vibelock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ServiceInfo;

public class MainActivity extends Activity {
    private SeekBar ampSeek;
    private SeekBar lenSeek;
    private TextView ampText;
    private TextView lenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 전역 예외 핸들러: 크래시 메시지를 토스트로 표시
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "CRASH: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        });

        setContentView(R.layout.activity_main);

        ampSeek = findViewById(R.id.seek_amp);
        lenSeek = findViewById(R.id.seek_len);
        ampText = findViewById(R.id.text_amp);
        lenText = findViewById(R.id.text_len);
        Button btn = findViewById(R.id.btn_start);

        ampText.setText(getString(R.string.label_amp, ampSeek.getProgress()));
        lenText.setText(getString(R.string.label_len, lenSeek.getProgress()));

        ampSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ampText.setText(getString(R.string.label_amp, progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        lenSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lenText.setText(getString(R.string.label_len, progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btn.setOnClickListener(v -> {
            if (!isAccessibilityEnabled()) {
                Toast.makeText(this, "Enable accessibility permission", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }
            Intent serviceIntent = new Intent(this, VibeService.class);
            serviceIntent.putExtra("amp", ampSeek.getProgress());
            serviceIntent.putExtra("len", lenSeek.getProgress());
            startService(serviceIntent);
            Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        });

        try {
            // Additional init if needed
        } catch (Throwable e) {
            Toast.makeText(this, "Main init error: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isAccessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (am == null) return false;
        for (AccessibilityServiceInfo service :
                am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)) {
            ServiceInfo si = service.getResolveInfo().serviceInfo;
            if (si.packageName.equals(getPackageName()) && si.name.equals(VibeAccessibilityService.class.getName())) {
                return true;
            }
        }
        return false;
    }
}
