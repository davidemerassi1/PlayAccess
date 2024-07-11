package com.example.accessibilityservice;

import android.accessibilityservice.GestureDescription;

public record StrokeInProgress(GestureDescription.StrokeDescription strokeDescription, int x, int y) {
}
