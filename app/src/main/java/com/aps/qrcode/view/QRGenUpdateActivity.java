package com.aps.qrcode.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.aps.qrcode.R;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.model.PaymentQrGen;
import com.aps.qrcode.util.PaymentFields;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


/**
 * This activity handles the payment generated QR Code update.
 */
public class QRGenUpdateActivity extends AppCompatActivity {
    EditText clientTypeEditTxt, clientCategoryEditTxt, clientCompanyNameEditTxt, mobileEditTxt, emailEditTxt, province,district, bankNameEditTxt, accountNumberEditTxt, currencyTypeEditTxt, amountEditTxt;
    Button nextBtn,qrScannedSaveBtn;
    CheckBox paymentRolesAndPolicyCheckBox;

    private DBHelper db;
    private int qr_img_gen_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_qr_contents_displayer);

        clientTypeEditTxt = (EditText) findViewById(R.id.edit_txt_client_type);
        clientCategoryEditTxt = (EditText) findViewById(R.id.edit_txt_client_category);
        clientCompanyNameEditTxt = (EditText) findViewById(R.id.edit_txt_client_company_name);
        mobileEditTxt = (EditText) findViewById(R.id.edit_txt_mobile);
        emailEditTxt = (EditText) findViewById(R.id.edit_txt_email);
        province = (EditText) findViewById(R.id.edit_txt_client_province);
        district = (EditText) findViewById(R.id.edit_txt_client_district);
        bankNameEditTxt = (EditText) findViewById(R.id.edit_txt_bank_name);
        accountNumberEditTxt = (EditText) findViewById(R.id.edit_txt_account_number);
        currencyTypeEditTxt = (EditText) findViewById(R.id.edit_txt_currency_type);
        amountEditTxt = (EditText) findViewById(R.id.edit_txt_amount);

        paymentRolesAndPolicyCheckBox = (CheckBox) findViewById(R.id.chb_payment_roles_policy);
        paymentRolesAndPolicyCheckBox.setVisibility(View.GONE);

        nextBtn = (Button) findViewById(R.id.btn_proceed_payment_qr);
        qrScannedSaveBtn = (Button) findViewById(R.id.btn_save_scanned_qr);
        qrScannedSaveBtn.setVisibility(View.GONE);

        db = new DBHelper(this, DATABASE_NAME, null,DATABASE_VERSION);

        qr_img_gen_id = getIntent().getIntExtra("qr_gen_id",0);
        boolean qr_update = getIntent().getBooleanExtra("qr_update", false);
        int favorite = getIntent().getIntExtra("favorite",0);
        long id = qr_img_gen_id;
        if(qr_img_gen_id != 0 && qr_update) {

            nextBtn.setText("Next");
            displayPaymentGeneratedQRCodeDetails(db.getGeneratedPaymentQrCodeById(id));
            //Toast.makeText(QRGenUpdateActivity.this, "QR Details:  "+merchant,Toast.LENGTH_LONG).show();
        }else if(qr_img_gen_id != 0 && !qr_update){
            clientTypeEditTxt.setKeyListener(null);
            clientCategoryEditTxt.setKeyListener(null);
            clientCompanyNameEditTxt.setKeyListener(null);
            mobileEditTxt.setKeyListener(null);
            emailEditTxt.setKeyListener(null);
            province.setKeyListener(null);
            district.setKeyListener(null);
            bankNameEditTxt.setKeyListener(null);
            accountNumberEditTxt.setKeyListener(null);
            currencyTypeEditTxt.setKeyListener(null);
            amountEditTxt.setKeyListener(null);
            nextBtn.setText("Show Next Details");
            displayPaymentGeneratedQRCodeDetails(db.getGeneratedPaymentQrCodeById(id));
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentQrGen paymentQrGen = db.getGeneratedPaymentQrCodeById(id);
                String qrImgName = paymentQrGen.getQrImgName();
                byte[] qrImg = paymentQrGen.getQrImg();
                Intent intent = new Intent(QRGenUpdateActivity.this, QRDisplayActivity.class);
                intent.putExtra("qrID",qr_img_gen_id);
                intent.putExtra("edit_txt_qr_img_name", qrImgName);
                intent.putExtra("QrImgView",qrImg);
                intent.putExtra("qrImgDetails",getUpdatedQRCode());
                intent.putExtra("qr_update",qr_update);
                intent.putExtra("favorite",favorite);
                intent.putExtra("noSaveUpdate",true);
                startActivity(intent);
                //Toast.makeText(QRGenUpdateActivity.this, "QRImg id is: "+qr_img_gen_id,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayPaymentGeneratedQRCodeDetails(PaymentQrGen paymentQrGenQR) {
        clientTypeEditTxt.setText(paymentQrGenQR.getQrCategory());
        if(paymentQrGenQR.getMerchantCategory().equals("NULL")){
            clientCategoryEditTxt.setVisibility(View.GONE);
        }else if(!paymentQrGenQR.getMerchantCategory().equals("NULL")){
            clientCategoryEditTxt.setVisibility(View.VISIBLE);
            clientCategoryEditTxt.setText(paymentQrGenQR.getMerchantCategory());
        }
        clientCompanyNameEditTxt.setText(paymentQrGenQR.getMerchantCompanyName());
        mobileEditTxt.setText(paymentQrGenQR.getMerchantPhone());
        emailEditTxt.setText(paymentQrGenQR.getMerchantEmail());
        province.setText(paymentQrGenQR.getMerchantProvince());
        district.setText(paymentQrGenQR.getMerchantDistrict());
        bankNameEditTxt.setText(paymentQrGenQR.getBankName());
        accountNumberEditTxt.setText(paymentQrGenQR.getAccountNumber());
        currencyTypeEditTxt.setText(paymentQrGenQR.getCurrency());
        amountEditTxt.setText(paymentQrGenQR.getAmount());
    }

    private String getUpdatedQRCode(){
        StringBuilder qrUpdateStrBuilder = new StringBuilder();
        qrUpdateStrBuilder.append(PaymentFields.QR_CODE_VERSION+PaymentFields.KEY_VALUE_DELIMITER+"01"+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.QR_Code_TYPE+PaymentFields.KEY_VALUE_DELIMITER+clientTypeEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        if(TextUtils.isEmpty(clientCategoryEditTxt.getText().toString().trim())){
            qrUpdateStrBuilder.append(PaymentFields.MERCHANT_CATEGORY+PaymentFields.KEY_VALUE_DELIMITER+"NULL"+PaymentFields.FIELDS_DELIMITER);

        }else if(!TextUtils.isEmpty(clientCategoryEditTxt.getText().toString().trim())){
            qrUpdateStrBuilder.append(PaymentFields.MERCHANT_CATEGORY+PaymentFields.KEY_VALUE_DELIMITER+clientCategoryEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        }
        qrUpdateStrBuilder.append(PaymentFields.MERCHANT_COMPANY_NAME+PaymentFields.KEY_VALUE_DELIMITER+clientCompanyNameEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.MOBILE+PaymentFields.KEY_VALUE_DELIMITER+mobileEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.EMAIL+PaymentFields.KEY_VALUE_DELIMITER+emailEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.PROVINCE+PaymentFields.KEY_VALUE_DELIMITER+province.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.DISTRICT+PaymentFields.KEY_VALUE_DELIMITER+district.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.BANK_NAME+PaymentFields.KEY_VALUE_DELIMITER+bankNameEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.PAN+PaymentFields.KEY_VALUE_DELIMITER+accountNumberEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.CURRENCY+PaymentFields.KEY_VALUE_DELIMITER+currencyTypeEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrUpdateStrBuilder.append(PaymentFields.AMOUNT+PaymentFields.KEY_VALUE_DELIMITER+amountEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        return qrUpdateStrBuilder.toString();

    }
}
