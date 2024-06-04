package actionsConfigurator.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.eventsexecutor.R;

import java.util.ArrayList;
import java.util.List;

import actionsConfigurator.OverlayManager;
import actionsConfigurator.OverlayService;
import actionsConfigurator.OverlayView;
import it.unimi.di.ewlab.iss.common.database.Event;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class EventDialog extends FrameLayout {
    private Action selectedAction = null;
    private Action selectedAction2 = null;
    private Action selectedAction3 = null;
    private Event event;
    private RadioGroup radioGroup;
    private EventDialog secondaryEventDialog;
    private TextView faceOptionText;
    private TextView controllerOptionText;
    private MutableLiveData<List<Action>> availableActions = new MutableLiveData<>();


    public EventDialog(Context context) {
        super(context);
    }

    public EventDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(EventButton eventButton, OnClickListener okListener, OnClickListener cancelListener, OnClickListener deleteListener) {
        setElevation(30);
        radioGroup = findViewById(R.id.radioGroup);
        event = eventButton.getEvent();

        if (event == Event.SWIPE_UP || event == Event.SWIPE_DOWN || event == Event.SWIPE_LEFT || event == Event.SWIPE_RIGHT) {
            findViewById(R.id.selectSwipeDirectionLayout).setVisibility(VISIBLE);
            Spinner spinner = findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.swipe_directions, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(event.ordinal() - 1);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            event = Event.SWIPE_UP;
                            break;
                        case 1:
                            event = Event.SWIPE_DOWN;
                            break;
                        case 2:
                            event = Event.SWIPE_LEFT;
                            break;
                        case 3:
                            event = Event.SWIPE_RIGHT;
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        if (event == Event.MONODIMENSIONAL_SLIDING) {
            findViewById(R.id.mainActionLayout).setVisibility(GONE);
            findViewById(R.id.additional_actions_layout).setVisibility(VISIBLE);
            TextView actionLeftTextView = findViewById(R.id.actionLeftTextView);
            TextView actionRightTextView = findViewById(R.id.actionRightTextView);
            TextView actionResetTextView = findViewById(R.id.actionResetTextView);

            actionLeftTextView.setOnClickListener(v -> {
                Log.d("EventDialog", "Left action selected");
                LayoutInflater inflater = LayoutInflater.from(getContext());
                secondaryEventDialog = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                addView(secondaryEventDialog, layoutParams);
                secondaryEventDialog.initSecondaryAction(
                        v1 -> {
                            Action a = secondaryEventDialog.getSelectedAction();
                            if (a == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            if ((selectedAction2 != null && selectedAction2 == a) || (selectedAction3 != null && selectedAction3 == a)) {
                                secondaryEventDialog.showSameKeyErrorMessage();
                                return;
                            }
                            selectedAction = a;
                            ((TextView) findViewById(R.id.actionLeftTextView)).setText(selectedAction.getName());
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        v1 -> {
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        availableActions.getValue());
            });

            actionRightTextView.setOnClickListener(v -> {
                Log.d("EventDialog", "Right action selected");
                LayoutInflater inflater = LayoutInflater.from(getContext());
                secondaryEventDialog = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                addView(secondaryEventDialog, layoutParams);
                secondaryEventDialog.initSecondaryAction(
                        v1 -> {
                            if (secondaryEventDialog.getSelectedAction() == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            Action a = secondaryEventDialog.getSelectedAction();
                            if (a == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            if ((selectedAction != null && selectedAction == a) || (selectedAction3 != null && selectedAction3 == a)) {
                                secondaryEventDialog.showSameKeyErrorMessage();
                                return;
                            }
                            selectedAction2 = a;
                            ((TextView) findViewById(R.id.actionRightTextView)).setText(selectedAction2.getName());
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        v1 -> {
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        availableActions.getValue());
            });

            actionResetTextView.setOnClickListener(v -> {
                Log.d("EventDialog", "Reset action selected");
                LayoutInflater inflater = LayoutInflater.from(getContext());
                secondaryEventDialog = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                addView(secondaryEventDialog, layoutParams);
                secondaryEventDialog.initSecondaryAction(
                        v1 -> {
                            if (secondaryEventDialog.getSelectedAction() == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            Action a = secondaryEventDialog.getSelectedAction();
                            if (a == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            if ((selectedAction2 != null && selectedAction2 == a) || (selectedAction != null && selectedAction == a)) {
                                secondaryEventDialog.showSameKeyErrorMessage();
                                return;
                            }
                            selectedAction3 = a;
                            ((TextView) findViewById(R.id.actionResetTextView)).setText(selectedAction3.getName());
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        v1 -> {
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        availableActions.getValue());
            });
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                hideErrorMessages();
                selectedAction = (Action) findViewById(checkedId).getTag();
            }
        });

        faceOptionText = findViewById(R.id.faceOptionTextView);
        controllerOptionText = findViewById(R.id.externalButtonOptionTextView);
        faceOptionText.setOnClickListener(selectListener);
        controllerOptionText.setOnClickListener(selectListener);

        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);

        if (deleteListener != null) {
            findViewById(R.id.deleteButtonLayout).setVisibility(VISIBLE);
            findViewById(R.id.deleteButton).setOnClickListener(deleteListener);
            selectedAction = eventButton.getAction();
            if (event == Event.MONODIMENSIONAL_SLIDING) {
                ResizableSlidingDraggableButton b = (ResizableSlidingDraggableButton) eventButton;
                selectedAction2 = b.getAction2();
                selectedAction3 = b.getAction3();
                if (b.getResetToStart())
                    ((CheckBox) findViewById(R.id.resetToStartCheckBox)).setChecked(true);
                ((TextView) findViewById(R.id.actionLeftTextView)).setText(selectedAction.getName());
                ((TextView) findViewById(R.id.actionRightTextView)).setText(selectedAction2.getName());
                ((TextView) findViewById(R.id.actionResetTextView)).setText(selectedAction3.getName());
            /*} else if (selectedAction.getActionType() < 0) {
                RadioButton currentChoice = new RadioButton(getContext());
                CameraAction action = CameraAction.valueOf(selectedAction);
                currentChoice.setText(action.getName());
                currentChoice.setTag(action.getTag());
                radioGroup.addView(currentChoice);
                radioGroup.check(currentChoice.getId());
            } else {
                ((TextView) findViewById(R.id.controllerTextView)).setText(selectedAction.getName());
                controllerOptionText.performClick();*/
            }
        }

        availableActions.observeForever(actions -> {
            if (actions != null) {
                findViewById(R.id.progressBar).setVisibility(GONE);
                findViewById(R.id.availableActionsLayout).setVisibility(VISIBLE);
                controllerOptionText.performClick();
            }
        });
        requestAvailableActions();
    }

    private void requestAvailableActions() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> {
            if (!availableActions.isInitialized()) {
                findViewById(R.id.progressBar).setVisibility(GONE);
                findViewById(R.id.errorRetrievingActions).setVisibility(VISIBLE);
            }
        };
        handler.postDelayed(runnable, 10000);

        OverlayService.getInstance().requestActions(availableActions);

        /*availableActions = new ArrayList<>(mainModel.getActions());
        availableActions.add(OverlayView.FACE_MOVEMENT_ACTION);*/
    }

    private void initSecondaryAction(OnClickListener okListener, OnClickListener cancelListener, List<Action> availableActions) {
        setElevation(30);
        this.availableActions.setValue(availableActions);

        findViewById(R.id.progressBar).setVisibility(GONE);
        findViewById(R.id.availableActionsLayout).setVisibility(VISIBLE);

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                hideErrorMessages();
                selectedAction = (Action) findViewById(checkedId).getTag();
            }
        });

        faceOptionText = findViewById(R.id.faceOptionTextView);
        controllerOptionText = findViewById(R.id.externalButtonOptionTextView);
        faceOptionText.setOnClickListener(selectListener);
        controllerOptionText.setOnClickListener(selectListener);

        controllerOptionText.performClick();

        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);
    }

    private OnClickListener selectListener = v -> {
        faceOptionText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        faceOptionText.setTypeface(Typeface.DEFAULT);
        controllerOptionText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        controllerOptionText.setTypeface(Typeface.DEFAULT);
        ((TextView) v).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
        ((TextView) v).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        while (radioGroup.getChildCount() > 0) {
            radioGroup.removeView(radioGroup.getChildAt(0));
        }

        Action.ActionType actionType = Action.ActionType.valueOf((String) v.getTag());
        findViewById(R.id.noEventsTextview).setVisibility(GONE);
        for (Action option : availableActions.getValue()) {
            if ((event == Event.JOYSTICK) != option.is2d())
                continue;
            if (option.getActionType() != actionType)
                continue;
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setTag(option);
            radioButton.setText(option.getName());
            radioButton.setTextColor(Color.BLACK);
            if (selectedAction != null && selectedAction.equals(option))
                radioButton.setChecked(true);
            radioGroup.addView(radioButton);
        }
        if (radioGroup.getChildCount() == 0)
            findViewById(R.id.noEventsTextview).setVisibility(VISIBLE);
    };

    public void showErrorMessage() {
        findViewById(R.id.errorMessage).setVisibility(VISIBLE);
    }

    public void hideErrorMessages() {
        findViewById(R.id.errorMessage).setVisibility(GONE);
        findViewById(R.id.sameEventErrorMessage).setVisibility(GONE);
    }

    public void showSameKeyErrorMessage() {
        findViewById(R.id.sameEventErrorMessage).setVisibility(VISIBLE);
    }

    public Event getEvent() {
        return event;
    }

    public Action getSelectedAction() {
        return selectedAction;
    }

    public Action getSelectedAction2() {
        return selectedAction2;
    }

    public Action getSelectedAction3() {
        return selectedAction3;
    }

    public boolean getResetToStart() {
        CheckBox checkBox = findViewById(R.id.resetToStartCheckBox);
        return checkBox.isChecked();
    }
}
