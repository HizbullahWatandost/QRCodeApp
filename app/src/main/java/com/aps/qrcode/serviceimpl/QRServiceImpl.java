package com.aps.qrcode.serviceimpl;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.aps.qrcode.service.QRService;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


// This service implementation class is used to implements all the defined methods in service class.
public class QRServiceImpl implements QRService {

    @Override
    public byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(new Date());
    }


    @Override
    public String encryptQRCodeContent(String qr_content) {
        StringBuilder encode = new StringBuilder();
        for (int i = 0; i < qr_content.length(); i++) {
            if (i != qr_content.length() - 1) {
                encode.append(String.valueOf((int) qr_content.charAt(i) + 120)).append(".");
            } else {
                encode.append(String.valueOf((int) qr_content.charAt(i) + 120));
            }
        }
        return encode.toString();
    }

    @Override
    public String decryptQRCodeContent(String qr_content) {
        String[] qrContentCharArrays = qr_content.split("\\.");
        String[] decode = new String[qrContentCharArrays.length];

        for (int i = 0; i < qrContentCharArrays.length; i++) {
            decode[i] = Character.toString((char) ((Integer.parseInt(qrContentCharArrays[i])) - 120));
        }
        StringBuilder decodedQRContent = new StringBuilder();
        for (String s : decode) {
            decodedQRContent.append(s);
        }
        return decodedQRContent.toString();
    }

    @Override
    public String toNonSenseConverter(String qr_content) {
        String[] qrContentCharArrays = qr_content.split("\\.");
        String[] decode = new String[qrContentCharArrays.length];

        for (int i = 0; i < qrContentCharArrays.length; i++) {
            decode[i] = Character.toString((char) ((Integer.parseInt(qrContentCharArrays[i])) + 220));
        }
        StringBuilder decodedQRContent = new StringBuilder();
        for (String s : decode) {
            decodedQRContent.append(s);
        }
        return decodedQRContent.toString();

    }

    @Override
    public String getSecretQREncryptionKey(String qr_content) {
        String qrDecryptedContent = decryptQRCodeContent(qr_content);
        int startIndexOfEncryptionKey = qrDecryptedContent.indexOf("|*_pass:") + ("|*_pass:").length();
        int endIndexOfEncryptionKey = qrDecryptedContent.lastIndexOf("_*|");
        String encryptionKey = qrDecryptedContent.substring(startIndexOfEncryptionKey, endIndexOfEncryptionKey);
        return encryptionKey;
    }

    @Override
    public String removeEncryptionKeyFromSecretQR(String qr_content) {
        String qrDecryptedContent = decryptQRCodeContent(qr_content);
        String decryptedQrContent = qrDecryptedContent.substring(0, qrDecryptedContent.indexOf("|*_pass:"));
        return decryptedQrContent;
    }

    @Override
    public String addEncryptionKeyToQR(String qr_pass) {
        return encryptQRCodeContent(" |*_pass:" + qr_pass + "_*| ");
    }


}
