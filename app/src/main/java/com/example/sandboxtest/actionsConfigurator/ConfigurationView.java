package com.example.sandboxtest.actionsConfigurator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    private void showFABMenu(){
        isFABOpen=true;
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

    private void closeFABMenu(){
        isFABOpen=false;
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
            if(!isFABOpen)
                showFABMenu();
            else
                closeFABMenu();
        });

        newTouchFab.setOnClickListener(view -> {
            closeFABMenu();
            DraggableButton draggableButton = new DraggableButton(context, Action.TAP);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(125, 125);
            addView(draggableButton, layoutParams);
        });

        newJoystickFab.setOnClickListener(view -> {
            closeFABMenu();
            ResizableDraggableButton resizableDraggableButton = new ResizableDraggableButton(context);
            addView(resizableDraggableButton);
        });
    }
}