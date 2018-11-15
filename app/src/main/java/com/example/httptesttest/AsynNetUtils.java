package com.example.httptesttest;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsynNetUtils {
    public interface Callback{
        void onResponse(String response);
    }

    public static void get(final String url, final Callback callback){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetUtils.get(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void post(final String url, final String content, final String inputStyle, final Callback callback){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {

                String k =""; // 图片的base64串

                try {
                    //MainActivity.writeStringToFile("content1.txt",content);

                    // 把 图片里面的回车 去掉
                    Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                    Matcher m = p.matcher(content);
                    String dest;
                    dest = m.replaceAll("");

                    k = "{\"style\":\""+ inputStyle + "\",\"image\":\"" + dest + "\",\"date\":\"2018-10-11\" }";

                    //MainActivity.writeStringToFile("content2.txt",dest);

                }catch (Exception e){
                    e.printStackTrace();
                }

                //System.out.println("senddata:"+k);

                final String response = NetUtils.post(url,k);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }
}