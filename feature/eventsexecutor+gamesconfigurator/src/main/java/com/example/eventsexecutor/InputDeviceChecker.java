package com.example.eventsexecutor;

import android.content.Context;
import android.hardware.input.InputManager;
import android.util.Log;
import android.view.InputDevice;

public class InputDeviceChecker {
    private int controllerId = -1;
    private InputDeviceObserver observer;

    private InputManager.InputDeviceListener inputDeviceListener = new InputManager.InputDeviceListener() {
        @Override
        public void onInputDeviceAdded(int deviceId) {
            // Chiamato quando un nuovo dispositivo di input viene aggiunto al sistema
            InputDevice device = InputDevice.getDevice(deviceId);
            // Verifica se il dispositivo di input potrebbe essere un "generico dispositivo di input"
            if ((device.getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
                Log.d("InputDeviceChecker", "Aggiunto controller: " + device.getName());
                controllerId = deviceId;
                observer.onInputModeChanged(true);
            }
        }

        @Override
        public void onInputDeviceRemoved(int deviceId) {
            // Chiamato quando un dispositivo di input viene rimosso dal sistema
            // Puoi gestire questa situazione se necessario
            if (deviceId == controllerId) {
                Log.d("InputDeviceChecker", "Rimosso controller");
                controllerId = -1;
                observer.onInputModeChanged(false);
            }
        }

        @Override
        public void onInputDeviceChanged(int deviceId) {
            // Chiamato quando un dispositivo di input cambia stato o proprietà
            // Puoi gestire questa situazione se necessario
        }
    };

    public InputDeviceChecker(Context context, InputDeviceObserver observer) {
        this.observer = observer;
        InputManager inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        inputManager.registerInputDeviceListener(inputDeviceListener, null);
        int[] deviceIds = inputManager.getInputDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice device = InputDevice.getDevice(deviceId);
            if ((device.getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
                Log.d("InputDeviceChecker", "Gamepad: " + device.getName());
                controllerId = deviceId;
                observer.onInputModeChanged(true);
                return;
            }
        }
        Log.d("InputDeviceChecker", "Nessun controller");
        observer.onInputModeChanged(false);
    }
}
