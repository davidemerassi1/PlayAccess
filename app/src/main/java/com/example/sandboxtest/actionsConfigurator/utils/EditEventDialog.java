package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.CameraEvent;

import java.util.List;

public class EditEventDialog extends LinearLayout {
    private boolean isControllerSelected = false;
    private int pressedButton;

    public EditEventDialog(Context context) {
        super(context);
    }

    public EditEventDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditEventDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(String currentEvent, OnClickListener okListener, OnClickListener deleteListener, OnClickListener cancelListener, boolean joystick, List<CameraEvent> availableEvents) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        if (CameraEvent.exists(currentEvent)) {
            RadioButton currentChoice = new RadioButton(getContext());
            CameraEvent event = CameraEvent.valueOf(currentEvent);
            currentChoice.setText(event.getName());
            currentChoice.setTag(event);
            radioGroup.addView(currentChoice);
            radioGroup.check(currentChoice.getId());
        }

        for (CameraEvent option : availableEvents) {
            if (joystick != option.isJoystickEvent())
                continue;
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setTag(option);
            radioButton.setText(option.getName());
            radioGroup.addView(radioButton);
        }

        if (radioGroup.getChildCount() == 0)
            findViewById(R.id.noEventsTextview).setVisibility(VISIBLE);

        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);
        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.deleteButton).setOnClickListener(deleteListener);

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
            ((TextView) findViewById(R.id.controllerTextView)).setText("Premi il tasto che vuoi associare a questo evento");
        });

        controllerOptionText.setOnClickListener(v -> {
            controllerOptionText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
            controllerOptionText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            faceOptionText.setTextColor(ContextCompat.getColor(getContext(),android.R.color.darker_gray));
            faceOptionText.setTypeface(Typeface.DEFAULT);
            findViewById(R.id.controllerLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.faceControlLayout).setVisibility(View.GONE);
            isControllerSelected = true;
            ((RadioGroup) findViewById(R.id.radioGroup)).clearCheck();
        });
    }

    public String getSelectedEvent() {
        if (isControllerSelected)
            return KeyEvent.keyCodeToString(pressedButton);
        else {
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1)
                return null;
            RadioButton radioButton = findViewById(selectedId);
            return radioButton.getTag().toString();
        }
    }

    public void showErrorMessage() {
        findViewById(R.id.errorMessage).setVisibility(VISIBLE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isControllerSelected) {
            pressedButton = keyCode;
            ((TextView) findViewById(R.id.controllerTextView)).setText(KeyEvent.keyCodeToString(keyCode));
        }
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Verifica se l'evento proviene da un joystick
        if (isControllerSelected) {
            ((TextView) findViewById(R.id.controllerTextView)).setText("JOYSTICK");
            pressedButton = -1;
        }
        return false;
    }
}
