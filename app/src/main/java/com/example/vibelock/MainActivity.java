package com.example.vibelock;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.Manifest;
import android.widget.Toast;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ServiceInfo;

public class MainActivity extends Activity {
    private static final int REQ_POST_NOTI = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 전역 예외 핸들러: 크래시 메시지를 토스트로 표시
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "CRASH: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        });

        try {
            if (Build.VERSION.SDK_INT >= 33 &&
                    checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTI);
                return;
            }
            if (!isAccessibilityEnabled()) {
                Toast.makeText(this, "Enable accessibility permission", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }
            startSvcAndFinish();
        } catch (Throwable e) {
            Toast.makeText(this, "Main init error: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (!isAccessibilityEnabled()) {
            Toast.makeText(this, "Enable accessibility permission", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }
        startSvcAndFinish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isAccessibilityEnabled()) {
            startSvcAndFinish();
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

    private void startSvcAndFinish() {
        try {
            Intent serviceIntent = new Intent(this, VibeService.class);
            startForegroundService(serviceIntent);
        } catch (Throwable e) {
            Toast.makeText(this, "startSvc error: " + e.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        } finally {
            finish();
        }
    }
}
