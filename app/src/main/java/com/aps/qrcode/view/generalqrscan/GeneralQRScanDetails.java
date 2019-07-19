package com.aps.qrcode.view.generalqrscan;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aps.qrcode.R;
import com.aps.qrcode.database.DBManager;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.helper.ZXingHelper;
import com.aps.qrcode.model.GeneralQrScan;
import com.aps.qrcode.serviceimpl.QRServiceImpl;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;

public class GeneralQRScanDetails extends AppCompatActivity {

    private final Context context = this;
    private TextView qrInfoTxtView;
    private EditText qrContentEditTxt;
    private Button decryptBtn;
    private ImageView qrScanImgHolder;

    private DBHelper db;
    private QRServiceImpl qrService;
    private String qrDecryptedContent;
    private ZXingHelper zXingHelper;
    private boolean decrypted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_payment_qrcode_scan_result);

        db = new DBHelper(this, DATABASE_NAME, null, DATABASE_VERSION);
        db.queryData(DBManager.PAYMENT_QR_GEN_TABLE);
        qrService = new QRServiceImpl();
        zXingHelper = new ZXingHelper();

        qrContentEditTxt = (EditText) findViewById(R.id.edit_txt_qr_content);

        decryptBtn = (Button) findViewById(R.id.btn_qr_decrypt);

        qrService = new QRServiceImpl();

        qrInfoTxtView = (TextView) findViewById(R.id.txt_vw_encryption_details);

        qrScanImgHolder = (ImageView) findViewById(R.id.img_vw_qr_scan_img_holder);

        int scanned_qr_id = getIntent().getIntExtra("scanned_qr_id", 0);

        GeneralQrScan generalQrScan = db.getScannedGeneralQrCodeById((long) scanned_qr_id);

        qrInfoTxtView.setText("The details of Scanned Secret QR code.");
        decryptBtn.setText("Next");

        qrScanImgHolder.setImageBitmap(BitmapFactory.decodeByteArray(generalQrScan.getQrImg(), 0, generalQrScan.getQrImg().length));

        qrContentEditTxt.setText(qrService.removeEncryptionKeyFromSecretQR(zXingHelper.qr2Txt(qrScanImgHolder)));
        qrContentEditTxt.setOnKeyListener(null);
        qrContentEditTxt.setFocusable(false);

        decryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeneralQRScanDetails.this, ScannedGeneralQRCodeContentDisplay.class);
                intent.putExtra("scan_qr_id", generalQrScan.getQrId());
                intent.putExtra("qr_details", true);
                startActivity(intent);
            }
        });

    }
}
