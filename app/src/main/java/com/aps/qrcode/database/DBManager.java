package com.aps.qrcode.database;

public class DBManager {

    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "aps_qrcode_db";

    // Tables Name
    public static final String GENERATE_PAYMENT_QR_CODE_TABLE_NAME = "paymentqrgentbl";
    public static final String SCAN_PAYMENT_QR_CODE_TABLE_NAME = "paymentqrscantbl";
    public static final String CREATE_GENERAL_QR_CODE_TABLE_NAME = "generalqrgentbl";
    public static final String SCAN_GENERAL_QR_CODE_TABLE_NAME = "generalqrscantbl";

    // Tables' Columns for payments qr codes
    public static final String QR_ID_COLUMN = "qr_id";
    public static final String QR_VERSION = "qr_version";
    public static final String QR_CATEGORY_COLUMN = "merchant_category";
    public static final String COMPANY_CATEGORY_COLUMN = "spinner_company_category";
    public static final String MERCHANT_NAME_COLUMN = "merchant_name";
    public static final String MERCHANT_PHONE_COLUMN = "merchant_phone";
    public static final String MERCHANT_EMAIL_COLUMN = "merchant_email";
    public static final String MERCHANT_PROVINCE_COLUMN = "merchant_province";
    public static final String MERCHANT_DISTRICT_COLUMN = "merchant_district";
    public static final String MERCHANT_BANK_COLUMN = "bank_name";
    public static final String MERCHANT_ACCOUNT_COLUMN = "bank_account";
    public static final String MERCHANT_CURRENCY_COLUMN = "currency";
    public static final String MERCHANT_AMOUNT_COLUMN = "edit_txt_amount";
    public static final String GENERATED_QR_IMG = "img_vw_qr_img"; // IN PAYMENT QR IMAGE WE HAVE STORED QR IMG IN SQLITE AS WELL
    public static final String QR_IMG_NAME = "edit_txt_qr_img_name";
    public static final String QR_IMG_GENERATED_TIMESTAMP = "timestamp";
    public static final String FAVORITE_QR_CODE = "favorite";

    // Tables's Columns for non-payment qr codes
    public static final String GENERAL_QR_CODE_CONTENT = "general_qr_content"; // IN NON-PAYMENT QR CODE, WE STORE THE CONTENT
    public static final String SECRET_QR_CODE = "qr_secret";

    /**
     * Merchant Generated payment QR Code table:
     * The merchant payment generated QR code will be stored in this table
     */
    public static final String PAYMENT_QR_GEN_TABLE =
            "CREATE TABLE IF NOT EXISTS " + GENERATE_PAYMENT_QR_CODE_TABLE_NAME + "("
                    + QR_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QR_VERSION + " INTEGER DEFAULT 1, "
                    + QR_CATEGORY_COLUMN + " VARCHAR(50), "
                    + COMPANY_CATEGORY_COLUMN + " VARCHAR(50), "
                    + MERCHANT_NAME_COLUMN + " VARCHAR(50), "
                    + MERCHANT_PHONE_COLUMN + " VARCHAR(20), "
                    + MERCHANT_EMAIL_COLUMN + " VARCHAR(50), "
                    + MERCHANT_PROVINCE_COLUMN + " VARCHAR(20), "
                    + MERCHANT_DISTRICT_COLUMN + " VARCHAR(30), "
                    + MERCHANT_BANK_COLUMN + " VARCHAR(30), "
                    + MERCHANT_ACCOUNT_COLUMN + " VARCHAR(20), "
                    + MERCHANT_CURRENCY_COLUMN + " VARCHAR(8), "
                    + MERCHANT_AMOUNT_COLUMN + " VARCHAR(15), "
                    + GENERATED_QR_IMG + " BLOB, "
                    + QR_IMG_NAME + " VARCHAR(50), "
                    + QR_IMG_GENERATED_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + FAVORITE_QR_CODE + " INTEGER DEFAULT 0"
                    + ")";

    // QR Scan Table SQL Query
    /**
     * Customer payment Scanned QR Code table:
     * All the scanned payment QR Code image will be stored in this table
     */
    public static final String PAYMENT_QR_SCAN_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SCAN_PAYMENT_QR_CODE_TABLE_NAME + "("
                    + QR_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GENERATED_QR_IMG + " BLOB, "
                    + QR_IMG_NAME + " VARCHAR(50), "
                    + QR_IMG_GENERATED_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + FAVORITE_QR_CODE + " INTEGER DEFAULT 0"
                    + ")";

    public static final String GENERAL_QR_GEN_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ CREATE_GENERAL_QR_CODE_TABLE_NAME +"("
                    + QR_ID_COLUMN +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GENERAL_QR_CODE_CONTENT +" VARCHAR(1500), "
                    + GENERATED_QR_IMG +" BLOB, "
                    + QR_IMG_NAME +" VARCHAR(50), "
                    + QR_IMG_GENERATED_TIMESTAMP +" DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + SECRET_QR_CODE +" INTEGER DEFAULT 0, "
                    + FAVORITE_QR_CODE +" INTEGER DEFAULT 0"
                    + ")";

    public static final String GENERAL_QR_SCAN_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ SCAN_GENERAL_QR_CODE_TABLE_NAME +"("
                    + QR_ID_COLUMN +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GENERATED_QR_IMG +" BLOB, "
                    + QR_IMG_NAME + " VARCHAR(50), "
                    + QR_IMG_GENERATED_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + SECRET_QR_CODE +" INTEGER DEFAULT 0, "
                    + FAVORITE_QR_CODE +" INTEGER DEFAULT 0"
                    + ")";

}
