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

    public static AppCompatActivity act;
    TextView textView ;
    Button button;

    ViewPager viewPager;
    MyImagePager myImagePager;

    Bitmap currentBitmap;

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

        album = (Button) findViewById(R.id.btn_from_album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPic();
            }
        });
        Button carmea = (Button) findViewById(R.id.btn_from_carema);
        carmea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecPicFromCarema();
            }
        });
        Button test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQQ(imageURI);
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
        if( requestCode == CARMEAR_REQUEST_CODE && resultCode==RESULT_OK){  // 从照相机返回
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            //image.setImageBitmap(bitmap);
            img = bitmap;

        }else if( requestCode == ALBUM_REQUEST_CODE && resultCode == RESULT_OK ) {// 从相册返回
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                 Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                 img = bitmap;
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }else{
            //操作错误或没有选择图片
            Log.d("MainActivity","requestCode:"+requestCode+",resultCode:"+resultCode+",data:"+data.toString());
            Log.i("MainActivtiy", "operation error");
        }

        if( img != null ){
            currentBitmap = img;
            //conventImage();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    // 把照片??
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
                Bitmap bt = base642Bitmap(images);
//                image.setImageBitmap(bt);

            }

        });
    }
    public void setAllBtnToCommon(){
        monet.setBackground(getResources().getDrawable (  R.drawable.common, null ) );
        cezanne.setBackground(getResources().getDrawable (  R.drawable.common, null ) );
        ukiyoe.setBackground(getResources().getDrawable (  R.drawable.common, null ) );
        vangogh.setBackground(getResources().getDrawable (  R.drawable.common, null ) );
    }
    public static void writeBitmapToFile(String filePath, Bitmap b, int quality) {
        try {
            File desFile = new File(filePath);
            FileOutputStream fos = act.openFileOutput(filePath,Context.MODE_PRIVATE);


            BufferedOutputStream bos = new BufferedOutputStream(fos);
            b.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeStringToFile(String filePath, String b) {
        try {
            File desFile = new File(filePath);
            FileOutputStream fos = act.openFileOutput(filePath,Context.MODE_PRIVATE);

            Writer writer = new OutputStreamWriter(fos);

            writer.write(b.toCharArray());

            writer.flush();
            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap base642Bitmap(String base){
        byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    public static Bitmap getThumb(Bitmap bm,int w,int h){
        Bitmap b = Bitmap.createBitmap(bm);
         bm.setHeight(h);
        bm.setWidth(w);
        return b;
    }
    //打开相机选择图片
    private void selecPicFromCarema(){
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it,CARMEAR_REQUEST_CODE);
    }
    //打开本地相册选择图片
    private void selectPic(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }


}

