package com.example.sandboxtest.actionsConfigurator;

import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sandboxtest.MyApplication;
import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Association;
import com.example.sandboxtest.database.AssociationDao;
import com.example.sandboxtest.facedetector.CameraFaceDetector;
import com.example.sandboxtest.utils.DraggableButton;
import com.example.sandboxtest.utils.ResizableDraggableButton;
import com.google.mlkit.vision.face.Face;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverlayView extends RelativeLayout implements OnFaceRecognizedListener {
    private AssociationDao associationsDb;
    private Map<Event, Association> map = new HashMap<>();
    private int statusBarHeight;

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
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 10;
        params.y = 10;
        windowManager.addView(this, params);
        View collapsedView = this.findViewById(R.id.layoutCollapsed);
        View expandedView = this.findViewById(R.id.configurationView);

        ConfigurationView configurationView = findViewById(R.id.configurationView);

        MyApplication application = (MyApplication) getContext().getApplicationContext();
        associationsDb = application.getDatabase().getDao();

        new Thread(() -> {
            for (Association association : associationsDb.getAssociations(applicationPackage))
                map.put(association.event, association);
            configurationView.setup(applicationPackage, associationsDb);
        }).start();

        expandedView.findViewById(R.id.closeConfigurationBtn).setOnClickListener(v -> {
            collapsedView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.GONE);
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManager.updateViewLayout(this, params);
            map = configurationView.save();
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

        statusBarHeight = getStatusBarHeight(getContext());
        new Thread(() -> {
            CameraFaceDetector cameraFaceDetector = new CameraFaceDetector(getContext(), this);
            cameraFaceDetector.startDetection();
        }).start();

    }

    private boolean smiling = false;
    @Override
    public void onFaceRecognized(Face face) {
        if (face.getSmilingProbability() > 0.25 && !smiling) {
            //map.get(Event.SMILE).execute();
            smiling = true;
            touch(map.get(Event.SMILE).x, map.get(Event.SMILE).y);
        } else if (face.getSmilingProbability() <= 0.25 && smiling) {
            //map.get(Event.SMILE).execute();
            smiling = false;
            release(map.get(Event.SMILE).x, map.get(Event.SMILE).y);
        }
    }

    Instrumentation mInstrumentation = new Instrumentation();
    private void touch(int targetX, int targetY) {
        long now = SystemClock.uptimeMillis();
        MotionEvent touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, targetX, targetY + statusBarHeight, 0);

        new Thread(() -> {
            try {
                mInstrumentation.sendPointerSync(touchEvent);
            } catch (SecurityException e) {
                //Toast.makeText(getContext(), "Errore durante la simulazione del tocco: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private void release(int targetX, int targetY) {
        long now = SystemClock.uptimeMillis();
        MotionEvent touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, targetX, targetY + statusBarHeight, 0);

        new Thread(() -> {
            try {
                mInstrumentation.sendPointerSync(touchEvent);
            } catch (SecurityException e) {
                //Toast.makeText(getContext(), "Errore durante la simulazione del tocco: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        Log.d("OverlayView", "StatusBar height: " + statusBarHeight);
        return statusBarHeight;
    }
}
