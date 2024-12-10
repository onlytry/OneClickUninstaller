package com.example.oneclickuninstaller;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.PopupMenu;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * MainActivity 是应程序的主要活动。
 * 它显示已安装应用程序的列表，并允许用户卸载选定的应用程序。
 */
public class MainActivity extends AppCompatActivity {
    private static final int UNINSTALL_REQUEST_CODE = 1;
    private List<AppInfo> userApps;
    private List<AppInfo> systemApps;
    private AppPagerAdapter pagerAdapter;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton uninstallButton;
    private FloatingActionButton moveToTopButton;
    private SortState currentSortState = SortState.ALPHABETICAL_ASC;
    private EditText searchEditText;
    private Button sortButton;
    private List<ApplicationInfo> appList;
    private AppListAdapter adapter;

    private enum SortState {
        ALPHABETICAL_ASC,
        ALPHABETICAL_DESC,
        INSTALL_TIME_ASC,
        INSTALL_TIME_DESC
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userApps = new ArrayList<>();
        systemApps = new ArrayList<>();

        loadApps();

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        uninstallButton = findViewById(R.id.uninstallButton);

        pagerAdapter = new AppPagerAdapter(this, userApps, systemApps);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "用户应用" : "系统应用")
        ).attach();

        bottomAppBar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_move_to_top) {
                moveSelectedToTop();
                return true;
            } else if (itemId == R.id.action_sort) {
                showSortMenu();
                return true;
            }
            return false;
        });

        uninstallButton.setOnClickListener(v -> {
            uninstallSelectedApps();
        });

        loadSelectedApps();
        updateUninstallButtonState();

        searchEditText = findViewById(R.id.search_edit_text);
        sortButton = findViewById(R.id.sort_button);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterApps(s.toString());
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortApps();
            }
        });
    }

    /**
     * 加载所有已安装的应用程序
     */
    private void loadApps() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        
        userApps.clear();
        systemApps.clear();

        for (PackageInfo packageInfo : packages) {
            AppInfo appInfo = new AppInfo(
                packageInfo.applicationInfo.loadLabel(pm).toString(),
                packageInfo.packageName,
                packageInfo.applicationInfo.loadIcon(pm),
                packageInfo.firstInstallTime,
                (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
            );
            if (appInfo.isSystemApp) {
                systemApps.add(appInfo);
            } else {
                userApps.add(appInfo);
            }
        }

        Log.d("MainActivity", "Loaded " + userApps.size() + " user apps and " + systemApps.size() + " system apps");

        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 根据当前���序状态对应用列进行排序
     */
    private void sortApps() {
        Comparator<AppInfo> comparator = (a1, a2) -> {
            switch (currentSortState) {
                case ALPHABETICAL_DESC:
                    return Collator.getInstance().compare(a2.appName, a1.appName);
                case INSTALL_TIME_ASC:
                    return Long.compare(a1.installTime, a2.installTime);
                case INSTALL_TIME_DESC:
                    return Long.compare(a2.installTime, a1.installTime);
                case ALPHABETICAL_ASC:
                default:
                    return Collator.getInstance().compare(a1.appName, a2.appName);
            }
        };

        Collections.sort(userApps, comparator);
        Collections.sort(systemApps, comparator);

        if (pagerAdapter != null) {
            pagerAdapter.updateApps(userApps, systemApps);
        }
    }

    /**
     * 卸载选定的应用程序
     */
    private void uninstallSelectedApps() {
        List<AppInfo> selectedApps = new ArrayList<>(pagerAdapter.getSelectedUserApps());
        selectedApps.addAll(pagerAdapter.getSelectedSystemApps());

        if (selectedApps.isEmpty()) {
            Toast.makeText(this, "请选择要卸载的应用", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("卸载确认")
                .setMessage("确定要卸载选中的 " + selectedApps.size() + " 个应用吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    for (AppInfo app : selectedApps) {
                        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        intent.setData(Uri.parse("package:" + app.packageName));
                        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                        startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 卸载指定名的应用
     * @param packageName 要卸载的应用的包名
     */
    private void uninstallApp(String packageName) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(intent, UNINSTALL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("MainActivity", "App uninstalled successfully");
                loadApps();
                pagerAdapter.updateApps(userApps, systemApps);
                Toast.makeText(this, "应用卸载成功", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("MainActivity", "App uninstall cancelled or failed");
                Toast.makeText(this, "卸载已取消或失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_name_asc) {
            currentSortState = SortState.ALPHABETICAL_ASC;
        } else if (id == R.id.action_sort_name_desc) {
            currentSortState = SortState.ALPHABETICAL_DESC;
        } else if (id == R.id.action_sort_time_asc) {
            currentSortState = SortState.INSTALL_TIME_ASC;
        } else if (id == R.id.action_sort_time_desc) {
            currentSortState = SortState.INSTALL_TIME_DESC;
        } else {
            return super.onOptionsItemSelected(item);
        }
        sortApps();
        updateBottomAppBarIcon();
        return true;
    }

    private void saveSelectedApps() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        Set<String> selectedPackages = new HashSet<>();
        for (AppInfo app : userApps) {
            if (app.isSelected) {
                selectedPackages.add(app.packageName);
            }
        }
        for (AppInfo app : systemApps) {
            if (app.isSelected) {
                selectedPackages.add(app.packageName);
            }
        }
        prefs.edit().putStringSet("selectedApps", selectedPackages).apply();
    }

    private void loadSelectedApps() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        Set<String> selectedPackages = prefs.getStringSet("selectedApps", new HashSet<>());
        for (AppInfo app : userApps) {
            app.isSelected = selectedPackages.contains(app.packageName);
        }
        for (AppInfo app : systemApps) {
            app.isSelected = selectedPackages.contains(app.packageName);
        }
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSelectedApps();
    }

    private void showSortMenu() {
        PopupMenu popup = new PopupMenu(this, bottomAppBar);
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_sort_name_asc) {
                currentSortState = SortState.ALPHABETICAL_ASC;
            } else if (id == R.id.action_sort_name_desc) {
                currentSortState = SortState.ALPHABETICAL_DESC;
            } else if (id == R.id.action_sort_time_asc) {
                currentSortState = SortState.INSTALL_TIME_ASC;
            } else if (id == R.id.action_sort_time_desc) {
                currentSortState = SortState.INSTALL_TIME_DESC;
            } else {
                return false;
            }
            sortApps();
            updateBottomAppBarIcon();
            return true;
        });
        popup.show();
    }

    private void updateBottomAppBarIcon() {
        int iconRes;
        switch (currentSortState) {
            case ALPHABETICAL_DESC:
                iconRes = R.drawable.ic_sort_descending;
                break;
            case INSTALL_TIME_ASC:
            case INSTALL_TIME_DESC:
                iconRes = R.drawable.ic_sort_time;
                break;
            case ALPHABETICAL_ASC:
            default:
                iconRes = R.drawable.ic_sort_ascending;
                break;
        }
        bottomAppBar.setNavigationIcon(ContextCompat.getDrawable(this, iconRes));
    }

    public void updateUninstallButtonState() {
        if (pagerAdapter != null) {
            boolean hasSelectedApps = !pagerAdapter.getSelectedUserApps().isEmpty() || !pagerAdapter.getSelectedSystemApps().isEmpty();
            uninstallButton.setEnabled(hasSelectedApps);
            uninstallButton.setVisibility(hasSelectedApps ? View.VISIBLE : View.GONE);
        }
    }

    private void moveSelectedToTop() {
        if (pagerAdapter != null) {
            pagerAdapter.moveSelectedToTop();
            Toast.makeText(this, "已将选中的应用移至顶部", Toast.LENGTH_SHORT).show();
        }
    }

    // 确保在选择或取消选择应用时调用此方法
    public void onAppSelectionChanged() {
        updateUninstallButtonState();
    }

    private void filterApps(String query) {
        List<AppInfo> filteredUserApps = new ArrayList<>();
        List<AppInfo> filteredSystemApps = new ArrayList<>();

        for (AppInfo app : userApps) {
            if (app.appName.toLowerCase().contains(query.toLowerCase())) {
                filteredUserApps.add(app);
            }
        }

        for (AppInfo app : systemApps) {
            if (app.appName.toLowerCase().contains(query.toLowerCase())) {
                filteredSystemApps.add(app);
            }
        }

        if (pagerAdapter != null) {
            pagerAdapter.updateApps(filteredUserApps, filteredSystemApps);
        }
    }
}
