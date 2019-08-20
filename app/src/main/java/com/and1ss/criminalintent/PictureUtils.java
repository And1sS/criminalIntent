package com.and1ss.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {

    private PictureUtils(){}

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth) {
            float scaleWidth = srcWidth / destWidth;
            float scaleHeight = srcHeight / destHeight;

            inSampleSize = Math.round(scaleWidth > scaleHeight ?
                    scaleWidth : scaleHeight);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        Bitmap result = BitmapFactory.decodeFile(path, options);

        return result;
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();

        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
}
