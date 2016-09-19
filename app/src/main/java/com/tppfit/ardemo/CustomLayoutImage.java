package com.tppfit.ardemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by luyendt on 9/19/2016.
 */
public class CustomLayoutImage extends LinearLayout {

    private ImageView imgvImage;
    public CustomLayoutImage(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_display_image, this, true);

    }
    public CustomLayoutImage(Context context) {
        this(context, null);
    }
}
