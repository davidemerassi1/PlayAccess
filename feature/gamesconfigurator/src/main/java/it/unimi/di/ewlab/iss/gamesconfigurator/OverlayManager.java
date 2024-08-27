package it.unimi.di.ewlab.iss.gamesconfigurator;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import it.unimi.di.ewlab.iss.common.model.MainModel;

public class OverlayManager {
    private OverlayView overlay;
    private TouchIndicatorView touchIndicatorView;
    private TutorialView tutorialView;

    public OverlayManager(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        overlay = (OverlayView) LayoutInflater.from(context).inflate(R.layout.overlay_layout, null);
        touchIndicatorView = new TouchIndicatorView(context);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);

        overlay.init(windowManager);
        windowManager.addView(touchIndicatorView, params);
        if (MainModel.getInstance().getTutorialStep() != null) {
            tutorialView = (TutorialView) LayoutInflater.from(context).inflate(R.layout.tutorial_layout, null);
            windowManager.addView(tutorialView, params);
            tutorialView.init();
        }
    }

    public void changeGame(String packageName) {
        overlay.closeConfiguration();
        overlay.changeGame(packageName);
    }

    public void showOverlay() {
        overlay.start();
        if (tutorialView != null)
            tutorialView.show();
    }

    public void hideOverlay() {
        overlay.stop();
        if (tutorialView != null)
            tutorialView.hide();
    }

    public TouchIndicatorView getTouchIndicatorView() {
        return touchIndicatorView;
    }
}
