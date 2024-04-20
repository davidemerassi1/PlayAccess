package com.example.sandboxtest.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.sandboxtest.R;
import com.example.sandboxtest.actionsConfigurator.Action;
import com.example.sandboxtest.actionsConfigurator.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DraggableButton extends androidx.appcompat.widget.AppCompatImageButton implements View.OnTouchListener {
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;
    private Action action;
    private Event event;

    public DraggableButton(Context context, Action action, Event event) {
        super(context);
        this.action = action;
        this.event = event;
        setOnTouchListener(this);
        setPadding(15, 15, 15, 15);
        setBackgroundResource(R.drawable.circle_background_white);
        setScaleType(ScaleType.FIT_XY);
        setLayoutParams(new FrameLayout.LayoutParams(120, 120));
        switch (action) {
            case TAP:
                setImageResource(R.drawable.touch_icon);
                break;
        }
    }

    public DraggableButton(Context context) {
        super(context);
    }

    public DraggableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
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
            default:
                return false;
        }
        return true;
    }

    public Action getAction() {
        return action;
    }

    public Event getEvent() {
        return event;
    }
}
