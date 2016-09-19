package com.tppfit.ardemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tppfit.ardemo.camera.CameraResourcePreview;

import com.tppfit.ardemo.camera.CameraSource;
import com.tppfit.ardemo.camera.GraphicOverlay;

import java.io.IOException;

public class BarcodeCaptureActivity extends AppCompatActivity {

    private static final String TAG = "BarcodeCapture";
    private static final int HANDLE_GMS = 9001;
    //perMission need to be < 256
    private static final int HANDLE_CAMERA_PERMISSION = 2;

    public static final String BarcodeObject = "Barcode";
    private CameraSource cameraSource;
    private CameraResourcePreview cameraResourcePreview;
    private GraphicOverlay<BarcodeGraphic> graphicOverlay;


    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_capture);

        cameraResourcePreview = (CameraResourcePreview) findViewById(R.id.cameraPreview);
        graphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);

        //check the camera permission before accessing camera, if not -> request
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false);
        } else {
            requestCameraPernission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


        Snackbar.make(graphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG)
                .show();
    }

    private void requestCameraPernission() {
        final String[] permissions = new String[]{
                Manifest.permission.CAMERA
        };

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, HANDLE_CAMERA_PERMISSION);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        HANDLE_CAMERA_PERMISSION);
            }
        };

        Snackbar.make(graphicOverlay, "Access to camera is required",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = scaleGestureDetector.onTouchEvent(event);
        boolean c = gestureDetector.onTouchEvent(event);
        return b || c || super.onTouchEvent(event);
    }

    @SuppressLint("InlineApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();

        BarcodeTrackerFactory barcodeTrackerFactory = new BarcodeTrackerFactory(graphicOverlay);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeTrackerFactory).build());

        if (!barcodeDetector.isOperational()) {

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
            if (hasLowStorage) {
                Toast.makeText(getApplicationContext(), "Face detector can not be dowloaded", Toast.LENGTH_SHORT).show();

            }
        }

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedPreviewSize(1600, 1024).setRequestedFps(15.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        cameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraResource();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraResourcePreview != null) {
            cameraResourcePreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraResourcePreview != null) {
            cameraResourcePreview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != HANDLE_CAMERA_PERMISSION) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
//            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
//            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(true, false);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage("cannot run because it does not have the camera permission")
                .setPositiveButton("OK", listener)
                .show();
    }

    private void startCameraResource() throws SecurityException {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                cameraResourcePreview.start(cameraSource, graphicOverlay);

            } catch (IOException e) {
                Log.e(TAG, "unable start camera resource", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private boolean onTap(float rawX, float rawY) {
        BarcodeGraphic graphic = graphicOverlay.getFirstGraphic();
        Barcode barcode = null;
        if (graphic != null) {
            barcode = graphic.getBarcode();
            if (barcode != null) {
                Intent dataIntent = new Intent();
                dataIntent.putExtra(BarcodeObject, barcode);
                setResult(CommonStatusCodes.SUCCESS, dataIntent);
//                MediaPlayer.create(this, R.raw.audio).start();
                finish();
            } else {
                Log.d(TAG, "barcode data is null");
            }
        } else {
            Log.d(TAG, "no barcode detected");
        }
        return barcode != null;
    }


    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            cameraSource.doZoom(detector.getScaleFactor());
        }
    }

}
