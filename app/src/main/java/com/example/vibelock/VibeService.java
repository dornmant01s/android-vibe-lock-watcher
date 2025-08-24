package com.example.vibelock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

public class VibeService extends Service {
    private Vibrator vibrator;
    private BroadcastReceiver screenReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, "vibe_channel")
                .setContentTitle("VibeLockWatcher")
                .setContentText("Running in background")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setOngoing(true)
                .build();
        startForeground(1, notification);

        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    // 잠금 해제됨 → 연속 진동 시작
                    startContinuousVibration();
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)
                        || Intent.ACTION_SCREEN_ON.equals(action)) {
                    // 화면 꺼짐 또는 켜짐(잠금화면 진입) → 진동 중지
                    stopVibration();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenReceiver, filter);
    }

    private void startContinuousVibration() {
        if (vibrator == null || !vibrator.hasVibrator()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 사실상 '지속 진동' – 아주 긴 one-shot(예: 24시간)으로 처리
            VibrationEffect effect = VibrationEffect.createOneShot(
                    24L * 60L * 60L * 1000L,  // 24시간
                    VibrationEffect.DEFAULT_AMPLITUDE
            );
            vibrator.vibrate(effect);
        } else {
            // 구형용(패턴 반복)
            long[] pattern = new long[]{0, 1000};
            vibrator.vibrate(pattern, 0);
        }
    }

    private void stopVibration() {
        if (vibrator != null) vibrator.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (screenReceiver != null) unregisterReceiver(screenReceiver);
        stopVibration();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "vibe_channel", "VibeLockWatcher",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}

