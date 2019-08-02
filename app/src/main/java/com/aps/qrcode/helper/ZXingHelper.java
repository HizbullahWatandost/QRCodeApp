package com.aps.qrcode.helper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.Map;

/**
 * This helper class is used for generating and reading QR Code using ZXing library.
 */
public class ZXingHelper {

    /**
     * The method which creates QR Code based on the given properties
     *
     * @param qrChatacterSet:       the chracterset in which the content of QR Code is encode
     * @param errorCorrectionLevel: the error correction level which helps the user to scan qr code if it is 7% damaged
     * @param width:                the width of the QR Code image
     * @param height:               the height of QR Code image
     * @param qrContent:            the actual content of QR Code image i.e. merchant name, account number and etc.
     * @return the bitmap which can be stored in table and displayed in image view
     * @throws WriterException
     * @throws UnsupportedEncodingException
     */
    public Bitmap createQRImage(String qrChatacterSet, String errorCorrectionLevel, int width, int height, String qrContent) throws WriterException, UnsupportedEncodingException {
        Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, qrChatacterSet);
        hintMap.put(EncodeHintType.MARGIN, 2);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.valueOf(errorCorrectionLevel));
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                new String(qrContent.getBytes(qrChatacterSet), qrChatacterSet),
                BarcodeFormat.QR_CODE, width, height, hintMap);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        return bitmap;
    }

    /**
     * The method which read QR Code image without scanning.
     * The user import QR Code image from gallery, then click on read button.
     * The image will be read and displayed in form
     * NOTE: keep it in mind, this method decodes the payment QR Code image.
     *
     * @param qrImageView
     * @return the content of QR Code image in string format.
     */
    public String qr2Txt(ImageView qrImageView) {
        Bitmap bitmap = ((BitmapDrawable) qrImageView.getDrawable()).getBitmap();
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            Result result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)));
            return result.getText();
        } catch (Exception e) {
            return null;
        }
    }
}
