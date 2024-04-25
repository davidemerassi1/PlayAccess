package com.example.sandboxtest.homeScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;

import com.example.sandboxtest.R;
import com.example.sandboxtest.actionsConfigurator.OverlayView;
import com.example.sandboxtest.databinding.ActivityMainBinding;
import com.example.sandboxtest.facedetector.CameraFaceDetector;
import com.example.sandboxtest.installedApps.InstalledAppsActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.Html;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppAdapter adapter;
    private List<ApplicationInfo> installedApps;
    private BlackBoxCore core = BlackBoxCore.get();
    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 123;
    private CameraFaceDetector cameraFaceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Home</font>"));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, InstalledAppsActivity.class);
            startActivity(intent);
        });

        checkOverlayPermission();

        requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
        //cameraFaceDetector = new CameraFaceDetector(this);
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This app needs permission to draw over other apps. Grant permission to continue")
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
                    })
                    .show();
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onResume() {
        super.onResume();
        installedApps = core.getInstalledApplications(0, 0);
        Log.d("InstalledApps", installedApps.size() + "");
        if (installedApps.isEmpty())
            findViewById(R.id.alert).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.alert).setVisibility(View.GONE);
        RecyclerView recyclerView = findViewById(R.id.appGrid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AppAdapter(installedApps, getApplicationContext(), new OnItemClickListener() {
            @Override
            public void onItemClick(String packageName) {
                /*Intent intent = VirtualCore.get().getLaunchIntent(packageName, 0);
                VActivityManager.get().startActivity(intent, 0);*/
                core.launchApk(packageName, 0);
                showOverlayView(packageName);
            }

            @Override
            public void onItemLongClick(String name, String packageName, int pos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Uninstall " + name + "?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    core.uninstallPackage(packageName);
                    installedApps.remove(pos);
                    adapter.notifyItemRemoved(pos);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showOverlayView(String applicationPackage) {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        OverlayView overlay = (OverlayView) LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
        overlay.init(windowManager, applicationPackage);
    }

    /*private void removeOverlay() {
        if (overlay != null && overlay.getParent() != null) {
            windowManager.removeView(overlay);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DRAW_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                // L'utente ha concesso il permesso di sovrapposizione, puoi procedere con l'applicazione
                // Avvia il flusso dell'applicazione o esegui altre azioni necessarie
            } else {
                // L'utente non ha concesso il permesso di sovrapposizione
                // Puoi informare l'utente che il permesso è necessario per utilizzare l'applicazione
                Toast.makeText(this, "Il permesso di sovrapposizione è necessario per utilizzare l'applicazione", Toast.LENGTH_SHORT).show();
                // Chiudi l'applicazione o esegui altre azioni appropriate
                finish();
            }
        }
    }
}