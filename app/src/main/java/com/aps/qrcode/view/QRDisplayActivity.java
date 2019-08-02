package com.aps.qrcode.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.aps.qrcode.model.PaymentQrGen;
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


/**
 * After payment QR Code is generated, the user will be directed to this activity where the user
 * generate the QR Code
 */
public class QRDisplayActivity extends AppCompatActivity {

    private ImageView qrImgView;
    private EditText qrNameEditTxt;

    private Button btnSave, btnShare;

    private DBHelper db;
    private ZXingHelper zXingHelper;
    private QRServiceImpl qrService;
    private int qrId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdisplay);

        db = new DBHelper(this, DATABASE_NAME, null, DATABASE_VERSION);
        db.queryData(DBManager.PAYMENT_QR_GEN_TABLE);
        zXingHelper = new ZXingHelper();
        qrService = new QRServiceImpl();

        /**
         * Receiving the QR Code content through intent once the user fill the form successfully
         */
        String qrCreateContent = getIntent().getStringExtra("qrGenData");

        qrImgView = findViewById(R.id.img_vw_qr_img);
        qrNameEditTxt = findViewById(R.id.edit_txt_qr_img_name);
        btnSave = findViewById(R.id.btn_qr_save);
        btnShare = findViewById(R.id.btn_qr_share);

        /**
         * Receiving the QR Code details if the user wants to update the QR Code
         */
        qrId = getIntent().getIntExtra("qrID", 0);
        String qrImgDetails = getIntent().getStringExtra("qrImgDetails");
        String qrImgName = getIntent().getStringExtra("edit_txt_qr_img_name");
        byte[] qrImg = getIntent().getByteArrayExtra("QrImgView");

        boolean qr_update = getIntent().getBooleanExtra("qr_update", false);
        boolean qr_view_details_only = getIntent().getBooleanExtra("noSaveUpdate",false);
        if(!qr_update && qr_view_details_only){
            btnSave.setVisibility(View.GONE);
        }
        // Generating and displaying QR Code image
        if (TextUtils.isEmpty(qrImgName) && TextUtils.isEmpty(qrImgDetails)) {
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
            qrImgView.setImageBitmap(BitmapFactory.decodeByteArray(qrImg, 0, qrImg.length));
            qrNameEditTxt.setText(qrImgName);
            //Bitmap qrImgBitMap = createQRImage(CHARACTER_SET, ERROR_CORRECTION_LEVEL, WIDTH, HEIGHT, qrImgDetails, QR_IMG_FORMAT);
            if (!qr_update) {
                qrNameEditTxt.setKeyListener(null);
                btnSave.setText("Save");
            }
            btnSave.setText("Update");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(qrNameEditTxt.getText().toString().trim())) {
                    if (!btnSave.getText().toString().equals("Update")) {
                        saveQRImg(convertQRContentToMerchantObject(qrCreateContent));
                        Toast.makeText(QRDisplayActivity.this, "QR Image has been saved successfully", Toast.LENGTH_LONG).show();
                    } else {
                        db.updateGeneratedGeneralQrCode(convertQRContentToMerchantObject(qrImgDetails));
                        Toast.makeText(QRDisplayActivity.this, "QR Code updated successfully!", Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(QRDisplayActivity.this, "Please enter a name for QR Image", Toast.LENGTH_LONG).show();
                }
            }
        });


        // sharing QR Code
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareQRImg(zXingHelper.createQRImage(
                            QRCodeProperties.QR_CHARACTER_SET,
                            QRCodeProperties.QR_ERROR_CORRECTION_LEVEL,
                            QRCodeProperties.WIDTH,
                            QRCodeProperties.HEIGHT,
                            zXingHelper.qr2Txt(qrImgView)));
                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private PaymentQrGen convertQRContentToMerchantObject(String qrStringContent) {
        PaymentQrGen paymentQrGen = new PaymentQrGen();
        paymentQrGen.setQrId(qrId);
        String[] qr2Txt = qrStringContent.split("\\*");

        for (String paymentField : qr2Txt) {

            String[] keyValuePair = paymentField.split("\\:");
            int key = Integer.parseInt(keyValuePair[0]);
            String value = keyValuePair[1];

            switch (key) {
                // 1 = QR_VERSION
                case 1:
                    paymentQrGen.setQrVersion(Integer.parseInt(value));
                    break;
                // 2 = QR_TYPE
                case 2:
                    paymentQrGen.setQrCategory(value);
                    break;
                // 3 = MERCHANT_OR_COMPANY_NAME
                case 3:
                    paymentQrGen.setMerchantCompanyName(value);
                    break;
                // 4 = MERCHANT_CATEGORY
                case 4:
                    paymentQrGen.setMerchantCategory(value);
                    break;
                // 5 = MERCHANT_PHONE_NUMBER
                case 5:
                    paymentQrGen.setMerchantPhone(value);
                    break;
                // 6 = MERCHANT_EMAIL
                case 6:
                    paymentQrGen.setMerchantEmail(value);
                    break;
                case 7:
                    // 7 = PROVINCE
                    paymentQrGen.setMerchantProvince(value);
                    break;
                // 8 = DISTRICT
                case 8:
                    paymentQrGen.setMerchantDistrict(value);
                    break;
                // 9 = BANK_NAME
                case 9:
                    paymentQrGen.setBankName(value);
                    break;
                // 10 = PAN
                case 10:
                    paymentQrGen.setAccountNumber(value);
                    break;
                // 11 = CURRENCY
                case 11:
                    paymentQrGen.setCurrency(value);
                    break;
                // 12 = AMOUNT
                case 12:
                    paymentQrGen.setAmount(value);
                    break;
            }
        }
        paymentQrGen.setQrImg(qrService.imageViewToByte(qrImgView));
        paymentQrGen.setQrImgName(qrNameEditTxt.getText().toString().trim());
        return paymentQrGen;

    }

    private void saveQRImg(PaymentQrGen paymentQrGen) {
        db.saveGeneratedPaymentQrCode(
                paymentQrGen.getQrCategory(),
                paymentQrGen.getMerchantCategory(),
                paymentQrGen.getMerchantCompanyName(),
                paymentQrGen.getMerchantPhone(),
                paymentQrGen.getMerchantEmail(),
                paymentQrGen.getMerchantProvince(),
                paymentQrGen.getMerchantDistrict(),
                paymentQrGen.getBankName(),
                paymentQrGen.getAccountNumber(),
                paymentQrGen.getCurrency(),
                paymentQrGen.getAmount(),
                paymentQrGen.getQrImgName(),
                paymentQrGen.getQrImg()
        );
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
}
