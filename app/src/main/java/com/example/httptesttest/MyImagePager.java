package com.example.httptesttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyImagePager  {
    private ViewPager viewPager;


    LayoutInflater layi = getLayoutInflater();
        for( int i = 0; i<imagelist.length; i++){
        View v = layi.inflate(R.layout.viewpager_item,null,false);
        ImageView image = (ImageView)v.findViewById(R.id.image);
        Bitmap bm = BitmapFactory.decodeResource(getResources(),imagelist[i]);
        image.setImageBitmap(bm);
        TextView tv = (TextView)v.findViewById(R.id.textview);
        tv.setText("<"+i+">");

        views.add(v);

    }
    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    };


    private MyImagePager(Context context, List<Bitmap> bitmaplist,int viewpageId){
        super(context);
    }
    public getPager(Context context, List<Bitmap> bitmaplist,int viewpageId){

    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public void transformPage(View page, float position) {

    }
}
