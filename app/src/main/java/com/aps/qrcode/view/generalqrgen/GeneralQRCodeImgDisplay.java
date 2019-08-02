package com.aps.qrcode.view.generalqrgen;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.database.DBManager;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.helper.ZXingHelper;
import com.aps.qrcode.model.GeneralQrGen;
import com.aps.qrcode.serviceimpl.QRServiceImpl;
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


public class GeneralQRCodeImgDisplay extends AppCompatActivity {
    private ImageView qrImgView;
    private EditText qrNameEditTxt;

    private Button btnSave, btnShare;
    private String qrCreateContent;

    private DBHelper db;
    private ZXingHelper zXingHelper;
    private QRServiceImpl qrService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_qrcode_img_display);

        db = new DBHelper(this, DATABASE_NAME, null, DATABASE_VERSION);
        db.queryData(DBManager.GENERAL_QR_GEN_TABLE);
        zXingHelper = new ZXingHelper();
        qrService = new QRServiceImpl();

        /**
         * Receiving the QR Code content through intent
         */
        qrCreateContent = getIntent().getStringExtra("qrCodeContent");

        qrImgView = findViewById(R.id.img_vw_qr_img);
        qrNameEditTxt = findViewById(R.id.edit_txt_qr_img_name);
        btnSave = findViewById(R.id.btn_qr_save);
        btnShare = findViewById(R.id.btn_qr_share);

        int qr_id = getIntent().getIntExtra("qr_img_id", 0);
        String qr_img_name = getIntent().getStringExtra("qr_img_name");
        String qr_img_content = getIntent().getStringExtra("qr_img_content");
        boolean qr_img_update = getIntent().getBooleanExtra("qr_img_update", false);


        if (qr_img_update) {
            qrCreateContent = qr_img_content;
            // Toast.makeText(this, qrCreateContent, Toast.LENGTH_LONG).show();
            qrNameEditTxt.setText(qr_img_name);
            //Toast.makeText(this, qrNameEditTxt.getText().toString().trim(), Toast.LENGTH_LONG).show();
            btnSave.setText("Update");
        }


        if (!qrCreateContent.isEmpty()) {
            try {
                Bitmap bitmap = zXingHelper.createQRImage(
                        QRCodeProperties.QR_CHARACTER_SET,
                        QRCodeProperties.QR_ERROR_CORRECTION_LEVEL,
                        QRCodeProperties.WIDTH,
                        QRCodeProperties.HEIGHT,
                        qrCreateContent);
                qrImgView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No content is provided for QR code!", Toast.LENGTH_LONG).show();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(qrNameEditTxt.getText().toString().trim())) {
                    if (!qr_img_update) {
                        saveGeneralQrCode(convertQRContentToNonPaymentQRGen(qrCreateContent));
                        Toast.makeText(GeneralQRCodeImgDisplay.this, "QR Image has been saved successfully", Toast.LENGTH_LONG).show();
                    } else if (qr_img_update) {
                        db.updateGeneratedGeneralQrCode(updateSecretQRCode(qr_id, qrCreateContent));
                        Toast.makeText(GeneralQRCodeImgDisplay.this, "QR Code updated successfully!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(GeneralQRCodeImgDisplay.this, "Please enter a name for QR Image", Toast.LENGTH_LONG).show();
                }
            }
        });


        // sharing QR Code
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(getApplicationContext(), "QR Code Share", Toast.LENGTH_LONG).show();
                    shareQRCode(zXingHelper.createQRImage(QRCodeProperties.QR_CHARACTER_SET,
                            QRCodeProperties.QR_ERROR_CORRECTION_LEVEL,
                            QRCodeProperties.WIDTH,
                            QRCodeProperties.HEIGHT,
                            qrCreateContent));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private GeneralQrGen convertQRContentToNonPaymentQRGen(String qr_content) {
        GeneralQrGen generalQrGen = new GeneralQrGen();
        generalQrGen.setQrCodeContent(qr_content);
        generalQrGen.setQrImg(qrService.imageViewToByte(qrImgView));
        generalQrGen.setQrImgName(qrNameEditTxt.getText().toString().trim());
        return generalQrGen;

    }

    private GeneralQrGen updateSecretQRCode(int id, String qr_content) {
        GeneralQrGen generalQrGen = new GeneralQrGen();
        generalQrGen.setQrId(id);
        generalQrGen.setQrCodeContent(qr_content);
        generalQrGen.setQrImg(qrService.imageViewToByte(qrImgView));
        generalQrGen.setQrImgName(qrNameEditTxt.getText().toString().trim());
        return generalQrGen;
    }

    private void saveGeneralQrCode(GeneralQrGen generalQrGen) {
        db.saveGeneralQrCode(generalQrGen.getQrCodeContent(), generalQrGen.getQrImg(),
                generalQrGen.getQrImgName());
    }

    // this method is used to share QR Code on social medias
    protected void shareQRCode(Bitmap bitmap) {
        Dexter.withActivity(GeneralQRCodeImgDisplay.this)
                .withPermissions(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
