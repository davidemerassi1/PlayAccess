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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    //ricordarsi che deve essere aggiornato quando si aggiorna il db
    private Collection<Association> associations;
    private OnClickListener updateListener = v -> {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        EditEventDialog dialogLayout = (EditEventDialog) inflater.inflate(R.layout.edit_dialog_layout, this, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams.setMargins(50, 0, 50, 0);
        EventButton eventButton = (EventButton) v;
        dialogLayout.init(
                eventButton.getEvent(),
                view -> {
                    eventButton.setEvent(dialogLayout.getSelectedEvent());
                    removeView(dialogLayout);
                },
                view -> {
                    actions.removeView(v);
                    removeView(dialogLayout);
                },
                view -> removeView(dialogLayout),
                availableEvents());
        addView(dialogLayout, layoutParams);
    };

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

    public void setup(String applicationPackage, AssociationDao associationsDb, Collection<Association> associations) {
        this.applicationPackage = applicationPackage;
        this.associationsDb = associationsDb;
        this.associations = associations;
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

        for (Association association : associations) {
            if (association.action == Action.JOYSTICK) {
                ResizableDraggableButton button = new ResizableDraggableButton(context, association.event);
                button.setOnClickListener(updateListener);
                button.setX(positionStart(association.x, association.radius));
                button.setY(positionStart(association.y, association.radius));
                actions.addView(button);
                button.setDimensions(association.radius * 2);
                button.setPadding(association.radius * 2);
            } else {
                DraggableButton button = new DraggableButton(context, association.action, association.event);
                button.setOnClickListener(updateListener);
                actions.addView(button);
                button.setX(positionStart(association.x, button.getLayoutParams().width/2));
                button.setY(positionStart(association.y, button.getLayoutParams().width/2));
            }
        }
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
                        resizableDraggableButton.setOnClickListener(updateListener);
                        actions.addView(resizableDraggableButton);
                    } else {
                        DraggableButton draggableButton = new DraggableButton(context, action, selectedEvent);
                        draggableButton.setOnClickListener(updateListener);
                        actions.addView(draggableButton);
                    }
                },
                view1 -> removeView(dialogLayout),
                availableEvents()
        );
    }

    public void save() {
        new Thread(() -> {
            associationsDb.deleteAssociations(applicationPackage);
            associations.clear();

            for (int i = 0; i < actions.getChildCount(); i++) {
                View view = actions.getChildAt(i);
                if (view instanceof DraggableButton) {
                    DraggableButton button = (DraggableButton) view;
                    int xCenter = center(button.getX(), button.getWidth());
                    int yCenter = center(button.getY(), button.getHeight());
                    associations.add(new Association(applicationPackage, button.getEvent(), button.getAction(), xCenter, yCenter, null));
                } else if (view instanceof ResizableDraggableButton) {
                    ResizableDraggableButton button = (ResizableDraggableButton) view;
                    int xCenter = center(button.getX(), button.getWidth());
                    int yCenter = center(button.getY(), button.getHeight());
                    associations.add(new Association(applicationPackage, button.getEvent(), Action.JOYSTICK, xCenter, yCenter, button.getWidth() / 2));
                }
            }
            associationsDb.insert(associations.toArray(new Association[0]));
        }).start();
    }

    private int center(float value, int size) {
        return (int)value + size / 2;
    }

    private float positionStart(int center, int radius) {
        return (float) (center - radius);
    }

    private List<Event> availableEvents() {
        ArrayList<Event> events =  new ArrayList<>(Arrays.asList(Event.values()));
        for (int i = 0; i < actions.getChildCount(); i++) {
            EventButton button = (EventButton) actions.getChildAt(i);
            events.remove(button.getEvent());
        }
        return events;
    }
}