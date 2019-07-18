package com.aps.qrcode.view;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


/**
 * The activity is used to update the Scanned QR Code name
 */
public class QRScannedUpdateActivity extends AppCompatActivity {

    private ImageView qrImgView;
    private EditText qrNameEditTxt;
    private Button btnUpdate;

    private DBHelper db;
    private int qrId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanned_update);

        db = new DBHelper(this, DATABASE_NAME, null, DATABASE_VERSION);
        db.queryData(DBManager.PAYMENT_QR_SCAN_TABLE);


        qrImgView = (ImageView) findViewById(R.id.img_vw_qr_img);
        qrNameEditTxt = (EditText) findViewById(R.id.edit_txt_qr_img_name);
        btnUpdate = (Button) findViewById(R.id.btn_qr_save);

        /**
         * Receiving the QR Code details if the user wants to update the name of the scanned QR Code
         */
        qrId = getIntent().getIntExtra("qr_scanned_id", 0);
        String qrImgName = getIntent().getStringExtra("qr_name");
        byte[] qrImg = getIntent().getByteArrayExtra("QrImgView");

        // Generating and displaying QR Code image
        if (!TextUtils.isEmpty(String.valueOf(qrId)) && !TextUtils.isEmpty(qrImgName)) {
            qrImgView.setImageBitmap(BitmapFactory.decodeByteArray(qrImg, 0, qrImg.length));
            qrNameEditTxt.setText(qrImgName);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(qrNameEditTxt.getText().toString().trim())) {
                    db.updateScannedGeneralQrCode(qrId, qrNameEditTxt.getText().toString().trim());
                    Toast.makeText(QRScannedUpdateActivity.this, "QR Code updated successfully!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(QRScannedUpdateActivity.this, QRScannedHistory.class));
                } else {
                    Toast.makeText(QRScannedUpdateActivity.this, "Please enter a name for QR Image", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
