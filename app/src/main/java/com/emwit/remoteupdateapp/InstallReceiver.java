package com.emwit.remoteupdateapp;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class InstallReceiver extends BroadcastReceiver {
    private static final String TAG = "InstallReceiver";

    // 安装下载接收器
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            Log4j1Util.i(TAG, "Download complete, start installing.");
            int resultCode = runShellCommand("pm install -r " + GlobalConst.DOWNLOAD_APP_PATH);
            if (resultCode != GlobalConst.OK) {
                Log4j1Util.i(TAG, "shell command install failed, try method 2");
                if (GlobalConst.OK != installApk(context, GlobalConst.DOWNLOAD_APP_PATH)) {
                    return;
                } else {
                    Log4j1Util.i(TAG, "Install apk success through method 2");
                }
            } else {
                Log4j1Util.i(TAG, "Install apk success through method 1");
            }
            Log4j1Util.i(TAG, "Start the app.");
            startApp(context, GlobalConst.ROOM_ATTENDANCE_PKG_NAME);
            Log4j1Util.i(TAG, "Update local xml file " + UpdateService.remoteVersionName + " "
                    + UpdateService.remoteVersionCode + " " + UpdateService.remoteAppDownloadUrl + " " + UpdateService.remoteDiscription);
            Util.updateXmlFile(GlobalConst.LOCAL_XML_FILE_PATH, UpdateService.remoteVersionName,
                    UpdateService.remoteVersionCode, UpdateService.remoteAppDownloadUrl, UpdateService.remoteDiscription);
            File apkFile = new File(GlobalConst.DOWNLOAD_APP_PATH);
            if (apkFile.exists()) {
                apkFile.delete();
            }
        }
    }

    // 安装Apk
    private int installApk(Context context, String appfilePath) {
        File apkfile = new File(appfilePath);
        if (!apkfile.exists()) {
            return GlobalConst.APP_FILE_NOT_EXIST;
        }
        // 通过Intent安装APK文件
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            return GlobalConst.APP_INSTALL_FAIL;
        }
        return GlobalConst.OK;
    }

    private int runShellCommand(String command) {
        Process process = null;
        BufferedReader bufferedReader = null;
        StringBuilder mShellCommandSB = new StringBuilder();
        Log4j1Util.i(TAG, "runShellCommand :" + command);
        mShellCommandSB.delete(0, mShellCommandSB.length());
        String[] cmd = new String[]{"/system/bin/sh", "-c", command};
        //调用bin文件
        try {
            byte b[] = new byte[1024];
            process = Runtime.getRuntime().exec(cmd);
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                mShellCommandSB.append(line);
            }
            Log4j1Util.i(TAG, "runShellCommand result : " + mShellCommandSB.toString());
            if (!mShellCommandSB.toString().equals("Success")) {
                return GlobalConst.SHELL_COMMMAND_FAILED;
            }
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            return GlobalConst.SHELL_COMMMAND_FAILED;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return GlobalConst.SHELL_COMMMAND_FAILED;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    return GlobalConst.SHELL_COMMMAND_FAILED;
                    // TODO: handle exception
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return GlobalConst.OK;
    }

    private void startApp(Context context, String pkgName) {
        //设置包名称　和　amin activity的名称
        ComponentName componentName = new ComponentName("com.emwit.ClassroomAttendance", "com.emwit.ClassroomAttendance.MainActivity");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.putExtra("type", "110");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
