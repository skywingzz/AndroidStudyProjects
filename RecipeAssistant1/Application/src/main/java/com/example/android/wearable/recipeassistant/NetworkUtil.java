package com.example.android.wearable.recipeassistant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/////////////////// jes1 ////////////////////////////
public class NetworkUtil {

    ////////////////////////// v6? v4? ////////////////////
    public static String V6URL="http://[2001:2b8:51:2099:51fb:4f87:9bb9:b44a]:8080/CookCook_IoT_Service";
    public static String V4URL="http://192.168.0.107:8080/CookCook_IoT_Service";
    public static String ServerURL;
    static{

        try {
            // Create the connection where we're going to send the file.
            URL url = new URL(V6URL);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;

            // Set the appropriate HTTP parameters.
            httpConn.setRequestMethod("GET");
            httpConn.setDoInput(true);

            // Read the response and write it to standard out.
            int resCode = httpConn.getResponseCode();
            Log.d("jes", "웹응답:" + resCode);
            if (resCode == HttpURLConnection.HTTP_OK) {
                ServerURL=V6URL;
            }
        }catch(Exception e){
            ServerURL=V4URL;
        }
    }
///////////////////////////////////////////////////////////////////////////

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }


    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    ///////////////////////////////////////////////////





    //////////////////// jes5 /////////////////////////////////
    public static List<Item> sendRequestSoapPost(String SOAPUrl,Context mContext,MyParser myParser) throws Exception{

        OutputStream out=null;
        InputStream in=null;

        try {

            String xmlFile2Send = "soapRequest.xml";
            String SOAPAction = "";

            // Create the connection where we're going to send the file.
            URL url = new URL(SOAPUrl);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;


            byte[] b =AssetUtils.loadAsset(mContext,xmlFile2Send);

            // Set the appropriate HTTP parameters.
            httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestProperty("SOAPAction", SOAPAction);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            // Everything's set up; send the XML that was read in to b.
            out = httpConn.getOutputStream();
            out.write(b);


            // Read the response and write it to standard out.
            int resCode = httpConn.getResponseCode();
            Log.d("jes", "웹응답:" + resCode);
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();

                return (List<Item>) myParser.parse(in,mContext);

            }
            return null;
        }finally {
            if(out !=null) out.close();
            if(in !=null) in.close();
        }
    }




    //////////////////// jes9 /////////////////////////////////
    public static List<? extends Object> sendRequestGet(String urlStr,MyParser myParser) throws Exception{
        InputStream in=null;

        try {

            // Create the connection where we're going to send the file.
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;

            // Set the appropriate HTTP parameters.
            httpConn.setRequestMethod("GET");
            httpConn.setDoInput(true);

            // Read the response and write it to standard out.
            int resCode = httpConn.getResponseCode();
            Log.d("jes", "웹응답:" + resCode);
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
                return myParser.parse(in);

            }
            return null;
        }finally {
            if(in !=null) in.close();
        }
    }
///////////////////////////////////////////////////////////////////////////


    public static BufferedInputStream sendRequestGetImg(String imgUrl) throws Exception{
        URL Url = new URL(imgUrl);
        // 웹사이트에 접속 설정
        URLConnection urlcon = Url.openConnection();
        // 연결하시오
        urlcon.connect();
        // 이미지 길이 불러옴
        int imagelength = urlcon.getContentLength();
        // 스트림 클래스를 이용하여 이미지를 불러옴
        BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream(), imagelength);
        // 스트림을 통하여 저장된 이미지를 이미지 객체에 넣어줌
        return bis;
    }




}
