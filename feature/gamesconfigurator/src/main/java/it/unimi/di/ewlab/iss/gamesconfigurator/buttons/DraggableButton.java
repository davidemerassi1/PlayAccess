package it.unimi.di.ewlab.iss.gamesconfigurator.buttons;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.gamesconfigurator.R;

import it.unimi.di.ewlab.iss.common.database.Event;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class DraggableButton extends RelativeLayout implements EventButton, View.OnTouchListener {
    private float lastTouchX;
    private float lastTouchY;
    private float posX;
    private float posY;
    private Event event;
    private Action action;
    private OnClickListener listener;

    public DraggableButton(Context context, Event event) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.button_layout, this, true);
        ImageButton fab = findViewById(R.id.fab);

        this.event = event;
        fab.setOnTouchListener(this);

        switch (event) {
            case TAP, TAP_ON_OFF -> fab.setImageResource(R.drawable.tap_icon);
            case SWIPE_DOWN -> fab.setImageResource(R.drawable.swipe_down_icon);
            case SWIPE_UP -> fab.setImageResource(R.drawable.swipe_up_icon);
            case SWIPE_LEFT -> fab.setImageResource(R.drawable.swipe_left_icon);
            case SWIPE_RIGHT -> fab.setImageResource(R.drawable.swipe_right_icon);
            case LONG_TAP -> fab.setImageResource(R.drawable.long_tap_icon);
            case MONODIMENSIONAL_SLIDING -> fab.setImageResource(R.drawable.monodimensional_sliding_icon);
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
                if (MainModel.getInstance().getTutorialStep() != null && MainModel.getInstance().getTutorialStep().getValue() == 4)
                    MainModel.getInstance().setNextTutorialStep();
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

    @Override
    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void showAlert() {
        findViewById(R.id.alert).setVisibility(VISIBLE);
    }

    @Override
    public void hideAlert() {
        findViewById(R.id.alert).setVisibility(GONE);
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
}
