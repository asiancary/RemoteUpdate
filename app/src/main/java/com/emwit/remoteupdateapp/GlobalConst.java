package com.emwit.remoteupdateapp;

import android.os.Environment;

import java.io.File;

public class GlobalConst {
    public static final String VERSION_NODENAME = "version";
    public static final String VERSION_CODE = "versionCode";
    public static final String APP_DOWNLOAD_URL_NODENAME = "url";
    public static final String APP_DISCREPTION_NODENAME = "description";

    public static final String UPDATE_XML_URL = "https://emwit.com/APK/KQ/update.xml";

    private static String SDCARD_DIR = Environment.getExternalStorageDirectory().getPath();   // /sdcard
    //    private static String SDCARD_DIR = "/sdcard/";
    public static final String CLASSROOM_APP_REMOTE_UPDATE_DIR = SDCARD_DIR;
    //    public static final String UPDATE_DIR = "/sdcard/remoteupdateapp";
    public static final String UPDATE_XML_FILE_PATH = CLASSROOM_APP_REMOTE_UPDATE_DIR + File.separator + "update.xml";
    public static final String LOCAL_XML_FILE_PATH = CLASSROOM_APP_REMOTE_UPDATE_DIR + File.separator + "ClassroomAttendanceVersionInfo.xml";
    public static final String DOWNLOAD_APP_PATH = CLASSROOM_APP_REMOTE_UPDATE_DIR + File.separator + "ClassroomAttendance.apk";

    public static final String ROOM_ATTENDANCE_PKG_NAME = "com.emwit.ClassroomAttendance";

    // error code
    public static final int OK = 100;
    public static final int XML_FILE_NOT_EXIST = 101;
    public static final int APP_FILE_NOT_EXIST = 102;
    public static final int APP_DOWNLOAD_FAILED = 103;
    public static final int SHELL_COMMMAND_FAILED = 104;
    public static final int APP_INSTALL_FAIL = 105;
}
