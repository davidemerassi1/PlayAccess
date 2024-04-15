package com.example.sandboxtest.homeScreen;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sandboxtest.R;

public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private TextView appName;
    private ImageView appIcon;
    private PackageManager packageManager;
    private OnItemClickListener listener;
    private String packageName;

    public AppViewHolder(View itemView, PackageManager packageManager, OnItemClickListener listener) {
        super(itemView);
        appName = itemView.findViewById(R.id.homeAppName);
        appIcon = itemView.findViewById(R.id.homeAppIcon);
        this.packageManager = packageManager;
        this.listener = listener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void set(ApplicationInfo app) {
        appName.setText(app.loadLabel(packageManager));
        appIcon.setImageDrawable(app.loadIcon(packageManager));
        packageName = app.packageName;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick(packageName);
        }
    }


    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            listener.onItemLongClick(appName.getText().toString(), packageName, getAdapterPosition());
        }
        return true;
    }
}