package com.example.sandboxtest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.sandboxtest.R;

public class ResizableDraggableButton extends FrameLayout {
    private ImageButton fab;
    private ImageButton resizeButton;
    private Context context;
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;

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

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.resizable_fab_layout, this, true);

        fab = findViewById(R.id.fab);
        resizeButton = findViewById(R.id.resize_button);

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
                        int newDim = initialWidth + delta;

                        if (newDim < 100) {
                            newDim = 100;
                            delta = 100 - initialWidth;
                        }

                        // Imposta le nuove dimensioni del FAB
                        fab.getLayoutParams().width = newDim;
                        fab.getLayoutParams().height = newDim;
                        setX(initialX - delta);
                        setY(initialY - delta);
                        fab.requestLayout();
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
            }
            return true;
        });
    }
}
