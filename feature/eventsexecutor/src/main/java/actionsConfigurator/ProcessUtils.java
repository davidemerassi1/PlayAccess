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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ProcessUtils {
    private static Handler handler;
    private static Runnable runnable;
    public static ArrayList<String> packages = new ArrayList<>();

    public static void startMonitoring(OverlayView overlayView) {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                ActivityManager activityManager = (ActivityManager) overlayView.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
                ActivityManager.RecentTaskInfo taskInfo = activityManager.getAppTasks().get(0).getTaskInfo();
                String sandboxName = taskInfo.baseIntent.getComponent().getPackageName();

                if (runningAppProcesses != null) {
                    for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                        int pid = processInfo.pid;
                        String processName = processInfo.processName;
                        if (processName != null && !processName.startsWith("com.google.android") && !processName.startsWith("com.google.process") && !processName.startsWith("com.android") && !processName.equals("com.example.sandboxtest") && !packages.contains(processName)) {
                            Log.d("ProcessUtils", "New process : " + processName + ", PID: " + pid);
                            packages.add(processName);
                            overlayView.changeGame(processName);
                        }
                    }
                }

                if (sandboxName.equals(getForegroundApp(overlayView.getContext())))
                    overlayView.start();
                else
                    overlayView.stop();

                handler.postDelayed(this, 1000); // Esegui di nuovo dopo 1 secondo (1000 ms)
            }
        };
        handler.post(runnable);
    }

    private static String getForegroundApp(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
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
