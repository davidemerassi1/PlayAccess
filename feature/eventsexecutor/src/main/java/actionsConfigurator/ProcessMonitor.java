package actionsConfigurator;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessMonitor {
    private final Handler handler;
    private final Runnable runnable;
    private final ActivityManager activityManager;
    private final UsageStatsManager usageStatsManager;
    private final String sandboxName;
    private String activePackage;

    public ProcessMonitor(OverlayView overlayView) {
        activityManager = (ActivityManager) overlayView.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        usageStatsManager = (UsageStatsManager) overlayView.getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        sandboxName = getForegroundApp();
        if (sandboxName == null) {
            throw new IllegalStateException("No foreground app");
        }

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

                if (runningAppProcesses != null) {
                    for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                        //Log.d("ProcessMonitor", "Process: " + processInfo.processName + ", PID: " + processInfo.pid + ", importance: " + processInfo.importance);
                        int pid = processInfo.pid;
                        String processName = processInfo.processName;
                        if (processName != null && !processName.startsWith("com.google.android.gms") && !processName.startsWith("com.google.process") && !processName.startsWith("com.android") && !processName.equals("com.example.sandboxtest")) {
                            Log.d("ProcessMonitor", "Current process : " + processName + ", PID: " + pid);
                            if (activePackage == null || !activePackage.equals(processName)) {
                                Log.d("ProcessMonitor", "Process changed: " + activePackage + " -> " + processName);
                                overlayView.changeGame(processName);
                                activePackage = processName;
                            }
                            break;
                        }
                    }
                }

                if (sandboxName.equals(getForegroundApp()))
                    overlayView.start();
                else
                    overlayView.stop();

                handler.postDelayed(this, 1000); // Esegui di nuovo dopo 1 secondo (1000 ms)
            }
        };
        handler.post(runnable);
    }

    private String getForegroundApp() {
        long currentTime = System.currentTimeMillis();

        // Query for events in the last minute
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                currentTime - 1000 * 60,
                currentTime
        );

        if (usageStatsList == null || usageStatsList.isEmpty()) {
            return null;
        }

        // Sort the stats by the last time used
        Collections.sort(usageStatsList, (o1, o2) -> Long.compare(o2.getLastTimeUsed(), o1.getLastTimeUsed()));

        // The first one is the most recent used app
        return usageStatsList.get(0).getPackageName();
    }
}
