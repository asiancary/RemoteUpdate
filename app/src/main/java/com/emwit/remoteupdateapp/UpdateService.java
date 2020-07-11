package com.emwit.remoteupdateapp;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.emwit.core.IMyAidlInterface;
import com.emwit.core.KeepAliveService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {
    String TAG = "UpdateService";
    public static String remoteVersionName;
    public static String remoteVersionCode;
    public static String remoteAppDownloadUrl;
    public static String remoteDiscription;
    public static String localVersion;
    public static String localVersionCode;
    public static String localAppDownloadUrl;
    public static String localDiscription;

    private MyBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            try {
                Log4j1Util.i("UpdateService", "connected with " + iMyAidlInterface.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            Toast.makeText(UpdateService.this, "链接断开，重新启动 KeepAliveService", Toast.LENGTH_LONG).show();
            startService(new Intent(UpdateService.this, KeepAliveService.class));
            bindService(new Intent(UpdateService.this, KeepAliveService.class), connection, Context.BIND_IMPORTANT);
        }
    };

    public UpdateService() {
    }

    @Override
    public void onCreate() {
        Log4j1Util.i(TAG, "***************  start update service  ***************");

//        timer2.schedule(timerTask, 1000, 1000);


        new Thread(new Runnable() {
            @Override
            public void run() {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
//                        downloadApkIfUpdate();
                        Log4j1Util.i("UpdateService","service is running");

               boolean FlagOfProcess =   isMyAppRunning(getApplicationContext(),"com.emwit.facejoy");
               if(FlagOfProcess){

               }

                    }
                };
                Timer timer = new Timer();
//                timer.schedule(timerTask, 60 * 1000, 2 * 60 * 60 * 1000);   // delay 1 min, period 2 h
                timer.schedule(timerTask, 1000, 5 * 1000);
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "UpdateService 启动", Toast.LENGTH_LONG).show();
        startService(new Intent(UpdateService.this, KeepAliveService.class));
        bindService(new Intent(this, KeepAliveService.class), connection, Context.BIND_IMPORTANT);

        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public void onDestroy() {
    }

    private void downloadApkIfUpdate() {
        File localXmlFile = new File(GlobalConst.LOCAL_XML_FILE_PATH);
        if (!localXmlFile.exists()) {
            try {
                localXmlFile.createNewFile();
                Util.updateXmlFile(GlobalConst.LOCAL_XML_FILE_PATH, "1.0", "1", "https://emwit.com/APK/KQ/ClassroomAttendance.apk", "First Version");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] localVersionContents = Util.parseXmlFile(GlobalConst.LOCAL_XML_FILE_PATH);
        if (localVersionContents != null) {
            localVersion = localVersionContents[0];
            localVersionCode = localVersionContents[1];
            localAppDownloadUrl = localVersionContents[2];
            localDiscription = localVersionContents[3];
            Log4j1Util.i(TAG, "localVersion: " + localVersion);
            Log4j1Util.i(TAG, "localVersionCode: " + localVersionCode);
            Log4j1Util.i(TAG, "localAppDownloadUrl: " + localAppDownloadUrl);
            Log4j1Util.i(TAG, "localDiscreption: " + localDiscription);
        }

        // parse xml file in server and get version number
        int resultCode = Util.downLoadFile(GlobalConst.UPDATE_XML_URL, GlobalConst.UPDATE_XML_FILE_PATH);
        if (resultCode != GlobalConst.OK) {
            return;
        }
        String[] remoteVersionContents = Util.parseXmlFile(GlobalConst.UPDATE_XML_FILE_PATH);
        if (remoteVersionContents != null) {
            remoteVersionName = remoteVersionContents[0];
            remoteVersionCode = remoteVersionContents[1];
            remoteAppDownloadUrl = remoteVersionContents[2];
            remoteDiscription = remoteVersionContents[3];
            Log4j1Util.i(TAG, "version: " + remoteVersionName);
            Log4j1Util.i(TAG, "remoteVersionCode: " + remoteVersionCode);
            Log4j1Util.i(TAG, "appDownloadUrl: " + remoteAppDownloadUrl);
            Log4j1Util.i(TAG, "discreption: " + remoteDiscription);
            File updateXmlFile = new File(GlobalConst.UPDATE_XML_FILE_PATH);
            updateXmlFile.delete();
        }
        if (Integer.parseInt(remoteVersionCode) > Integer.parseInt(localVersionCode)) {
            Log4j1Util.i(TAG, "start update app");
            Util.downloadApk(getApplicationContext(), remoteAppDownloadUrl, "ClassroomAttendance.apk");

            /* If download complete, InstallReceiver receive the broadcast and start installing and active the app. */

        } else {
            Log4j1Util.i(TAG, "The app is the latest, it remains the same");
        }
    }

    private boolean isMyAppRunning(Context context, String packageName) {
        boolean result = false;
        Log4j1Util.i("isMyAppRunning","start");
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses != null) {
            Log4j1Util.i("isMyAppRunning",packageName + " not running");
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                if (runningAppProcessInfo.processName.equals(packageName)) {
                    int status = runningAppProcessInfo.importance;
                    if (status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                            || status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        result = true;
                        Log4j1Util.i("isMyAppRunning",packageName + " active success  " +runningAppProcessInfo.processName);

                    }
                }
            }
        }

        return result;

    }

    public static boolean isServicesExisted(Context context, String serName) {

        ActivityManager ac = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = ac.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(serName)) {
                return true;
            }
        }
        return false;
    }
    private boolean isAppForeground(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }

        return false;
    }

    /**
     * 把应用置为前台
     */

    private void bring2Front() {
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }


    private  int real_detect;
    private Timer timer2 = new Timer();
    /**
     * 定时任务，定时更新某些信息和清零计数器
     */
    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {

            if (++real_detect == 10) {
                real_detect = 0;
//                isMyAppRunning(getApplicationContext(),"com.emwit.facejoy");
                Log4j1Util.i("实时监测", "run: ");
            }

        }
    };

    private class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return UpdateService.class.getName();
        }

    }
}
