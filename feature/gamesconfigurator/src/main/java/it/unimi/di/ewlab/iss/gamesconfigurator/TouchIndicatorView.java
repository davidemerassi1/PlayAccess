package it.unimi.di.ewlab.iss.gamesconfigurator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

public class TouchIndicatorView extends View {
    private Integer x;
    private Integer y;
    private Paint paint;
    private int statusBarHeight;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Thread thread;

    public TouchIndicatorView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.primaryColor, null) + 0x80000000);
        paint.setStyle(Paint.Style.FILL);
        this.statusBarHeight = getStatusBarHeight(context);
        invalidate();
    }

    public void onTouch(int x, int y) {
        this.x = x;
        this.y = y - statusBarHeight;
        handler.post(this::invalidate);
    }

    public void clear() {
        x = null;
        y = null;
        if (thread != null && thread.isAlive())
            thread.interrupt();
        handler.post(this::invalidate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x == null || y == null)
            return;
        canvas.drawCircle(x, y, 40, paint);
    }

    private int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        Log.d("OverlayView", "StatusBar height: " + statusBarHeight);
        return statusBarHeight;
    }

    public void drawSwipe(int x, int y, int length, SwipeDirection swipeDirection, int duration, boolean up) {
        int steps = 10;
        float stepLength = (float) length / 10;
        int stepDuration = duration / steps;
        onTouch(x, y);
        thread = new Thread(() -> {
            for (int i = 1; i <= steps; i++) {
                switch (swipeDirection) {
                    case UP -> onTouch(x, y - (int) (stepLength * i));
                    case DOWN -> onTouch(x, y + (int) (stepLength * i));
                    case LEFT -> onTouch(x - (int) (stepLength * i), y);
                    case RIGHT -> onTouch(x + (int) (stepLength * i), y);
                }
                try {
                    Thread.sleep(stepDuration);
                } catch (InterruptedException e) {
                    Log.d("TouchIndicatorView", "Thread interrupted");
                    clear();
                }
            }
            if (up)
                clear();
        });
        thread.start();
    }

    public enum SwipeDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
