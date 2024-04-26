package com.example.sandboxtest.actionsConfigurator;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sandboxtest.MyApplication;
import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Association;
import com.example.sandboxtest.database.AssociationDao;
import com.example.sandboxtest.database.AssociationsDb;
import com.example.sandboxtest.utils.DraggableButton;
import com.example.sandboxtest.utils.ResizableDraggableButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationView extends RelativeLayout {
    private boolean isFABOpen = false;
    private FloatingActionButton fab;
    private RelativeLayout optionsLayout;
    private List<LinearLayout> fabLayouts = new ArrayList<>();
    private RelativeLayout actions;
    private Context context;
    private String applicationPackage;
    private AssociationDao associationsDb;
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
        int dp = -55;
        for (LinearLayout layout : fabLayouts) {
            layout.animate().translationY(toPx(dp));
            dp -= 50;
            layout.getChildAt(0).setVisibility(VISIBLE);
        }
    }

    private void closeFABMenu() {
        isFABOpen = false;
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_reverse);
        fab.startAnimation(rotateAnimation);
        for (LinearLayout layout : fabLayouts) {
            layout.animate().translationY(0);
            layout.getChildAt(0).setVisibility(GONE);
        }
    }

    public void setup(String applicationPackage, AssociationDao associationsDb) {
        this.applicationPackage = applicationPackage;
        this.associationsDb = associationsDb;
        fab = findViewById(R.id.fab);
        optionsLayout = findViewById(R.id.optionsLayout);
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) optionsLayout.getChildAt(i);
            fabLayouts.add(layout);
        }

        actions = findViewById(R.id.actions);

        fab.setOnClickListener(view -> {
            if (!isFABOpen)
                showFABMenu();
            else
                closeFABMenu();
        });

        fabLayouts.get(0).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(Action.TAP, false);
        });

        fabLayouts.get(1).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(Action.JOYSTICK, true);
        });

        fabLayouts.get(2).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDirectionDialog();
        });

        fabLayouts.get(3).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(Action.LONG_TAP, false);
        });

        for (Association association : associationsDb.getAssociations(applicationPackage)) {
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
                button.setX(positionStart(association.x, button.getLayoutParams().width / 2));
                button.setY(positionStart(association.y, button.getLayoutParams().width / 2));
            }
        }
    }

    private void showDirectionDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        SwipeDirectionDialog dialogLayout = (SwipeDirectionDialog) inflater.inflate(R.layout.dialog_swipe_direction_layout, this, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams.setMargins(50, 0, 50, 0);
        addView(dialogLayout, layoutParams);
        dialogLayout.init((adapterView, view, i, l) -> {
            Action selectedAction = (Action) adapterView.getItemAtPosition(i);
            removeView(dialogLayout);
            showDialog(selectedAction, false);
        });
    }

    private void showDialog(Action action, boolean joystick) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        EventDialog dialogLayout = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams.setMargins(50, 0, 50, 0);
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

    public Map<Event, Association> save() {
        Map<Event, Association> map = new HashMap<>();

        for (int i = 0; i < actions.getChildCount(); i++) {
            View view = actions.getChildAt(i);
            if (view instanceof DraggableButton) {
                DraggableButton button = (DraggableButton) view;
                int xCenter = center(button.getX(), button.getWidth());
                int yCenter = center(button.getY(), button.getHeight());
                map.put(button.getEvent(), new Association(applicationPackage, button.getEvent(), button.getAction(), xCenter, yCenter, null));
            } else if (view instanceof ResizableDraggableButton) {
                ResizableDraggableButton button = (ResizableDraggableButton) view;
                int xCenter = center(button.getX(), button.getWidth());
                int yCenter = center(button.getY(), button.getHeight());
                map.put(button.getEvent(), new Association(applicationPackage, button.getEvent(), Action.JOYSTICK, xCenter, yCenter, button.getWidth() / 2));
            }
        }

        new Thread(() -> {
            associationsDb.deleteAssociations(applicationPackage);
            associationsDb.insert(map.values().toArray(new Association[0]));
        }).start();

        return map;
    }

    private int center(float value, int size) {
        return (int) value + size / 2;
    }

    private float positionStart(int center, int radius) {
        return (float) (center - radius);
    }

    private List<Event> availableEvents() {
        ArrayList<Event> events = new ArrayList<>(Arrays.asList(Event.values()));
        for (int i = 0; i < actions.getChildCount(); i++) {
            EventButton button = (EventButton) actions.getChildAt(i);
            events.remove(button.getEvent());
        }
        return events;
    }

    private static int toPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}