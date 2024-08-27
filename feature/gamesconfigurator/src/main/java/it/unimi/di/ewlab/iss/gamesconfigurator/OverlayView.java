package it.unimi.di.ewlab.iss.gamesconfigurator;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class OverlayView extends RelativeLayout {
    private ConfigurationView configurationView;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private View collapsedView;
    private View expandedView;

    public OverlayView(Context context) {
        super(context);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(WindowManager windowManager) {
        this.windowManager = windowManager;
        params = new WindowManager.LayoutParams(
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
        collapsedView = this.findViewById(R.id.layoutCollapsed);
        expandedView = this.findViewById(R.id.configurationView);

        configurationView = findViewById(R.id.configurationView);
        configurationView.setup();

        expandedView.findViewById(R.id.closeConfigurationBtn).setOnClickListener(v -> {
            closeConfiguration();
            new Thread(() -> configurationView.save()).start();
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
                            configurationView.open();
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
    }

    public void changeGame(String applicationPackage) {
        configurationView.changeGame(applicationPackage);

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
    }

    public void stop() {
        setVisibility(GONE);
    }

    public void closeConfiguration() {
        announceForAccessibility("CONFIGURATION_CLOSED");
        collapsedView.setVisibility(View.VISIBLE);
        expandedView.setVisibility(View.GONE);
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowManager.updateViewLayout(this, params);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.d("OverlayView", "onGenericMotionEvent: " + event.getSource() + " " + event.getAction() + " " + event.getAxisValue(MotionEvent.AXIS_X) + " " + event.getAxisValue(MotionEvent.AXIS_Y));
        return false;
    }
}
