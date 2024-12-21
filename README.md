# One Click Uninstaller

One Click Uninstaller 是一款功能丰富的Android应用卸载工具，让用户能够快速、方便地管理和卸载不需要的应用程序。

## 功能特点

- 一键卸载：只需点击一下即可卸载选定的应用
- 应用列表：分别显示用户应用和系统应用
- 批量卸载：支持同时选择多个应用进行卸载
- 排序功能：可按名称或安装时间排序，支持升序和降序
- 搜索功能：快速查找特定应用
- 移动到顶部：可将选中的应用移动到列表顶部
- 记忆选择：应用会记住用户上次选择的应用
- 启动画面：优雅的启动画面提升用户体验



## 技术栈

- Java
- Android SDK
- ViewPager2
- RecyclerView
- Material Design Components
- SharedPreferences
- AlertDialog

## 项目结构

- `MainActivity.java`: 应用的主界面
- `AppPagerAdapter.java`: 管理应用列表的不同页面
- `AppListAdapter.java`: 处理应用列表的显示
- `AppInfo.java`: 定义应用信息的数据模型
- `SplashActivity.java`: 处理应用启动画面
- `splash_screen.xml`: 定义启动画面的布局
- `themes.xml`: 定义应用的主题样式
- `colors.xml`: 定义应用使用的颜色
- `app_name_text.xml`: 定义应用名称的文本样式

## 安装

1. 克隆此仓库
2. 在Android Studio中打开项目
3. 构建并运行应用

## 使用说明

1. 启动应用后,您将看到已安装应用的列表
2. 使用底部的标签页在系统应用和用户应用之间切换
3. 点击应用图标旁边的卸载按钮即可卸载该应用
4. 使用搜索栏快速查找特定应用

## 贡献

欢迎提交问题和拉取请求。对于重大更改,请先开issue讨论您想要改变的内容。

## 许可证

[MIT](https://choosealicense.com/licenses/mit/)
