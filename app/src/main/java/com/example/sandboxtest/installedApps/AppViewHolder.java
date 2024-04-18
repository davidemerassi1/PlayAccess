package com.example.sandboxtest.installedApps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandboxtest.R;

public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView appName;
    private ImageView appIcon;
    private PackageManager packageManager;
    private OnItemClickListener listener;
    private String packageName;
    private String src;

    public AppViewHolder(View itemView, PackageManager packageManager, OnItemClickListener listener) {
        super(itemView);
        appName = itemView.findViewById(R.id.appName);
        appIcon = itemView.findViewById(R.id.appIcon);
        this.packageManager = packageManager;
        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    public void set(ApplicationInfo app) {
        appName.setText(app.loadLabel(packageManager));
        appIcon.setImageDrawable(app.loadIcon(packageManager));
        packageName = app.packageName;
        src = app.sourceDir;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick(appName.getText().toString(), src, getAdapterPosition());
        }
    }
}