package com.example.sandboxtest.homeScreen;

import android.content.Intent;
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

import android.text.Html;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppAdapter adapter;
    private List<InstalledPackage> installedApps;
    private FCore fcore = FCore.get();

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
    }

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
}