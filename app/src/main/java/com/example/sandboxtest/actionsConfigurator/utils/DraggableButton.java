package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Event;

public class DraggableButton extends androidx.appcompat.widget.AppCompatImageButton implements EventButton, View.OnTouchListener {
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;
    private Event event;
    private String action;
    private OnClickListener listener;

    public DraggableButton(Context context, Event event, String action) {
        super(context);
        this.action = action;
        this.event = event;
        setOnTouchListener(this);
        setPadding(15, 15, 15, 15);
        setBackgroundResource(R.drawable.circle_background_white);
        setScaleType(ScaleType.FIT_XY);
        setLayoutParams(new FrameLayout.LayoutParams(120, 120));
        switch (event) {
            case TAP -> setImageResource(R.drawable.tap_icon);
            case SWIPE_DOWN -> setImageResource(R.drawable.swipe_down_icon);
            case SWIPE_UP -> setImageResource(R.drawable.swipe_up_icon);
            case SWIPE_LEFT -> setImageResource(R.drawable.swipe_left_icon);
            case SWIPE_RIGHT -> setImageResource(R.drawable.swipe_right_icon);
            case LONG_TAP -> setImageResource(R.drawable.long_tap_icon);
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
            case MotionEvent.ACTION_UP:
                if (Math.abs(touchX - lastTouchX) < 10 && Math.abs(touchY - lastTouchY) < 10)
                    listener.onClick(this);
                break;
            default:
                return false;
        }
        return true;
    }

    public Event getEvent() {
        return event;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }
}
