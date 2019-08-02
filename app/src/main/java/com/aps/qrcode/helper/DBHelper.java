package com.aps.qrcode.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aps.qrcode.database.DBManager;
import com.aps.qrcode.model.GeneralQrGen;
import com.aps.qrcode.model.GeneralQrScan;
import com.aps.qrcode.model.PaymentQrGen;
import com.aps.qrcode.model.PaymentQrScan;

import java.util.ArrayList;
import java.util.List;


/**
 * DBHelper class is used for creating database and tables
 * All the table CRUD operations are configured here.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Constructor
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(DBManager.PAYMENT_QR_GEN_TABLE);
        db.execSQL(DBManager.PAYMENT_QR_SCAN_TABLE);
        db.execSQL(DBManager.GENERAL_QR_GEN_TABLE);
        db.execSQL(DBManager.GENERAL_QR_SCAN_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    //query data
    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    // ===================================
    // CRUD for payment QR Code generation
    // ====================================
    // Inserting Payment QR Code into table
    // generate payment QR Code image
    // NOTE: the three columns (id, timestamp and favorite) will be automatically inserted into table.

    // saving payment generated QR Code into table
    public long saveGeneratedPaymentQrCode(
            String QrCategory,
            String companyCategory,
            String merchantCompanyName,
            String merchantPhone,
            String merchantEmail,
            String merchantProvince,
            String merchantDistrict,
            String bankName,
            String accountNumber,
            String currency,
            String amount,
            String QrImgName,
            byte[] QrImg) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(DBManager.QR_CATEGORY_COLUMN, QrCategory);
        values.put(DBManager.COMPANY_CATEGORY_COLUMN, companyCategory);
        values.put(DBManager.MERCHANT_NAME_COLUMN, merchantCompanyName);
        values.put(DBManager.MERCHANT_PHONE_COLUMN, merchantPhone);
        values.put(DBManager.MERCHANT_EMAIL_COLUMN, merchantEmail);
        values.put(DBManager.MERCHANT_PROVINCE_COLUMN, merchantProvince);
        values.put(DBManager.MERCHANT_DISTRICT_COLUMN, merchantDistrict);
        values.put(DBManager.MERCHANT_BANK_COLUMN, bankName);
        values.put(DBManager.MERCHANT_ACCOUNT_COLUMN, accountNumber);
        values.put(DBManager.MERCHANT_CURRENCY_COLUMN, currency);
        values.put(DBManager.MERCHANT_AMOUNT_COLUMN, amount);
        values.put(DBManager.QR_IMG_NAME, QrImgName);
        values.put(DBManager.GENERATED_QR_IMG, QrImg);

        // insert row
        long id = db.insert(DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    // get payment generated QR Code record
    public PaymentQrGen getGeneratedPaymentQrCodeById(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME,
                new String[]{
                        DBManager.QR_ID_COLUMN,
                        DBManager.QR_VERSION,
                        DBManager.QR_CATEGORY_COLUMN,
                        DBManager.COMPANY_CATEGORY_COLUMN,
                        DBManager.MERCHANT_NAME_COLUMN,
                        DBManager.MERCHANT_PHONE_COLUMN,
                        DBManager.MERCHANT_EMAIL_COLUMN,
                        DBManager.MERCHANT_PROVINCE_COLUMN,
                        DBManager.MERCHANT_DISTRICT_COLUMN,
                        DBManager.MERCHANT_BANK_COLUMN,
                        DBManager.MERCHANT_ACCOUNT_COLUMN,
                        DBManager.MERCHANT_CURRENCY_COLUMN,
                        DBManager.MERCHANT_AMOUNT_COLUMN,
                        DBManager.GENERATED_QR_IMG,
                        DBManager.QR_IMG_NAME,
                        DBManager.QR_IMG_GENERATED_TIMESTAMP,
                        DBManager.FAVORITE_QR_CODE
                },
                DBManager.QR_ID_COLUMN + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        PaymentQrGen paymentQrGen = new PaymentQrGen(
                cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)),
                cursor.getInt(cursor.getColumnIndex(DBManager.QR_VERSION)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_CATEGORY_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.COMPANY_CATEGORY_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_NAME_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_PHONE_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_EMAIL_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_PROVINCE_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_DISTRICT_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_BANK_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_ACCOUNT_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_CURRENCY_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_AMOUNT_COLUMN)),
                cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

        // close the db connection
        cursor.close();
        return paymentQrGen;
    }

    // get all payment generated QR Codes
    public List<PaymentQrGen> getAllGeneratedPaymentQrCodes() {
        List<PaymentQrGen> generatedQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME + " ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PaymentQrGen paymentQrGen = new PaymentQrGen();
                paymentQrGen.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                paymentQrGen.setQrVersion(cursor.getInt(cursor.getColumnIndex(DBManager.QR_VERSION)));
                paymentQrGen.setQrCategory(cursor.getString(cursor.getColumnIndex(DBManager.QR_CATEGORY_COLUMN)));
                paymentQrGen.setMerchantCategory(cursor.getString(cursor.getColumnIndex(DBManager.COMPANY_CATEGORY_COLUMN)));
                paymentQrGen.setMerchantCompanyName(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_NAME_COLUMN)));
                paymentQrGen.setMerchantPhone(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_PHONE_COLUMN)));
                paymentQrGen.setMerchantEmail(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_EMAIL_COLUMN)));
                paymentQrGen.setMerchantProvince(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_PROVINCE_COLUMN)));
                paymentQrGen.setMerchantDistrict(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_DISTRICT_COLUMN)));
                paymentQrGen.setBankName(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_BANK_COLUMN)));
                paymentQrGen.setAccountNumber(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_ACCOUNT_COLUMN)));
                paymentQrGen.setAmount(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_AMOUNT_COLUMN)));
                paymentQrGen.setCurrency(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_CURRENCY_COLUMN)));
                paymentQrGen.setAmount(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_AMOUNT_COLUMN)));
                paymentQrGen.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                paymentQrGen.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                paymentQrGen.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                paymentQrGen.setFavQrGen(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                generatedQRCodes.add(paymentQrGen);
            } while (cursor.moveToNext());
            cursor.close();
        }
        // close db connection
        db.close();
        // return notes list
        return generatedQRCodes;
    }

    // get number of generated payment QR Codes
    public int getGeneratedPaymentQrCodesCount() {
        String countQuery = "SELECT * FROM " + DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // update payment generated QR Code
    public int updateGeneratedGeneralQrCode(PaymentQrGen paymentQrGen) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.QR_CATEGORY_COLUMN, paymentQrGen.getQrCategory());
        values.put(DBManager.COMPANY_CATEGORY_COLUMN, paymentQrGen.getMerchantCategory());
        values.put(DBManager.MERCHANT_NAME_COLUMN, paymentQrGen.getMerchantCompanyName());
        values.put(DBManager.MERCHANT_PHONE_COLUMN, paymentQrGen.getMerchantPhone());
        values.put(DBManager.MERCHANT_EMAIL_COLUMN, paymentQrGen.getMerchantEmail());
        values.put(DBManager.MERCHANT_PROVINCE_COLUMN, paymentQrGen.getMerchantProvince());
        values.put(DBManager.MERCHANT_DISTRICT_COLUMN, paymentQrGen.getMerchantDistrict());
        values.put(DBManager.MERCHANT_BANK_COLUMN, paymentQrGen.getBankName());
        values.put(DBManager.MERCHANT_ACCOUNT_COLUMN, paymentQrGen.getAccountNumber());
        values.put(DBManager.MERCHANT_CURRENCY_COLUMN, paymentQrGen.getCurrency());
        values.put(DBManager.MERCHANT_AMOUNT_COLUMN, paymentQrGen.getAmount());
        values.put(DBManager.QR_IMG_NAME, paymentQrGen.getQrImgName());
        values.put(DBManager.GENERATED_QR_IMG, paymentQrGen.getQrImg());

        // updating row
        return db.update(DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(paymentQrGen.getQrId())});
    }

    // delete payment generated QR Code from the table
    public void deleteGeneratedPaymentQrCodeById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Adding payment generated QR Code to favorite list of payment generated QR Codes.
    public int addGeneratedPaymentQrCodeToFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 1);

        // updating row
        return db.update(DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }

    // Removing the generated payment QR Code from favorite list
    public int rermoveGeneratedPaymentQrCodeFromFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 0);

        // updating row
        return db.update(DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }

    // get all the payment favorite generated QR Images
    public List<PaymentQrGen> getAllFavGeneratedPaymentQrCodes() {
        List<PaymentQrGen> generatedQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME + " WHERE " + DBManager.FAVORITE_QR_CODE + " = 1 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PaymentQrGen paymentQrGen = new PaymentQrGen();
                paymentQrGen.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                paymentQrGen.setQrVersion(cursor.getInt(cursor.getColumnIndex(DBManager.QR_VERSION)));
                paymentQrGen.setQrCategory(cursor.getString(cursor.getColumnIndex(DBManager.QR_CATEGORY_COLUMN)));
                paymentQrGen.setMerchantCategory(cursor.getString(cursor.getColumnIndex(DBManager.COMPANY_CATEGORY_COLUMN)));
                paymentQrGen.setMerchantCompanyName(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_NAME_COLUMN)));
                paymentQrGen.setMerchantPhone(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_PHONE_COLUMN)));
                paymentQrGen.setMerchantEmail(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_EMAIL_COLUMN)));
                paymentQrGen.setMerchantProvince(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_PROVINCE_COLUMN)));
                paymentQrGen.setMerchantDistrict(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_DISTRICT_COLUMN)));
                paymentQrGen.setBankName(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_BANK_COLUMN)));
                paymentQrGen.setAccountNumber(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_ACCOUNT_COLUMN)));
                paymentQrGen.setAmount(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_AMOUNT_COLUMN)));
                paymentQrGen.setCurrency(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_CURRENCY_COLUMN)));
                paymentQrGen.setAmount(cursor.getString(cursor.getColumnIndex(DBManager.MERCHANT_AMOUNT_COLUMN)));
                paymentQrGen.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                paymentQrGen.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                paymentQrGen.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                paymentQrGen.setFavQrGen(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                generatedQRCodes.add(paymentQrGen);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return generatedQRCodes;
    }

    // get number of payment generated favorite QR Codes
    public int getFavGeneratedPaymentQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.GENERATE_PAYMENT_QR_CODE_TABLE_NAME + " WHERE " + DBManager.FAVORITE_QR_CODE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // ===================================
    // CRUD for scanned payment QR Code
    // ===================================
    // Inserting Scanned Payment QR Code into table
    // CRUD for QR Code generation
    // Scanned payment QR Code image
    // NOTE: the three columns (id, timestamp and favorite) will be automatically inserted into table.

    // saving scanned payment QR Code in table
    public long saveScannedPaymentQrCode(
            String QrImgName,
            byte[] QrImg) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(DBManager.QR_IMG_NAME, QrImgName);
        values.put(DBManager.GENERATED_QR_IMG, QrImg);

        // insert row
        long id = db.insert(DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    // get payment scanned QR Code records
    public PaymentQrScan getScannedPaymentQrCodeById(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME,
                new String[]{
                        DBManager.QR_ID_COLUMN,
                        DBManager.GENERATED_QR_IMG,
                        DBManager.QR_IMG_NAME,
                        DBManager.QR_IMG_GENERATED_TIMESTAMP,
                        DBManager.FAVORITE_QR_CODE
                },
                DBManager.QR_ID_COLUMN + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        PaymentQrScan paymentQrScan = new PaymentQrScan(
                cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)),
                cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

        // close the db connection
        cursor.close();
        return paymentQrScan;
    }


    // get all payment scanned QR Code images
    public List<PaymentQrScan> getAllScannedPaymentQrCodes() {
        List<PaymentQrScan> scannedQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME + " ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PaymentQrScan paymentQrScan = new PaymentQrScan();
                paymentQrScan.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                paymentQrScan.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                paymentQrScan.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                paymentQrScan.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                paymentQrScan.setFavQrScan(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                scannedQRCodes.add(paymentQrScan);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return scannedQRCodes;
    }

    // get number of scanned payment QR Codes
    public int getScannedPaymentQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    // update scanned payment QR Code
    public int updateScannedGeneralQrCode(int qrId, String qrUpdatedName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBManager.QR_IMG_NAME, qrUpdatedName);

        // updating row
        return db.update(DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(qrId)});
    }

    // delete scammed payment QR Code from the table
    public void deleteScannedPaymentQrCodeById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // adding scanned payment QR Code to favorite list of scanned payment QR Codes
    public int addScannedPaymentQrCodeToFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 1);

        // updating row
        return db.update(DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }

    // removing the scanned payment QR Code from favorite list
    public int removeScannedPaymentQrCodeFromFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 0);

        // updating row
        return db.update(DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }

    // get all the favorite payment scanned QR Images
    public List<PaymentQrScan> getAllFavScannedPaymentQrCodes() {
        List<PaymentQrScan> scannedQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME + " WHERE " + DBManager.FAVORITE_QR_CODE + " = 1 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PaymentQrScan paymentQrScan = new PaymentQrScan();
                paymentQrScan.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                paymentQrScan.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                paymentQrScan.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                paymentQrScan.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                paymentQrScan.setFavQrScan(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                scannedQRCodes.add(paymentQrScan);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return scannedQRCodes;
    }

    // get number of scanned favorite payment QR Codes
    public int getFavScannedPaymentQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.SCAN_PAYMENT_QR_CODE_TABLE_NAME + " WHERE " + DBManager.FAVORITE_QR_CODE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    //  ==================================
    //  || General Generated Secret QR CODE
    //  ==================================
    // CRUD operations
    // save a generated secret QR Code
    public long saveSecretQrCode(
            String secretQrCodeContent,
            byte[] secretQrImg,
            String QrImgName,
            int secretQr) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(DBManager.GENERAL_QR_CODE_CONTENT, secretQrCodeContent);
        values.put(DBManager.GENERATED_QR_IMG, secretQrImg);
        values.put(DBManager.QR_IMG_NAME, QrImgName);
        values.put(DBManager.SECRET_QR_CODE, secretQr);

        // insert row
        long id = db.insert(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    // get the general generated QR Code
    public GeneralQrGen getGeneratedGeneralQrCodeById(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME,
                new String[]{
                        DBManager.QR_ID_COLUMN,
                        DBManager.GENERAL_QR_CODE_CONTENT,
                        DBManager.GENERATED_QR_IMG,
                        DBManager.QR_IMG_NAME,
                        DBManager.QR_IMG_GENERATED_TIMESTAMP,
                        DBManager.SECRET_QR_CODE,
                        DBManager.FAVORITE_QR_CODE
                },
                DBManager.QR_ID_COLUMN + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        GeneralQrGen generalQrGen = new GeneralQrGen(
                cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)),
                cursor.getString(cursor.getColumnIndex(DBManager.GENERAL_QR_CODE_CONTENT)),
                cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(DBManager.SECRET_QR_CODE)),
                cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

        // close the db connection
        cursor.close();
        return generalQrGen;
    }

    // get all generated secret QR Code
    public List<GeneralQrGen> getAllGeneratedGeneralSecretQrCodes() {
        List<GeneralQrGen> generatedSecretQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 1 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeneralQrGen generalQrGen = new GeneralQrGen();
                generalQrGen.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                generalQrGen.setQrCodeContent(cursor.getString(cursor.getColumnIndex(DBManager.GENERAL_QR_CODE_CONTENT)));
                generalQrGen.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                generalQrGen.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                generalQrGen.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                generalQrGen.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.SECRET_QR_CODE)));
                generalQrGen.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                generatedSecretQRCodes.add(generalQrGen);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return generatedSecretQRCodes;
    }


    // get number of generated secret QR Codes
    public int getGeneratedGeneralSecretQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    // update generated secret QR Code
    public int updateGeneratedGeneralSecretQrCode(GeneralQrGen generalQrGen) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.GENERAL_QR_CODE_CONTENT, generalQrGen.getQrCodeContent());
        values.put(DBManager.GENERATED_QR_IMG, generalQrGen.getQrImg());
        values.put(DBManager.QR_IMG_NAME, generalQrGen.getQrImgName());

        // updating row
        return db.update(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ? AND " + DBManager.SECRET_QR_CODE + " = 1",
                new String[]{String.valueOf(generalQrGen.getQrId())});
    }


    // delete a generated secret QR Code from the table
    public void deleteGeneratedGeneralSecretQrCodeById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, DBManager.QR_ID_COLUMN + " = ? AND " + DBManager.SECRET_QR_CODE + " = 1",
                new String[]{String.valueOf(id)});
        db.close();
    }


    // adding generated secret QR Code to favorite list of generated secret QR Codes by
    public int addGeneratedGeneralSecretQrCodeToFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 1);

        // updating row
        return db.update(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }

    // removing the generated secret QR Code from favorite list
    public int removeGeneratedGeneralSecretQrCodeFromFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 0);

        // updating row
        return db.update(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }


    // get all generated secret QR Code
    public List<GeneralQrGen> getAllFavGeneratedGeneralSecretQrCodes() {
        List<GeneralQrGen> generatedSecretQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 1 AND " + DBManager.FAVORITE_QR_CODE + " = 1 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeneralQrGen generalQrGen = new GeneralQrGen();
                generalQrGen.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                generalQrGen.setQrCodeContent(cursor.getString(cursor.getColumnIndex(DBManager.GENERAL_QR_CODE_CONTENT)));
                generalQrGen.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                generalQrGen.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                generalQrGen.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                generalQrGen.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.SECRET_QR_CODE)));
                generalQrGen.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                generatedSecretQRCodes.add(generalQrGen);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return generatedSecretQRCodes;
    }


    // get number of generated secret QR Codes
    public int getFavGeneratedGeneralSecretQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 1 AND " + DBManager.FAVORITE_QR_CODE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    // save general generated QR Code
    public long saveGeneralQrCode(
            String generalQrCodeContent,
            byte[] generalQrImg,
            String QrImgName) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(DBManager.GENERAL_QR_CODE_CONTENT, generalQrCodeContent);
        values.put(DBManager.GENERATED_QR_IMG, generalQrImg);
        values.put(DBManager.QR_IMG_NAME, QrImgName);

        // insert row
        long id = db.insert(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    // get all general generated QR Code
    public List<GeneralQrGen> getAllGeneratedGeneralQrCodes() {
        List<GeneralQrGen> generatedSecretQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 0 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeneralQrGen generalQrGen = new GeneralQrGen();
                generalQrGen.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                generalQrGen.setQrCodeContent(cursor.getString(cursor.getColumnIndex(DBManager.GENERAL_QR_CODE_CONTENT)));
                generalQrGen.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                generalQrGen.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                generalQrGen.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                generalQrGen.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                generatedSecretQRCodes.add(generalQrGen);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return generatedSecretQRCodes;
    }


    // get number of general generated QR Codes
    public int getGeneratedGeneralQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    // update general generated QR Code
    public int updateGeneratedGeneralQrCode(GeneralQrGen generalQrGen) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.GENERAL_QR_CODE_CONTENT, generalQrGen.getQrCodeContent());
        values.put(DBManager.GENERATED_QR_IMG, generalQrGen.getQrImg());
        values.put(DBManager.QR_IMG_NAME, generalQrGen.getQrImgName());

        // updating row
        return db.update(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ? AND " + DBManager.SECRET_QR_CODE + " = 0",
                new String[]{String.valueOf(generalQrGen.getQrId())});
    }

    // delete a general generated QR Code from the table
    public void deleteGeneratedGeneralQrCodeById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, DBManager.QR_ID_COLUMN + " = ? AND " + DBManager.SECRET_QR_CODE + " = 0",
                new String[]{String.valueOf(id)});
        db.close();
    }


    // adding general generated QR Code to favorite list of generated QR Codes
    public int addGeneratedGeneralQrCodeToFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 1);

        // updating row
        return db.update(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }

    // Removing the general generated QR Code from favorite list
    public int removeGeneratedGeneralQrCodeFromFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 0);

        // updating row
        return db.update(DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }


    // get all general generated QR Code
    public List<GeneralQrGen> getAllFavGeneratedGeneralQrCodes() {
        List<GeneralQrGen> generatedSecretQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 0 AND " + DBManager.FAVORITE_QR_CODE + " = 1 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeneralQrGen generalQrGen = new GeneralQrGen();
                generalQrGen.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                generalQrGen.setQrCodeContent(cursor.getString(cursor.getColumnIndex(DBManager.GENERAL_QR_CODE_CONTENT)));
                generalQrGen.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                generalQrGen.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                generalQrGen.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                generalQrGen.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                generatedSecretQRCodes.add(generalQrGen);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return generatedSecretQRCodes;
    }


    // get number of general generated QR Codes
    public int getFavGeneratedGeneralQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.CREATE_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 0 AND " + DBManager.FAVORITE_QR_CODE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    //  ==================================
    //  || General SCANNED QR CODE  ||
    //  ==================================
    // CRUD operation

    // save scanned secret qr code
    public long saveScannedGeneralSecretQrCode(
            byte[] QrImg,
            String QrImgName,
            int secretQr) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(DBManager.GENERATED_QR_IMG, QrImg);
        values.put(DBManager.QR_IMG_NAME, QrImgName);
        values.put(DBManager.SECRET_QR_CODE, secretQr);

        // insert row
        long id = db.insert(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    // get scanned QR code
    public GeneralQrScan getScannedGeneralQrCodeById(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME,
                new String[]{
                        DBManager.QR_ID_COLUMN,
                        DBManager.GENERATED_QR_IMG,
                        DBManager.QR_IMG_NAME,
                        DBManager.QR_IMG_GENERATED_TIMESTAMP,
                        DBManager.SECRET_QR_CODE,
                        DBManager.FAVORITE_QR_CODE
                },
                DBManager.QR_ID_COLUMN + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        GeneralQrScan generalQrScan = new GeneralQrScan(
                cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)),
                cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)),
                cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(DBManager.SECRET_QR_CODE)),
                cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

        // close the db connection
        cursor.close();
        return generalQrScan;
    }

    // get all scanned secret QR Code
    public List<GeneralQrScan> getAllScannedGeneralSecretQrCodes() {
        List<GeneralQrScan> scannedSecretQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 1 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeneralQrScan generalQrScan = new GeneralQrScan();
                generalQrScan.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                generalQrScan.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                generalQrScan.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                generalQrScan.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                generalQrScan.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.SECRET_QR_CODE)));
                generalQrScan.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                scannedSecretQRCodes.add(generalQrScan);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return scannedSecretQRCodes;
    }


    // get number of scanned secret QR Codes
    public int getScannedGeneralSecretQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    // update a scanned secret QR Code
    public int updateScannedGeneralSecretQrCode(GeneralQrScan generalQrScan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.QR_IMG_NAME, generalQrScan.getQrImgName());

        // updating row
        return db.update(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ? AND " + DBManager.SECRET_QR_CODE + " = 1",
                new String[]{String.valueOf(generalQrScan.getQrId())});
    }


    // delete a scanned secret QR Code from the table
    public void deleteScannedGeneralSecretQrCodeById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, DBManager.QR_ID_COLUMN + " = ? AND " + DBManager.SECRET_QR_CODE + " = 1",
                new String[]{String.valueOf(id)});
        db.close();
    }


    // adding scanned secret QR Code to favorite list of scanned secret QR Codes
    public int addScannedGeneralSecretQrCodeToFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 1);

        // updating row
        return db.update(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }

    // removing the scanned secret QR Code from favorite list
    public int removeScannedGeneralSecretQrCodeFromFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 0);

        // updating row
        return db.update(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }


    // get all scanned secret QR Code
    public List<GeneralQrScan> getAllFavScannedGeneralSecretQrCodes() {
        List<GeneralQrScan> scanneSecretFavoriteQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 1 AND " + DBManager.FAVORITE_QR_CODE + " = 1 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeneralQrScan generalQrScan = new GeneralQrScan();
                generalQrScan.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                generalQrScan.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                generalQrScan.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                generalQrScan.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                generalQrScan.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.SECRET_QR_CODE)));
                generalQrScan.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                scanneSecretFavoriteQRCodes.add(generalQrScan);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return scanneSecretFavoriteQRCodes;
    }


    // get number of scanned secret QR Codes
    public int getScannedFavGeneralSecretQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 1 AND " + DBManager.FAVORITE_QR_CODE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    // save scanned general QR Code
    public long saveScannedGeneralQrCode(
            byte[] QrImg,
            String QrImgName) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them

        values.put(DBManager.GENERATED_QR_IMG, QrImg);
        values.put(DBManager.QR_IMG_NAME, QrImgName);

        // insert row
        long id = db.insert(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    // get all scanned general QR Code
    public List<GeneralQrScan> getAllScannedGeneralQrCodes() {
        List<GeneralQrScan> scannedQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 0 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeneralQrScan generalQrScan = new GeneralQrScan();
                generalQrScan.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                generalQrScan.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                generalQrScan.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                generalQrScan.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                generalQrScan.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.SECRET_QR_CODE)));
                generalQrScan.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                scannedQRCodes.add(generalQrScan);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return scannedQRCodes;
    }


    // get number of scanned general QR Codes
    public int getScannedGeneralQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


    // update a scanned general QR Code
    public int updateScannedGeneralQrCode(GeneralQrScan generalQrScan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.QR_IMG_NAME, generalQrScan.getQrImgName());

        // updating row
        return db.update(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ? AND " + DBManager.SECRET_QR_CODE + " = 0",
                new String[]{String.valueOf(generalQrScan.getQrId())});
    }


    // delete a scanned general QR Code from the table
    public void deleteScannedGeneralQrCodeById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, DBManager.QR_ID_COLUMN + " = ? AND " + DBManager.SECRET_QR_CODE + " = 0",
                new String[]{String.valueOf(id)});
        db.close();
    }


    // Adding scanned general QR Code to favorite list of scanned general QR Codes
    public int addScannedGeneralQrCodeToFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 1);

        // updating row
        return db.update(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }

    // removing the scanned general QR Code from favorite list
    public int removeScannedGeneralQrCodeFromFavList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBManager.FAVORITE_QR_CODE, 0);

        // updating row
        return db.update(DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME, values, DBManager.QR_ID_COLUMN + " = ?",
                new String[]{String.valueOf(id)});

    }


    // get all scanned general QR Code
    public List<GeneralQrScan> getAllFavScannedGeneralQrCodes() {
        List<GeneralQrScan> scanneSecretFavoriteQRCodes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 0 AND " + DBManager.FAVORITE_QR_CODE + " = 1 ORDER BY " +
                DBManager.QR_IMG_GENERATED_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GeneralQrScan generalQrScan = new GeneralQrScan();
                generalQrScan.setQrId(cursor.getInt(cursor.getColumnIndex(DBManager.QR_ID_COLUMN)));
                generalQrScan.setQrImg(cursor.getBlob(cursor.getColumnIndex(DBManager.GENERATED_QR_IMG)));
                generalQrScan.setQrImgName(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_NAME)));
                generalQrScan.setTimestamp(cursor.getString(cursor.getColumnIndex(DBManager.QR_IMG_GENERATED_TIMESTAMP)));
                generalQrScan.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.SECRET_QR_CODE)));
                generalQrScan.setFavoriteQr(cursor.getInt(cursor.getColumnIndex(DBManager.FAVORITE_QR_CODE)));

                scanneSecretFavoriteQRCodes.add(generalQrScan);
            } while (cursor.moveToNext());
        }
        // close db connection
        db.close();
        // return notes list
        return scanneSecretFavoriteQRCodes;
    }


    // get number of scanned general QR Codes
    public int getScannedFavGeneralQrCodesCount() {
        String countQuery = "SELECT  * FROM " + DBManager.SCAN_GENERAL_QR_CODE_TABLE_NAME + " WHERE " + DBManager.SECRET_QR_CODE + " = 0 AND " + DBManager.FAVORITE_QR_CODE + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }


}
