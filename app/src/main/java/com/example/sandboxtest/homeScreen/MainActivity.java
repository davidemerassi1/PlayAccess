package com.example.sandboxtest.homeScreen;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.sandboxtest.R;
import com.example.sandboxtest.databinding.ActivityMainBinding;
import com.example.sandboxtest.installedApps.InstalledAppsActivity;
import com.fvbox.lib.FCore;
import com.fvbox.lib.common.pm.InstalledPackage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppAdapter adapter;
    private List<InstalledPackage> installedApps;
    private FCore fcore = FCore.get();
    private View overlayButtonView;
    private WindowManager windowManager;
    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 123;

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
        installedApps = fcore.getInstalledApplications(0);

        RecyclerView recyclerView = findViewById(R.id.appGrid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AppAdapter(installedApps, getApplicationContext(), new OnItemClickListener() {
            @Override
            public void onItemClick(String packageName) {
                fcore.launchApk(packageName, 0);
            }

            @Override
            public void onItemLongClick(String name, String packageName, int pos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Uninstall " + name + "?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    fcore.uninstallPackage(packageName);
                    installedApps.remove(pos);
                    adapter.notifyItemRemoved(pos);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });
        recyclerView.setAdapter(adapter);

        checkOverlayPermission();
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
        installedApps = fcore.getInstalledApplications(0);
        RecyclerView recyclerView = findViewById(R.id.appGrid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AppAdapter(installedApps, getApplicationContext(), new OnItemClickListener() {
            @Override
            public void onItemClick(String packageName) {
                fcore.launchApk(packageName, 0);
                showOverlayButton();
            }

            @Override
            public void onItemLongClick(String name, String packageName, int pos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Uninstall " + name + "?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    fcore.uninstallPackage(packageName);
                    installedApps.remove(pos);
                    adapter.notifyItemRemoved(pos);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showOverlayButton() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayButtonView = LayoutInflater.from(this).inflate(R.layout.overlay_button_layout, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 200;
        windowManager.addView(overlayButtonView, params);
        Button overlayButton = overlayButtonView.findViewById(R.id.overlayButton);
        overlayButton.setOnClickListener(v -> {
            // Simula un tocco sullo schermo
            simulateTouch();
        });
    }

    private void removeOverlayButton() {
        // Rimuovi il bottone dal WindowManager
        if (overlayButtonView != null && overlayButtonView.getParent() != null) {
            windowManager.removeView(overlayButtonView);
        }
    }

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

    Instrumentation mInstrumentation = new Instrumentation();
    private void simulateTouch() {
        int targetX = 200; // Coordinata X della posizione di destinazione
        int targetY = 400; // Coordinata Y della posizione di destinazione
        long now = SystemClock.uptimeMillis();

        // Crea un evento di tocco simulato
        MotionEvent touchEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, targetX, targetY, 0);
        MotionEvent touchEvent2 = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, targetX, targetY, 0);

        new Thread(() -> {
            try {
                mInstrumentation.sendPointerSync(touchEvent);
                mInstrumentation.sendPointerSync(touchEvent2);
            } catch (SecurityException e) {
                Toast.makeText(getApplicationContext(), "Errore durante la simulazione del tocco: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }
}