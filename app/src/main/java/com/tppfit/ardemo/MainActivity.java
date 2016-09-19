package com.tppfit.ardemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {

    private static  final  int BARCODE_CAPTURE = 9001;
    private static final String TAG = "MainAcivity";
    private Button btnCaptureBarcode;
    private ImageView imgvAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        imgvAvatar = (ImageView) findViewById(R.id.imgvAvatar);
//        Picasso.with(this).load("https://graph.facebook.com/100005457258299/picture?type=large").placeholder(R.drawable.icon_user).error(R.drawable.icon_user).resize(100, 100).centerCrop().into(imgvAvatar);

        btnCaptureBarcode = (Button) findViewById(R.id.btnQrBarcode);
        btnCaptureBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_CAPTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BARCODE_CAPTURE){
            if(resultCode == CommonStatusCodes.SUCCESS){

                if(data != null){
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Toast.makeText(getApplicationContext(), "read barcode successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "barcode value "+barcode.displayValue, Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "no barcode captured", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "read barcode ERROR", Toast.LENGTH_SHORT).show();
            }

        }else{
        super.onActivityResult(requestCode, resultCode, data);}
    }
}
