package com.example.vibelock;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 앱 시작 시 바로 서비스 실행
        Intent serviceIntent = new Intent(this, VibeService.class);
        startForegroundService(serviceIntent);

        finish(); // UI 없이 바로 종료
    }
}
