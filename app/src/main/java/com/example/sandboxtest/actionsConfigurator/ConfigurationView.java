package com.example.sandboxtest.actionsConfigurator;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.sandboxtest.R;
import com.example.sandboxtest.utils.DraggableButton;
import com.example.sandboxtest.utils.ResizableDraggableButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ConfigurationView extends RelativeLayout {
    private boolean isFABOpen = false;
    private FloatingActionButton fab;
    private FloatingActionButton newTouchFab;
    private FloatingActionButton newJoystickFab;
    private TextView newTouchTextView;
    private TextView newJoystickTextView;
    private Context context;

    public ConfigurationView(Context context) {
        super(context);
        this.context = context;
    }

    public ConfigurationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    private void showFABMenu() {
        isFABOpen = true;
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        fab.startAnimation(rotateAnimation);
        newTouchFab.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        newJoystickFab.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        //fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        newTouchTextView.setVisibility(VISIBLE);
        newJoystickTextView.setVisibility(VISIBLE);
        newTouchTextView.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        newJoystickTextView.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_reverse);
        fab.startAnimation(rotateAnimation);
        newTouchFab.animate().translationY(0);
        newJoystickFab.animate().translationY(0);
        newTouchTextView.setVisibility(GONE);
        newJoystickTextView.setVisibility(GONE);
        newTouchTextView.animate().translationY(0);
        newJoystickTextView.animate().translationY(0);
    }

    public void setup() {
        fab = findViewById(R.id.fab);
        newTouchFab = findViewById(R.id.fab1);
        newJoystickFab = findViewById(R.id.fab2);
        newTouchTextView = findViewById(R.id.textViewFab1);
        newJoystickTextView = findViewById(R.id.textViewFab2);

        fab.setOnClickListener(view -> {
            if (!isFABOpen)
                showFABMenu();
            else
                closeFABMenu();
        });

        newTouchFab.setOnClickListener(view -> {
            closeFABMenu();
            showDialog(Action.TAP, false);
        });

        newJoystickFab.setOnClickListener(view -> {
            closeFABMenu();
            showDialog(Action.JOYSTICK, true);
        });
    }

    private void showDialog(Action action, boolean joystick) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        EventDialog dialogLayout = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(dialogLayout, layoutParams);
        dialogLayout.init(joystick,
                view1 -> {
                    Event selectedEvent = dialogLayout.getSelectedEvent();
                    if (selectedEvent == null) {
                        dialogLayout.showErrorMessage();
                        return;
                    }
                    removeView(dialogLayout);
                    if (joystick) {
                        ResizableDraggableButton resizableDraggableButton = new ResizableDraggableButton(context, selectedEvent);
                        addView(resizableDraggableButton);
                    } else {
                        DraggableButton draggableButton = new DraggableButton(context, action, selectedEvent);
                        addView(draggableButton);
                    }
                },
                view1 -> removeView(dialogLayout)
        );
    }
}