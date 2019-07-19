package com.aps.qrcode.view.generalqrscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aps.qrcode.view.MainActivity;


public class ScanGeneralQR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requesting the activity to handle the QR Code scanning
        Intent intent = new Intent(ScanGeneralQR.this, MainActivity.class);
        intent.putExtra("qr_scan_request", "Scan_QRCodePlease");
        startActivity(intent);

        //initiate scan with our custom scan activity
//
//        String scan_request = getIntent().getStringExtra("qr_scan_request");
//        if(!TextUtils.isEmpty(scan_request)) {
//            new IntentIntegrator(ScanGeneralQR.this).setCaptureActivity(ScannerActivity.class).initiateScan();
//        }
    }

//
//    /**
//     * QR CODE scan activity result
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        //We will get scan results here
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        //check for null
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "Scan Cancelled!", Toast.LENGTH_LONG).show();
//            } else {
//                //if the scan is successful then, direct it to the QRScanResultDisplayActivity to display the result in form.
//                Intent intent = new Intent(this, GeneralQRCodeScanResult.class);
//                intent.putExtra("non_payment_qr_content",result.getContents());
//                startActivity(intent);
//            }
//        } else {
//            // This is important, otherwise the result will not be passed to the activity
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}
