package com.example.kotlinstudy.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RamUitl {
    /**
     * 方法描述：判断某一应用是否正在运行
     * Created by cafeting on 2017/2/4.
     *
     * @param context     上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    //获取已安装应用的 uid，-1 表示未安装此应用或程序异常
    public static int getPackageUid(Context context, String packageName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                Log.d("ram", "getPackageUid: applicationInfo.uid" + applicationInfo.uid);
                return applicationInfo.uid;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    /**
     * 判断某一 uid 的程序是否有正在运行的进程，即是否存活
     * Created by cafeting on 2017/2/4.
     *
     * @param context 上下文
     * @param uid     已安装应用的 uid
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isProcessRunning(Context context, int uid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() > 0) {
            for (ActivityManager.RunningServiceInfo appProcess : runningServiceInfos) {
                if (uid == appProcess.uid) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final String BOOT_START_PERMISSION = "android.permission.RECEIVE_BOOT_COMPLETED";

    /**
     * 　　      * 获取Android开机启动列表
     */
    public static List<Map<String, Object>> fetchInstalledApps(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> appInfo = pm.getInstalledApplications(0);
        Iterator<ApplicationInfo> appInfoIterator = appInfo.iterator();
        List<Map<String, Object>> appList = new ArrayList<Map<String, Object>>(appInfo.size());
        while (appInfoIterator.hasNext()) {
            ApplicationInfo app = appInfoIterator.next();
            int flag = pm.checkPermission(BOOT_START_PERMISSION, app.packageName);
            if (flag == PackageManager.PERMISSION_GRANTED) {
                Map<String, Object> appMap = new HashMap<String, Object>();
                String label = pm.getApplicationLabel(app).toString();
                Drawable icon = pm.getApplicationIcon(app);
                String desc = app.packageName;
                appMap.put("label", label);
                appMap.put("icon", icon);
                appMap.put("desc", desc);

                appList.add(appMap);
            }
        }
        return appList;
    }


    public static boolean isSystemApp(Context context,String pkgName) {
        boolean isSystemApp = false;
        PackageInfo pi = null;
        try {
            PackageManager pm =context.getPackageManager();
            pi = pm.getPackageInfo(pkgName, 0);
        } catch (Throwable t) {
            //Log.w(TAG, t.getMessage(), t);
        }
        // 是系统中已安装的应用
        if (pi != null) {
            boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
            isSystemApp = isSysApp || isSysUpd;
        }
        return isSystemApp;
    }


    public static void getRunningApp(Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        List localList = localPackageManager.getInstalledPackages(0);
        for (int i =0; i < localList.size(); i++) {
            PackageInfo localPackageInfo =(PackageInfo) localList.get(i);
            String packageStr = localPackageInfo.packageName.split(":")[0];
            if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo.applicationInfo.flags) ==0)
                    &&((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & localPackageInfo.applicationInfo.flags) ==0)
                    &&((ApplicationInfo.FLAG_STOPPED & localPackageInfo.applicationInfo.flags) ==0)) {
                Log.e("ram","running app"+ packageStr);
            }
        }

    }

    public static void getRunning3rdApp(Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        List localList = localPackageManager.getInstalledPackages(0);
        for (int i = 0; i < localList.size(); i++) {
            PackageInfo localPackageInfo1 = (PackageInfo) localList.get(i);
            String str1 = localPackageInfo1.packageName.split(":")[0];
            if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo1.applicationInfo.flags) == 0)
                    && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & localPackageInfo1.applicationInfo.flags) == 0)
                    && ((ApplicationInfo.FLAG_STOPPED & localPackageInfo1.applicationInfo.flags) == 0)) {

                Log.d("ram", "packageName =====>:" + str1);
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + str1));
                //startActivity(intent);
            }
        }
    }

    public static void getRunningApp2(Context context){
        PackageManager localPackageManager = context.getPackageManager();
        List localList = localPackageManager.getInstalledPackages(0);
        for (int i = 0; i < localList.size(); i++) {
            PackageInfo localPackageInfo1 = (PackageInfo) localList.get(i);
            String str1 = localPackageInfo1.packageName.split(":")[0];
            if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo1.applicationInfo.flags) == 0) && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & localPackageInfo1.applicationInfo.flags) == 0) && ((ApplicationInfo.FLAG_STOPPED & localPackageInfo1.applicationInfo.flags) == 0)) {
                Log.e("ram",str1);
            }
        }

    }

}
