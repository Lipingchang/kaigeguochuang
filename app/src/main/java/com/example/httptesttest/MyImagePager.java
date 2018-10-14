package com.example.httptesttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyImagePager {
    static int layoutid = R.layout.viewpager_item;

    public static ViewPager getPager(AppCompatActivity context, List<Bitmap> bitmaplist, int viewpageId) {
        ViewPager viewPager = (ViewPager) context.findViewById(viewpageId);

        final List<View> views = new ArrayList<>();
        for (Bitmap bm : bitmaplist) {
            View layout = context.getLayoutInflater().inflate(layoutid, null, false);
            ImageView iv = (ImageView) layout.findViewById(R.id.image);
            iv.setImageBitmap(bm);
            views.add(iv);
        }
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
                container.addView(views.get(position));
                return views.get(position);
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
        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                TextView t = (TextView) page.findViewById(R.id.textview);
                TextView t2 = (TextView) page.findViewById(R.id.textview2);

                int width = page.getWidth();

                if (position < 0 && position > -1) { // 最左边的view
                    page.setAlpha((float) ((1 + position) * 0.7 + 0.3));
                    page.setScaleX((float) ((1 + position) * 0.7 + 0.3));
                    page.setScaleY((float) ((1 + position) * 0.7 + 0.3));
                } else if (position > 0 && position < 1) { // 中间的view
                    page.setAlpha((float) ((1 - position) * 0.5 + 0.5));
                    page.setScaleX((float) ((1 - position) * 0.5 + 0.5));
                    page.setScaleY((float) ((1 - position) * 0.5 + 0.5));

                } else if (position > 1 && position < 2) { // 右边的view
                    page.setAlpha((float) ((position - 1) * 0.5 + 0.5));
                    page.setScaleX((float) ((position - 1) * 0.5 + 0.5));
                    page.setScaleY((float) ((position - 1) * 0.5 + 0.5));

                }


            }
        });

        return viewPager;
    }

}
