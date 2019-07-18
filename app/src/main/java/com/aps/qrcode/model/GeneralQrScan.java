package com.aps.qrcode.model;

import java.util.Arrays;

public class GeneralQrScan {

    private int QrId;
    private byte[] QrImg;
    private String QrImgName;
    private String timestamp;
    private int secretQr;
    private int favoriteQr;

    public GeneralQrScan(int QrId, byte[] QrImg, String QrImgName, String timestamp, int secretQr, int favoriteQr) {
        this.QrId = QrId;
        this.QrImg = QrImg;
        this.QrImgName = QrImgName;
        this.timestamp = timestamp;
        this.secretQr = secretQr;
        this.favoriteQr = favoriteQr;
    }


    public GeneralQrScan() {
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

    public int getSecretQr() {
        return secretQr;
    }

    public void setSecretQr(int secretQr) {
        this.secretQr = secretQr;
    }

    public int getFavoriteQr() {
        return favoriteQr;
    }

    public void setFavoriteQr(int favoriteQr) {
        this.favoriteQr = favoriteQr;
    }

    @Override
    public String toString() {
        return "GeneralQrScan{" +
                "QrId=" + QrId +
                ", QrImg=" + Arrays.toString(QrImg) +
                ", QrImgName='" + QrImgName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", secretQr=" + secretQr +
                ", favoriteQr=" + favoriteQr +
                '}';
    }
}
