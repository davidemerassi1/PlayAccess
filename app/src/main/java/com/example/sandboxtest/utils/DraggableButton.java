package com.example.sandboxtest.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DraggableButton extends FloatingActionButton implements View.OnTouchListener {
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;

    public DraggableButton(Context context) {
        super(context);
        init();
    }

    public DraggableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DraggableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
        //setScaleType(ScaleType.FIT_CENTER);
        //setCustomSize(125);
        setBackgroundTintList(ColorStateList.valueOf(0x80FFFFFF));
        setImageTintList(ColorStateList.valueOf(0xFF000000));
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
}
