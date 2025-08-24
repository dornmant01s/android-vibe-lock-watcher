package com.example.vibelock;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class VibeAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // No-op
    }

    @Override
    public void onInterrupt() {
        // No-op
    }
}
