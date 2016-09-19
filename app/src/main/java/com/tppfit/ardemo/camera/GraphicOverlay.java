package com.tppfit.ardemo.camera;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.tppfit.ardemo.AppOwn;
import com.tppfit.ardemo.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by luyendt on 9/10/2016.
 */

public class GraphicOverlay<T extends GraphicOverlay.Graphic> extends View {
    private final Object lock = new Object();
    private int previewWidth;
    private float widthScaleFactor = 1.0f;
    private int previewHeight;
    private float heightScaleFactor = 1.0f;
    private int facing = CameraSource.CAMERA_FACING_BACK;
    private Set<T> graphic = new HashSet<>();
    private T firstGraphic;

    public static abstract class Graphic {

        private GraphicOverlay graphicOverlay;

        public Graphic(GraphicOverlay overlay) {
            graphicOverlay = overlay;
        }

        public abstract void draw(Canvas canvas, Bitmap bitmap);

        public abstract void displayContent(Canvas canvas, Bitmap bitmap);

        //adjust width from preview to view
        public float scaleX(float horizontal) {
            return horizontal * graphicOverlay.widthScaleFactor;
        }

        //adjust height from preview to view
        public float scaleY(float vertical) {
            return vertical * graphicOverlay.heightScaleFactor;
        }

        //adjust x coordiante from preview to view
        public float translateX(float x) {
            if (graphicOverlay.facing == CameraSource.CAMERA_FACING_FRONT) {
                return graphicOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        //adjust y coordinate from preview to view
        public float translateY(float y) {
            return scaleY(y);
        }

        public void postInvalidate() {
            graphicOverlay.postInvalidate();
        }

    }

    public GraphicOverlay(Context context) {
        super(context);
    }

    public GraphicOverlay(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public GraphicOverlay(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    //remove all graphic from overlay
    public void clear() {
        synchronized (lock) {

            graphic.clear();
            firstGraphic = null;
        }
        postInvalidate();
    }

    //add graphic to overlay
    public void add(T mGraphic) {
        synchronized (lock) {
            graphic.add(mGraphic);
            if (firstGraphic == null) {
                firstGraphic = mGraphic;
            }
        }
        postInvalidate();
    }

    // remove graphic from overlay
    public void remove(T mGraphic) {
        synchronized (lock) {

            graphic.remove(mGraphic);
            if (firstGraphic != null && firstGraphic.equals(mGraphic)) {
                firstGraphic = null;
            }

            postInvalidate();
        }
    }

    public T getFirstGraphic() {
        synchronized (lock) {
            return firstGraphic;
        }

    }

    //set camera atrr: size, facing..
    public void setCameraInfo(int preWidth, int preHeight, int facingDir) {
        synchronized (lock) {
            previewWidth = preWidth;
            previewHeight = preHeight;
            facing = facingDir;
        }
        postInvalidate();
    }

    //draw overlay

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (lock) {
            if ((previewWidth != 0) && (previewHeight != 0)) {
                widthScaleFactor = (float) canvas.getWidth() / (float) previewWidth;
                heightScaleFactor = (float) canvas.getHeight() / (float) previewHeight;
            }
            Bitmap b= BitmapFactory.decodeResource(getResources(), R.drawable.icon_android);

            for (Graphic mGraphic : graphic) {
//                mGraphic.draw(canvas, b);

                mGraphic.displayContent(canvas, b);
            }
        }
    }

    public static Bitmap getBitmapFromURL(String urlBitmap){
        try {
            URL url = new URL(urlBitmap);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
