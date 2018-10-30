package com.example.httptesttest.testField;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.httptesttest.R;
import com.example.httptesttest.myutil.Util;

public class ImageColorTest extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    ImageView imageView;
    Bitmap bitmap;
    int image_id = R.drawable.carmera_button_grey;
    private SeekBar sb_hue;

    EditText r,g,b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_color_test);
        imageView = (ImageView) findViewById(R.id.image);
        Resources resources = this.getResources();

        bitmap =  Util.getCompassImage(this,
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(image_id ) + '/' + resources.getResourceTypeName(image_id) + '/' + resources.getResourceEntryName( image_id) )
        );

        imageView.setImageBitmap(bitmap);

        sb_hue = (SeekBar)findViewById(R.id.sb_hue);
        sb_hue.setMax(MAX_VALUE);
        sb_hue.setProgress(MID_VALUE);

        r = (EditText)findViewById(R.id.r);
        g = (EditText)findViewById(R.id.g);
        b = (EditText) findViewById(R.id.b);

        r.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setColor();
            }
        });
        g.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setColor();
            }
        });
        b.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setColor();
            }
        });



       // sb_hue.setOnSeekBarChangeListener(this);

    }
    private final static int MAX_VALUE = 255;
    private final static int MID_VALUE = 127;
    private float mHue = 0.0f;
    private float mStauration = 1.0f;
    private float mLum = 1.0f;

    void setColor(){
        int color;
        try {
            color = Color.rgb(Integer.parseInt(r.getText().toString()), Integer.parseInt(g.getText().toString()), Integer.parseInt(b.getText().toString()));
        }catch (Exception e){
            return;
        }
        Drawable wrappedDrawable = DrawableCompat.wrap(this.getDrawable(R.drawable.carmera_button_grey));

        DrawableCompat.setTint(wrappedDrawable, color);
        imageView.setImageDrawable(wrappedDrawable);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_hue:
                mHue = (progress - MID_VALUE) * 1.0F / MID_VALUE * 180;
                break;
        }
        imageView.setImageBitmap(ImageHelper.handleImageEffect(bitmap, mHue, mStauration, mLum));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
