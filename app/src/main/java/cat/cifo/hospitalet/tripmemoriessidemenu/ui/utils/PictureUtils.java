package cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Base64;
import androidx.exifinterface.media.ExifInterface;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PictureUtils {

    public static Bitmap getScaledBitmap (String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        return getScaledBitmap(path,size.x, size.y);
    }

    public static Bitmap getScaledBitmap (String path, int destWidh, int destHeigh){
        //Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeigh = options.outHeight;

        //Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeigh > destHeigh || srcWidth > destWidh){
            float heightScale = srcHeigh/destHeigh;
            float widthScale = srcWidth/destWidh;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }


    //Function that rotates image if required
    public static Bitmap rotateImageIfRequired(Bitmap img, InputStream input) throws IOException {

        ExifInterface ei = new ExifInterface(input);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }


    //Function that returns Bitmap rotated
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    //Encode image to base64 string
    public static String encodeImage(Bitmap img) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }


    //Decode image from base64 string to image
    public static Bitmap decodeImate (String img){
        byte [] imageBytes = Base64.decode(img, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    //Resize bitmap
    public static Bitmap resizeBitmap (Bitmap btmp){
        int maxSize = 960;
        int outWidth, outHeight;
        int inWidth = btmp.getWidth();
        int inHeight = btmp.getHeight();
        Bitmap resizedBitmap;

        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        resizedBitmap = Bitmap.createScaledBitmap(btmp, outWidth, outHeight, false);
        return resizedBitmap;
    }


}
