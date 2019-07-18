package com.aps.qrcode.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.aps.qrcode.R;


/**
 * After successfull scanning QR Code, the result will be forwarded to this activity
 * and this activity displays all the payment QR Code details in a form.
 */
public class QRScanResultDisplayActivity extends AppCompatActivity {

    private EditText clientTypeEditTxt, clientCategoryEditTxt, clientCompanyNameEditTxt, mobileEditTxt, emailEditTxt, province,district, bankNameEditTxt, accountNumberEditTxt, currencyTypeEditTxt, amountEditTxt;
    private CheckBox paymentRolesAndPolicyCheckBox;
    private Button proceedPaymentBtn, saveScannedQRImgBtn;

    private String qRScanResult;

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

        proceedPaymentBtn = (Button) findViewById(R.id.btn_proceed_payment_qr);
        saveScannedQRImgBtn = (Button) findViewById(R.id.btn_save_scanned_qr);

        // getting the QR Scanned data from the MainActivity after the successfully scan
        qRScanResult = getIntent().getStringExtra("qr_contents");

        // getting the Scanned QR Code data after the user click on Details button
        // if there is a request for displaying the QR Code details
        if(!TextUtils.isEmpty(getIntent().getStringExtra("scanned_qr_code_details"))){
            // get the qr code details using intent
            qRScanResult = getIntent().getStringExtra("scanned_qr_code_details");
            paymentRolesAndPolicyCheckBox.setVisibility(View.GONE);
            proceedPaymentBtn.setVisibility(View.GONE);
            saveScannedQRImgBtn.setVisibility(View.GONE);
        }
        // display the qr content
        displayQRCodeResult(qRScanResult);


        proceedPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPaymentRolesAndPolicyAccepted()) {
                    Toast.makeText(QRScanResultDisplayActivity.this, "Please accept the payment roles and policy", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(QRScanResultDisplayActivity.this, "Your payment has been proceeded", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(QRScanResultDisplayActivity.this, MainActivity.class));
                }
            }
        });

        saveScannedQRImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRScanResultDisplayActivity.this,SaveScannedQRImgActivity.class);
                intent.putExtra("scannedQRImgDetails",qRScanResult);
                startActivity(intent);
            }
        });
    }

    // if the user click on view details of scanned QR Code, then we need to only show the details of scanned QR Code.
    // all other elements must be hidden.


    private void displayQRCodeResult(final String qr_scan_result) {
        String[] qr2Txt = qr_scan_result.split("\\*");
        for (String paymentField : qr2Txt) {
            String[] keyValuePair = paymentField.split("\\:");
            String key = keyValuePair[0];
            String value = keyValuePair[1];
            switch (key) {
                case "2":
                    clientTypeEditTxt.setText(value);
                    break;
                case "3":
                    clientCompanyNameEditTxt.setText(value);
                    break;
                case "4":
                    if (value.equals("NULL")) {
                        clientCategoryEditTxt.setVisibility(View.GONE);
                        break;
                    } else {
                        clientCategoryEditTxt.setVisibility(View.VISIBLE);
                        clientCategoryEditTxt.setText(value);
                        break;
                    }
                case "5":
                    mobileEditTxt.setText(value);
                    break;
                case "6":
                    emailEditTxt.setText(value);
                    break;
                case "7":
                    province.setText(value);
                    break;
                case "8":
                    district.setText(value);
                    break;
                case "9":
                    bankNameEditTxt.setText(value);
                    break;
                case "10":
                    accountNumberEditTxt.setText(value);
                    break;
                case "11":
                    currencyTypeEditTxt.setText(value);
                    break;
                case "12":
                    amountEditTxt.setText(value);
            }
        }
    }

    private boolean isPaymentRolesAndPolicyAccepted() {
        if (paymentRolesAndPolicyCheckBox.isChecked()) {
            return true;
        } else {
            return false;
        }
    }
}
