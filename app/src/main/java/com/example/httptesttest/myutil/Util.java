package com.example.httptesttest.myutil;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.example.httptesttest.UtilOld;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.httptesttest.MainActivity.act;

public class Util {
    public static final String TAG = "Util";
    public static int CompassRate = 300; // getCompassImage中压缩后的图片的长宽？
    public static final int REQUEST_PERMISSION_CODE = 100;
    public static final int ALBUM_REQUEST_CODE = 200;
    public static final int CAMERA_REQUEST_CODE = 300;


    private static final String CAMERA_DIR = "/dcim/";
    private static final String albumName ="CameraSample";



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


    // 从uri 到 绝对路径
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        // DocumentProvider
        if (isKitKat && FilePath.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (FilePath.isExternalStorageDocument(uri)) {
                final String docId = FilePath.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (FilePath.isDownloadsDocument(uri)) {

                final String id = FilePath.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return FilePath.getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (FilePath.isMediaDocument(uri)) {
                final String docId = FilePath.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return FilePath.getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (FilePath.isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return FilePath.getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /*
    ====================================图片====================================
     */

    // 获取一个限定矩形大小内的图片
    public static Bitmap getCompassImage(Context context, Uri uri,int width,int heigh){
        Bitmap img=null;
        ContentResolver cr = context.getContentResolver();
        try {
            // 读入这个uri文件的 图像头, 不占用内存
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);

            // 调用方法计算inSampleSize值
            options.inSampleSize = Util.calMinimumSize(options, width, heigh);
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            img = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);

            // 小米三星拍照是有过旋转的 调整照片的角度
            int r = RollingImage.readPictureDegree(Util.getPath(context,uri));
            img =  RollingImage.rotaingImageView(r,img);
        } catch (Exception e) {
            Log.e("getCompassImage", e.getMessage());
        }
        return  img;
    }

    // 给出可以放入的框的大小, 使得图像的宽和高 有一条是等于, 还有一定是大于 目标长度.
    public static int calMaximumSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        return inSampleSize;
    }
    // 给出可以放入的框的大小, 使得图像一定可以放入, 有一条是等于, 还有一条是小于 目标长度.
    public static int calMinimumSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ?  widthRatio : heightRatio; // 选大的
        return inSampleSize;
    }

    // 把传入的图片调小
    public static Bitmap setImageSize(Bitmap bm, int newWidth ,int newHeight){
        Log.d(TAG,"setImageSize -start");
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        Log.d(TAG,"\tbitmap原始大小:"+width+"*"+height+" "+bm.getAllocationByteCount()+"bytes");

        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        Log.d(TAG,"\tbitmap重绘后大小:"+newbm.getWidth()+"*"+newbm.getHeight()+" "+newbm.getAllocationByteCount()+"bytes");
        return newbm;
    }

    // 把base64的字符串 转化成图片
    public static Bitmap base642Bitmap(String base){
        byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    //打开本地相册选择图片
    public static void selectPic(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        act.startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }



}

class RollingImage{
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if(angle == 0){
            return bitmap;
        }
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }
}
class FilePath{
    private static final String PATH_DOCUMENT = "document";
    static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    static String getDocumentId(Uri documentUri) {
        final List<String> paths = documentUri.getPathSegments();
        if (paths.size() < 2) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        return paths.get(1);
    }
    static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    static boolean isDocumentUri(Context context, Uri uri) {
        final List<String> paths = uri.getPathSegments();
        if (paths.size() < 2) {
            return false;
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            return false;
        }

        return true;
    }
}