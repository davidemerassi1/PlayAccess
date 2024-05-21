package com.example.sandboxtest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.MainActivityConfAzioni;

public class SandboxVerifier extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sandbox_verifier);

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RecentTaskInfo taskInfo = activityManager.getAppTasks().get(0).getTaskInfo();
        String packageName = taskInfo.baseIntent.getComponent().getPackageName();
        if (!packageName.equals(getPackageName())) {
            Log.d("SandboxVerifier", "Package name: " + packageName);
            Intent intent = new Intent(this, MainActivityConfAzioni.class);
            startActivity(intent);
        }
    }
}