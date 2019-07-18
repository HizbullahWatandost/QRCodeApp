package com.aps.qrcode.view.secretqrscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.aps.qrcode.view.ScannerActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class NonPaymentQRCodeScan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initiate scan with our custom scan activity

        String scan_request = getIntent().getStringExtra("qr_scan_request");
        if(!TextUtils.isEmpty(scan_request)) {
            new IntentIntegrator(NonPaymentQRCodeScan.this).setCaptureActivity(ScannerActivity.class).initiateScan();
        }
    }


    /**
     * QR CODE scan activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We will get scan results here
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check for null
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled!", Toast.LENGTH_LONG).show();
            } else {
                //if the scan is successful then, direct it to the QRScanResultDisplayActivity to display the result in form.
                Intent intent = new Intent(this, NonPaymentQRCodeScanResult.class);
                intent.putExtra("non_payment_qr_content",result.getContents());
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the activity
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
