package com.example.httptesttest.myutil;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.httptesttest.UtilOld;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static int CompassRate = 300; // getCompassImage中压缩后的图片的长宽？
    public static final int REQUEST_PERMISSION_CODE = 100;
    public static final int ALBUM_REQUEST_CODE = 200;
    public static final int CAMERA_REQUEST_CODE = 300;


    private static final String CAMERA_DIR = "/dcim/";
    private static final String albumName ="CameraSample";


    public static Bitmap getCompassImage(Context context, Uri uri){
        Bitmap img=null;
        ContentResolver cr = context.getContentResolver();
        try {
            // 把图片压缩下
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);

            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = Util.calculateInSampleSize(options, CompassRate, CompassRate);
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);

            img = bitmap;

            // 小米三星拍照是有过旋转的 调整照片的角度
            int r = Util.readPictureDegree(Util.getPath(context,uri));
            img =  Util.rotaingImageView(r,img);
        } catch (Exception e) {
            Log.e("getCompassImage", e.getMessage());
        }

        return  img;
    }

    public static IUiListener getQQListener(final Activity context){
        return new IUiListener() {
            @Override
            public void onCancel() {
                UtilOld.toastMessage(context, "分享取消");
            }
            @Override
            public void onComplete(Object response) {
                UtilOld.toastMessage(context, "分享成功" + response.toString());
            }
            @Override
            public void onError(UiError e) {
                UtilOld.toastMessage(context, "分享出错" + e.errorMessage, "e");
            }
        };
    }

    public static void getPermission(Activity act){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWritePermission = act.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = act.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> permissions = new ArrayList<String>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else { }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else { }
            if (!permissions.isEmpty()) {
                act.requestPermissions(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                }, REQUEST_PERMISSION_CODE);
            }
        }
    }


}

class RollingImage{

}