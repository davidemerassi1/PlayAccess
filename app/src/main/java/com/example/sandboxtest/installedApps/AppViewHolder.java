package com.example.sandboxtest.installedApps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandboxtest.R;

public class AppViewHolder extends RecyclerView.ViewHolder {
    private TextView appName;
    private ImageView appIcon;
    private PackageManager packageManager;

    public AppViewHolder(View itemView, PackageManager packageManager) {
        super(itemView);
        appName = itemView.findViewById(R.id.appName);
        appIcon = itemView.findViewById(R.id.appIcon);
        this.packageManager = packageManager;
    }

    public void set(ApplicationInfo app) {
        appName.setText(app.loadLabel(packageManager));
        appIcon.setImageDrawable(app.loadIcon(packageManager));
    }
}