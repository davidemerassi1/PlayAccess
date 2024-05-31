package actionsConfigurator;

import static android.view.WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;

import com.example.actionsrecognizer.facialexpressionactionsrecognizer.ActionListener;
import com.example.actionsrecognizer.facialexpressionactionsrecognizer.FacialExpressionActionsRecognizer;
import com.example.eventsexecutor.R;

import it.unimi.di.ewlab.iss.common.database.Association;
import it.unimi.di.ewlab.iss.common.database.AssociationDao;
import it.unimi.di.ewlab.iss.common.database.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.model.actions.ButtonAction;
import it.unimi.di.ewlab.iss.common.model.actionsmodels.ButtonActionsModel;

public class OverlayView extends RelativeLayout implements ActionListener {
    private AssociationDao associationsDb;
    private Map<Action, Association> map = new HashMap<>();
    private boolean configurationOpened = false;
    private ConfigurationView configurationView;
    private MutableLiveData<Boolean> needCamera = new MutableLiveData<>(false);
    public static final Action FACE_MOVEMENT_ACTION;
    private ButtonActionsModel buttonActionsModel;
    MutableLiveData<Association[]> associations = new MutableLiveData<>();

    //TODO: spostare in Action
    static {
        FACE_MOVEMENT_ACTION = new Action(0, "Face Movement", Action.ActionType.FACIAL_EXPRESSION) {
            @Override
            public boolean equals(Object obj) {
                return this == obj;
            }
        };

        FACE_MOVEMENT_ACTION.setIs2d(true);
    }

    private String applicationPackage;

    public OverlayView(Context context) {
        super(context);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    public void init(WindowManager windowManager) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 10;
        params.y = 10;
        windowManager.addView(this, params);
        View collapsedView = this.findViewById(R.id.layoutCollapsed);
        View expandedView = this.findViewById(R.id.configurationView);

        associationsDb = MainModel.getInstance().getAssociationsDb().getDao();

        configurationView = findViewById(R.id.configurationView);
        configurationView.setup(associationsDb);

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

        /*new Thread(() -> {
            CameraFaceDetector cameraFaceDetector = new CameraFaceDetector(getContext(), this, this);
            cameraFaceDetector.startDetection();
        }).start();*/

        /*if (!MainModel.getInstance().getFacialExpressionActions().isEmpty()) {
            FacialExpressionActionsRecognizer.Companion.getInstance(MainModel.getInstance().getActions(), List.of(this)).init(
                    getContext(), this
            );
        }*/

        //new InputDeviceChecker(getContext(), this);

        needCamera.observeForever(nc -> {
            if (nc) {
                //lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
                //announceForAccessibility("Camera needed");
                getContext().sendBroadcast(new Intent("com.example.accessibilityservice.NEED_CAMERA"));
            } else {
                //lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
                //announceForAccessibility("Camera not needed");
                getContext().sendBroadcast(new Intent("com.example.accessibilityservice.NO_CAMERA"));
            }
        });

        associations.observeForever(associations -> {
            Log.d("OverlayView", "init: " + associations.length);
            for (Association association : associations) {
                if (association.action.getActionType() == Action.ActionType.FACIAL_EXPRESSION) {
                    Log.d("OverlayView", "init: need camera");
                    needCamera.setValue(true);
                }
                map.put(association.action, association);
                if (association.event == Event.MONODIMENSIONAL_SLIDING) {
                    map.put(association.additionalAction1, association);
                    map.put(association.additionalAction2, association);
                }
            }
            buttonActionsModel = new ButtonActionsModel(map.keySet());
            configurationView.changeGame(applicationPackage, associations);
        });
    }

    private EventExecutor executor = new EventExecutor(getContext());

    public void changeGame(String applicationPackage) {
        this.applicationPackage = applicationPackage;

        new Thread(() -> {
            needCamera.postValue(false);
            associations.postValue(associationsDb.getAssociations(applicationPackage));
        }).start();

        Drawable appIcon = getAppIconFromPackageName(getContext(), applicationPackage);
        if (appIcon != null)
            ((ImageView) findViewById(R.id.gameIcon)).setImageDrawable(appIcon);
    }

    private Drawable getAppIconFromPackageName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("OverlayView", "package not found: " + packageName);
        }
        return null;
    }

    public void start() {
        setVisibility(VISIBLE);
        if (needCamera.getValue())
            getContext().sendBroadcast(new Intent("com.example.accessibilityservice.NEED_CAMERA"));
    }

    public void stop() {
        setVisibility(GONE);
        getContext().sendBroadcast(new Intent("com.example.accessibilityservice.NO_CAMERA"));
    }

    private void execute1d(Association association, Action action) {
        if (!configurationOpened && getVisibility()==VISIBLE) {
            if (association.action.equals(action))
                executor.execute1d(association, EventExecutor.Action1D.MOVE_LEFT);
            else if (association.additionalAction1.equals(action))
                executor.execute1d(association, EventExecutor.Action1D.MOVE_RIGHT);
            else
                executor.execute1d(association, EventExecutor.Action1D.RESET);
        }
    }

    /*
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.d("OverlayView", "onGenericMotionEvent: " + event.getSource() + " " + event.getAction() + " " + event.getAxisValue(MotionEvent.AXIS_X) + " " + event.getAxisValue(MotionEvent.AXIS_Y));
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
    */

    @Override
    public void onActionStarts(@NonNull Action action) {
        if (!configurationOpened && getVisibility()==VISIBLE) {
            if (map.containsKey(action)) {
                Association association = map.get(action);
                if (association.event != Event.MONODIMENSIONAL_SLIDING)
                    executor.execute(association);
                else {
                    execute1d(association, action);
                }
            } else
                Log.d("OverlayView", "ho rilevato " + action.getName() + " ma non ho nessuna associazione");
        }
    }

    @Override
    public void onActionEnds(@NonNull Action action) {
        if (!configurationOpened && getVisibility()==VISIBLE) {
            if (map.containsKey(action)) {
                Association association = map.get(action);
                executor.stopExecuting(association);
            } else
                Log.d("OverlayView", "ho rilevato " + action.getName() + " ma non ho nessuna associazione");
        }
    }

    @Override
    public void on2dMovement(float x, float y) {
        if (!configurationOpened && getVisibility()==VISIBLE && map.containsKey(FACE_MOVEMENT_ACTION))
            executor.execute2d(map.get(FACE_MOVEMENT_ACTION), x, y);
    }
}
