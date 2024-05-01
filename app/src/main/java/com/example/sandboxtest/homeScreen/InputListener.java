package com.example.sandboxtest.homeScreen;

import android.hardware.input.InputManager;
import android.util.Log;

public class InputListener implements InputManager.InputDeviceListener{
    @Override
    public void onInputDeviceAdded(int deviceId) {
        Log.d("InputListener", "onInputDeviceAdded: " + deviceId);
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        Log.d("InputListener", "onInputDeviceRemoved: " + deviceId);
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        Log.d("InputListener", "onInputDeviceChanged: " + deviceId);
    }
}
