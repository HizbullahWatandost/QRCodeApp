package com.aps.qrcode.model;

import java.util.Arrays;

/**
 * The PaymentQrGen modal class holds all the properties of payment generated QR Code which the merchant creates
 */
public class PaymentQrGen {

    private int QrId;
    private int QrVersion;
    private String QrCategory;
    private String merchantCategory;
    private String merchantCompanyName;
    private String merchantPhone;
    private String merchantEmail;
    private String merchantProvince;
    private String merchantDistrict;
    private String bankName;
    private String accountNumber;
    private String currency;
    private String amount;
    private byte[] QrImg;
    private String QrImgName;
    private String timestamp;
    private int FavQrGen;

    public PaymentQrGen(int qrId, int QrVersion, String QrCategory, String merchantCategory, String merchantCompanyName, String merchantPhone, String merchantEmail, String merchantProvince, String merchantDistrict, String bankName, String accountNumber, String currency, String amount, byte[] qrImg, String qrImgName, String timestamp, int FavQrGen) {
        this.QrId = qrId;
        this.QrVersion = QrVersion;
        this.QrCategory = QrCategory;
        this.merchantCategory = merchantCategory;
        this.merchantCompanyName = merchantCompanyName;
        this.merchantPhone = merchantPhone;
        this.merchantEmail = merchantEmail;
        this.merchantProvince = merchantProvince;
        this.merchantDistrict = merchantDistrict;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.amount = amount;
        this.QrImg = qrImg;
        this.QrImgName = qrImgName;
        this.timestamp = timestamp;
        this.FavQrGen = FavQrGen;
    }

    public PaymentQrGen() {
    }

    public int getQrId() {
        return QrId;
    }

    public void setQrId(int qrId) {
        this.QrId = qrId;
    }

    public int getQrVersion() {
        return QrVersion;
    }

    public void setQrVersion(int qrVersion) {
        this.QrVersion = qrVersion;
    }

    public String getQrCategory() {
        return QrCategory;
    }

    public void setQrCategory(String qrCategory) {
        this.QrCategory = qrCategory;
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }

    public void setMerchantCategory(String merchantCategory) {
        this.merchantCategory = merchantCategory;
    }

    public String getMerchantCompanyName() {
        return merchantCompanyName;
    }

    public void setMerchantCompanyName(String merchantCompanyName) {
        this.merchantCompanyName = merchantCompanyName;
    }

    public String getMerchantPhone() {
        return merchantPhone;
    }

    public void setMerchantPhone(String merchantPhone) {
        this.merchantPhone = merchantPhone;
    }

    public String getMerchantEmail() {
        return merchantEmail;
    }

    public void setMerchantEmail(String merchantEmail) {
        this.merchantEmail = merchantEmail;
    }

    public String getMerchantProvince() {
        return merchantProvince;
    }

    public void setMerchantProvince(String merchantProvince) {
        this.merchantProvince = merchantProvince;
    }

    public String getMerchantDistrict() {
        return merchantDistrict;
    }

    public void setMerchantDistrict(String merchantDistrict) {
        this.merchantDistrict = merchantDistrict;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public byte[] getQrImg() {
        return QrImg;
    }

    public void setQrImg(byte[] qrImg) {
        this.QrImg = qrImg;
    }

    public String getQrImgName() {
        return QrImgName;
    }

    public void setQrImgName(String qrImgName) {
        this.QrImgName = qrImgName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getFavQrGen() {
        return FavQrGen;
    }

    public void setFavQrGen(int favQrGen) {
        this.FavQrGen = favQrGen;
    }

    @Override
    public String toString() {
        return "PaymentQrGen{" +
                "QrId=" + QrId +
                ", QrVersion=" + QrVersion +
                ", QrCategory='" + QrCategory + '\'' +
                ", merchantCategory='" + merchantCategory + '\'' +
                ", merchantCompanyName='" + merchantCompanyName + '\'' +
                ", merchantPhone='" + merchantPhone + '\'' +
                ", merchantEmail='" + merchantEmail + '\'' +
                ", merchantProvince='" + merchantProvince + '\'' +
                ", merchantDistrict='" + merchantDistrict + '\'' +
                ", edit_txt_bank_name='" + bankName + '\'' +
                ", edit_txt_account_number='" + accountNumber + '\'' +
                ", currency='" + currency + '\'' +
                ", edit_txt_amount='" + amount + '\'' +
                ", QrImgView=" + Arrays.toString(QrImg) +
                ", QrImgName='" + QrImgName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", FavQrGen=" + FavQrGen +
                '}';
    }
}
