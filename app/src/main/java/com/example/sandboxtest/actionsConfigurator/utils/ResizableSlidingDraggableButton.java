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

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class ResizableSlidingDraggableButton extends FrameLayout implements EventButton {
    private ImageButton fab;
    private RelativeLayout layout;
    private ImageButton resizeButton;
    private Context context;
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;
    private Action action;
    private Action action2;
    private Action action3;
    private boolean resetToStart;
    private OnClickListener listener;

    public ResizableSlidingDraggableButton(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ResizableSlidingDraggableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ResizableSlidingDraggableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public ResizableSlidingDraggableButton(Context context, Action action, Action action2, Action action3) {
        super(context);
        this.context = context;
        this.action = action;
        this.action2 = action2;
        this.action3 = action3;
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.resizable_sliding_button_layout, this, true);

        fab = findViewById(R.id.fab);
        layout = findViewById(R.id.resizable_sliding_button_layout);
        resizeButton = findViewById(R.id.resize_button);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        resizeButton.setOnTouchListener(new OnTouchListener() {
            private int initialWidth, initialX;
            private float initialTouchX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialWidth = layout.getWidth();
                        initialTouchX = event.getRawX();
                        initialX = (int) getX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int delta = (int) (initialTouchX - event.getRawX());
                        int newDim = initialWidth + 2 * delta;

                        if (newDim < 200) {
                            newDim = 200;
                            delta = (newDim - initialWidth) / 2;
                        }

                        if (newDim > width) {
                            newDim = width;
                            delta = (newDim - initialWidth) / 2;
                        }

                        setDimensions(newDim);

                        setX(initialX - delta);
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

    public void setDimensions(int newDim) {
        layout.getLayoutParams().width = newDim;
        layout.requestLayout();
    }

    public Action getAction() {
        return action;
    }

    public Action getAction2() {
        return action2;
    }

    public Action getAction3() {
        return action3;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setAction2(Action action) {
        this.action2 = action;
    }

    public void setAction3(Action action) {
        this.action3 = action;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Event getEvent() {
        return Event.MONODIMENSIONAL_SLIDING;
    }

    @Override
    public void setEvent(Event event) {
    }

    public void setResetToStart(boolean resetToStart) {
        this.resetToStart = resetToStart;
    }

    public boolean getResetToStart() {
        return resetToStart;
    }
}
