package com.tppfit.ardemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.google.android.gms.vision.barcode.Barcode;
import com.tppfit.ardemo.camera.GraphicOverlay;

import java.io.InputStream;

/**
 * Created by luyendt on 9/13/2016.
 */
public class BarcodeGraphic extends GraphicOverlay.Graphic {
    private int Id;
    private static final int COLOR_CHOICES = Color.DKGRAY;

    private Paint rectPaint;
    private Paint textPaint;
    private volatile Barcode barcode;
    private static MediaPlayer player;
    //    Bitmap bitmap = AppOwn.getImageBm();
    Paint paint;
    Drawable imageAndroid;

    BarcodeGraphic(GraphicOverlay overlay) {
        super(overlay);
        rectPaint = new Paint();
        rectPaint.setColor(COLOR_CHOICES);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4.0f);

        textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(36.0f);

//        paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setFilterBitmap(true);
//        paint.setDither(true);8970000000
//        paint.setColor(Color.RED);
//

    }

    public int getId() {
        return Id;
    }

    public void setId(int mId) {
        this.Id = mId;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    void updateItem(Barcode barcode) {
        this.barcode = barcode;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas, Bitmap bitmap) {

        Barcode mBarcode = barcode;
        if (mBarcode == null) {
            return;
        }

        // Draws a rectangle/ square around the barcode.


        RectF rectF = new RectF(mBarcode.getBoundingBox());
        rectF.left = translateX(rectF.left);
        rectF.top = translateY(rectF.top);
        rectF.right = translateX(rectF.right);
        rectF.bottom = translateY(rectF.bottom);
        canvas.drawRect(rectF, rectPaint);
//        canvas.drawText(mBarcode.rawValue, rectF.left, rectF.bottom, textPaint);

        canvas.drawBitmap(bitmap, rectF.left, rectF.top, rectPaint);

//        canvas.drawBitmap(bitmap, 0, 0, rectPaint);
//        bitmap.recycle();


    }

    public void drawContent(Canvas canvas, Bitmap bitmap) {

        Barcode mBarcode = barcode;
        if (mBarcode == null) {
            return;
        }

        // Draws a rectangle/ square around the barcode.


        RectF rectF = new RectF(mBarcode.getBoundingBox());
        rectF.left = translateX(rectF.left);
        rectF.top = translateY(rectF.top);
        rectF.right = translateX(rectF.right);
        rectF.bottom = translateY(rectF.bottom);
        canvas.drawRect(rectF, rectPaint);
//        canvas.drawText(mBarcode.rawValue, rectF.left, rectF.bottom, textPaint);
        Bitmap bitmap1 = GraphicOverlay.getBitmapFromURL(barcode.rawValue);
        canvas.drawBitmap(bitmap, rectF.left, rectF.top, rectPaint);
    }

    public void playSound() {
        try {
            stopPlaying();
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(barcode.rawValue);
            player.prepare();
            player.start();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void stopPlaying() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void displayContent(Canvas canvas, Bitmap bitmap) {
        //detect barcode content
        Barcode mBarcode = barcode;
        String url = mBarcode.rawValue;
        String urlType = url.substring(url.length() - 3, url.length());
        Log.d("BARCODEGRATRAC url d", urlType);
        switch (urlType) {
            //if barcode url is audio
            case "mp3":
                playSound();
                break;
            //if barcode url is video
            case "mp4":
                drawContent(canvas, bitmap);
                break;
            //if barcode url is image url
            case "jpg":
                drawContent(canvas, bitmap);
                break;
            default:
                drawContent(canvas, bitmap);
                break;
        }

    }

    public void removeContent() {
        Barcode mBarcode = barcode;
        String url = mBarcode.rawValue;
        String urlType = url.substring(url.length() - 3, url.length());
        Log.d("BARCODEGRATRAC url", urlType);
        switch (urlType) {
            case "mp3":
                stopPlaying();
                break;
            case "mp4":

                break;
            case "jpg":
                break;
            default:
                break;
        }
        postInvalidate();
    }

}

