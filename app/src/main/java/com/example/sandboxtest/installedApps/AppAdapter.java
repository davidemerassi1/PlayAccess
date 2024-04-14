package com.example.sandboxtest.installedApps;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandboxtest.R;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppViewHolder> {
    private List<ApplicationInfo> appList;
    private Context context;

    public AppAdapter(List<ApplicationInfo> appList, Context context) {
        this.appList = appList;
        this.context = context;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_installed_app, parent, false);
        return new AppViewHolder(view, context.getPackageManager());
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        ApplicationInfo app = appList.get(position);
        holder.set(app);
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
