package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.CameraAction;

import java.util.List;

public class EditEventDialog extends FrameLayout {
    private boolean isControllerSelected = false;
    private Integer selectedAction;
    private RadioGroup radioGroup;

    public EditEventDialog(Context context) {
        super(context);
    }

    public EditEventDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditEventDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(int currentAction, OnClickListener okListener, OnClickListener deleteListener, OnClickListener cancelListener, boolean joystick, List<CameraAction> availableActions) {
        setElevation(30);

        selectedAction = currentAction;
        radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                ((TextView) findViewById(R.id.controllerTextView)).setText("Premi il tasto che vuoi associare a questo evento");
                selectedAction = (Integer) findViewById(checkedId).getTag();
                hideErrorMessage();
            }
        });

        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);
        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.deleteButton).setOnClickListener(deleteListener);

        TextView faceOptionText = findViewById(R.id.option1TextView);
        TextView controllerOptionText = findViewById(R.id.option2TextView);

        faceOptionText.setOnClickListener(v -> {
            faceOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
            faceOptionText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            controllerOptionText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
            controllerOptionText.setTypeface(Typeface.DEFAULT);
            findViewById(R.id.faceControlLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.controllerLayout).setVisibility(View.GONE);
            isControllerSelected = false;
        });

        controllerOptionText.setOnClickListener(v -> {
            controllerOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
            controllerOptionText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            faceOptionText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
            faceOptionText.setTypeface(Typeface.DEFAULT);
            findViewById(R.id.controllerLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.faceControlLayout).setVisibility(View.GONE);
            isControllerSelected = true;
        });

        if (currentAction < 0) {
            RadioButton currentChoice = new RadioButton(getContext());
            CameraAction action = CameraAction.valueOf(currentAction);
            currentChoice.setText(action.getName());
            currentChoice.setTag(action.getTag());
            radioGroup.addView(currentChoice);
            radioGroup.check(currentChoice.getId());
        } else {
            ((TextView) findViewById(R.id.controllerTextView)).setText(KeyEvent.keyCodeToString(currentAction));
            controllerOptionText.performClick();
        }

        for (CameraAction option : availableActions) {
            if (joystick != option.isJoystickAction())
                continue;
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setTag(option.getTag());
            radioButton.setText(option.getName());
            radioGroup.addView(radioButton);
        }
    }

    public Integer getSelectedAction() {
        return selectedAction;
    }

    public void showSameKeyErrorMessage() {
        findViewById(R.id.errorMessage).setVisibility(VISIBLE);
    }

    private void hideErrorMessage() {
        findViewById(R.id.errorMessage).setVisibility(GONE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isControllerSelected) {
            radioGroup.clearCheck();
            selectedAction = keyCode;
            ((TextView) findViewById(R.id.controllerTextView)).setText(KeyEvent.keyCodeToString(keyCode));
            hideErrorMessage();
        }
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (isControllerSelected) {
            radioGroup.clearCheck();
            selectedAction = 0;
            ((TextView) findViewById(R.id.controllerTextView)).setText("JOYSTICK");
            hideErrorMessage();
        }
        return false;
    }
}
