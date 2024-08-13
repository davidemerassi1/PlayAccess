package it.unimi.di.ewlab.iss.gamesconfigurator.dialogs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import it.unimi.di.ewlab.iss.gamesconfigurator.ConfigurationView;
import it.unimi.di.ewlab.iss.gamesconfigurator.R;
import it.unimi.di.ewlab.iss.gamesconfigurator.buttons.EventButton;
import it.unimi.di.ewlab.iss.gamesconfigurator.buttons.ResizableSlidingDraggableButton;

import java.util.List;

import it.unimi.di.ewlab.iss.common.database.Event;
import it.unimi.di.ewlab.iss.common.model.actions.Action;

public class SlidingEventDialog extends FrameLayout {
    private Action selectedAction;
    private Action selectedAction2;
    private Action selectedAction3;
    private EventDialog secondaryEventDialog;
    private EventButton tempEventButton = new EventButton() {
        private Action action;
        private Event event;

        @Override
        public Action getAction() {
            return action;
        }

        @Override
        public void setAction(Action action) {
            this.action = action;
        }

        @Override
        public void setOnClickListener(OnClickListener listener) {
        }

        @Override
        public Event getEvent() {
            return event;
        }

        @Override
        public void setEvent(Event event) {
            this.event = event;
        }

        @Override
        public void showAlert() {
        }

        @Override
        public void hideAlert() {
        }
    };
    private ConfigurationView configurationView;
    private EventButton thisEventButton;

    public SlidingEventDialog(Context context) {
        super(context);
    }

    public SlidingEventDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingEventDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(ResizableSlidingDraggableButton eventButton, OnClickListener okListener, OnClickListener cancelListener, OnClickListener deleteListener, List<Action> availableActions, ConfigurationView configurationView) {
        this.thisEventButton = eventButton;
        setElevation(30);
        findViewById(R.id.additional_actions_layout).setVisibility(VISIBLE);
        TextView actionLeftTextView = findViewById(R.id.actionLeftTextView);
        TextView actionRightTextView = findViewById(R.id.actionRightTextView);
        TextView actionResetTextView = findViewById(R.id.actionResetTextView);

        findViewById(R.id.okButton).setOnClickListener(okListener);
        findViewById(R.id.cancelButton).setOnClickListener(cancelListener);

        if (deleteListener != null) {
            findViewById(R.id.deleteButtonLayout).setVisibility(VISIBLE);
            findViewById(R.id.deleteButton).setOnClickListener(deleteListener);
            selectedAction = eventButton.getAction();
            selectedAction2 = eventButton.getAction2();
            selectedAction3 = eventButton.getAction3();
            if (eventButton.getResetToStart())
                ((CheckBox) findViewById(R.id.resetToStartCheckBox)).setChecked(true);
            ((TextView) findViewById(R.id.actionLeftTextView)).setText(selectedAction.getName());
            if (!availableActions.contains(selectedAction))
                actionLeftTextView.setTextColor(getResources().getColor(R.color.red, null));

            ((TextView) findViewById(R.id.actionRightTextView)).setText(selectedAction2.getName());
            if (!availableActions.contains(selectedAction2))
                actionRightTextView.setTextColor(getResources().getColor(R.color.red, null));

            ((TextView) findViewById(R.id.actionResetTextView)).setText(selectedAction3.getName());
            if (!availableActions.contains(selectedAction3))
                actionResetTextView.setTextColor(getResources().getColor(R.color.red, null));
        }

        actionLeftTextView.setOnClickListener(v -> openSecondaryDialog(R.id.actionLeftTextView));

        actionRightTextView.setOnClickListener(v -> openSecondaryDialog(R.id.actionRightTextView));

        actionResetTextView.setOnClickListener(v -> openSecondaryDialog(R.id.actionResetTextView));

        this.configurationView = configurationView;
    }

    private void openSecondaryDialog(int selectedTextView) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        secondaryEventDialog = (EventDialog) inflater.inflate(R.layout.dialog_layout, this, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(secondaryEventDialog, layoutParams);
        secondaryEventDialog.init(
                tempEventButton,
                v1 -> {
                    Action a = secondaryEventDialog.getSelectedAction();
                    if (a == null) {
                        secondaryEventDialog.showErrorMessage();
                        return;
                    }
                    if (configurationView.giaAssegnata(a, thisEventButton)) {
                        secondaryEventDialog.showSameActionErrorMessage();
                        return;
                    }
                    if (selectedTextView == R.id.actionLeftTextView)
                        selectedAction = a;
                    else if (selectedTextView == R.id.actionRightTextView)
                        selectedAction2 = a;
                    else
                        selectedAction3 = a;
                    ((TextView) findViewById(selectedTextView)).setText(a.getName());
                    ((TextView) findViewById(selectedTextView)).setTextColor(getResources().getColor(R.color.primaryColor, null));
                    removeView(secondaryEventDialog);
                    secondaryEventDialog = null;
                },
                v1 -> {
                    removeView(secondaryEventDialog);
                    secondaryEventDialog = null;
                },
                null);
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

    public void showErrorMessage() {
        findViewById(R.id.errorMessage).setVisibility(VISIBLE);
    }

    public void showSameActionErrorMessage() {
        findViewById(R.id.sameActionErrorMessage).setVisibility(VISIBLE);
    }

    public boolean getResetToStart() {
        CheckBox checkBox = findViewById(R.id.resetToStartCheckBox);
        return checkBox.isChecked();
    }
}
