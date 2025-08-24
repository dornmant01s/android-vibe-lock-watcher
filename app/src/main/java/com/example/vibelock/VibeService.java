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
                .build();

        startForeground(1, notification);

        // 화면 ON/OFF, 잠금 해제 이벤트 감지
        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    // 잠금 해제됨 → 진동 시작
                    vibrator.vibrate(new long[]{0, 1000}, 0);
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    // 화면 꺼짐 → 진동 중지
                    vibrator.cancel();
                } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    // 화면 켜짐(잠금상태) → 진동 중지
                    vibrator.cancel();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (screenReceiver != null) unregisterReceiver(screenReceiver);
        vibrator.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "vibe_channel", "VibeLockWatcher",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}
