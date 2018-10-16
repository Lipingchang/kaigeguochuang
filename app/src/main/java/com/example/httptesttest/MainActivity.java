package com.example.httptesttest;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
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

    int ALBUM_REQUEST_CODE = 1;
    int CARMEAR_REQUEST_CODE = 2;
    Button monet,cezanne,ukiyoe, vangogh,album;
    public static String style = "";
    int[]  imagelist = {R.drawable.viewpage1,R.drawable.viewpage2,R.drawable.viewpage3,R.drawable.viewpage4,R.drawable.viewpage5,R.drawable.viewpage6};
    int[] toplist = {R.id.top_1,R.id.top_2,R.id.top_3,R.id.top_4,R.id.top_5,R.id.top_6};

    MyIUiListener uiListener;
    Tencent mTencent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main2);
        act = this;

//        ukiyoe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ukiyoe.setText("设置等待");
//                myImagePager.setLoading(1);
//            }
//        });
//        vangogh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //  更换viewpager中的图片
//                vangogh.setText("改变图片&完成加载");
//                myImagePager.setImage(1,BitmapFactory.decodeResource(getResources(),R.drawable.viewpage666) );
//                myImagePager.setLoaded(1);
//            }
//        });

        // 先把要展示的图片放到 list 中
        List<Bitmap> views = new ArrayList<>();
        for( int i = 0; i<imagelist.length; i++){
             Bitmap bm = BitmapFactory.decodeResource(getResources(),imagelist[i]);
             views.add(bm);
        }
        // 获取设置好的viewPager
        myImagePager = MyImagePager.getPager(this,views,R.id.viewpager);
        viewPager =  myImagePager.viewPager;

        //设置 滚动窗口上面的 滑动条
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                View view = myImagePager.views.get(position);  // @@@ Get target page reference
//                view.bringToFront();
//                System.out.println("2:"+position);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                View view = myImagePager.views.get(position);  // @@@ Get target page reference
//                view.bringToFront();
//                System.out.println(position);
//
//                for( int i = 0; i<toplist.length; i++){
//                    TextView t = (TextView)findViewById(toplist[i]);
//                    t.setBackgroundColor(0xffffffff);
//                }
//                TextView t = (TextView)findViewById(toplist[position]);
//                t.setBackgroundColor(0xff00ff00);
//             }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });



        album = (Button)findViewById(R.id.btn_from_album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPic();
            }
        });
        Button carmea = (Button)findViewById(R.id.btn_from_carema);
        carmea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecPicFromCarema();
            }
        });
        Button test = (Button)findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImgToQQ(Uri.parse("android.resource://com.example.httptesttest/"+R.drawable.viewpage666).toString());
            }
        });
        mTencent = Tencent.createInstance("your APP ID",getApplicationContext());
        uiListener = new MyIUiListener();

    }

    class MyIUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            // 操作成功
            System.out.println("complete");
        }
        @Override
        public void onError(UiError uiError) {
            // 分享异常
            System.out.println("error");

        }
        @Override
        public void onCancel() {
            // 取消分享
            System.out.println("cancel");

        }
    }

    private Bundle params;
    private void shareImgToQQ(String imgUrl) {
        params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);// 设置分享类型为纯图片分享
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imgUrl);// 需要分享的本地图片URL
        // 分享操作要在主线程中完成
        new Handler(act.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mTencent.shareToQQ(act, params, uiListener);
            }
        });
    }

//
//    作者：紫豪
//    链接：https://www.jianshu.com/p/4e2184649545
//    來源：简书
//    简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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
            conventImage();
        }

        Tencent.onActivityResultData(requestCode, resultCode, data, uiListener);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, uiListener);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // 把照片
    public void conventImage(){
        Bitmap bitmap = currentBitmap;

        // 解码图片，转成 jpeg 格式
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,s);
        //writeBitmapToFile("jpg.jpg",bitmap,50);
        byte[] b = s.toByteArray();
        String lastString= Base64.encodeToString(b, Base64.DEFAULT);

        //writeStringToFile("jpg.txt",lastString);
        //System.out.println("base64 size:"+lastString.length() );

        AsynNetUtils.post("http://10.66.4.114:9999",  lastString , new AsynNetUtils.Callback() {
            @Override
            public void onResponse(String response) {
                //textView.setText(response);
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
//---------------------
//    作者：陌天恒
//    来源：CSDN
//    原文：https://blog.csdn.net/QUBUBING/article/details/51040338?utm_source=copy
//    版权声明：本文为博主原创文章，转载请附上博文链接！

}

