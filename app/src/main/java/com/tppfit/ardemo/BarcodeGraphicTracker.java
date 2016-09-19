package com.tppfit.ardemo;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.tppfit.ardemo.BarcodeGraphic;
import com.tppfit.ardemo.camera.GraphicOverlay;

/**
 * Created by luyendt on 9/13/2016.
 */
public class BarcodeGraphicTracker extends Tracker<Barcode> {
    private GraphicOverlay<BarcodeGraphic> overlay;
    private BarcodeGraphic graphic;

    BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> overlay, BarcodeGraphic graphic) {
        this.overlay = overlay;
        this.graphic = graphic;
    }

    @Override
    public void onNewItem(int i, Barcode barcode) {
        graphic.setId(i);
        graphic.updateItem(barcode);
        Log.d("BARCODEGRATRAC", "1--"+barcode.rawValue+"--1");

    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detections, Barcode barcode) {
        overlay.add(graphic);
//        graphic.updateItem(barcode);

        Log.d("BARCODEGRATRAC", "2--"+barcode.rawValue+"--2");

    }

    @Override
    public void onMissing(Detector.Detections<Barcode> detections) {
        overlay.remove(graphic);
        Log.d("BARCODEGRATRAC", "3CC");
    }

    @Override
    public void onDone() {
        overlay.remove(graphic);
        Log.d("BARCODEGRATRAC", "3DD");
    }
}


























































































































































































































































