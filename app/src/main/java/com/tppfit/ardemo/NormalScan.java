package com.tppfit.ardemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class NormalScan extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_scan);

        Button btnScanQr = (Button) findViewById(R.id.btnScanQr);
        Button btnScanBar = (Button) findViewById(R.id.btnScanBar);

        btnScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//                startActivityForResult(intent, 0);
                new IntentIntegrator(NormalScan.this).initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == 0){
//            if(resultCode == RESULT_OK){
//                String content = data.getStringExtra("SCAN_RESULT");
//                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
//                // Handle successful scan
//                Toast toast = Toast.makeText(this, "Content:" + content + " Format:" + format , Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 25, 400);
//                toast.show();
//            } else {
//                Toast toast = Toast.makeText(this, "San fail" , Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 25, 400);
//                toast.show();
//            }
//        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

