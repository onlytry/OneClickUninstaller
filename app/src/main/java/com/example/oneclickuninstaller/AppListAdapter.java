package com.example.oneclickuninstaller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {
    private final Context context;
    private List<AppInfo> apps;
    private final List<AppInfo> selectedApps = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public AppListAdapter(Context context, List<AppInfo> apps) {
        this.context = context;
        this.apps = apps;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo app = apps.get(position);
        holder.appName.setText(app.appName);
        holder.packageName.setText(app.packageName);
        holder.appIcon.setImageDrawable(app.icon);
        holder.installTime.setText(dateFormat.format(new Date(app.installTime)));
        holder.checkBox.setChecked(app.isSelected);

        holder.itemView.setOnClickListener(v -> {
            app.isSelected = !app.isSelected;
            holder.checkBox.setChecked(app.isSelected);
            notifyItemChanged(position);
            if (context instanceof MainActivity) {
                ((MainActivity) context).onAppSelectionChanged();
            }
        });

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.isSelected = isChecked;
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateUninstallButtonState();
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public void updateApps(List<AppInfo> apps) {
        this.apps = apps;
        notifyDataSetChanged();
    }

    public List<AppInfo> getSelectedApps() {
        return selectedApps;
    }

    public void moveSelectedToTop() {
        Collections.sort(apps, (a1, a2) -> {
            if (a1.isSelected && !a2.isSelected) {
                return -1;
            } else if (!a1.isSelected && a2.isSelected) {
                return 1;
            } else {
                return 0;
            }
        });
        notifyDataSetChanged();
    }

    public void updateList(List<AppInfo> newList) {
        this.apps = newList;
        notifyDataSetChanged();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
        TextView installTime;
        CheckBox checkBox;

        AppViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            packageName = itemView.findViewById(R.id.app_package);
            installTime = itemView.findViewById(R.id.app_install_time);
            checkBox = itemView.findViewById(R.id.app_checkbox);
        }
    }
}
