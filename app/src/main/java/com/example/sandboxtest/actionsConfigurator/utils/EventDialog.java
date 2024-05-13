package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.CameraAction;
import com.example.sandboxtest.database.Event;

import java.util.ArrayList;
import java.util.List;

public class EventDialog extends FrameLayout {
    private boolean isControllerSelected = false;
    private Integer selectedAction = null;
    private Integer selectedAction2 = null;
    private Integer selectedAction3 = null;
    private Event event;
    private RadioGroup radioGroup;
    private EventDialog secondaryEventDialog;

    public EventDialog(Context context) {
        super(context);
    }

    public EventDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(EventButton eventButton, OnClickListener okListener, OnClickListener cancelListener, OnClickListener deleteListener, List<CameraAction> availableActions) {
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
                secondaryEventDialog.initSecondaryAction(availableActions,
                        v1 -> {
                            Integer a = secondaryEventDialog.getSelectedAction();
                            if (a == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            if ((selectedAction2 != null && selectedAction2 == a) || (selectedAction3 != null && selectedAction3 == a)) {
                                secondaryEventDialog.showSameKeyErrorMessage();
                                return;
                            }
                            selectedAction = a;
                            ((TextView) findViewById(R.id.actionLeftTextView)).setText(getActionName(selectedAction));
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        v1 -> {
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        });
            });

            actionRightTextView.setOnClickListener(v -> {
                Log.d("EventDialog", "Right action selected");
                LayoutInflater inflater = LayoutInflater.from(getContext());
                secondaryEventDialog = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                addView(secondaryEventDialog, layoutParams);
                secondaryEventDialog.initSecondaryAction(availableActions,
                        v1 -> {
                            if (secondaryEventDialog.getSelectedAction() == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            Integer a = secondaryEventDialog.getSelectedAction();
                            if (a == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            if ((selectedAction != null && selectedAction == a) || (selectedAction3 != null && selectedAction3 == a)) {
                                secondaryEventDialog.showSameKeyErrorMessage();
                                return;
                            }
                            selectedAction2 = a;
                            ((TextView) findViewById(R.id.actionRightTextView)).setText(getActionName(selectedAction2));
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        v1 -> {
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        });
            });

            actionResetTextView.setOnClickListener(v -> {
                Log.d("EventDialog", "Reset action selected");
                LayoutInflater inflater = LayoutInflater.from(getContext());
                secondaryEventDialog = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                addView(secondaryEventDialog, layoutParams);
                secondaryEventDialog.initSecondaryAction(availableActions,
                        v1 -> {
                            if (secondaryEventDialog.getSelectedAction() == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            Integer a = secondaryEventDialog.getSelectedAction();
                            if (a == null) {
                                secondaryEventDialog.showErrorMessage();
                                return;
                            }
                            if ((selectedAction2 != null && selectedAction2 == a) || (selectedAction != null && selectedAction == a)) {
                                secondaryEventDialog.showSameKeyErrorMessage();
                                return;
                            }
                            selectedAction3 = a;
                            ((TextView) findViewById(R.id.actionResetTextView)).setText(getActionName(selectedAction3));
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        },
                        v1 -> {
                            removeView(secondaryEventDialog);
                            secondaryEventDialog = null;
                        });
            });
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                ((TextView) findViewById(R.id.controllerTextView)).setText("Premi il tasto che vuoi associare a questo evento");
                hideErrorMessages();
                selectedAction = (Integer) findViewById(checkedId).getTag();
            }
        });

        TextView faceOptionText = findViewById(R.id.option1TextView);
        TextView controllerOptionText = findViewById(R.id.option2TextView);

        faceOptionText.setOnClickListener(v -> {
            faceOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
            faceOptionText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            controllerOptionText.setTextColor(ContextCompat.getColor(getContext(),android.R.color.darker_gray));
            controllerOptionText.setTypeface(Typeface.DEFAULT);
            findViewById(R.id.faceControlLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.controllerLayout).setVisibility(View.GONE);
            isControllerSelected = false;
        });

        controllerOptionText.setOnClickListener(v -> {
            controllerOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
            controllerOptionText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            faceOptionText.setTextColor(ContextCompat.getColor(getContext(),android.R.color.darker_gray));
            faceOptionText.setTypeface(Typeface.DEFAULT);
            findViewById(R.id.controllerLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.faceControlLayout).setVisibility(View.GONE);
            isControllerSelected = true;
        });

        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);

        if (deleteListener != null) {
            findViewById(R.id.deleteButtonLayout).setVisibility(VISIBLE);
            findViewById(R.id.deleteButton).setOnClickListener(deleteListener);
            selectedAction = eventButton.getAction();
            if (event == Event.MONODIMENSIONAL_SLIDING) {
                selectedAction2 = ((ResizableSlidingDraggableButton) eventButton).getAction2();
                selectedAction3 = ((ResizableSlidingDraggableButton) eventButton).getAction3();
                ((TextView) findViewById(R.id.actionLeftTextView)).setText(getActionName(selectedAction));
                ((TextView) findViewById(R.id.actionRightTextView)).setText(getActionName(selectedAction2));
                ((TextView) findViewById(R.id.actionResetTextView)).setText(getActionName(selectedAction3));
            } else if (selectedAction < 0) {
                RadioButton currentChoice = new RadioButton(getContext());
                CameraAction action = CameraAction.valueOf(selectedAction);
                currentChoice.setText(action.getName());
                currentChoice.setTag(action.getTag());
                radioGroup.addView(currentChoice);
                radioGroup.check(currentChoice.getId());
            } else {
                ((TextView) findViewById(R.id.controllerTextView)).setText(getActionName(selectedAction));
                controllerOptionText.performClick();
            }
        }

        for (CameraAction option : availableActions) {
            if ((eventButton instanceof ResizableDraggableButton) != option.isJoystickAction())
                continue;
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setTag(option.getTag());
            radioButton.setText(option.getName());
            radioGroup.addView(radioButton);
        }
        if (radioGroup.getChildCount() == 0)
            findViewById(R.id.noEventsTextview).setVisibility(VISIBLE);
    }

    private String getActionName(Integer action) {
        if (action < 0)
            return CameraAction.valueOf(action).getName();
        else if (action == 0)
            return "JOYSTICK";
        else
            return KeyEvent.keyCodeToString(action);
    }

    private void initSecondaryAction(List<CameraAction> availableActions, OnClickListener okListener, OnClickListener cancelListener) {
        setElevation(30);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                ((TextView) findViewById(R.id.controllerTextView)).setText("Premi il tasto che vuoi associare a questo evento");
                hideErrorMessages();
                selectedAction = (Integer) findViewById(checkedId).getTag();
            }
        });

        TextView faceOptionText = findViewById(R.id.option1TextView);
        TextView controllerOptionText = findViewById(R.id.option2TextView);

        faceOptionText.setOnClickListener(v -> {
            faceOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
            faceOptionText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            controllerOptionText.setTextColor(ContextCompat.getColor(getContext(),android.R.color.darker_gray));
            controllerOptionText.setTypeface(Typeface.DEFAULT);
            findViewById(R.id.faceControlLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.controllerLayout).setVisibility(View.GONE);
            isControllerSelected = false;
        });

        controllerOptionText.setOnClickListener(v -> {
            controllerOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
            controllerOptionText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            faceOptionText.setTextColor(ContextCompat.getColor(getContext(),android.R.color.darker_gray));
            faceOptionText.setTypeface(Typeface.DEFAULT);
            findViewById(R.id.controllerLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.faceControlLayout).setVisibility(View.GONE);
            isControllerSelected = true;
        });

        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);

        for (CameraAction option : availableActions) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setTag(option.getTag());
            radioButton.setText(option.getName());
            radioGroup.addView(radioButton);
        }
        if (radioGroup.getChildCount() == 0)
            findViewById(R.id.noEventsTextview).setVisibility(VISIBLE);
    }

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

    public Integer getSelectedAction() {
        return selectedAction;
    }

    public Integer getSelectedAction2() {
        return selectedAction2;
    }

    public Integer getSelectedAction3() {
        return selectedAction3;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (secondaryEventDialog != null)
            return secondaryEventDialog.onKeyUp(keyCode, event);
        if (isControllerSelected) {
            radioGroup.clearCheck();
            selectedAction = keyCode;
            ((TextView) findViewById(R.id.controllerTextView)).setText(KeyEvent.keyCodeToString(keyCode));
            hideErrorMessages();
        }
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (secondaryEventDialog != null)
            return secondaryEventDialog.onGenericMotionEvent(event);
        if (isControllerSelected) {
            radioGroup.clearCheck();
            selectedAction = 0;
            ((TextView) findViewById(R.id.controllerTextView)).setText("JOYSTICK");
            hideErrorMessages();
        }
        return false;
    }
}
