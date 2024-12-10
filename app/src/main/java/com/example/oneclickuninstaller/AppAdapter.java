package com.example.oneclickuninstaller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AppAdapter extends ArrayAdapter<AppInfo> {
    public AppAdapter(Context context, List<AppInfo> apps) {
        super(context, 0, apps);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_app, parent, false);
        }

        AppInfo appInfo = getItem(position);

        ImageView iconView = convertView.findViewById(R.id.app_icon);
        TextView nameView = convertView.findViewById(R.id.app_name);
        TextView packageView = convertView.findViewById(R.id.app_package);

        iconView.setImageDrawable(appInfo.icon);
        nameView.setText(appInfo.appName);
        packageView.setText(appInfo.packageName);

        return convertView;
    }
}
