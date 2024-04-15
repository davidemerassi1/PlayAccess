package com.example.sandboxtest.installedApps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandboxtest.R;
import com.example.sandboxtest.databinding.ActivityInstalledAppsBinding;
import com.fvbox.lib.FCore;
import com.fvbox.lib.abs.InstallPackageCallback;
import com.fvbox.lib.common.pm.InstallResult;

import java.util.ArrayList;
import java.util.List;

public class InstalledAppsActivity extends AppCompatActivity {
    private ActivityInstalledAppsBinding binding;
    private List<ApplicationInfo> installedApplications;
    private AppAdapter appAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Add apps</font>"));

        binding = ActivityInstalledAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FCore fcore = FCore.get();
        PackageManager packageManager = getPackageManager();
        installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
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
        appAdapter = new AppAdapter(installedApplications, getApplicationContext(), (appName, packageName, pos) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Installare " + appName + "?");
            builder.setPositiveButton("Si", (dialog, which) -> {
                View overlay = findViewById(R.id.overlay_layout);
                overlay.setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.installation_textview)).setText("Installing " + appName + "...");
                fcore.installPackageAsUserAsync(packageName, 0, installResult -> {
                    overlay.setVisibility(View.GONE);
                    Toast.makeText(this, installResult.getSuccess() ? "Installazione completata" : "Impossibile installare", Toast.LENGTH_SHORT).show();
                    list.remove(pos);
                    appAdapter.notifyItemRemoved(pos);
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                Log.d("AppViewHolder", "Non installare " + packageName);
            });
            builder.create().show();
        });
        recyclerView.setAdapter(appAdapter);
    }
}
