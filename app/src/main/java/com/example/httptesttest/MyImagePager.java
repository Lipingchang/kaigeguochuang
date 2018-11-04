package com.example.httptesttest;

import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.httptesttest.MainActivity.act;

public class MyImagePager {
    // viewpager 中每个view的 布局文件.
    static int viewpager_item_layout = R.layout.viewpager_item;

    // 本类管理的viewpager
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

        RequestOptions options = new RequestOptions()
                .transforms(
                        new GlideCircleBorderTransform(30,(int)(bitmap.getWidth()*0.08),0xffffaaff),
                        new RoundedCornersTransformation((int)(bitmap.getWidth()*0.08), 0,RoundedCornersTransformation.CornerType.ALL)

                        );

        Glide.with(act)
                .load(bitmap)
                .apply(options)
                .into((ImageView)views.get(index).findViewById(R.id.image));

        bms.set(index,bitmap);
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
                .transforms(
                        new BlurTransformation(10),
                        new GrayscaleTransformation(),
                        new GlideCircleBorderTransform(30,(int)(black_bm.getWidth()*0.08),0xffffaaff),
                        new RoundedCornersTransformation((int)(black_bm.getWidth()*0.08), 0,RoundedCornersTransformation.CornerType.ALL));

        Glide.with(act)
                .load( black_bm )
                .apply(  options )
                .into( im );

        layout_view.setVisibility(View.VISIBLE);


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

        Glide.with(act)
                .load(bms.get(index))
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation((int)(bms.get(index).getWidth()*0.08), 0,RoundedCornersTransformation.CornerType.ALL)))
                .into(im );

        layout.setVisibility(View.INVISIBLE);
    }



    public static MyImagePager getPager(AppCompatActivity context, List<Bitmap> bitmaplist, int viewpageId) {
        final ViewPager viewPager = (ViewPager) context.findViewById(viewpageId);
        final MyImagePager m = new MyImagePager();

//        GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
//            long lastclick = System.currentTimeMillis();
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                long t = System.currentTimeMillis();
//
//                // 左右滑动：
//                if( Math.abs(e1.getX()-e2.getX())>200 )
//                    return super.onFling(e1, e2, velocityX, velocityY);
//
//                if( t-lastclick <= 7000 ){
//                    System.out.println("fuck");
//                    TastyToast.makeText(act.getApplicationContext(), "Proccessing..", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
//                    return super.onFling(e1, e2, velocityX, velocityY);
//                }
//                lastclick = t;
//                System.out.println("save");
//
//                if( (e1.getY() - e2.getY())> 220  ){
//                    // 上划分享
////                    int itemNumber = viewPager.getCurrentItem();
////                    Uri u = Util.saveImageToGallery(act,m.bms.get(itemNumber));
////                    Util.shareQQ( Util.getPath(act,u),MainActivity.qqShareListener);
//                }else if( (e1.getY() - e2.getY())< -220 ){
//                    // 下滑 保存
//                    //System.out.println("down:"+velocityX + " " +velocityY);
//                    int itemNumber = viewPager.getCurrentItem();
//                    MediaStore.Images.Media.insertImage(act.getContentResolver(), m.bms.get(itemNumber), "title", "description");
//                    TastyToast.makeText(act.getApplicationContext(), "Saving..", TastyToast.LENGTH_LONG, TastyToast.INFO);
//
//                }
//                return super.onFling(e1, e2, velocityX, velocityY);
//            }
//
//        };
//        final GestureDetector detector = new GestureDetector(act, listener);

        // 初始化view
        final List<View> views = new ArrayList<>();
        LayoutInflater inflater = context.getLayoutInflater();
        for (Bitmap bm : bitmaplist) {
            View v = inflater.inflate(viewpager_item_layout, null, false);
            //ImageView iv = (ImageView) v.findViewById(R.id.image);
            //iv.setImageBitmap(bm);
            views.add(v);
        }

//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override public boolean onTouch(View v, MotionEvent event) {
//                return detector.onTouchEvent(event);
//            }
//        });

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

        for( int i =0;i<bitmaplist.size(); i++){
            m.setImage(i,bitmaplist.get(i));
        }
        return m;
    }

}
