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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class VibeService extends Service {
    private Vibrator vibrator;
    private BroadcastReceiver screenReceiver;
    private NotificationManager nm;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            createNotificationChannel();
            startForeground(1, buildNotif("Starting…"));

            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            screenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent i) {
                    try {
                        String a = i.getAction();
                        if (Intent.ACTION_USER_PRESENT.equals(a)) {
                            show("USER_PRESENT → 진동 시작");
                            startContinuousVibration();
                            updateNotif("Vibrating (unlocked)");
                        } else if (Intent.ACTION_SCREEN_OFF.equals(a)) {
                            show("SCREEN_OFF → 진동 중지");
                            stopVibration();
                            updateNotif("Stopped (screen off)");
                        } else if (Intent.ACTION_SCREEN_ON.equals(a)) {
                            show("SCREEN_ON → 진동 중지");
                            stopVibration();
                            updateNotif("Stopped (screen on/lock)");
                        }
                    } catch (Throwable e) {
                        updateNotif("Receiver error: " + e.getClass().getSimpleName());
                    }
                }
            };

            IntentFilter f = new IntentFilter();
            f.addAction(Intent.ACTION_USER_PRESENT);
            f.addAction(Intent.ACTION_SCREEN_OFF);
            f.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(screenReceiver, f);

            updateNotif("Idle, waiting events");
        } catch (Throwable e) {
            updateNotif("Service onCreate error: " + e.getClass().getSimpleName());
            Toast.makeText(this, "Service error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // 크래시 방지
        }
    }

    private void startContinuousVibration() {
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] timings = new long[]{0, 1000, 50};
            int[] amps = new int[]{0, 255, 0};
            VibrationEffect effect = VibrationEffect.createWaveform(timings, amps, 0);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(new long[]{0, 1000, 50}, 0);
        }
    }

    private void stopVibration() {
        if (vibrator != null) vibrator.cancel();
    }

    private Notification buildNotif(String text) {
        return new NotificationCompat.Builder(this, "vibe_channel")
                .setContentTitle("VibeLockWatcher")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setOngoing(true)
                .build();
    }

    private void updateNotif(String text) {
        if (nm != null) nm.notify(1, buildNotif(text));
    }

    private void show(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try { if (screenReceiver != null) unregisterReceiver(screenReceiver); } catch (Throwable ignored) {}
        stopVibration();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    "vibe_channel", "VibeLockWatcher",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            m.createNotificationChannel(ch);
        }
    }
}
