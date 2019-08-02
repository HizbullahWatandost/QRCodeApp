package com.aps.qrcode.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.util.PaymentFields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * This fragment is used to generate payment QR Code for merchant.
 * The merchant enters all the details, then click on create QR Code button,
 * then it will ask for a name to be assigned to generated QR Code.
 */
public class QRCreateFragment extends Fragment {

    private Spinner qrOwnerSpinner, companyCategorySpinner, provinceSpinner, districtSpinner, bankMemberSpinner;
    private String clientTypeName;
    private EditText clientNameEditTxt, companyNameEditTxt, mobileEditTxt, emailEditTxt, accountNoEditTxt, amountEditTxt;
    private RadioButton selectedCurrency;
    private CheckBox policyCheck;
    private Button qrCreateBtn;

    //the alert message in case of invalid input
    private String validAertMsg = "";

    //the details from which we create a QR Code for the user
    private String qr_data = "";

    public QRCreateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrcreate, container, false);

        qrOwnerSpinner = view.findViewById(R.id.edit_txt_client_type);
        companyCategorySpinner = view.findViewById(R.id.spinner_company_category);
        provinceSpinner = view.findViewById(R.id.spinner_province);
        districtSpinner = view.findViewById(R.id.spinner_district);
        bankMemberSpinner = view.findViewById(R.id.spinner_bank_members);

        clientNameEditTxt = view.findViewById(R.id.edit_txt_client_name);
        companyNameEditTxt = view.findViewById(R.id.edit_txt_company_name);
        mobileEditTxt = view.findViewById(R.id.edit_txt_mobile);
        emailEditTxt = view.findViewById(R.id.edit_txt_email);
        accountNoEditTxt = view.findViewById(R.id.edit_txt_account_number);
        amountEditTxt = view.findViewById(R.id.edit_txt_amount);


        qrCreateBtn = view.findViewById(R.id.btn_qr_create);

        addItemsInSpinner(R.array.qr_code_owner, qrOwnerSpinner);
        addListenerOnQrOwnerSpinnerItemSelection();

        addItemsInSpinner(R.array.company_category, companyCategorySpinner);
        addListenerOnSpinnerItemSelection(companyCategorySpinner);

        addItemsInSpinner(R.array.province, provinceSpinner);
        addItemsInSpinner(R.array.kabul_districts, districtSpinner);
        addListenerOnSpinnerItemSelection(districtSpinner);

        addListenerOnProvinceItemSelection(provinceSpinner);

        addItemsInSpinner(R.array.bank_members, bankMemberSpinner);
        addListenerOnSpinnerItemSelection(bankMemberSpinner);

        RadioGroup currencyRadioGroup = view.findViewById(R.id.rg_currency);
        int currencySelect = currencyRadioGroup.getCheckedRadioButtonId();
        selectedCurrency = view.findViewById(currencySelect);

        policyCheck = view.findViewById(R.id.chb_payment_qr_code_policy);


        qrCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFormDataToNextActivity();
            }
        });

        return view;
    }

    private void addListenerOnQrOwnerSpinnerItemSelection() {
        qrOwnerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clientTypeName = qrOwnerSpinner.getSelectedItem().toString();
                if (qrOwnerSpinner.getSelectedItemPosition() == 1) {
                    clientNameEditTxt.setVisibility(View.VISIBLE);
                    companyCategorySpinner.setVisibility(View.GONE);
                    companyNameEditTxt.setVisibility(View.GONE);
                } else if (qrOwnerSpinner.getSelectedItemPosition() == 2) {
                    clientNameEditTxt.setVisibility(View.GONE);
                    companyCategorySpinner.setVisibility(View.VISIBLE);
                    companyNameEditTxt.setVisibility(View.VISIBLE);
                } else {
                    clientNameEditTxt.setVisibility(View.GONE);
                    companyCategorySpinner.setVisibility(View.GONE);
                    companyNameEditTxt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clientNameEditTxt.setVisibility(View.GONE);
                companyCategorySpinner.setVisibility(View.GONE);
                companyNameEditTxt.setVisibility(View.GONE);
            }

        });
    }

    private void addItemsInSpinner(int arr_name, Spinner spinner) {
        ArrayAdapter<String> adapter_name = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_item, getResources().getStringArray(arr_name)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(getResources().getColor(R.color.lightGray));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.whiteSmoke));
                }
                return view;
            }
        };

        adapter_name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_name);

    }


    private void addListenerOnSpinnerItemSelection(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void addListenerOnProvinceItemSelection(final Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spinner.getSelectedItem().toString()) {
                    case "Kabul":
                        addItemsInSpinner(R.array.kabul_districts, districtSpinner);
                        break;
                    case "Mazar":
                        addItemsInSpinner(R.array.mazar_districts, districtSpinner);
                        break;
                   /* case "other provinces":
                        addItemsInSpinner(R.array.mazar_districts,districtSpinner);
                        break;*/
                    default:
                        addItemsInSpinner(R.array.kabul_districts, districtSpinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean validateInput() {

        clientTypeName = qrOwnerSpinner.getSelectedItem().toString();
        String clineName = clientNameEditTxt.getText().toString().trim();
        String companyName = companyNameEditTxt.getText().toString().trim();
        String email = emailEditTxt.getText().toString().trim();
        String mobile = mobileEditTxt.getText().toString().trim();
        String bankAccount = accountNoEditTxt.getText().toString().trim();
        String amount = amountEditTxt.getText().toString().trim();

        if (qrOwnerSpinner.getSelectedItemPosition() == 0) {
            validAertMsg = "Please select the QR Code owner";
            return false;

        } else if (qrOwnerSpinner.getSelectedItemPosition() == 1 && TextUtils.isEmpty(clineName)) {
            validAertMsg = "Please enter your name";
            return false;

        } else if (qrOwnerSpinner.getSelectedItemPosition() == 1 && !isValidFullName(clineName)) {
            validAertMsg = "Invalid name, name can only contain characters and space!";
            return false;

        } else if (qrOwnerSpinner.getSelectedItemPosition() == 1 && clineName.length() > 100) {
            validAertMsg = "Invalid name, name is too long!";
            return false;

        } else if (qrOwnerSpinner.getSelectedItemPosition() == 1 && clineName.length() < 3) {
            validAertMsg = "invalid name, name is too short!";
            return false;

        } else if (qrOwnerSpinner.getSelectedItemPosition() == 2 && companyCategorySpinner.getSelectedItemPosition() == 0) {
            validAertMsg = "Please select the company category";
            return false;
        } else if (qrOwnerSpinner.getSelectedItemPosition() == 2 && TextUtils.isEmpty(companyName)) {
            validAertMsg = "Enter the company name";
            return false;
        } else if (qrOwnerSpinner.getSelectedItemPosition() == 2 && (companyName.length() > 120 || companyName.length() < 3)) {
            validAertMsg = "Invalid company name!";
            return false;

        } else if (TextUtils.isEmpty(mobile)) {
            validAertMsg = "Mobile number field is empty";
            return false;
        } else if (mobile.length() > 25 || mobile.length() < 8) {
            validAertMsg = "Invalid edit_txt_mobile number!";
            return false;
        } else if (TextUtils.isEmpty(email)) {
            validAertMsg = "Email field is empty!";
            return false;
        } else if (!isValidMail(email)) {
            validAertMsg = "Invalid edit_txt_email address!";
            return false;
        } else if (provinceSpinner.getSelectedItemPosition() == 0) {
            validAertMsg = "Select the spinner_province";
            return false;
        } else if (districtSpinner.getSelectedItemPosition() == 0) {
            validAertMsg = "Select the spinner_district";
            return false;
        } else if (bankMemberSpinner.getSelectedItemPosition() == 0) {
            validAertMsg = "Select the bank";
            return false;
        } else if (TextUtils.isEmpty(bankAccount)) {
            validAertMsg = "Please enter your bank account";
            return false;
        } else if (bankAccount.length() > 25 || bankAccount.length() < 8) {
            validAertMsg = "Invalid bank account";
            return false;
        } else if (TextUtils.isEmpty(amount)) {
            validAertMsg = "Please enter the edit_txt_amount";
            return false;
        } else if (amount.length() > 10 || amount.length() < 1 || Integer.parseInt(amount) < 0) {
            validAertMsg = "Invalid edit_txt_amount";
            return false;
        } else if (!policyCheck.isChecked()) {
            validAertMsg = "Please accept our roles and policy";
            return false;
        } else {
            return true;
        }
    }

    private String getAllUserInputs() {
        StringBuilder qrGenFormInputs = new StringBuilder();
        qrGenFormInputs.append(PaymentFields.QR_CODE_VERSION+PaymentFields.KEY_VALUE_DELIMITER+"01"+PaymentFields.FIELDS_DELIMITER);
        qrGenFormInputs.append(PaymentFields.QR_Code_TYPE+PaymentFields.KEY_VALUE_DELIMITER+qrOwnerSpinner.getSelectedItem().toString()+PaymentFields.FIELDS_DELIMITER);
        //adding some qr head elements in case required (need to discuss)
        if (qrOwnerSpinner.getSelectedItemPosition() == 1) {
            qrGenFormInputs.append(PaymentFields.MERCHANT_CATEGORY+PaymentFields.KEY_VALUE_DELIMITER+"NULL"+PaymentFields.FIELDS_DELIMITER);
            qrGenFormInputs.append(PaymentFields.MERCHANT_COMPANY_NAME+PaymentFields.KEY_VALUE_DELIMITER+clientNameEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);

        } else if (qrOwnerSpinner.getSelectedItemPosition() == 2) {
            qrGenFormInputs.append(PaymentFields.MERCHANT_CATEGORY+PaymentFields.KEY_VALUE_DELIMITER+companyCategorySpinner.getSelectedItem().toString()+PaymentFields.FIELDS_DELIMITER);
            qrGenFormInputs.append(PaymentFields.MERCHANT_COMPANY_NAME+PaymentFields.KEY_VALUE_DELIMITER+companyNameEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);

        }
        qrGenFormInputs.append(PaymentFields.MOBILE+PaymentFields.KEY_VALUE_DELIMITER+mobileEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrGenFormInputs.append(PaymentFields.EMAIL+PaymentFields.KEY_VALUE_DELIMITER+emailEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrGenFormInputs.append(PaymentFields.PROVINCE+PaymentFields.KEY_VALUE_DELIMITER+provinceSpinner.getSelectedItem().toString()+PaymentFields.FIELDS_DELIMITER);
        qrGenFormInputs.append(PaymentFields.DISTRICT+PaymentFields.KEY_VALUE_DELIMITER+districtSpinner.getSelectedItem().toString()+PaymentFields.FIELDS_DELIMITER);
        qrGenFormInputs.append(PaymentFields.BANK_NAME+PaymentFields.KEY_VALUE_DELIMITER+bankMemberSpinner.getSelectedItem().toString()+PaymentFields.FIELDS_DELIMITER);
        qrGenFormInputs.append(PaymentFields.PAN+PaymentFields.KEY_VALUE_DELIMITER+accountNoEditTxt.getText().toString().trim()+PaymentFields.FIELDS_DELIMITER);
        qrGenFormInputs.append(PaymentFields.CURRENCY+PaymentFields.KEY_VALUE_DELIMITER+getCurrency()+PaymentFields.FIELDS_DELIMITER);
        qrGenFormInputs.append(PaymentFields.AMOUNT+PaymentFields.KEY_VALUE_DELIMITER+amountEditTxt.getText().toString().trim());
        return qrGenFormInputs.toString();
    }

    /**
     * the method is used to check for valid edit_txt_email address.
     * @param email
     * @return true if the edit_txt_email is valid, else false
     */
    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidFullName(String fullName) {
        //regular expression which matches the string only
        String regexFullname = "^[\\p{L} .'-]+$";
        Pattern fullnamePattern = Pattern.compile(regexFullname);
        Matcher fullnameMatcher = fullnamePattern.matcher(fullName);
        if (fullnameMatcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private String getCurrency() {
        if (selectedCurrency.getText().toString().equals("Afghani (Afn)")) {
            return "Afn";
        } else {
            return "USD";
        }
    }

    private void sendFormDataToNextActivity(){
        Intent intent = new Intent(getActivity(), QRDisplayActivity.class);
        if(!validateInput()){
            Toast.makeText(getActivity(), validAertMsg,Toast.LENGTH_LONG).show();
        }else{
            intent.putExtra("qrGenData",getAllUserInputs());
            startActivity(intent);
        }
    }


}
