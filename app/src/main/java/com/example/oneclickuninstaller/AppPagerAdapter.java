package com.example.oneclickuninstaller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppPagerAdapter extends RecyclerView.Adapter<AppPagerAdapter.AppListViewHolder> {
    private final Context context;
    private List<AppInfo> userApps;
    private List<AppInfo> systemApps;
    private final AppListAdapter userAppAdapter;
    private final AppListAdapter systemAppAdapter;

    public AppPagerAdapter(Context context, List<AppInfo> userApps, List<AppInfo> systemApps) {
        this.context = context;
        this.userApps = userApps;
        this.systemApps = systemApps;
        this.userAppAdapter = new AppListAdapter(context, userApps);
        this.systemAppAdapter = new AppListAdapter(context, systemApps);
    }

    @NonNull
    @Override
    public AppListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_list_page, parent, false);
        return new AppListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppListViewHolder holder, int position) {
        RecyclerView.Adapter adapter = position == 0 ? 
            new AppListAdapter(context, userApps) : 
            new AppListAdapter(context, systemApps);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return 2; // 用户应用和系统应用两个页面
    }

    public void updateApps(List<AppInfo> userApps, List<AppInfo> systemApps) {
        this.userApps = userApps;
        this.systemApps = systemApps;
        userAppAdapter.updateApps(userApps);
        systemAppAdapter.updateApps(systemApps);
        notifyDataSetChanged();
    }

    public List<AppInfo> getSelectedUserApps() {
        return userAppAdapter.getSelectedApps();
    }

    public List<AppInfo> getSelectedSystemApps() {
        return systemAppAdapter.getSelectedApps();
    }

    public void moveSelectedToTop() {
        userAppAdapter.moveSelectedToTop();
        systemAppAdapter.moveSelectedToTop();
        notifyDataSetChanged();
    }

    static class AppListViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        AppListViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.appRecyclerView);
        }
    }
}
