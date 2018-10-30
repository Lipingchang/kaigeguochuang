package com.example.httptesttest;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Base64;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;


import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import com.example.httptesttest.myutil.*;

public class MainActivity extends AppCompatActivity {

    // 待初始化的变量
    public Button album_btn,camera_btn;
    ImageView bg_view;
    ViewPager viewPager;
    MyImagePager myImagePager;
    static Tencent mTencent;
    static IUiListener qqShareListener;
    public static AppCompatActivity act;


    //全局分享的??
    public static Uri savedPhoto; // 相机照片缓存文件
    public static Uri currentPhotoUri; // 要发送给后台的照片的uri

    Bitmap currentBitmap;
    public static String style = "";
    String imageURI;


    int[] imagelist = {R.drawable.viewpage1, R.drawable.viewpage2, R.drawable.viewpage3, R.drawable.viewpage4, R.drawable.viewpage5, R.drawable.viewpage6};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        act = this;

        InitViews();

        // 设置QQ分享
        mTencent = Tencent.createInstance("1107906730", getApplicationContext()); // 设置和QQ的分享的实例
        qqShareListener = Util.getQQListener(act); // 设置成功分享后的回调

        Util.getPermission(act);// 收集权限
    }


    //收集权限后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Util.REQUEST_PERMISSION_CODE: {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == Constants.REQUEST_QQ_SHARE){
            Tencent.onActivityResultData(requestCode,resultCode,data,qqShareListener);
        }

        // 提取图片的绝对uri 提取图片到bitmap
        Bitmap img = null;
        if( (requestCode == Util.CAMERA_REQUEST_CODE || requestCode==Util.ALBUM_REQUEST_CODE )&& resultCode==RESULT_OK){
            // 照相机返回的照片已经保存在savedPhoto上了,从相册返回的照片的信息还需要从data中获取
            currentPhotoUri= requestCode==Util.CAMERA_REQUEST_CODE ? savedPhoto : data.getData();
            img = Util.getCompassImage(act,currentPhotoUri);
        }

        if( img != null ){
            setCurrentImage(img);
            //conventImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public  void setBlurryBackground(Bitmap img){
        int w,h,img_w,img_h;

        w = bg_view.getWidth();  h = bg_view.getHeight();
        img_w = img.getWidth();   img_h = img.getHeight();

        float w_bei = (float)(w*1.0/img_w);
        float h_bei = (float)(h*1.0/img_h);
        float bei = w_bei<h_bei ? h_bei : w_bei;

        // 放大
        img =  Util.setImgSize(img,(int)(img_w*bei)+1 , (int)(img_h*bei)+1);
        // 裁剪中间的
        Bitmap center = Bitmap.createBitmap(img,(img.getWidth()-w)/2,(img.getHeight()-h)/2,w,h);
        // 模糊化 放上去
        jp.wasabeef.blurry.Blurry.with(act).radius(30).from(center).into( bg_view );
        // TODO 把状态栏的颜色调整成和 图片主色调一样
    }

    // 设置当前要转换的图片，背景是拉升之后毛玻璃话的图片，
    public void setCurrentImage(Bitmap img){
        currentBitmap = img;

        setBlurryBackground(img);
        //更新viewpager 并且设置在载入中
        for( int i = 0; i<imagelist.length; i++){
            myImagePager.setImage(i,currentBitmap);
            Glide.with(act)
                    .load(currentBitmap)
                    .apply(
                            RequestOptions.bitmapTransform(
                                    new RoundedCornersTransformation(
                                            (int)(currentBitmap.getWidth()*0.08),
                                            0,
                                            RoundedCornersTransformation.CornerType.ALL)
                            ))
                    .into(myImagePager.getImageView(i));
            myImagePager.setLoading(i);
        }
    }

    // 把照片 发送出去 !!!!!!!!未改动
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
//                textView.setText("receive ok.");
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

    void InitViews(){
        Resources resources = this.getResources();


        // 先把要展示的图片放到 views 中
        List<Bitmap> views = new ArrayList<>();
        for (int i = 0; i < imagelist.length; i++) {
            Bitmap bm = Util.getCompassImage(this,
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(imagelist[i] ) + '/' + resources.getResourceTypeName(imagelist[i]) + '/' + resources.getResourceEntryName(imagelist[i]) )
            );//BitmapFactory.decodeResource(getResources(), imagelist[i]);
            views.add(bm);
        }
        // 获取设置好的viewPager
        myImagePager = MyImagePager.getPager(this, views, R.id.viewpager);
        viewPager = myImagePager.viewPager;

        // 设置两个按钮
        album_btn = (Button) findViewById(R.id.btn_from_album);
        album_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.selectPic();
            }
        });
        camera_btn = (Button) findViewById(R.id.btn_from_carema);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.selecPicFromCarema();
            }
        });

        // 设置背景
        bg_view = (ImageView)findViewById(R.id.bgimageview);
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.welcome) + '/' + resources.getResourceTypeName(R.drawable.welcome) + '/' + resources.getResourceEntryName(R.drawable.welcome) );
        Bitmap center = Util.getCompassImage(this, uri);
        jp.wasabeef.blurry.Blurry.with(act).radius(10).from(center).into( bg_view );

        Palette.Builder builder = Palette.from(center);
        final MainActivity that = this;
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override public void onGenerated(Palette palette) {
                //获取到充满活力的这种色调
                Palette.Swatch vibrant = palette.getDominantSwatch();
                Palette.Swatch color2 = palette.getDarkMutedSwatch();
               // Palette.Swatch color3 = palette.getLightVibrantColor(  );
//                Util.setStatusBarColor(act,vibrant.getRgb());
                Util.changeTheme(that,color2.getRgb());

            }
        });

    }


//
//    作者：imflyn
//    链接：https://www.jianshu.com/p/932568ed31af
//    來源：简书
//    简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。



}

