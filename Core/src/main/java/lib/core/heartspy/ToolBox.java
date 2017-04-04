package lib.core.heartspy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ToolBox {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


    static public Bitmap getScaledBitmap(int Width, int Height, Resources EmbeddedDatas, int Id) {

        BitmapFactory.Options DecodingOptions = new BitmapFactory.Options();
        DecodingOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(EmbeddedDatas, Id, DecodingOptions);

        int IntrinsicWidth = DecodingOptions.outWidth;
        int IntrinsicHeight = DecodingOptions.outHeight;

        float ScalingFactor = Math.min(Width / (float) IntrinsicWidth, Height / (float) IntrinsicHeight);
        int FittedWidth = (int) (IntrinsicWidth * ScalingFactor);
        int FittedHeight = (int) (IntrinsicHeight * ScalingFactor);

 //       Log.i("Bitmaps:", "Required["+Width+"x"+Height+"]--> Fitted["+FittedWidth+"x"+FittedHeight+"]");

        // Calculate sub-sampling to minimize memory consumption
        int SubSamplingFactor = Math.min(IntrinsicWidth / Width, IntrinsicHeight / Height);

        DecodingOptions.inJustDecodeBounds = false;
        DecodingOptions.inSampleSize = SubSamplingFactor;

        Bitmap SubSampledBitmap = BitmapFactory.decodeResource(EmbeddedDatas, Id, DecodingOptions);

        return Bitmap.createScaledBitmap(SubSampledBitmap, FittedWidth, FittedHeight, true);
    }


    public static String BytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}