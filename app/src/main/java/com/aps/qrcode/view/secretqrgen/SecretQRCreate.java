package com.aps.qrcode.view.secretqrgen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.serviceimpl.QRServiceImpl;


public class SecretQRCreate extends AppCompatActivity {

    private final Context mContext = this;
    private Button mCreatQrBtn;
    private EditText mQrContentEditTxt;


    private QRServiceImpl qrService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_qrcreate);

        qrService = new QRServiceImpl();

        mCreatQrBtn = findViewById(R.id.secret_qr_create_btn);
        mQrContentEditTxt = findViewById(R.id.edit_txt_qr_content);


        mCreatQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the qr code content from the edit text
                String qr_content = mQrContentEditTxt.getText().toString().trim();

                if (qr_content.isEmpty()) {
                    Toast.makeText(SecretQRCreate.this, "No content to be decoded!", Toast.LENGTH_LONG).show();
                    return;
                }
                // get qr_key_prompt.xml view
                LayoutInflater li = LayoutInflater.from(mContext);
                View promptView = li.inflate(R.layout.qr_key_prompt, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                // set qr_key_prompt.xml to alertdialog builder
                alertDialogBuilder.setView(promptView);
                final EditText qrPass = (EditText) promptView.findViewById(R.id.qr_key);
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String encryptionKey = qrPass.getText().toString().trim();
                                        String qrEncryptedContent = qrService.encryptQRCodeContent(qr_content + "\n" + "|*_pass:" + encryptionKey + "_*|");

                                        if (qrPass.length() < 2) {
                                            Toast.makeText(getApplicationContext(), "The password is very short, easy to guess!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        Toast.makeText(getApplicationContext(), qrEncryptedContent, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SecretQRCreate.this, SecretQRCodeImgDisplay.class);
                                        intent.putExtra("qrCodeContent", qrEncryptedContent);
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });

    }


}
