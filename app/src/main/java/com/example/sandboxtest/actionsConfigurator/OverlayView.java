package com.example.sandboxtest.actionsConfigurator;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.sandboxtest.MyApplication;
import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Association;
import com.example.sandboxtest.database.AssociationDao;
import com.example.sandboxtest.database.CameraAction;
import com.example.sandboxtest.database.Event;
import com.example.sandboxtest.facedetector.CameraFaceDetector;
import com.example.sandboxtest.facedetector.OnFaceRecognizedListener;
import com.google.mlkit.vision.face.Face;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction;
import it.unimi.di.ewlab.iss.common.model.actionsmodels.ButtonActionsModel;

public class OverlayView extends RelativeLayout implements OnFaceRecognizedListener, LifecycleOwner {
    private AssociationDao associationsDb;
    private Map<Action, Association> map = new HashMap<>();
    private boolean configurationOpened = false;
    private ConfigurationView configurationView;
    private LifecycleRegistry lifecycleRegistry;
    private MutableLiveData<Boolean> needCamera = new MutableLiveData<>(false);
    private ButtonActionsModel buttonActionsModel;

    public OverlayView(Context context) {
        super(context);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(WindowManager windowManager, String applicationPackage) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 10;
        params.y = 10;
        windowManager.addView(this, params);
        View collapsedView = this.findViewById(R.id.layoutCollapsed);
        View expandedView = this.findViewById(R.id.configurationView);

        configurationView = findViewById(R.id.configurationView);

        MyApplication application = (MyApplication) getContext().getApplicationContext();
        associationsDb = application.getDatabase().getDao();

        new Thread(() -> {
            needCamera.postValue(false);
            for (Association association : associationsDb.getAssociations(applicationPackage)) {
                if (association.action.getActionType() == Action.ActionType.FACIAL_EXPRESSION) {
                    Log.d("OverlayView", "init: need camera");
                    needCamera.postValue(true);
                }
                map.put(association.action, association);
                if (association.event == Event.MONODIMENSIONAL_SLIDING) {
                    map.put(association.additionalAction1, association);
                    map.put(association.additionalAction2, association);
                }
            }
            buttonActionsModel = new ButtonActionsModel(map.keySet());
            configurationView.setup(applicationPackage, associationsDb);
        }).start();

        expandedView.findViewById(R.id.closeConfigurationBtn).setOnClickListener(v -> {
            new Thread(() -> executor.releaseAll()).start();
            collapsedView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.GONE);
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManager.updateViewLayout(this, params);
            List<Association> associationList = configurationView.save();
            map.clear();
            boolean needed = false;
            for (Association association : associationList) {
                map.put(association.action, association);
                if (association.action.getActionType() == Action.ActionType.FACIAL_EXPRESSION) {
                    needed = true;
                }
                if (association.event == Event.MONODIMENSIONAL_SLIDING) {
                    map.put(association.additionalAction1, association);
                    map.put(association.additionalAction2, association);
                }
            }
            buttonActionsModel = new ButtonActionsModel(map.keySet());
            needCamera.postValue(needed);
            configurationOpened = false;
        });

        collapsedView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (Math.abs(params.x - initialX) < 5 && Math.abs(params.y - initialY) < 5) {
                            collapsedView.setVisibility(View.GONE);
                            expandedView.setVisibility(View.VISIBLE);
                            params.width = WindowManager.LayoutParams.MATCH_PARENT;
                            params.height = WindowManager.LayoutParams.MATCH_PARENT;
                            windowManager.updateViewLayout(OverlayView.this, params);
                            configurationOpened = true;
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(OverlayView.this, params);
                        return true;
                }
                return false;
            }
        });

        new Thread(() -> {
            CameraFaceDetector cameraFaceDetector = new CameraFaceDetector(getContext(), this, this);
            cameraFaceDetector.startDetection();
        }).start();

        lifecycleRegistry = new LifecycleRegistry(this);
        //new InputDeviceChecker(getContext(), this);

        needCamera.observeForever(nc -> {
            if (nc) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            } else {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
            }
        });
    }

    private EventExecutor executor = new EventExecutor(getContext());

    @Override
    public void onFaceRecognized(Face face) {
        if (getVisibility() == GONE || configurationOpened)
            return;
        /*if (face.getSmilingProbability() > 0.3 && map.containsKey(CameraAction.SMILE.getTag())) {
            Association association = map.get(CameraAction.SMILE.getTag());
            if (association.event == Event.MONODIMENSIONAL_SLIDING)
                execute2d(association, CameraAction.SMILE.getTag());
            else
                executor.execute(map.get(CameraAction.SMILE.getTag()));
        }
        if (map.containsKey(CameraAction.FACE_MOVEMENT.getTag())) {
            Association association = map.get(CameraAction.FACE_MOVEMENT.getTag());
            executor.execute2d(association, -face.getHeadEulerAngleY() / 35, -(face.getHeadEulerAngleX() - 10) / 30);
        }*/
    }

    public void start() {
        setVisibility(VISIBLE);
        if (needCamera.getValue())
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    public void stop() {
        setVisibility(GONE);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    public void destroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    private void execute1d(Association association, Action action) {
        if (association.action.equals(action))
            executor.execute1d(association, EventExecutor.Action1D.MOVE_LEFT);
        else if (association.additionalAction1.equals(action))
            executor.execute1d(association, EventExecutor.Action1D.MOVE_RIGHT);
        else
            executor.execute1d(association, EventExecutor.Action1D.RESET);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            if (!configurationOpened) {
                Log.d("Hai premuto", "" + keyCode);
                ButtonAction ba = buttonActionsModel.getButtonActionByIds(String.valueOf(event.getSource()), String.valueOf(keyCode));
                if (ba != null && map.containsKey(ba)) {
                    Association association = map.get(ba);
                    if (association.event != Event.MONODIMENSIONAL_SLIDING)
                        executor.execute(association);
                    else {
                        execute1d(association, ba);
                    }
                }
            } else
                configurationView.onKeyUp(keyCode, event);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            if (!configurationOpened)
                Log.d("OverlayView", "onKeyDown: " + KeyEvent.keyCodeToString(keyCode));
            else
                configurationView.onKeyDown(keyCode, event);
            return true;
        }
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Verifica se l'evento proviene da un joystick
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {
            if (!configurationOpened) {
                //TODO: da verificare il codice: 19 corrisponde a KEYCODE_DPAD_UP
                ButtonAction ba = buttonActionsModel.getButtonActionByIds(String.valueOf(event.getSource()), String.valueOf(19));
                if (ba != null && map.containsKey(ba)) {
                    Association association = map.get(ba);
                    float x = -event.getAxisValue(MotionEvent.AXIS_X);
                    float y = -event.getAxisValue(MotionEvent.AXIS_Y);
                    Log.d("OverlayView", "onGenericMotionEvent: " + x + " " + y);
                    executor.execute2d(association, x, y);
                }
            } else {
                configurationView.onGenericMotionEvent(event);
            }
            return true;
        }
        return false;
    }
}
