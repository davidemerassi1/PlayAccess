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

        String sandboxName = getSandboxName();
        if (sandboxName != null) {
            Log.d("SandboxVerifier", "Package name: " + sandboxName);
            Intent intent = new Intent(this, PermissionCheckerActivity.class);
            intent.putExtra("sandboxName", sandboxName);
            startActivity(intent);
        }
    }

    private String getSandboxName() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
            for (ActivityManager.AppTask task : appTasks) {
                ActivityManager.RecentTaskInfo taskInfo = task.getTaskInfo();
                if (taskInfo != null && taskInfo.baseIntent.getComponent() != null) {
                    String packageName = taskInfo.baseIntent.getComponent().getPackageName();
                    if (!packageName.equals(getPackageName()))
                        return packageName;
                }
            }
        }
        return null;
    }
}