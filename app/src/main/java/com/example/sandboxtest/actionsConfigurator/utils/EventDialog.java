package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.CameraAction;
import com.example.sandboxtest.database.Event;

import java.util.List;

public class EventDialog extends FrameLayout {
    private boolean isControllerSelected = false;
    private Integer selectedAction = null;
    private Event event;
    private RadioGroup radioGroup;

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
            if (selectedAction < 0) {
                RadioButton currentChoice = new RadioButton(getContext());
                CameraAction action = CameraAction.valueOf(selectedAction);
                currentChoice.setText(action.getName());
                currentChoice.setTag(action.getTag());
                radioGroup.addView(currentChoice);
                radioGroup.check(currentChoice.getId());
            } else {
                ((TextView) findViewById(R.id.controllerTextView)).setText(KeyEvent.keyCodeToString(selectedAction));
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
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
        if (isControllerSelected) {
            radioGroup.clearCheck();
            selectedAction = 0;
            ((TextView) findViewById(R.id.controllerTextView)).setText("JOYSTICK");
            hideErrorMessages();
        }
        return false;
    }
}
