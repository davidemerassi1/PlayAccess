package com.example.sandboxtest;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import it.unimi.di.ewlab.iss.actionsconfigurator.ui.activity.MainActivityConfAzioni;

public class PermissionCheckerActivity extends AppCompatActivity {
    private static final int overlayRequestCode = 1234;
    private static final int usageStatsRequestCode = 5678;
    private Button button;
    private TextView textView;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_checker);
        button = findViewById(R.id.grantPermissionButton);
        textView = findViewById(R.id.permissionTextView);
        alert = new AlertDialog.Builder(this)
                .setTitle("Permission required")
                .setMessage("Please grant the permission to continue")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create();

        checkOverlayPermission();
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            button.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, overlayRequestCode);
            });
        } else {
            checkUsageStatsPermission();
        }
    }

    private void checkUsageStatsPermission() {
        if (!hasUsageStatsPermission()) {
            textView.setText("secondo permesso");
            button.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getIntent().getStringExtra("sandboxName"));
                }
                startActivityForResult(intent, usageStatsRequestCode);
            });
        } else {
            Intent intent = new Intent(this, MainActivityConfAzioni.class);
            startActivity(intent);
        }
    }

    private boolean hasUsageStatsPermission() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                currentTime - 1000 * 60,
                currentTime
        );

        return usageStatsList != null && !usageStatsList.isEmpty();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case overlayRequestCode:
                if (!Settings.canDrawOverlays(this))
                    alert.show();
                checkOverlayPermission();
                break;
            case usageStatsRequestCode:
                if (!hasUsageStatsPermission())
                    alert.show();
                checkUsageStatsPermission();
                break;
        }
    }
}