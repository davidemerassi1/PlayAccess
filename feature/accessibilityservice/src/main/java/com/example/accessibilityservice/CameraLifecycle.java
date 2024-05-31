package com.example.accessibilityservice;

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
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
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
