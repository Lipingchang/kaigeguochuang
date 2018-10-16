package com.example.httptesttest;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static Uri savedPhoto;

    public static AppCompatActivity act;
    TextView textView ;
    Button button;

    ViewPager viewPager;
    MyImagePager myImagePager;

    Bitmap currentBitmap;
    ImageView bgview;

    static final int ALBUM_REQUEST_CODE = 1;
    static final int CARMEAR_REQUEST_CODE = 2;
    static final int REQUEST_PERMISSION_CODE = 0;

    Button monet, cezanne, ukiyoe, vangogh, album;
    public static String style = "";
    int[] imagelist = {R.drawable.viewpage1, R.drawable.viewpage2, R.drawable.viewpage3, R.drawable.viewpage4, R.drawable.viewpage5, R.drawable.viewpage6};


    static Tencent mTencent;
    IUiListener qqShareListener;
    String imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        act = this;

        // 先把要展示的图片放到 list 中
        List<Bitmap> views = new ArrayList<>();
        for (int i = 0; i < imagelist.length; i++) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), imagelist[i]);
            views.add(bm);
        }
        // 获取设置好的viewPager
        myImagePager = MyImagePager.getPager(this, views, R.id.viewpager);
        viewPager = myImagePager.viewPager;

        textView = (TextView) findViewById(R.id.textview);
        bgview = (ImageView)findViewById(R.id.bgimageview);
        album = (Button) findViewById(R.id.btn_from_album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.selectPic();
            }
        });
        Button carmea = (Button) findViewById(R.id.btn_from_carema);
        carmea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.selecPicFromCarema();
            }
        });
        Button test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareQQ(imageURI);
                myImagePager.setLoading(1);
            }
        });


        mTencent = Tencent.createInstance("1107906730", getApplicationContext()); // 设置和QQ的分享的实例
        qqShareListener = Util.getQQListener(act); // 设置成功分享后的回调

        Util.getPermission(act);// 收集权限
    }

    //收集权限后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        // TODO 权限没收到
    }


    //发送QQ分享
    public void shareQQ(String uri) {
        final Bundle params = new Bundle();
        params.putString( QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, uri);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "appnamenamename");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00); //0x00
        ThreadManager.getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null != MainActivity.mTencent) {
                    MainActivity.mTencent.shareToQQ(act, params, qqShareListener);
                }
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == Constants.REQUEST_QQ_SHARE){
            Tencent.onActivityResultData(requestCode,resultCode,data,qqShareListener);
        }

        // 提取图片的绝对uri
        String absuri = "";
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();// 根据返回的URI获取对应的SQLite信息
                absuri = Util.getPath(this, uri);
                System.out.println("imageURl:"+imageURI);
            }
        }
        imageURI = absuri;

        // 提取可以看的图片
        Bitmap img = null;
        if( (requestCode == CARMEAR_REQUEST_CODE || requestCode==ALBUM_REQUEST_CODE )&& resultCode==RESULT_OK){  // 从照相机返回
            Uri uri = requestCode==CARMEAR_REQUEST_CODE ? savedPhoto : data.getData();
            int r = Util.readPictureDegree(Util.getPath(act,uri));
            System.out.println("sb not 3:"+r);

            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                img = bitmap;
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(),e);
            }

            img =  Util.rotaingImageView(r,img);


        }else{
            //操作错误或没有选择图片
            Log.d("MainActivity","requestCode:"+requestCode+",resultCode:"+resultCode);
            Log.i("MainActivtiy", "operation error");
        }

        if( img != null ){
            setCurrentImage(img);
            //conventImage();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    // 设置当前要转换的图片，背景是拉升之后毛玻璃话的图片，
    public void setCurrentImage(Bitmap img){
        currentBitmap = img;
        int w,h,img_w,img_h;

        w = bgview.getWidth();  h = bgview.getHeight();
        img_w = img.getWidth();   img_h = img.getHeight();

        float w_bei = (float)(w*1.0/img_w);
        float h_bei = (float)(h*1.0/img_h);
        float bei = w_bei<h_bei ? h_bei : w_bei;

        // 放大
        img =  Util.setImgSize(img,(int)(img_w*bei)+1 , (int)(img_h*bei)+1);
        // 裁剪中间的
        Bitmap center = Bitmap.createBitmap(img,(img.getWidth()-w)/2,(img.getHeight()-h)/2,w,h);
        // 模糊化 放上去
        //Blurry.with(act).radius(30).from(center).into( bgview);

        // 计算viewpager 中的image的大小
        //if( img_w < )
        //更新viewpager
        for( int i = 0; i<imagelist.length; i++){
            myImagePager.setImage(i,currentBitmap);
        }
        System.out.printf("w%d h%d\n",currentBitmap.getWidth(),currentBitmap.getHeight());

    }

    // 把照片 发送出去
    public void conventImage(){
        Bitmap bitmap = currentBitmap;

        // 解码图片，转成 jpeg 格式
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,s);
        byte[] b = s.toByteArray();
        String lastString= Base64.encodeToString(b, Base64.DEFAULT);


        AsynNetUtils.post("http://10.66.4.114:9999",  lastString , new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                // 解析json串
                String images="",style="",msg="",date="",reason="";
                textView.setText("receive ok.");
                try {
                    JsonReader reader = new JsonReader(new StringReader(response));

                    reader.beginObject();
                    while( reader.hasNext() ){
                        String keyname = reader.nextName();

                        if( "image".equals(keyname) ){
                            images = reader.nextString();
                        }else if( "msg".equals(keyname) ){
                            msg = reader.nextString();
                        }else if( "date".equals(keyname) ){
                            date = reader.nextString();
                        }else if( "style".equals(keyname) ){
                            style = reader.nextString();
                        }else if("reason".equals(keyname)){
                            reason = reader.nextString();
                        }

                    }
                    reader.endObject();

                }catch (Exception e){
                    e.printStackTrace();
                }


                //textView.setText(images);

                // 把json串中的 image转成bitmap 然后展示
                Bitmap bt = Util.base642Bitmap(images);
//                image.setImageBitmap(bt);

            }

        });
    }



}

