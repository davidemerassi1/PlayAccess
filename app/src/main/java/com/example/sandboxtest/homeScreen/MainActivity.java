package com.example.sandboxtest.homeScreen;

import android.content.Intent;
import android.os.Bundle;

import com.example.sandboxtest.R;
import com.example.sandboxtest.databinding.ActivityMainBinding;
import com.example.sandboxtest.installedApps.InstalledAppsActivity;
import com.fvbox.lib.FCore;
import com.fvbox.lib.common.pm.InstalledPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Home Screen");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, InstalledAppsActivity.class);
            startActivity(intent);
        });

        FCore fcore = FCore.get();
        List<InstalledPackage> installedApps = fcore.getInstalledApplications(0);
        Log.d("FCore", "Installed apps: " + installedApps.size());

        RecyclerView recyclerView = findViewById(R.id.appGrid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        AppAdapter adapter = new AppAdapter(installedApps, getApplicationContext(), packageName -> {
            fcore.launchApk(packageName, 0);
        });
        recyclerView.setAdapter(adapter);
    }
}