package com.example.httptesttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MyImagePager {
    static int layoutid = R.layout.viewpager_item;

    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    List<Bitmap> bms;
    List<View> views;


    public void setData(ViewPager viewPager, PagerAdapter pagerAdapter, List<Bitmap> bms, List<View> views) {
        this.viewPager = viewPager;
        this.pagerAdapter = pagerAdapter;
        this.bms = bms;
        this.views = views;
    }

    public MyImagePager(){

    }

    // 把 第 index 个的图片替换掉
    public void setImage(int index,Bitmap bitmap){
        if( index<0 || index>=views.size() ){
            return;
        }

        bms.set(index,bitmap);
        View v = views.get(index);
        ((ImageView)v.findViewById(R.id.image)).setImageBitmap(bitmap);
        pagerAdapter.notifyDataSetChanged();
    }
    public ImageView getImageView(int index){
        if( index<0 || index>=views.size() ){
            return null;
        }
        return (ImageView)(views.get(index).findViewById(R.id.image));
    }

    // 把第 index 设置成正在加载
    public void setLoading(int index){
        if( index<0 || index>=views.size() ){
            return;
        }

        View v = views.get(index);
        View layout_view = (LinearLayout) v.findViewById(R.id.loadinglayout);
        ImageView im = (ImageView)v.findViewById(R.id.image);

        Bitmap black_bm =  bms.get(index);
        RequestOptions options = new RequestOptions()
                .transforms(new BlurTransformation(10), new GrayscaleTransformation(),new RoundedCornersTransformation((int)(black_bm.getWidth()*0.08), 0,RoundedCornersTransformation.CornerType.ALL));
        Glide.with(MainActivity.act)
                .load( black_bm )
                .apply(  options )
                .into( im );


        //im.setImageBitmap(  black_bm );

        layout_view.setVisibility(View.VISIBLE);

        // 设置等待层 的图片一样大小
//        int imgviewwidth = im.getWidth();
//        int black_bmwidth = black_bm.getWidth();
//        int imagewidth = black_bmwidth > imgviewwidth ? imgviewwidth : black_bmwidth;
//        int imageviewheight = (int)( black_bm.getHeight() * (imagewidth*1.0 / (black_bmwidth*1.0)) );
//
//
//        ViewGroup.LayoutParams imgparams = im.getLayoutParams();
//        imgparams.height =  imageviewheight;
//        imgparams.width = imagewidth;
//        layout_view.setLayoutParams(imgparams);


    }
    public void setLoaded(int index){
        if( index<0 || index>=views.size() ){
            return;
        }

        View v = views.get(index);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.loadinglayout);
        // 把图片的灰度设置回去
        ImageView im = (ImageView)v.findViewById(R.id.image);
        //im.setImageBitmap(  bms.get(index) );

        Glide.with(MainActivity.act)
                .load(bms.get(index))
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation((int)(bms.get(index).getWidth()*0.08), 0,RoundedCornersTransformation.CornerType.ALL)))
                .into(im );

        layout.setVisibility(View.INVISIBLE);
    }

    public static Bitmap getGrayBitmap(Bitmap bm) {
        Bitmap bitmap = null;
        //获取图片的宽和高
        int width = bm.getWidth();
        int height = bm.getHeight();
        //创建灰度图片
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //创建画布
        Canvas canvas = new Canvas(bitmap);
        //创建画笔
        Paint paint = new Paint();
        //创建颜色矩阵
        ColorMatrix matrix = new ColorMatrix();
        //设置颜色矩阵的饱和度:0代表灰色,1表示原图
        matrix.setSaturation(0);
        //颜色过滤器
        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(matrix);
        //设置画笔颜色过滤器
        paint.setColorFilter(cmcf);
        //画图
        canvas.drawBitmap(bm, 0, 0, paint);
        return bitmap;

//        ---------------------
//                作者：xuwenneng
//        来源：CSDN
//        原文：https://blog.csdn.net/xuwenneng/article/details/52634979?utm_source=copy
//        版权声明：本文为博主原创文章，转载请附上博文链接！
    }





    public static MyImagePager getPager(AppCompatActivity context, List<Bitmap> bitmaplist, int viewpageId) {
        final ViewPager viewPager = (ViewPager) context.findViewById(viewpageId);
        final MyImagePager m = new MyImagePager();

        GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            long lastclick = System.currentTimeMillis();
            @Override public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(MainActivity.act, "双击666", Toast.LENGTH_SHORT).show();
                return super.onDoubleTap(e);
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                long t = System.currentTimeMillis();

                // 左右滑动：
                if( Math.abs(e1.getX()-e2.getX())>200 )
                    return super.onFling(e1, e2, velocityX, velocityY);

                if( t-lastclick <= 7000 ){
                    System.out.println("fuck");
                    TastyToast.makeText(MainActivity.act.getApplicationContext(), "Proccessing..", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
                lastclick = t;
                System.out.println("save");

                if( (e1.getY() - e2.getY())> 220  ){
//                    Toast.makeText(MainActivity.act, "上划", Toast.LENGTH_SHORT).show();
                    // 上划分享
                    int itemNumber = viewPager.getCurrentItem();
                    Uri u = Util.saveImageToGallery(MainActivity.act,m.bms.get(itemNumber));
                    Util.shareQQ( Util.getPath(MainActivity.act,u),MainActivity.qqShareListener);
                }else if( (e1.getY() - e2.getY())< -220 ){
//                    Toast.makeText(MainActivity.act, "下划", Toast.LENGTH_SHORT).show();
                    // 下滑 保存
                    //System.out.println("down:"+velocityX + " " +velocityY);
                    int itemNumber = viewPager.getCurrentItem();
                    MediaStore.Images.Media.insertImage(MainActivity.act.getContentResolver(), m.bms.get(itemNumber), "title", "description");
                    //SnackbarUtil.LongSnackbar( m.views.get(itemNumber),"保存ing",SnackbarUtil.blue,SnackbarUtil.green).show();
                    TastyToast.makeText(MainActivity.act.getApplicationContext(), "Saving..", TastyToast.LENGTH_LONG, TastyToast.INFO);

                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

        };
        final GestureDetector detector = new GestureDetector(MainActivity.act, listener);

        // 初始化view
        final List<View> views = new ArrayList<>();
        LayoutInflater inflater = context.getLayoutInflater();
        for (Bitmap bm : bitmaplist) {
            View v = inflater.inflate(layoutid, null, false);
            ImageView iv = (ImageView) v.findViewById(R.id.image);
            iv.setImageBitmap(bm);
            views.add(v);
        }

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });

        // 设置适配器
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = views.get(position);

                container.addView(v);
                return v;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }
        };
        viewPager.setAdapter(pagerAdapter);

        // 设置可以看到 不是main的item
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPageMargin(-240);

        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {

                if (position < 0 && position > -1) { // 最左边的view
                   // page.setAlpha((float) ((1 + position) * 0.7 + 0.3));
                    page.setScaleX((float) ((1 + position) * 0.7 + 0.3));
                    page.setScaleY((float) ((1 + position) * 0.7 + 0.3));

                } else if (position > 1 && position < 2) { // 右边的view
                    //page.setAlpha((float) ((position - 1) * 0.5 + 0.5));
                    page.setScaleX((float) ((position - 1) * 0.5 + 0.5));
                    page.setScaleY((float) ((position - 1) * 0.5 + 0.5));
                }
                else if (position > 0 && position < 1) { // 中间的view
                 //   page.setAlpha((float) ((1 - position) * 0.5 + 0.5));
                    page.setScaleX((float) ((1 - position) * 0.5 + 0.5));
                    page.setScaleY((float) ((1 - position) * 0.5 + 0.5));

                }else if( position==0 ){

                }

            }
        });
        // 初始化第二个item的大小
        viewPager.setCurrentItem(0);
        //views.get(1).setAlpha((float)0.5);
        views.get(1).setScaleX((float)0.5);
        views.get(1).setScaleY((float)0.5);

        m.setData(viewPager,pagerAdapter,bitmaplist,views);


        return m;
    }

}
