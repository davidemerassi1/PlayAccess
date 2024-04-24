package com.example.sandboxtest.homeScreen;

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
    private OnItemClickListener listener;

    public AppAdapter(List<ApplicationInfo> appList, Context context, OnItemClickListener listener) {
        this.appList = appList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_app, parent, false);
        return new AppViewHolder(view, context.getPackageManager(), listener);
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
