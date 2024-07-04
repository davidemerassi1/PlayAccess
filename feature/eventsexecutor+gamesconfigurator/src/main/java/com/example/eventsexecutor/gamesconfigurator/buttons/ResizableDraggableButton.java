package com.example.eventsexecutor.gamesconfigurator.buttons;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.eventsexecutor.R;

import it.unimi.di.ewlab.iss.common.database.Event;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class ResizableDraggableButton extends FrameLayout implements EventButton {
    private ImageButton fab;
    private ImageButton resizeButton;
    private Context context;
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;
    private Action action;
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

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.resizable_button_layout, this, true);

        fab = findViewById(R.id.fab);
        resizeButton = findViewById(R.id.resize_button);


        int width = getDisplayWidth();

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

    private int getDisplayWidth() {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
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

    @Override
    public void showAlert() {
        findViewById(R.id.alert).setVisibility(VISIBLE);
    }

    @Override
    public void hideAlert() {
        findViewById(R.id.alert).setVisibility(GONE);
    }
}
