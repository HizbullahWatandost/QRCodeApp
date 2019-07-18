package com.aps.qrcode.view;

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
import com.aps.qrcode.model.PaymentQrScan;
import com.aps.qrcode.serviceimpl.QRServiceImpl;
import com.aps.qrcode.util.QRCodeProperties;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class SaveScannedQRImgActivity extends AppCompatActivity {

    private ImageView qrImgView;
    private EditText qrNameEditTxt;

    private Button btnSave,btnShare;

    private DBHelper db;
    private ZXingHelper zXingHelper;
    private QRServiceImpl qrService;
    private int qrId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdisplay);


        db = new DBHelper(this, DATABASE_NAME, null,DATABASE_VERSION);
        db.queryData(DBManager.PAYMENT_QR_SCAN_TABLE);
        zXingHelper = new ZXingHelper();
        qrService = new QRServiceImpl();

        /**
         * Receiving the QR Code content through intent once the user Click on Save the Scanned QR Code
         */
        String qrCreateContent = getIntent().getStringExtra("scannedQRImgDetails");

        qrImgView = (ImageView) findViewById(R.id.img_vw_qr_img);
        qrNameEditTxt = (EditText) findViewById(R.id.edit_txt_qr_name);
        btnSave = (Button) findViewById(R.id.btn_qr_save);
        btnShare = (Button) findViewById(R.id.btn_qr_share);

        /**
         * Receiving the QR Code details if the user wants to update the QR Code
         */
        qrId = getIntent().getIntExtra("qrID",0);
        String qrImgDetails = getIntent().getStringExtra("changeable");
        String qrImgName = getIntent().getStringExtra("edit_txt_qr_img_name");
        byte[] qrImg = getIntent().getByteArrayExtra("QrImgView");


        // Generating and displaying QR Code image
        if(TextUtils.isEmpty(qrImgName) && TextUtils.isEmpty(qrImgDetails)) {
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
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(qrNameEditTxt.getText().toString().trim())){
                        saveScannedQRImg(convertQRContentToMerchantScanObject());
                        Toast.makeText(SaveScannedQRImgActivity.this, "QR Image has been saved successfully", Toast.LENGTH_LONG).show();
                    }else{
                     Toast.makeText(SaveScannedQRImgActivity.this, "Please provide name of QR Code!", Toast.LENGTH_LONG).show();

                }
            }
        });

        // sharing QR Code
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareQRCode(zXingHelper.createQRImage(
                            QRCodeProperties.QR_CHARACTER_SET,
                            QRCodeProperties.QR_ERROR_CORRECTION_LEVEL,
                            QRCodeProperties.WIDTH,
                            QRCodeProperties.HEIGHT,
                            qrCreateContent));
                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private PaymentQrScan convertQRContentToMerchantScanObject(){
        PaymentQrScan paymentQrScan = new PaymentQrScan();
        paymentQrScan.setQrId(qrId);
        paymentQrScan.setQrImg(qrService.imageViewToByte(qrImgView));
        paymentQrScan.setQrImgName(qrNameEditTxt.getText().toString().trim());
        return paymentQrScan;

    }

    private void saveScannedQRImg(PaymentQrScan merchantscan){
        db.saveScannedPaymentQrCode(
                merchantscan.getQrImgName(),
                merchantscan.getQrImg()
        );
    }

    protected void shareQRCode(Bitmap bitmap) {
        Intent shareQRCodeIntent = new Intent(Intent.ACTION_SEND);
        shareQRCodeIntent.setType("image/*");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                bitmap, "APS payment QR Code", "Pay by one click within few seconds");
        Uri imageUri =  Uri.parse(path);
        shareQRCodeIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(shareQRCodeIntent, "Share this QR Code image via"));

    }
}

