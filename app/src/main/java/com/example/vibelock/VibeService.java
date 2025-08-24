package com.example.vibelock;

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

public class VibeService extends Service {
    private Vibrator vibrator;
    private BroadcastReceiver screenReceiver;
    private int amplitude = 255;
    private int lengthMs = 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            screenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent i) {
                    try {
                        String a = i.getAction();
                        if (Intent.ACTION_USER_PRESENT.equals(a)) {
                            startContinuousVibration();
                        } else if (Intent.ACTION_SCREEN_OFF.equals(a) || Intent.ACTION_SCREEN_ON.equals(a)) {
                            stopVibration();
                        }
                    } catch (Throwable e) {
                        // ignore
                    }
                }
            };

            IntentFilter f = new IntentFilter();
            f.addAction(Intent.ACTION_USER_PRESENT);
            f.addAction(Intent.ACTION_SCREEN_OFF);
            f.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(screenReceiver, f);

        } catch (Throwable e) {
            Toast.makeText(this, "Service error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // 크래시 방지
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            amplitude = intent.getIntExtra("amp", amplitude);
            lengthMs = intent.getIntExtra("len", lengthMs);
        }
        return START_STICKY;
    }

    private void startContinuousVibration() {
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] timings = new long[]{0, lengthMs, 50};
            int[] amps = new int[]{0, amplitude, 0};
            VibrationEffect effect = VibrationEffect.createWaveform(timings, amps, 0);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(new long[]{0, lengthMs, 50}, 0);
        }
    }

    private void stopVibration() {
        if (vibrator != null) vibrator.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try { if (screenReceiver != null) unregisterReceiver(screenReceiver); } catch (Throwable ignored) {}
        stopVibration();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
