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


public class GeneralQRCreate extends AppCompatActivity {

    private final Context mContext = this;
    private Button mCreatQrBtn;
    private EditText mQrContentEditTxt;
    private TextView mQrCreateHint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_qrcreate);

        mCreatQrBtn = findViewById(R.id.secret_qr_create_btn);
        mQrContentEditTxt = findViewById(R.id.edit_txt_qr_content);
        mQrCreateHint = findViewById(R.id.qr_create_hint);
        mQrCreateHint.setVisibility(View.GONE);

        mCreatQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the qr code content from the edit text
                String qr_content = mQrContentEditTxt.getText().toString().trim();

                if (qr_content.isEmpty()) {
                    Toast.makeText(GeneralQRCreate.this, "No content to be decoded!", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(GeneralQRCreate.this, GeneralQRCodeImgDisplay.class);
                intent.putExtra("qrCodeContent", mQrContentEditTxt.getText().toString().trim());
                startActivity(intent);
            }
        });

    }


}
