package it.unimi.di.ewlab.iss.accessibilityservice;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class CameraLifecycle implements LifecycleOwner {
    private final LifecycleRegistry lifecycleRegistry;

    public CameraLifecycle() {
        lifecycleRegistry = new LifecycleRegistry(this);
    }

    public void resume() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    public void pause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override
    @NonNull
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
