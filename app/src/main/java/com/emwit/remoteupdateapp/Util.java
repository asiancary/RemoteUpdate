package com.emwit.remoteupdateapp;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Util {

    public static int downLoadFile(String fileUrl, String filePath) {
        File file = null;
        try {
            URL url = new URL(fileUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            int fileLength = httpURLConnection.getContentLength();

            URLConnection con = url.openConnection();
            BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
            //String path = storeDir + File.separatorChar + fileFullName;
            file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(file);
            int size = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            while ((size = bin.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
            }
            bin.close();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return GlobalConst.APP_DOWNLOAD_FAILED;
        } catch (IOException e) {
            e.printStackTrace();
            return GlobalConst.APP_DOWNLOAD_FAILED;
        }

        return GlobalConst.OK;
    }

    public static void downloadApk(Context context, String downLoadUrl, String apkSaveName) {

        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(downLoadUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //在通知栏显示下载进度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        //设置保存下载apk保存路径
        String apkPath = GlobalConst.CLASSROOM_APP_REMOTE_UPDATE_DIR + File.separator + apkSaveName;
        File apkFile = new File(apkPath);
        if (apkFile.exists()) {
            apkFile.delete();
        }
        request.setDestinationInExternalPublicDir(".", apkSaveName);

        Context appContext = context.getApplicationContext();
        DownloadManager manager = (DownloadManager) appContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //进入下载队列
        manager.enqueue(request);
    }

    /**
     * if success return contents including version, url, description
     * if failed, return null
     */
    public static String[] parseXmlFile(String filePath) {
        String[] contents = new String[4];

        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(f);
            contents[0] = doc.getElementsByTagName(GlobalConst.VERSION_NODENAME).item(0).getFirstChild().getNodeValue();
            contents[1] = doc.getElementsByTagName(GlobalConst.VERSION_CODE).item(0).getFirstChild().getNodeValue();
            contents[2] = doc.getElementsByTagName(GlobalConst.APP_DOWNLOAD_URL_NODENAME).item(0).getFirstChild().getNodeValue();
            contents[3] = doc.getElementsByTagName(GlobalConst.APP_DISCREPTION_NODENAME).item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }

    /**
     * update local version xml file
     */
    public static int updateXmlFile(String filePath, String version, String versionCode, String url, String description) {

        try {
            // 第一步：创建DOM树
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.newDocument();
            document.setXmlStandalone(true);
            // 第二步：完善树结构
            // 创建节点
            Element infoElement = document.createElement("info");
            Element versionElement = document.createElement("version");
            Element versionCodeElement = document.createElement("versionCode");
            Element urlElement = document.createElement("url");
            Element documentElement = document.createElement("description");

            // 设置节点的属性、内容
            versionElement.setTextContent(version);
            versionCodeElement.setTextContent(versionCode);
            urlElement.setTextContent(url);
            documentElement.setTextContent(description);

            // 将子节点添加到父节点
            infoElement.appendChild(versionElement);
            infoElement.appendChild(versionCodeElement);
            infoElement.appendChild(urlElement);
            infoElement.appendChild(documentElement);
            document.appendChild(infoElement);

            // 第三步：将树结构导入book.xml文件中
            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf = tff.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");// 节点换行
            tf.transform(new DOMSource(document), new StreamResult(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return GlobalConst.OK;
    }
}
