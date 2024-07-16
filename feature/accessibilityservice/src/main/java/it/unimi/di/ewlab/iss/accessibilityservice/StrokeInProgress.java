package it.unimi.di.ewlab.iss.accessibilityservice;

import android.accessibilityservice.GestureDescription;

public record StrokeInProgress(GestureDescription.StrokeDescription strokeDescription, int x, int y) {
}
