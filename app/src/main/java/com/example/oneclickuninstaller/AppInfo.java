package com.example.oneclickuninstaller;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String appName;
    public String packageName;
    public Drawable icon;
    public long installTime;
    public boolean isSystemApp;
    public boolean isSelected;

    public AppInfo(String appName, String packageName, Drawable icon, long installTime, boolean isSystemApp) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.installTime = installTime;
        this.isSystemApp = isSystemApp;
        this.isSelected = false;
    }
}
