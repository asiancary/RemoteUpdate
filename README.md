# 新版教室考勤自动更新 APP

## 功能

- 每两小时下载 http://47.106.133.29/APK/KQ/update.xml，和本地 ClassroomAttendanceVersionInfo.xml 文件比对 versionCode，发现新版本则下载 apk 并安装启动。
- 单进程双服务守护

## 编译使用说明

编译成 debug 或 release 版本后要签名为系统应用

```bash
java -jar signapk.jar platform.x509.pem platform.pk8 source.apk target.apk
```

## Release 签名

jks 文件路径：SignAPK/ReleaseKey.jks

Key store password: 123456

Key alias: key0

Key password: 123456

