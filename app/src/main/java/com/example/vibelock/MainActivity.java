package com.example.vibelock;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.Manifest;

public class MainActivity extends Activity {
    private static final int REQ_POST_NOTI = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTI);
                return;
            }
        }
        startSvcAndFinish();
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        startSvcAndFinish(); // 허용/거부 상관없이 서비스 시도(거부해도 포그라운드 알림은 보이는 기종이 많음)
    }

    private void startSvcAndFinish() {
        Intent serviceIntent = new Intent(this, VibeService.class);
        startForegroundService(serviceIntent);
        finish();
    }
}
