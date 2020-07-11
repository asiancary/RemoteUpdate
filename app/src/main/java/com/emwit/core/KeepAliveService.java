package com.emwit.core;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.emwit.remoteupdateapp.Log4j1Util;
import com.emwit.remoteupdateapp.UpdateService;

public class KeepAliveService extends Service {
    private MyBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        //提升Service的优先级
        Notification notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(1, notification);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            try {
                Log4j1Util.i("KeepAliveService", "connected with " + iMyAidlInterface.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            Toast.makeText(KeepAliveService.this, "链接断开，重新启动 UpdateService", Toast.LENGTH_LONG).show();
            startService(new Intent(KeepAliveService.this, UpdateService.class));
            bindService(new Intent(KeepAliveService.this, UpdateService.class), connection, Context.BIND_IMPORTANT);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "KeepAliveService 启动", Toast.LENGTH_LONG).show();
        bindService(new Intent(this, UpdateService.class), connection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }

    private class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return KeepAliveService.class.getName();
        }
    }
}
