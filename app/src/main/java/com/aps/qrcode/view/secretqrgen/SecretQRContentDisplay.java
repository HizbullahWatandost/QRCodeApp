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
import android.widget.TextView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.model.GeneralQrGen;
import com.aps.qrcode.serviceimpl.QRServiceImpl;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class SecretQRContentDisplay extends AppCompatActivity {

    private DBHelper db;
    private final Context mContext = this;
    private int mQrImgGenId;
    private EditText mQrContentEditTxt;
    private Button mSecretQrUpdateBtn;
    private TextView mQrUpdateHintTxtView;
    private QRServiceImpl qrService;
    private boolean locked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_payment_qrcontent_display);

        mSecretQrUpdateBtn = (Button) findViewById(R.id.btn_qr_unlock);
        mQrContentEditTxt = (EditText) findViewById(R.id.edit_txt_qr_content);
        mQrUpdateHintTxtView = (TextView) findViewById(R.id.txt_vw_qr_update_hing);

        db = new DBHelper(this, DATABASE_NAME, null,DATABASE_VERSION);

        qrService = new QRServiceImpl();

        locked = false;

        mQrImgGenId = getIntent().getIntExtra("qr_gen_id", 0);
        boolean qr_update = getIntent().getBooleanExtra("qr_update", false);
        long id = mQrImgGenId;

        GeneralQrGen generalQrGen = db.getGeneratedGeneralQrCodeById(id);
        String qrContent = generalQrGen.getQrCodeContent();
        mQrContentEditTxt.setText(qrContent);

        if (mQrImgGenId != 0 && qr_update && !locked) {
            mSecretQrUpdateBtn.setText("Unlock QR Code");
        }

        mSecretQrUpdateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!locked) {
                            if (unlockQRCode(mQrContentEditTxt.getText().toString().trim())) {
                                Toast.makeText(SecretQRContentDisplay.this, "QR is unlocked now", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } else {
                            if (mQrContentEditTxt.getText().toString().trim().isEmpty()) {
                                Toast.makeText(SecretQRContentDisplay.this, "No content to be decoded!", Toast.LENGTH_LONG).show();
                            } else {

                                String qr_content_with_key = qrService.encryptQRCodeContent(mQrContentEditTxt.getText().toString().trim()) + qrService.addEncryptionKeyToQR(qrService.getSecretQREncryptionKey(qrContent));
                                Intent intent = new Intent(SecretQRContentDisplay.this, SecretQRCodeImgDisplay.class);
                                intent.putExtra("secret_qr_img_id", mQrImgGenId);
                                intent.putExtra("edit_txt_qr_img_name", generalQrGen.getQrImgName());
                                intent.putExtra("qr_img_content", qr_content_with_key);
                                intent.putExtra("qr_img_update", true);
                                startActivity(intent);
                            }
                        }
                    }
                });

    }

    private boolean unlockQRCode(String qr_content) {
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
                                String encryptionKey = qrService.getSecretQREncryptionKey(qr_content);
                                String decryptionKey = qrPass.getText().toString().trim();

                                if(!encryptionKey.equals(decryptionKey)){
                                    Toast.makeText(getApplicationContext(),"Invalid decryption key!",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String getOnlyQRContent = qrService.removeEncryptionKeyFromSecretQR(qr_content);
                                mQrContentEditTxt.setText(getOnlyQRContent);
                                mSecretQrUpdateBtn.setText("Update QR Code");
                                mQrUpdateHintTxtView.setVisibility(View.GONE);
                                locked = true;
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

        return locked;

    }
}
