package com.aps.qrcode.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aps.qrcode.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;


/**
 * This activity captures or scan QR code
 */
public class ScannerActivity extends AppCompatActivity implements
        DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView qrCodeAndbarcodeScannerView;
    private ImageView switchFlashlightButton;
    private boolean isFlashLightOn = false;

    private Button qrImgSelectBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        //Initialize barcode scanner view
        qrCodeAndbarcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        //set torch listener
        qrCodeAndbarcodeScannerView.setTorchListener(this);

        //switch flashlight button
        switchFlashlightButton = (ImageView) findViewById(R.id.img_vw_switch_flashlight);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        } else {
            switchFlashlightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchFlashlight();
                }
            });
        }

        //start capture
        capture = new CaptureManager(this, qrCodeAndbarcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        //directing the user to select image from gallery or take image from the camera
        qrImgSelectBtn = findViewById(R.id.btn_select_qr_img);
        qrImgSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScannerActivity.this,QRImageSelectActivity.class));
            }
        });
    }


    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight() {
        if (isFlashLightOn) {
            qrCodeAndbarcodeScannerView.setTorchOff();
            isFlashLightOn = false;
        } else {
            qrCodeAndbarcodeScannerView.setTorchOn();
            isFlashLightOn = true;
        }

    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setImageResource(R.drawable.ic_lightbulb_outline_gray_24dp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return qrCodeAndbarcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

}