package com.aps.qrcode.model;

import java.util.Arrays;

/**
 * The PaymentQrScan modal class holds all the properties of the payment QR Code image which the user scan to pay.
 */
public class PaymentQrScan {

    private int QrId;
    private byte[] QrImg;
    private String QrImgName;
    private String timestamp;
    private int FavQrScan;

    public PaymentQrScan(int QrId, byte[] qrImg, String qrImgName, String timestamp, int FavQrScan) {
        this.QrId = QrId;
        this.QrImg = qrImg;
        this.QrImgName = qrImgName;
        this.timestamp = timestamp;
        this.FavQrScan = FavQrScan;
    }

    public PaymentQrScan() {
    }

    public int getQrId() {
        return QrId;
    }

    public void setQrId(int qrId) {
        this.QrId = qrId;
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

    public int getFavQrScan() {
        return FavQrScan;
    }

    public void setFavQrScan(int favQrScan) {
        this.FavQrScan = favQrScan;
    }

    @Override
    public String toString() {
        return "PaymentQrScan{" +
                "QrId=" + QrId +
                ", QrImgView=" + Arrays.toString(QrImg) +
                ", QrImgName='" + QrImgName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", FavQrScan=" + FavQrScan +
                '}';
    }
}
