package com.example.eventsexecutor.gamesconfigurator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.eventsexecutor.R;

import java.util.List;

import com.example.eventsexecutor.OverlayService;
import com.example.eventsexecutor.gamesconfigurator.buttons.EventButton;

import it.unimi.di.ewlab.iss.common.database.Event;

import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class EventDialog extends FrameLayout {
    private Action selectedAction = null;
    private Event event;
    private RadioGroup radioGroup;
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

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                hideErrorMessage();
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

    public void hideErrorMessage() {
        findViewById(R.id.errorMessage).setVisibility(GONE);
    }

    public Event getEvent() {
        return event;
    }

    public Action getSelectedAction() {
        return selectedAction;
    }
}
