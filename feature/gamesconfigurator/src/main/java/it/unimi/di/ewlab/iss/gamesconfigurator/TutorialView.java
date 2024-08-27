package it.unimi.di.ewlab.iss.gamesconfigurator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import it.unimi.di.ewlab.iss.common.model.MainModel;

public class TutorialView extends RelativeLayout {
    private MainModel mainModel;
    private View tutorialStep1;
    private View tutorialStep2;
    private View tutorialStep3;

    public TutorialView(@NonNull Context context) {
        super(context);
    }

    public TutorialView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        mainModel = MainModel.getInstance();
        tutorialStep1 = findViewById(R.id.tutorialStep1);
        tutorialStep2 = findViewById(R.id.tutorialStep2);
        tutorialStep3 = findViewById(R.id.tutorialStep3);
        mainModel.getTutorialStep().observeForever(this::changeStep);
    }

    public void changeStep(int step) {
        switch (step) {
            case 1 -> {
                tutorialStep1.setVisibility(VISIBLE);
                tutorialStep2.setVisibility(GONE);
                tutorialStep3.setVisibility(GONE);
            }
            case 2 -> {
                tutorialStep1.setVisibility(GONE);
                tutorialStep2.setVisibility(VISIBLE);
                tutorialStep3.setVisibility(GONE);
            }
            case 3, 5 -> {
                tutorialStep1.setVisibility(GONE);
                tutorialStep2.setVisibility(GONE);
                tutorialStep3.setVisibility(GONE);
            }
            case 4 -> {
                tutorialStep1.setVisibility(GONE);
                tutorialStep2.setVisibility(GONE);
                tutorialStep3.setVisibility(VISIBLE);
            }
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }
}
