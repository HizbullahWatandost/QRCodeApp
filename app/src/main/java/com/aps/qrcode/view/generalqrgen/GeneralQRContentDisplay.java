package com.aps.qrcode.view.generalqrgen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.model.GeneralQrGen;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class GeneralQRContentDisplay extends AppCompatActivity {

    private final Context mContext = this;
    private DBHelper db;
    private int mQrImgGenId;
    private EditText mQrContentEditTxt;
    private Button mSecretQrUpdateBtn;
    private TextView mQrUpdateHintTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_payment_qrcontent_display);

        mSecretQrUpdateBtn = (Button) findViewById(R.id.btn_qr_unlock);
        mSecretQrUpdateBtn.setText("Update QR Code");
        mQrContentEditTxt = (EditText) findViewById(R.id.edit_txt_qr_content);
        mQrUpdateHintTxtView = (TextView) findViewById(R.id.txt_vw_qr_update_hing);
        mQrUpdateHintTxtView.setVisibility(View.GONE);

        db = new DBHelper(this, DATABASE_NAME, null, DATABASE_VERSION);


        mQrImgGenId = getIntent().getIntExtra("qr_gen_id", 0);
        boolean qr_update = getIntent().getBooleanExtra("qr_update", false);
        long id = mQrImgGenId;

        GeneralQrGen generalQrGen = db.getGeneratedGeneralQrCodeById(id);
        String qrContent = generalQrGen.getQrCodeContent();
        mQrContentEditTxt.setText(qrContent);


        mSecretQrUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mQrContentEditTxt.getText().toString().trim().isEmpty()) {
                    Toast.makeText(GeneralQRContentDisplay.this, "No content to be decoded!", Toast.LENGTH_LONG).show();
                } else {

                    String qr_content = mQrContentEditTxt.getText().toString().trim();
                    Intent intent = new Intent(GeneralQRContentDisplay.this, GeneralQRCodeImgDisplay.class);
                    intent.putExtra("qr_img_id", mQrImgGenId);
                    intent.putExtra("qr_img_name", generalQrGen.getQrImgName());
                    intent.putExtra("qr_img_content", qr_content);
                    intent.putExtra("qr_img_update", true);
                    startActivity(intent);
                }
            }
        });

    }
}
