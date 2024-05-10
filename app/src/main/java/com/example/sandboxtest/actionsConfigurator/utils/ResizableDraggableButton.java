package com.example.sandboxtest.actionsConfigurator.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Event;

public class ResizableDraggableButton extends FrameLayout implements EventButton {
    private ImageButton fab;
    private ImageButton resizeButton;
    private Context context;
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;
    private Integer action;
    private OnClickListener listener;

    public ResizableDraggableButton(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ResizableDraggableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ResizableDraggableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public ResizableDraggableButton(Context context, Integer action) {
        super(context);
        this.context = context;
        this.action = action;
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.resizable_button_layout, this, true);

        fab = findViewById(R.id.fab);
        resizeButton = findViewById(R.id.resize_button);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        resizeButton.setOnTouchListener(new OnTouchListener() {
            private int initialWidth, initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Memorizza le dimensioni iniziali del FAB
                        initialWidth = fab.getWidth();

                        // Memorizza le coordinate iniziali
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        initialX = (int) getX();
                        initialY = (int) getY();

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Calcola il cambio di dimensione del FAB
                        int delta = (int) Math.min(initialTouchX - event.getRawX(), initialTouchY - event.getRawY());
                        int newDim = initialWidth + 2 * delta;

                        if (newDim < 100) {
                            newDim = 100;
                            delta = (newDim - initialWidth) / 2;
                        }

                        if (newDim > width) {
                            newDim = width;
                            delta = (newDim - initialWidth) / 2;
                        }

                        setDimensions(newDim);
                        setPadding(newDim);

                        setX(initialX - delta);
                        setY(initialY - delta);
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Aggiungi il listener per lo spostamento del FAB
        fab.setOnTouchListener((v, event) -> {
            float touchX = event.getRawX();
            float touchY = event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchX = touchX;
                    lastTouchY = touchY;
                    posX = getX();
                    posY = getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = touchX - lastTouchX;
                    float dy = touchY - lastTouchY;
                    setX(posX + dx);
                    setY(posY + dy);
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(touchX - lastTouchX) < 10 && Math.abs(touchY - lastTouchY) < 10)
                        listener.onClick(this);
                    break;
            }
            return true;
        });
    }

    public void setPadding(int newDim) {
        int padding = newDim / 2 - 30;
        double k = 0.2929;  //1-sin45
        fab.setPadding(padding, padding, padding, padding);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(resizeButton.getLayoutParams());
        params.setMargins((int) (k * newDim / 2 - 15), (int) (k * newDim / 2 - 15), 0, 0);
        resizeButton.setLayoutParams(params);
    }

    public void setDimensions(int newDim) {
        fab.getLayoutParams().width = newDim;
        fab.getLayoutParams().height = newDim;
        fab.requestLayout();
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Event getEvent() {
        return Event.JOYSTICK;
    }

    @Override
    public void setEvent(Event event) {
    }
}
