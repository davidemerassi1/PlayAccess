package com.example.sandboxtest;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;

import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.MainActivityConfAzioni;

public class SandboxVerifier extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sandbox_verifier);

        //qui dovrei cercare di farlo senza permesso
        String foregroundApp = getForegroundApp();
        if (!getPackageName().equals(foregroundApp)) {
            Log.d("SandboxVerifier", "Package name: " + foregroundApp);
            Intent intent = new Intent(this, PermissionCheckerActivity.class);
            intent.putExtra("sandboxName", foregroundApp);
            startActivity(intent);
        }
    }

    private String getForegroundApp() {
        /*UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();

        // Query for events in the last minute
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                currentTime - 1000 * 60,
                currentTime
        );

        if (usageStatsList == null || usageStatsList.isEmpty()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        // Sort the stats by the last time used
        Collections.sort(usageStatsList, (o1, o2) -> Long.compare(o2.getLastTimeUsed(), o1.getLastTimeUsed()));

        // The first one is the most recent used app
        return usageStatsList.get(0).getPackageName();*/

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
            if (appTasks != null && !appTasks.isEmpty()) {
                ActivityManager.RecentTaskInfo taskInfo = appTasks.get(0).getTaskInfo();
                if (taskInfo != null && taskInfo.baseIntent != null) {
                    String packageName = taskInfo.baseIntent.getComponent().getPackageName();
                    return packageName;
                }
            }
        }
        return null;
    }
}