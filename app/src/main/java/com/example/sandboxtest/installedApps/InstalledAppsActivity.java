package com.example.sandboxtest.installedApps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandboxtest.R;
import com.example.sandboxtest.databinding.ActivityInstalledAppsBinding;
import com.fvbox.lib.FCore;
import com.fvbox.lib.common.pm.InstallResult;

import java.util.ArrayList;
import java.util.List;

public class InstalledAppsActivity extends AppCompatActivity {
    private ActivityInstalledAppsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInstalledAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FCore fcore = FCore.get();
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        //si puo usare stream
        List<ApplicationInfo> list = new ArrayList<>();
        for (ApplicationInfo app : installedApplications) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !fcore.isInstalled(app.packageName, 0) && !app.packageName.equals("com.example.sandboxtest")) {
                list.add(app);
            }
        }
        installedApplications = list;
        RecyclerView recyclerView = findViewById(R.id.appList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AppAdapter appAdapter = new AppAdapter(installedApplications, getApplicationContext(), (appName, packageName) -> {
            Log.d("InstalledAppsActivity", "Applicazione selezionata: " + appName);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Installare " + appName + "?");
            builder.setPositiveButton("Si", (dialog, which) -> {
                InstallResult installResult = fcore.installPackageAsUser(packageName, 0);
                Log.d("InstalledAppsActivity", "Installazione di " + packageName + " " + installResult.getSuccess());

            });
            builder.setNegativeButton("No", (dialog, which) -> {
                Log.d("AppViewHolder", "Non installare " + packageName);
            });
            builder.create().show();
        });
        recyclerView.setAdapter(appAdapter);
    }
}
