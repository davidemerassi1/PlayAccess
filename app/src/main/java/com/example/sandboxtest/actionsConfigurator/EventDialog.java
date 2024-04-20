package com.example.sandboxtest.actionsConfigurator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.sandboxtest.R;

public class EventDialog extends LinearLayout {
    public EventDialog(Context context) {
        super(context);
    }

    public EventDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(boolean joystick, OnClickListener okListener, OnClickListener cancelListener) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        for (Event option : Event.values()) {
            if (joystick != option.isJoystickEvent())
                continue;
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setTag(option);
            radioButton.setText(option.getName());
            radioGroup.addView(radioButton);
        }
        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);
    }

    public Event getSelectedEvent() {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1)
            return null;
        else {
            RadioButton radioButton = findViewById(selectedId);
            return (Event) radioButton.getTag();
        }
    }

    public void showErrorMessage() {
        findViewById(R.id.errorMessage).setVisibility(VISIBLE);
    }
}
