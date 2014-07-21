package com.caterpillar.bitmap.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class ImageFetcherCrop extends ImageFetcher{
	
	public static enum CropType{DEFAULT, ROUNDED}

	public ImageFetcherCrop(Context context, int imageSize) {
		super(context, imageSize);
	}

	public ImageFetcherCrop(Context context, int imageWidth, int imageHeight) {
		super(context, imageWidth, imageHeight);
	}

	@Override
	protected Bitmap processBitmap(Object data) {
		Bitmap bitmap = super.processBitmap(data);
		return getCroppedBitmap(bitmap);
	}
	
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
    	if(bitmap != null){
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            // Esto pintaria el fondo de negro
            //canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            
            return output;
    	}else{
    		return bitmap;
    	}
    }
}
