package com.example.sandboxtest;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import it.unimi.di.ewlab.iss.common.ui.intro.PlayAccessIntroActivity;

public class SandboxVerifier extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sandbox_verifier);

        findViewById(R.id.googlePlayButton).setOnClickListener(v -> {
            String parallelSpaceId = "com.lbe.parallel.intl";
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + parallelSpaceId));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + parallelSpaceId));
                startActivity(intent);
            }
        });

        String sandboxPackageName = getSandboxPackageName();
        if (sandboxPackageName != null) {
            Log.d("SandboxVerifier", "Package name: " + sandboxPackageName);
            Intent intent = new Intent(this, PlayAccessIntroActivity.class);
            intent.putExtra("sandboxPackageName", sandboxPackageName);
            intent.putExtra("sandboxName", getSandboxName(sandboxPackageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private String getSandboxPackageName() {
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

    private String getSandboxName(String packageName) {
        PackageManager packageManager = getPackageManager();
        String appName = "your sandbox";
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            appName = packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }
}