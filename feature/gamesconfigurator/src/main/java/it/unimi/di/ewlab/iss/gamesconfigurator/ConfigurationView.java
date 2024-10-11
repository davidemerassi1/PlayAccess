package it.unimi.di.ewlab.iss.gamesconfigurator;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.lifecycle.MutableLiveData;

import it.unimi.di.ewlab.iss.gamesconfigurator.R;
import it.unimi.di.ewlab.iss.gamesconfigurator.buttons.EventButton;
import it.unimi.di.ewlab.iss.gamesconfigurator.buttons.ResizableSlidingDraggableButton;

import it.unimi.di.ewlab.iss.common.database.Event;
import it.unimi.di.ewlab.iss.common.database.Association;
import it.unimi.di.ewlab.iss.common.database.AssociationDao;

import it.unimi.di.ewlab.iss.gamesconfigurator.buttons.DraggableButton;
import it.unimi.di.ewlab.iss.gamesconfigurator.buttons.ResizableDraggableButton;
import it.unimi.di.ewlab.iss.gamesconfigurator.dialogs.EventDialog;
import it.unimi.di.ewlab.iss.gamesconfigurator.dialogs.SlidingEventDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class ConfigurationView extends RelativeLayout {
    private boolean isFABOpen = false;
    private ImageButton fab;
    private List<LinearLayout> fabLayouts = new ArrayList<>();
    private RelativeLayout events;
    private Context context;
    private String applicationPackage;
    private AssociationDao associationsDb;
    private SlidingEventDialog slidingEventDialog;
    private EventDialog eventDialog;
    private OnClickListener updateListener = v -> {
        EventButton eventButton = (EventButton) v;
        showDialog(eventButton, false);
    };
    private MutableLiveData<List<Action>> availableActions = new MutableLiveData<>();
    private MutableLiveData<Association[]> associations = new MutableLiveData<>();
    private boolean configurationChanged = false;

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
        findViewById(R.id.scollView).setVisibility(View.VISIBLE);
        if (MainModel.getInstance().getTutorialStep() != null && MainModel.getInstance().getTutorialStep().getValue() == 2)
            MainModel.getInstance().setNextTutorialStep();
    }

    private void closeFABMenu() {
        isFABOpen = false;
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_reverse);
        fab.startAnimation(rotateAnimation);
        findViewById(R.id.scollView).setVisibility(View.GONE);
    }

    public void setup() {
        fab = findViewById(R.id.fab);
        LinearLayout optionsLayout = findViewById(R.id.optionsLayout);
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
            showDialog(new DraggableButton(context, Event.LONG_TAP), true);
        });

        fabLayouts.get(2).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new DraggableButton(context, Event.TAP_ON_OFF), true);
        });

        fabLayouts.get(3).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new DraggableButton(context, Event.SWIPE_UP), true);
        });

        fabLayouts.get(4).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new ResizableSlidingDraggableButton(context, null, null, null, false), true);
        });

        fabLayouts.get(5).getChildAt(1).setOnClickListener(view -> {
            closeFABMenu();
            showDialog(new ResizableDraggableButton(context), true);
        });

        associationsDb = MainModel.getInstance().getAssociationsDb().getDao();
        associations.observeForever(ass -> {
            events.removeAllViews();
            for (Association association : ass) {
                if (association.event == Event.JOYSTICK) {
                    ResizableDraggableButton button = new ResizableDraggableButton(context);
                    button.setAction(association.action);
                    button.setOnClickListener(updateListener);
                    button.setX(positionStart(association.x, association.radius));
                    button.setY(positionStart(association.y, association.radius));
                    events.addView(button);
                    button.setDimensions(association.radius * 2);
                    button.setPadding(association.radius * 2);
                } else if (association.event == Event.MONODIMENSIONAL_SLIDING) {
                    ResizableSlidingDraggableButton button = new ResizableSlidingDraggableButton(context, association.action, association.additionalAction1, association.additionalAction2, association.resetToStart);
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
                    button.setX(positionStart(association.x, toPx(30)));
                    button.setY(positionStart(association.y, toPx(30)));
                }
            }
            refreshAlerts(availableActions.getValue());
        });

        availableActions.observeForever(this::refreshAlerts);
    }

    public void changeGame(String applicationPackage) {
        new Thread(() -> {
            if (configurationChanged) {
                save();
                configurationChanged = false;
            }
            this.applicationPackage = applicationPackage;
            Association[] a = associationsDb.getAssociations(applicationPackage);
            associations.postValue(a);
        }).start();

        requestActions();
    }

    private void showDialog(EventButton eventButton, boolean isNew) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (eventButton instanceof ResizableSlidingDraggableButton slidingEventButton) {
            slidingEventDialog = (SlidingEventDialog) inflater.inflate(R.layout.sliding_dialog_layout, this, false);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            addView(slidingEventDialog, layoutParams);
            slidingEventDialog.init(slidingEventButton,
                    view1 -> {
                        Action selectedAction = slidingEventDialog.getSelectedAction();
                        Action selectedAction2 = slidingEventDialog.getSelectedAction2();
                        Action selectedAction3 = slidingEventDialog.getSelectedAction3();
                        if (selectedAction == null || selectedAction2 == null || selectedAction3 == null) {
                            slidingEventDialog.showErrorMessage();
                            return;
                        }
                        if (selectedAction.equals(selectedAction2) || selectedAction.equals(selectedAction3) || selectedAction2.equals(selectedAction3)) {
                            slidingEventDialog.showSameActionErrorMessage();
                            return;
                        }
                        slidingEventButton.setAction(selectedAction);
                        slidingEventButton.setAction2(selectedAction2);
                        slidingEventButton.setAction3(selectedAction3);
                        slidingEventButton.setEvent(eventButton.getEvent());
                        slidingEventButton.setOnClickListener(updateListener);
                        slidingEventButton.setResetToStart(slidingEventDialog.getResetToStart());
                        removeView(slidingEventDialog);
                        slidingEventDialog = null;
                        if (isNew)
                            events.addView((View) eventButton);
                        requestActions();
                        if (MainModel.getInstance().getTutorialStep() != null && MainModel.getInstance().getTutorialStep().getValue() == 3)
                            MainModel.getInstance().setNextTutorialStep();
                    },
                    view1 -> {
                        removeView(slidingEventDialog);
                        slidingEventDialog = null;
                    },
                    isNew ? null :
                            view1 -> {
                                events.removeView((View) eventButton);
                                removeView(slidingEventDialog);
                                slidingEventDialog = null;
                            },
                    availableActions.getValue(),
                    this
            );
        } else {
            eventDialog = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            addView(eventDialog, layoutParams);
            eventDialog.init(eventButton,
                    view1 -> {
                        Action selectedAction = eventDialog.getSelectedAction();
                        if (selectedAction == null) {
                            eventDialog.showErrorMessage();
                            return;
                        }
                        //per ora non si può assegnare la stessa azione a più eventi
                        if (giaAssegnata(selectedAction, eventButton)) {
                            eventDialog.showSameActionErrorMessage();
                            return;
                        }
                        eventButton.setAction(selectedAction);
                        eventButton.setEvent(eventDialog.getEvent());
                        eventButton.setOnClickListener(updateListener);
                        removeView(eventDialog);
                        eventDialog = null;
                        if (isNew)
                            events.addView((View) eventButton);
                        requestActions();
                        if (MainModel.getInstance().getTutorialStep() != null && MainModel.getInstance().getTutorialStep().getValue() == 3)
                            MainModel.getInstance().setNextTutorialStep();
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
    }

    private void requestActions() {
        availableActions.setValue(MainModel.getInstance().getActions());
    }

    public void save() {
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

        Association[] associationsArray = list.toArray(new Association[0]);
        Log.d("ConfigurationView", "save: " + list.size());
        associationsDb.deleteAssociations(applicationPackage);
        associationsDb.insert(associationsArray);
        announceForAccessibility("RELOAD_ASSOCIATIONS");
    }

    private int center(float value, int size) {
        Log.d("ConfigurationView", "center: " + value + " " + size + " " + (int) value + size / 2);
        return (int) value + size / 2;
    }

    private float positionStart(int center, int radius) {
        Log.d("ConfigurationView", "positionStart: " + center + " " + radius);
        return (float) (center - radius);
    }

    private static int toPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void refreshAlerts(List<Action> availableActions) {
        Log.d("ConfigurationView", "refreshAlerts: " + availableActions.size());

        int eventsNumber = events.getChildCount();
        for (int i = 0; i < eventsNumber; i++) {
            EventButton eventButton = (EventButton) events.getChildAt(i);
            Log.d("ConfigurationView", "refreshAlerts: " + eventButton.getAction().getName() + " " + availableActions.contains(eventButton.getAction()));
            eventButton.hideAlert();
            if (!eventButton.getAction().getActionType().equals(Action.ActionType.OTHER_MODULE) && !availableActions.contains(eventButton.getAction())) {
                eventButton.showAlert();
            }
            if (eventButton instanceof ResizableSlidingDraggableButton slidingEventButton) {
                if (!availableActions.contains(slidingEventButton.getAction2()) || !availableActions.contains(slidingEventButton.getAction3())) {
                    slidingEventButton.showAlert();
                }
            }
        }
    }

    public void open() {
        announceForAccessibility("CONFIGURATION_OPENED");
        configurationChanged = true;
    }

    public boolean giaAssegnata (Action action, EventButton button) {
        for (int i = 0; i < events.getChildCount(); i++) {
            EventButton e = (EventButton) events.getChildAt(i);
            if (e != button) {
                if (e.getAction().equals(action)) {
                    return true;
                }
                if (e instanceof ResizableSlidingDraggableButton r) {
                    if (r.getAction2().equals(action) || r.getAction3().equals(action)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}