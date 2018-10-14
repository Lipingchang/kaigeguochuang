package com.example.httptesttest;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static AppCompatActivity act;
    TextView textView ;
    Button button;
    ImageView image;
    ViewPager viewPager;

    Button monet,cezanne,ukiyoe,vangogh;
    public static String style = "";
    int[]  imagelist = {R.drawable.viewpage1,R.drawable.viewpage2,R.drawable.viewpage3,R.drawable.viewpage4,R.drawable.viewpage5,R.drawable.viewpage6};
    int[] toplist = {R.id.top_1,R.id.top_2,R.id.top_3,R.id.top_4,R.id.top_5,R.id.top_6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.show);
        image = (ImageView) findViewById(R.id.image);


        monet = (Button)findViewById(R.id.monet);
        cezanne =(Button) findViewById(R.id.cezanne);
        ukiyoe = (Button)findViewById(R.id.ukiyoe);
        vangogh = (Button)findViewById(R.id.vangogh);
        act = this;

        monet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style = "monet";
                setAllBtnToCommon();
                monet.setBackground(getResources().getDrawable (  R.drawable.common_pressed, null ));
                selectPic();
            }
        });
        cezanne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style = "cezanne";

                setAllBtnToCommon();
                cezanne.setBackground(getResources().getDrawable (  R.drawable.common_pressed, null ));
                selectPic();

            }
        });
        ukiyoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style = "ukiyoe";
                setAllBtnToCommon();
                ukiyoe.setBackground(getResources().getDrawable (  R.drawable.common_pressed, null ));
                selectPic();
            }
        });
        vangogh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style = "vangogh";
                setAllBtnToCommon();
                vangogh.setBackground(getResources().getDrawable (  R.drawable.common_pressed, null ));
                selectPic();
            }
        });

        // 先把要展示的图片放到 list 中
        List<Bitmap> views = new ArrayList<>();
        for( int i = 0; i<imagelist.length; i++){
             Bitmap bm = BitmapFactory.decodeResource(getResources(),imagelist[i]);
             views.add(bm);
        }
        // 获取设置好的viewPager
        viewPager =  MyImagePager.getPager(this,views,R.id.viewpager);




//         设置 滚动窗口上面的 滑动条
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for( int i = 0; i<toplist.length; i++){
                    TextView t = (TextView)findViewById(toplist[i]);
                    t.setBackgroundColor(0xffffffff);
                }
                TextView t = (TextView)findViewById(toplist[position]);
                t.setBackgroundColor(0xff00ff00);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });





    }



    /**
     * 打开本地相册选择图片
     */
    private void selectPic(){
        //intent可以应用于广播和发起意图，其中属性有：ComponentName,action,data等
        Intent intent=new Intent();
        intent.setType("image/*");
        //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
        //类型的内容给你选择
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //如果第二个参数大于或等于0，那么当用户操作完成后会返回到本程序的onActivityResult方法
        startActivityForResult(intent, 1);
    }
    /**
     *把用户选择的图片显示在imageview中 然后发送请求，把返回的图片再次显示再imageview中
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if(resultCode==RESULT_OK){
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //获取图片
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                // 解码图片，转成 jpeg 格式
                ByteArrayOutputStream s = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,s);
                //writeBitmapToFile("jpg.jpg",bitmap,50);
                byte[] b = s.toByteArray();
                String lastString= Base64.encodeToString(b, Base64.DEFAULT);

                //writeStringToFile("jpg.txt",lastString);
                //System.out.println("base64 size:"+lastString.length() );

                // 展示图片
                image.setImageBitmap(bitmap);
                textView.setText("data transporting..." +
                        "\nplease wait");
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
                        image.setImageBitmap(bt);

                    }

                });
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }else{
            //操作错误或没有选择图片
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
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
//---------------------
//    作者：陌天恒
//    来源：CSDN
//    原文：https://blog.csdn.net/QUBUBING/article/details/51040338?utm_source=copy
//    版权声明：本文为博主原创文章，转载请附上博文链接！

}

