package com.example.vibelock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent serviceIntent = new Intent(this, VibeService.class);
        startForegroundService(serviceIntent);

        finish(); // UI 없이 바로 종료
    }
}
