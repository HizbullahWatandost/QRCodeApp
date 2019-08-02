package com.aps.qrcode.view.generalqrgen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.helper.ZXingHelper;
import com.aps.qrcode.model.GeneralQrGen;
import com.aps.qrcode.serviceimpl.QRServiceImpl;
import com.aps.qrcode.util.QRCodeProperties;
import com.aps.qrcode.util.QRImgShare;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class GeneralQRCodeDetails extends QRImgShare {


    private final Context mContext = this;
    private DBHelper db;
    private ZXingHelper zXingHelper;
    private EditText qrContentEditTxt;
    private TextView mQrDecryptHint;
    private Button mQrShareBtn;
    private QRServiceImpl qrService;

    // only for testing purpose changes yes changed

    private boolean resourceSharable = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_payment_qrcode_details);


        qrContentEditTxt = findViewById(R.id.edit_txt_qr_content);
        mQrDecryptHint = findViewById(R.id.txt_vw_secret_qr_decrypt_info);
        mQrDecryptHint.setVisibility(View.GONE);
        zXingHelper = new ZXingHelper();
        db = new DBHelper(this, DATABASE_NAME, null, DATABASE_VERSION);
        qrService = new QRServiceImpl();

        mQrShareBtn = findViewById(R.id.btn_qr_unlock);
        mQrShareBtn.setText("Share");
        // secret qr id to display its details
        int secret_qr_details_by_id = getIntent().getIntExtra("qr_details_by_id", 0);

        long qr_details_id = secret_qr_details_by_id;

        GeneralQrGen generalQrGen = db.getGeneratedGeneralQrCodeById(qr_details_id);

        if (secret_qr_details_by_id != 0) {
            qrContentEditTxt.setText(generalQrGen.getQrCodeContent());
        }

        mQrShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(getApplicationContext(), "QR Code Share", Toast.LENGTH_LONG).show();
                    shareQRCode(zXingHelper.createQRImage(QRCodeProperties.QR_CHARACTER_SET,
                            QRCodeProperties.QR_ERROR_CORRECTION_LEVEL,
                            QRCodeProperties.WIDTH,
                            QRCodeProperties.HEIGHT,
                            generalQrGen.getQrCodeContent()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    protected void shareQRCode(Bitmap bitmap) throws IOException {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent shareQRCodeIntent = new Intent(Intent.ACTION_SEND);
                            shareQRCodeIntent.setType("image/*");
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                                    bitmap, "APS payment QR Code", "Pay by one click within few seconds");
                            Uri imageUri = Uri.parse(path);
                            shareQRCodeIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            startActivity(Intent.createChooser(shareQRCodeIntent, "Share this QR Code image via"));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

}
