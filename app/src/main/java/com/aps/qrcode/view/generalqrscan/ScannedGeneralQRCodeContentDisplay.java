package com.aps.qrcode.view.generalqrscan;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.database.DBManager;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.helper.ZXingHelper;
import com.aps.qrcode.model.GeneralQrScan;
import com.aps.qrcode.util.QRCodeProperties;
import com.google.zxing.WriterException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class ScannedGeneralQRCodeContentDisplay extends AppCompatActivity {

    int qr_scan_id;
    private ImageView qrImgView;
    private EditText qrNameEditTxt;
    private Button btnSave, btnShare;
    private DBHelper db;
    private ZXingHelper zXingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_qrcode_content_display);


        db = new DBHelper(this, DATABASE_NAME, null, DATABASE_VERSION);
        db.queryData(DBManager.GENERAL_QR_SCAN_TABLE);
        zXingHelper = new ZXingHelper();


        qrImgView = findViewById(R.id.img_vw_qr_img);
        qrNameEditTxt = findViewById(R.id.edit_txt_qr_name);
        btnSave = findViewById(R.id.btn_qr_save);
        btnShare = findViewById(R.id.btn_qr_share);

        // getting scanned qr id through intent to display its content or update it
        qr_scan_id = getIntent().getIntExtra("scan_qr_id", 0);
        boolean qr_details = getIntent().getBooleanExtra("qr_details", false);
        boolean qr_update = getIntent().getBooleanExtra("qr_update", false);
        GeneralQrScan generalQrScan = db.getScannedGeneralQrCodeById((long) qr_scan_id);
        qrImgView.setImageBitmap(BitmapFactory.decodeByteArray(generalQrScan.getQrImg(), 0, generalQrScan.getQrImg().length));
        qrNameEditTxt.setText(generalQrScan.getQrImgName());

        if (qr_details) {
            qrNameEditTxt.setFocusable(false);
            qrNameEditTxt.setClickable(false);
            btnSave.setVisibility(View.GONE);
        } else if (qr_update) {
            qrNameEditTxt.setFocusable(true);
            qrNameEditTxt.setClickable(true);
            btnSave.setText("Update");
            btnSave.setVisibility(View.VISIBLE);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qr_update && !qrNameEditTxt.getText().toString().trim().isEmpty()) {
                    qrScanUpdate();
                    Toast.makeText(ScannedGeneralQRCodeContentDisplay.this, "Scanned QR name updated successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ScannedGeneralQRCodeContentDisplay.this, "Please provide a name for QR Code", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareQRImg(zXingHelper.createQRImage(QRCodeProperties.QR_CHARACTER_SET,
                            QRCodeProperties.QR_ERROR_CORRECTION_LEVEL, QRCodeProperties.WIDTH,
                            QRCodeProperties.HEIGHT, zXingHelper.qr2Txt(qrImgView)));
                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void shareQRImg(Bitmap bitmap) {
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

    public void qrScanUpdate() {
        GeneralQrScan generalQrScan = new GeneralQrScan();
        generalQrScan.setQrId(qr_scan_id);
        generalQrScan.setQrImgName(qrNameEditTxt.getText().toString().trim());
        db.updateScannedGeneralQrCode(generalQrScan);
    }

}
