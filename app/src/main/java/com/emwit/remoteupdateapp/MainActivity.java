package com.emwit.remoteupdateapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.emwit.core.CrashHandler;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        Log4j1Util.init();
        Log4j1Util.i("MainActivity", "start remoteupdateapp ");

        //create local version info xml
        File file = new File(GlobalConst.CLASSROOM_APP_REMOTE_UPDATE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        File localXmlFile = new File(GlobalConst.LOCAL_XML_FILE_PATH);
        if (!localXmlFile.exists()) {
            try {
                localXmlFile.createNewFile();
                Util.updateXmlFile(GlobalConst.LOCAL_XML_FILE_PATH, "1.0", "1", "http://47.106.133.29/APK/KQ/ClassroomAttendance.apk", "First Version");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent updateServiceIntent = new Intent(this, UpdateService.class);
        startService(updateServiceIntent);
    }
}
