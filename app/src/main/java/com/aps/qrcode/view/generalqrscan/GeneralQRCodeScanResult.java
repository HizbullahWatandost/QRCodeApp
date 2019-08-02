package com.aps.qrcode.view.generalqrscan;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.database.DBManager;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.helper.ZXingHelper;
import com.aps.qrcode.model.GeneralQrScan;
import com.aps.qrcode.serviceimpl.QRServiceImpl;
import com.aps.qrcode.util.QRCodeProperties;
import com.google.zxing.WriterException;

import java.io.UnsupportedEncodingException;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class GeneralQRCodeScanResult extends AppCompatActivity {

    private final Context context = this;
    private TextView qrInfoTxtView;
    private EditText qrContentEditTxt, qrNameEditTxt;
    private Button decryptBtn;
    private ImageView qrScanImgHolder;
    private String general_qr_scan_content;

    private DBHelper db;
    private QRServiceImpl qrService;
    private ZXingHelper zXingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_payment_qrcode_scan_result);

        Toast.makeText(this, "result", Toast.LENGTH_LONG).show();

        db = new DBHelper(this, DATABASE_NAME, null, DATABASE_VERSION);
        db.queryData(DBManager.PAYMENT_QR_GEN_TABLE);
        qrService = new QRServiceImpl();
        zXingHelper = new ZXingHelper();

        general_qr_scan_content = getIntent().getStringExtra("general_qr_contents");
        Toast.makeText(this, "Scan non payment qr code", Toast.LENGTH_LONG).show();

        qrContentEditTxt = findViewById(R.id.edit_txt_qr_content);

        decryptBtn = findViewById(R.id.btn_qr_decrypt);
        decryptBtn.setText("Save Scanned QR Code");

        qrService = new QRServiceImpl();

        qrInfoTxtView = findViewById(R.id.txt_vw_encryption_details);
        qrInfoTxtView.setVisibility(View.GONE);

        qrContentEditTxt.setText(general_qr_scan_content);
        qrContentEditTxt.setOnKeyListener(null);
        qrContentEditTxt.setFocusable(false);
        qrNameEditTxt = findViewById(R.id.edit_txt_qr_img_name);

        qrScanImgHolder = findViewById(R.id.img_vw_qr_scan_img_holder);


        Bitmap bitmap = null;
        try {
            bitmap = zXingHelper.createQRImage(QRCodeProperties.QR_CHARACTER_SET,
                    QRCodeProperties.QR_ERROR_CORRECTION_LEVEL,
                    QRCodeProperties.WIDTH,
                    QRCodeProperties.HEIGHT,
                    general_qr_scan_content);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        qrScanImgHolder.setImageBitmap(bitmap);
        qrContentEditTxt.setText(general_qr_scan_content);
        qrNameEditTxt.setVisibility(View.VISIBLE);

        decryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(qrContentEditTxt.getText().toString().trim())) {
                    Toast.makeText(GeneralQRCodeScanResult.this, "No content to be decoded!", Toast.LENGTH_LONG).show();
                    return;

                } else {
                    if (qrNameEditTxt.getText().toString().trim().isEmpty()) {
                        Toast.makeText(GeneralQRCodeScanResult.this, "Please provide a name for your scaned qr code", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GeneralQrScan generalQrScan = new GeneralQrScan();
                    generalQrScan.setQrImg(qrService.imageViewToByte(qrScanImgHolder));
                    generalQrScan.setQrImgName(qrNameEditTxt.getText().toString().trim());
                    saveScannedGeneralQR(generalQrScan);
                    Toast.makeText(GeneralQRCodeScanResult.this, "The scanned QR Code successfully saved!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void saveScannedGeneralQR(GeneralQrScan generalQrScan) {
        db.saveScannedGeneralQrCode(generalQrScan.getQrImg(), generalQrScan.getQrImgName());
    }
}
