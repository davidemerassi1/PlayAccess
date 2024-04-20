package com.example.sandboxtest.actionsConfigurator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sandboxtest.MyApplication;
import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Association;
import com.example.sandboxtest.database.AssociationDao;
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
    private RelativeLayout actions;
    private Context context;
    private String applicationPackage;
    private AssociationDao associationsDb;

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

    public void setup(String applicationPackage) {
        this.applicationPackage = applicationPackage;
        fab = findViewById(R.id.fab);
        newTouchFab = findViewById(R.id.fab1);
        newJoystickFab = findViewById(R.id.fab2);
        newTouchTextView = findViewById(R.id.textViewFab1);
        newJoystickTextView = findViewById(R.id.textViewFab2);
        actions = findViewById(R.id.actions);

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

        MyApplication application = (MyApplication) getContext().getApplicationContext();
        associationsDb = application.getDatabase().getDao();

        new Thread(() -> {
            Association[] associations = associationsDb.getAssociations(applicationPackage);
            Log.d("actions", ""+ associations.length);
            for (Association association : associations) {
                if (association.action == Action.JOYSTICK) {
                    ResizableDraggableButton button = new ResizableDraggableButton(context, association.event);
                    button.setX(positionStart(association.x, association.radius));
                    button.setY(positionStart(association.y, association.radius));
                    actions.addView(button);
                    button.setDimensions(association.radius * 2);
                    button.setPadding(association.radius * 2);
                } else {
                    DraggableButton button = new DraggableButton(context, association.action, association.event);
                    actions.addView(button);
                    button.setX(positionStart(association.x, button.getLayoutParams().width/2));
                    button.setY(positionStart(association.y, button.getLayoutParams().width/2));
                }
            }
        }).start();
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
                        actions.addView(resizableDraggableButton);
                    } else {
                        DraggableButton draggableButton = new DraggableButton(context, action, selectedEvent);
                        actions.addView(draggableButton);
                    }
                },
                view1 -> removeView(dialogLayout)
        );
    }

    public void save() {
        new Thread(() -> {
            associationsDb.deleteAssociations(applicationPackage);

            for (int i = 0; i < actions.getChildCount(); i++) {
                View view = actions.getChildAt(i);
                if (view instanceof DraggableButton) {
                    DraggableButton button = (DraggableButton) view;
                    int xCenter = center(button.getX(), button.getWidth());
                    int yCenter = center(button.getY(), button.getHeight());
                    associationsDb.insert(new Association(applicationPackage, button.getEvent(), button.getAction(), xCenter, yCenter, null));
                } else if (view instanceof ResizableDraggableButton) {
                    ResizableDraggableButton button = (ResizableDraggableButton) view;
                    int xCenter = center(button.getX(), button.getWidth());
                    int yCenter = center(button.getY(), button.getHeight());
                    associationsDb.insert(new Association(applicationPackage, button.getEvent(), Action.JOYSTICK, xCenter, yCenter, button.getWidth()/ 2));
                }
            }
        }).start();
    }

    private int center(float value, int size) {
        return (int)value + size / 2;
    }

    private float positionStart(int center, int radius) {
        return (float) (center - radius);
    }
}