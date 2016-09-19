package com.tppfit.ardemo;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.tppfit.ardemo.camera.GraphicOverlay;

/**
 * Created by luyendt on 9/13/2016.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private GraphicOverlay<BarcodeGraphic> graphicOverlay;

    BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> barcodeGraphicOverlay) {
        graphicOverlay = barcodeGraphicOverlay;

    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(graphicOverlay);
        return new BarcodeGraphicTracker(graphicOverlay, graphic);
    }
}

