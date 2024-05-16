package com.example.sandboxtest.actionsConfigurator;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.sandboxtest.R;
import com.example.sandboxtest.actionsConfigurator.utils.EventButton;
import com.example.sandboxtest.actionsConfigurator.utils.EventDialog;
import com.example.sandboxtest.actionsConfigurator.utils.ResizableSlidingDraggableButton;
import com.example.sandboxtest.database.Event;
import com.example.sandboxtest.database.Association;
import com.example.sandboxtest.database.AssociationDao;
import com.example.sandboxtest.actionsConfigurator.utils.DraggableButton;
import com.example.sandboxtest.actionsConfigurator.utils.ResizableDraggableButton;
import com.example.sandboxtest.database.CameraAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;
import it.unimi.di.ewlab.iss.common.storage.JsonManager;

public class ConfigurationView extends RelativeLayout {
    private boolean isFABOpen = false;
    private ImageButton fab;
    private RelativeLayout optionsLayout;
    private List<LinearLayout> fabLayouts = new ArrayList<>();
    private RelativeLayout events;
    private Context context;
    private String applicationPackage;
    private AssociationDao associationsDb;
    private EventDialog eventDialog;
    private OnClickListener updateListener = v -> {
        EventButton eventButton = (EventButton) v;
        showDialog(eventButton, false);
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

        events = findViewById(R.id.events);

        fab.setOnClickListener(view -> {
            if (!isFABOpen)
                showFABMenu();
            else
                closeFABMenu();
        });

        fabLayouts.get(0).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new DraggableButton(context, Event.TAP), true);
        });

        fabLayouts.get(1).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new ResizableDraggableButton(context, (Action) null), true);
        });

        fabLayouts.get(2).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new DraggableButton(context, Event.SWIPE_UP), true);
        });

        fabLayouts.get(3).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new DraggableButton(context, Event.LONG_TAP), true);
        });

        fabLayouts.get(4).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new ResizableSlidingDraggableButton(context, null, null, null), true);
        });

        for (Association association : associationsDb.getAssociations(applicationPackage)) {
            if (association.event == Event.JOYSTICK) {
                ResizableDraggableButton button = new ResizableDraggableButton(context, association.action);
                button.setOnClickListener(updateListener);
                button.setX(positionStart(association.x, association.radius));
                button.setY(positionStart(association.y, association.radius));
                events.addView(button);
                button.setDimensions(association.radius * 2);
                button.setPadding(association.radius * 2);
            } else if (association.event == Event.MONODIMENSIONAL_SLIDING) {
                ResizableSlidingDraggableButton button = new ResizableSlidingDraggableButton(context, association.action, association.additionalAction1, association.additionalAction2);
                events.addView(button);
                button.setOnClickListener(updateListener);
                button.setX(positionStart(association.x, association.radius));
                button.setY(positionStart(association.y, toPx(30)));
                button.setDimensions(association.radius * 2);
            } else {
                DraggableButton button = new DraggableButton(context, association.event);
                button.setAction(association.action);
                button.setOnClickListener(updateListener);
                events.addView(button);
                button.setX(positionStart(association.x, button.getLayoutParams().width / 2));
                button.setY(positionStart(association.y, button.getLayoutParams().width / 2));
            }
        }
    }

    private void showDialog(EventButton eventButton, boolean isNew) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        eventDialog = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(eventDialog, layoutParams);
        eventDialog.init(eventButton,
                view1 -> {
                    Action selectedAction = eventDialog.getSelectedAction();
                    if (selectedAction == null || (eventButton.getEvent() == Event.MONODIMENSIONAL_SLIDING && (eventDialog.getSelectedAction2() == null || eventDialog.getSelectedAction3() == null)) ) {
                        eventDialog.showErrorMessage();
                        return;
                    }
                    //se si toglie si può spostare tutta la logica in eventdialog
                    for (int i = 0; i < events.getChildCount(); i++) {
                        EventButton button = (EventButton) events.getChildAt(i);
                        if (eventButton != button && button.getAction() == selectedAction) {
                            eventDialog.showSameKeyErrorMessage();
                            return;
                        }
                    }
                    eventButton.setAction(selectedAction);
                    if (eventButton instanceof ResizableSlidingDraggableButton) {
                        ResizableSlidingDraggableButton resizableSlidingDraggableButton = (ResizableSlidingDraggableButton) eventButton;
                        resizableSlidingDraggableButton.setAction2(eventDialog.getSelectedAction2());
                        resizableSlidingDraggableButton.setAction3(eventDialog.getSelectedAction3());
                        resizableSlidingDraggableButton.setResetToStart(eventDialog.getResetToStart());
                    }
                    eventButton.setEvent(eventDialog.getEvent());
                    eventButton.setOnClickListener(updateListener);
                    removeView(eventDialog);
                    eventDialog = null;
                    if (isNew)
                        events.addView((View) eventButton);
                },
                view1 -> {
                    removeView(eventDialog);
                    eventDialog = null;
                },
                isNew ? null :
                view1 -> {
                    events.removeView((View) eventButton);
                    removeView(eventDialog);
                    eventDialog = null;
                }
        );
    }

    public List<Association> save() {
        List<Association> list = new LinkedList<>();

        for (int i = 0; i < events.getChildCount(); i++) {
            View view = events.getChildAt(i);
            if (view instanceof DraggableButton) {
                DraggableButton button = (DraggableButton) view;
                int xCenter = center(button.getX(), button.getWidth());
                int yCenter = center(button.getY(), button.getHeight());
                list.add(new Association(applicationPackage, button.getAction(), button.getEvent(), xCenter, yCenter, null, null, null, null));
            } else if (view instanceof ResizableDraggableButton) {
                ResizableDraggableButton button = (ResizableDraggableButton) view;
                int xCenter = center(button.getX(), button.getWidth());
                int yCenter = center(button.getY(), button.getHeight());
                list.add(new Association(applicationPackage, button.getAction(), Event.JOYSTICK, xCenter, yCenter, button.getWidth() / 2, null, null, null));
            } else if (view instanceof ResizableSlidingDraggableButton) {
                ResizableSlidingDraggableButton button = (ResizableSlidingDraggableButton) view;
                int xCenter = center(button.getX(), button.getWidth());
                int yCenter = center(button.getY(), toPx(60));
                list.add(new Association(applicationPackage, button.getAction(), Event.MONODIMENSIONAL_SLIDING, xCenter, yCenter, button.getWidth() / 2, button.getAction2(), button.getAction3(), button.getResetToStart()));
            }
        }

        new Thread(() -> {
            associationsDb.deleteAssociations(applicationPackage);
            associationsDb.insert(list.toArray(new Association[0]));
        }).start();

        return list;
    }

    private int center(float value, int size) {
        return (int) value + size / 2;
    }

    private float positionStart(int center, int radius) {
        return (float) (center - radius);
    }

    /*private List<CameraAction> availableActions() {
        Log.d("ConfigurationView", MainModel.getInstance().getActions().get(0).getName());
        ArrayList<CameraAction> actions = new ArrayList<>(Arrays.asList(CameraAction.values()));
        for (int i = 0; i < events.getChildCount(); i++) {
            EventButton button = (EventButton) events.getChildAt(i);
            if (button.getAction() >= 0)
                continue;
            CameraAction action = CameraAction.valueOf(button.getAction());
            actions.remove(action);
        }
        return actions;
    }*/

    private static int toPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (eventDialog != null)
            eventDialog.onKeyUp(keyCode, event);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (eventDialog != null)
            eventDialog.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (eventDialog != null)
            eventDialog.onGenericMotionEvent(event);
        return true;
    }
}