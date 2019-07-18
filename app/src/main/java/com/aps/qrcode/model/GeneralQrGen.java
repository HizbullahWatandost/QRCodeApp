package com.aps.qrcode.model;

public class GeneralQrGen {
    private int QrId;
    private String QrCodeContent;
    private byte[] QrImg;
    private String QrImgName;
    private String timestamp;
    private int secretQr;
    private int favoriteQr;


    public GeneralQrGen(int QrId, String QrCodeContent, byte[] QrImg, String QrImgName, String timestamp, int secretQr, int favoriteQr) {
        this.QrId = QrId;
        this.QrCodeContent = QrCodeContent;
        this.QrImg = QrImg;
        this.QrImgName = QrImgName;
        this.timestamp = timestamp;
        this.secretQr = secretQr;
        this.favoriteQr = favoriteQr;
    }

    public GeneralQrGen() {
    }

    public int getQrId() {
        return QrId;
    }

    public void setQrId(int qrId) {
        this.QrId = qrId;
    }

    public String getQrCodeContent() {
        return QrCodeContent;
    }

    public void setQrCodeContent(String qrCodeContent) {
        this.QrCodeContent = qrCodeContent;
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
}
