package com.tppfit.ardemo.camera;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;


import android.Manifest;

import java.io.IOException;
import java.util.jar.Attributes;

/**
 * Created by luyendt on 9/10/2016.
 */
public class CameraResourcePreview extends ViewGroup {
    private static final String TAG = "CameraResourcePreview";
    private Context context;
    private SurfaceView surfaceView;
    private boolean startRequestd;
    private boolean surfaceAvailable;
    private CameraSource cameraSource;

    GraphicOverlay graphicOverlay;

    public CameraResourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        startRequestd = false;
        surfaceAvailable = false;
        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(surfaceView);
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource mCameraSource) throws IOException, SecurityException {
        if (mCameraSource == null) {
            stop();
        }

        cameraSource = mCameraSource;
        if (cameraSource != null) {
            startRequestd = true;
            startIfReady();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource mCameraSource, GraphicOverlay mGraphicOverlay) throws IOException, SecurityException {
        graphicOverlay = mGraphicOverlay;
        start(mCameraSource);
    }

    public void stop() {
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    public void release() {
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void startIfReady() throws IOException, SecurityException {

        if (startRequestd && surfaceAvailable) {
            cameraSource.start(surfaceView.getHolder());
            if (graphicOverlay != null) {
                Size size = cameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    graphicOverlay.setCameraInfo(min, max, cameraSource.getCameraFacing());

                } else {
                    graphicOverlay.setCameraInfo(max, min, cameraSource.getCameraFacing());
                }
                graphicOverlay.clear();

            }
            startRequestd = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            surfaceAvailable = true;
            try {
                startIfReady();

            } catch (SecurityException se) {
                Log.e(TAG, "Do not have permission to start camera ", se);
            } catch (IOException e) {
                Log.e(TAG, "could not start camera source ", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            surfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = 320;
        int height = 240;
        if (cameraSource != null) {
            Size size = cameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();
            }

        }

        if (isPortraitMode()) {
            int temp = width;
            width = height;
            height = temp;
        }

        final int layoutWidth = r - l;
        final int layoutHeight = b - t;

        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = (int) (((float) layoutWidth / (float) width) * height);
        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = (int) (((float) layoutHeight / (float) height) * width);
        }

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(0, 0, childWidth, childHeight);
        }
        try {
            startIfReady();
        } catch (SecurityException se) {
            Log.e(TAG, "Do not have permission to start the camera", se);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }
        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }
}
