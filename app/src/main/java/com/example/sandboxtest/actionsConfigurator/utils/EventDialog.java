package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.CameraAction;

import java.util.List;

public class EventDialog extends FrameLayout {
    private boolean isControllerSelected = false;
    private Integer selectedAction = null;
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

    public void init(boolean joystick, OnClickListener okListener, OnClickListener cancelListener, List<CameraAction> availableActions) {
        setElevation(30);
        radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                ((TextView) findViewById(R.id.controllerTextView)).setText("Premi il tasto che vuoi associare a questo evento");
                hideErrorMessages();
                selectedAction = (Integer) findViewById(checkedId).getTag();
                Log.d("EventDialog", "Selected action: " + selectedAction);
            }
        });

        for (CameraAction option : availableActions) {
            if (joystick != option.isJoystickAction())
                continue;
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setTag(option.getTag());
            radioButton.setText(option.getName());
            radioGroup.addView(radioButton);
        }
        if (radioGroup.getChildCount() == 0)
            findViewById(R.id.noEventsTextview).setVisibility(VISIBLE);

        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);

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
    }

    public Integer getSelectedAction() {
        return selectedAction;
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
