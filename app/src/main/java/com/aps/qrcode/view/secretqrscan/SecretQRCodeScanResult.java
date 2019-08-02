package com.aps.qrcode.view.secretqrscan;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
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


public class SecretQRCodeScanResult extends AppCompatActivity {

    private final Context context = this;
    private TextView qrInfoTxtView;
    private EditText qrContentEditTxt, qrNameEditTxt;
    private Button decryptBtn;
    private ImageView qrScanImgHolder;
    private String non_payment_qr_scan_content;

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

        non_payment_qr_scan_content = getIntent().getStringExtra("secret_qr_contents");
        Toast.makeText(this, "Scan non payment qr code", Toast.LENGTH_LONG).show();

        qrContentEditTxt = findViewById(R.id.edit_txt_qr_content);

        decryptBtn = findViewById(R.id.btn_qr_decrypt);

        qrService = new QRServiceImpl();

        qrInfoTxtView = findViewById(R.id.txt_vw_encryption_details);

        qrContentEditTxt.setText(qrService.toNonSenseConverter(non_payment_qr_scan_content));
        qrNameEditTxt = findViewById(R.id.edit_txt_qr_img_name);

        qrScanImgHolder = findViewById(R.id.img_vw_qr_scan_img_holder);

        decryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!decrypted) {
                    if (TextUtils.isEmpty(qrContentEditTxt.getText().toString().trim())) {
                        Toast.makeText(SecretQRCodeScanResult.this, "No content to be decoded!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // get qr_key_prompt.xml view
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptView = li.inflate(R.layout.qr_key_prompt, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set qr_key_prompt.xml to alertdialog builder
                    alertDialogBuilder.setView(promptView);
                    final EditText qrPass = (EditText) promptView.findViewById(R.id.qr_key);
                    // set dialog message
                    alertDialogBuilder.setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            qrDecryptedContent = qrService.decryptQRCodeContent(non_payment_qr_scan_content);
                                            int startIndexOfEncryptionKey = qrDecryptedContent.indexOf("|*_pass:") + ("|*_pass:").length();
                                            int endIndexOfEncryptionKey = qrDecryptedContent.lastIndexOf("_*|");

                                            String encryptionKey = qrDecryptedContent.substring(startIndexOfEncryptionKey, endIndexOfEncryptionKey);
                                            String decryptionKey = qrPass.getText().toString().trim();
                                            //Toast.makeText(GeneralQRCodeScanResult.this, "QR text: "+qrContentEditTxt.getText().toString(),Toast.LENGTH_LONG).show();

                                            //String decryptionKey = qrPass.getText().toString().trim();
                                            //String qrDecryptedContent = qrService.encryptQRCodeContent(qr_content+"\n"+"|*_pass:"+encryptionKey+"_*|");

                                            if (decryptionKey.equals(encryptionKey)) {
                                                Toast.makeText(getApplicationContext(), "Correct decryption key", Toast.LENGTH_LONG).show();
                                                String decryptedQrContent = qrDecryptedContent.substring(0, qrDecryptedContent.indexOf("|*_pass:"));
                                                Bitmap bitmap = null;
                                                try {
                                                    bitmap = zXingHelper.createQRImage(QRCodeProperties.QR_CHARACTER_SET,
                                                            QRCodeProperties.QR_ERROR_CORRECTION_LEVEL,
                                                            QRCodeProperties.WIDTH,
                                                            QRCodeProperties.HEIGHT,
                                                            non_payment_qr_scan_content);
                                                } catch (WriterException e) {
                                                    e.printStackTrace();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                                qrScanImgHolder.setImageBitmap(bitmap);
                                                qrContentEditTxt.setText(decryptedQrContent);
                                                qrInfoTxtView.setVisibility(View.GONE);
                                                qrNameEditTxt.setVisibility(View.VISIBLE);
                                                decryptBtn.setText("Save");
                                                decrypted = true;
                                            } else {
                                                Toast.makeText(getApplicationContext(), "wrong decryption key", Toast.LENGTH_LONG).show();
                                                decrypted = false;
                                            }
                                            //Toast.makeText(getApplicationContext(),encryptionKey,Toast.LENGTH_LONG).show();
                                            // Intent intent = new Intent(GeneralQRCreate.this, GeneralQRCodeImgDisplay.class);
                                            // intent.putExtra("qrCodeContent",qrEncryptedContent);
                                            // startActivity(intent);
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

                } else {
                    Toast.makeText(SecretQRCodeScanResult.this, "save button", Toast.LENGTH_LONG).show();
                    if (qrNameEditTxt.getText().toString().trim().isEmpty()) {
                        Toast.makeText(SecretQRCodeScanResult.this, "Please provide a name for your scaned qr code", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GeneralQrScan generalQrScan = new GeneralQrScan();
                    generalQrScan.setQrImg(qrService.imageViewToByte(qrScanImgHolder));
                    Toast.makeText(SecretQRCodeScanResult.this, "the content is: " + non_payment_qr_scan_content, Toast.LENGTH_LONG).show();
                    generalQrScan.setQrImgName(qrNameEditTxt.getText().toString().trim());
                    generalQrScan.setSecretQr(1);
                    saveScannedSecureQR(generalQrScan);
                    Toast.makeText(SecretQRCodeScanResult.this, "The scanned QR Code successfully saved!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void saveScannedSecureQR(GeneralQrScan generalQrScan) {
        db.saveScannedGeneralSecretQrCode(generalQrScan.getQrImg(), generalQrScan.getQrImgName(), generalQrScan.getSecretQr());
    }
}
