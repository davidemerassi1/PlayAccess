package com.example.eventsexecutor;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimi.di.ewlab.iss.common.model.MainModel;

public class ProcessMonitor {
    private final String sandboxName;
    private String activePackage;
    private MutableLiveData<String> activeProcess;
    private boolean areThereActiveApps;
    private OverlayView overlayView;

    public ProcessMonitor(OverlayView overlayView) {
        this.overlayView = overlayView;
        ActivityManager activityManager = (ActivityManager) overlayView.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        sandboxName = null;
        if (sandboxName == null) {
            throw new IllegalStateException("No foreground app");
        }
        activeProcess = MainModel.getInstance().getActivePackage();
        activeProcess.setValue(sandboxName);

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
                areThereActiveApps = false;

                if (runningAppProcesses != null) {
                    for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                        int pid = processInfo.pid;
                        String processName = processInfo.processName;
                        if (processName != null && processName.equals("com.example.sandboxtest")) {
                            activePackage = null;
                            break;
                        }
                        if (processName != null && !processName.startsWith("com.google.android.gms") && !processName.startsWith("com.google.process") && !processName.startsWith("com.android")) {
                            //Log.d("ProcessMonitor", "Current process : " + processName + ", PID: " + pid);
                            areThereActiveApps = true;
                            if (activePackage == null || !activePackage.equals(processName)) {
                                Log.d("ProcessMonitor", "Process changed: " + activePackage + " -> " + processName);
                                overlayView.changeGame(processName);
                                activePackage = processName;
                            }
                            break;
                        }
                    }
                }
                showOverlay();

                handler.postDelayed(this, 1000); // Esegui di nuovo dopo 1 secondo (1000 ms)
            }
        };
        handler.post(runnable);

        activeProcess.observeForever((activeProcess) -> {
            showOverlay();
        });
    }

    private void showOverlay() {
        if (activeProcess.getValue().equals(sandboxName) && areThereActiveApps)
            overlayView.start();
        else if (overlayView.getVisibility() == View.VISIBLE)
            overlayView.stop();
    }
}
