package com.example.sandboxtest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;

public class SandboxVerifier {
    public static String getSandboxPackageName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
            for (ActivityManager.AppTask task : appTasks) {
                ActivityManager.RecentTaskInfo taskInfo = task.getTaskInfo();
                if (taskInfo != null && taskInfo.baseIntent.getComponent() != null) {
                    String packageName = taskInfo.baseIntent.getComponent().getPackageName();
                    if (!packageName.equals(context.getPackageName()))
                        return packageName;
                }
            }
        }
        return null;
    }
}
