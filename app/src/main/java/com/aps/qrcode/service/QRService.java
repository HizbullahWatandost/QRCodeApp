package com.aps.qrcode.service;

import android.widget.ImageView;

// The QRService holds two methods which are used for converting image to byte and getting the current timestamp
public interface QRService {

    byte[] imageViewToByte(ImageView image);

    String getCurrentDateTime();

    // the function is used to encrypt the content of QR Code
    String encryptQRCodeContent(String qr_content);
    // the function is used to decrypt the content of QR Code
    String decryptQRCodeContent(String qr_content);
    // this function is used to convert the QR content in a form which is not easy to understand by the user.
    String toNonSenseConverter(String qr_content);
    // this function is used to abstract secret QR encryption key from the QR content
    String getSecretQREncryptionKey(String qr_content);
    // this function is used to remove the encryption key from QR content and return only the content.
    String removeEncryptionKeyFromSecretQR(String qr_content);
    // this function is used to add encryption key to the QR content
    String addEncryptionKeyToQR(String qr_content);


}
