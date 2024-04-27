package com.example.sandboxtest.actionsConfigurator.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.example.sandboxtest.R;
import com.example.sandboxtest.database.Event;

import java.util.List;

public class EditEventDialog extends LinearLayout {
    public EditEventDialog(Context context) {
        super(context);
    }

    public EditEventDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditEventDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Event currentEvent, OnClickListener okListener, OnClickListener deleteListener, OnClickListener cancelListener, List<Event> availableEvents) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton currentChoice = new RadioButton(getContext());
        currentChoice.setTag(currentEvent);
        currentChoice.setText(currentEvent.getName());
        radioGroup.addView(currentChoice);
        radioGroup.check(currentChoice.getId());

        for (Event option : availableEvents) {
            if (currentEvent.isJoystickEvent() != option.isJoystickEvent())
                continue;
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setTag(option);
            radioButton.setText(option.getName());
            radioGroup.addView(radioButton);
        }

        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);
        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.deleteButton).setOnClickListener(deleteListener);
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
}
