package xyz.homapay.hampay.mobile.android.Helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.util.Log;

import xyz.homapay.hampay.mobile.android.model.EnabledHamPay;
import xyz.homapay.hampay.mobile.android.model.LatestPurchase;
import xyz.homapay.hampay.mobile.android.model.RecentPay;
import xyz.homapay.hampay.mobile.android.model.ViewedPaymentRequest;
import xyz.homapay.hampay.mobile.android.util.AESHelper;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/13/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "hampay";

    // Table Names
    private static final String TABLE_RECENT_PAY = "recent_pay";
    private static final String TABLE_ENABLED_HAMPAY = "enabled_hampay";
    private static final String TABLE_CANCELED_PENDING_PAYMENT = "canceled_pending_payment";
    private static final String TABLE_VIEWED_PAYMENT_REQUEST = "viewed_payment_request";


    // Recent Pay Table - column names
    private static final String KEY_ID = "id";
    private static final String KEY_STATUS = "status";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_MESSAGE = "message";

    //Enabled HamPay Table - column names
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_CELL_NUMBER = "cell_number";
    private static final String KEY_PHOTO_ID = "photo_id";

    // Canceled Pending Payment PurchaseRequestId
    private static final String KEY_PURCHASE_REQUEST_ID = "purchase_request_id";
    private static final String KEY_PURCHASE_REQUEST_IS_CANCELED = "is_canceled";

    // Viewed Payment Request
    private static final String KEY_VIEWED_PAYMENT_REQUEST_ID = "payment_id";
    private static final String KEY_VIEWED_PAYMENT_REQUEST_CODE = "payment_code";

    // recent pay table create statement
    private static final String CREATE_TABLE_RECENT_PAY = "CREATE TABLE "
            + TABLE_RECENT_PAY + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_STATUS + " TEXT,"
            + KEY_NAME + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_MESSAGE + " TEXT"
            + ")";

    // enabled hampay table create statement
    private static final String CREATE_TABLE_HAMPAY_ENABLED = "CREATE TABLE "
            + TABLE_ENABLED_HAMPAY + "("
            + KEY_DISPLAY_NAME + " TEXT,"
            + KEY_CELL_NUMBER + " TEXT,"
            + KEY_PHOTO_ID + " TEXT"
            + ")";

    // canceled purchase request table create statement
    private static final String CREATE_TABLE_CANCELED_PENDING_PAYMENT = "CREATE TABLE "
            + TABLE_CANCELED_PENDING_PAYMENT + "("
            + KEY_PURCHASE_REQUEST_ID + " TEXT,"
            + KEY_PURCHASE_REQUEST_IS_CANCELED + " TEXT"
            + ")";


    //Viewed payment request table create statement
    // recent pay table create statement
    private static final String CREATE_TABLE_VIEWED_PAYMENT_REQUEST = "CREATE TABLE "
            + TABLE_VIEWED_PAYMENT_REQUEST + "("
            + KEY_VIEWED_PAYMENT_REQUEST_ID + " INTEGER PRIMARY KEY,"
            + KEY_VIEWED_PAYMENT_REQUEST_CODE + " TEXT"
            + ")";


    SharedPreferences prefs;

    byte[] mobileKey;
    String serverKey;

    String encryptedData;
    String decryptedData;

    DeviceInfo deviceInfo;

    Context context;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
    }

    public DatabaseHelper(Activity context, String serverKey) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        prefs =  context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        deviceInfo = new DeviceInfo(context);

        try {

            mobileKey = SecurityUtils.getInstance(context).generateSHA_256(
                    deviceInfo.getMacAddress(),
                    deviceInfo.getIMEI(),
                    deviceInfo.getAndroidId());

            this.serverKey = serverKey;

//            serverKey = prefs.getString(Constants.USER_ID_TOKEN, "");

        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_RECENT_PAY);
        db.execSQL(CREATE_TABLE_HAMPAY_ENABLED);
        db.execSQL(CREATE_TABLE_CANCELED_PENDING_PAYMENT);
        db.execSQL(CREATE_TABLE_VIEWED_PAYMENT_REQUEST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_PAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENABLED_HAMPAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CANCELED_PENDING_PAYMENT);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_VIEWED_PAYMENT_REQUEST);
        // create new tables
        onCreate(db);
    }

    public long createRecentPay(RecentPay recentPay) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, recentPay.getStatus());
        encryptedData = AESHelper.encrypt(mobileKey, serverKey, recentPay.getName());
        values.put(KEY_NAME, encryptedData.trim());
        encryptedData = AESHelper.encrypt(mobileKey, serverKey, recentPay.getMessage());
        values.put(KEY_MESSAGE, encryptedData.trim());
        encryptedData = AESHelper.encrypt(mobileKey, serverKey, recentPay.getPhone());
        values.put(KEY_PHONE, encryptedData.trim());

        // insert row
        long recent_pay_id = db.insert(TABLE_RECENT_PAY, null, values);

        return recent_pay_id;
    }

    public long createEnabledHamPay(EnabledHamPay enabledHamPay) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        encryptedData = AESHelper.encrypt(mobileKey, serverKey, enabledHamPay.getDisplayName());
        values.put(KEY_DISPLAY_NAME, encryptedData.trim());
        encryptedData = AESHelper.encrypt(mobileKey, serverKey, enabledHamPay.getCellNumber());
        values.put(KEY_CELL_NUMBER, encryptedData.trim());
        if ( enabledHamPay.getPhotoId() != null) {
            encryptedData = AESHelper.encrypt(mobileKey, serverKey, enabledHamPay.getPhotoId());
            values.put(KEY_PHOTO_ID, encryptedData.trim());
        }else {
            values.put(KEY_PHOTO_ID, "");
        }

        long enabled_hampay_id = db.insert(TABLE_ENABLED_HAMPAY, null, values);

        return enabled_hampay_id;
    }

    public void deleteEnabledHamPays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENABLED_HAMPAY, null, null);
    }

    public void deleteAllDataBase(){
        context.deleteDatabase(DATABASE_NAME);
    }


    public RecentPay getRecentPay(String phone_no) {
        SQLiteDatabase db = this.getReadableDatabase();

        encryptedData = AESHelper.encrypt(mobileKey, serverKey, phone_no);

        String selectQuery = "SELECT  * FROM " + TABLE_RECENT_PAY + " WHERE "
                + KEY_PHONE + " = '" + encryptedData.trim() + "'";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        RecentPay recentPay = new RecentPay();
        recentPay.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        recentPay.setStatus((c.getString(c.getColumnIndex(KEY_STATUS))));
        recentPay.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        recentPay.setPhone(c.getString(c.getColumnIndex(KEY_PHONE)));
        recentPay.setMessage(c.getString(c.getColumnIndex(KEY_MESSAGE)));

        return recentPay;
    }


    public EnabledHamPay getEnabledHamPay(String cellNamber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ENABLED_HAMPAY + " WHERE "
                + KEY_CELL_NUMBER + " = '" + cellNamber + "'";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        EnabledHamPay enabledHamPay = new EnabledHamPay();
        enabledHamPay.setCellNumber(c.getString(c.getColumnIndex(KEY_CELL_NUMBER)));
        enabledHamPay.setDisplayName(c.getString(c.getColumnIndex(KEY_DISPLAY_NAME)));
        enabledHamPay.setPhotoId(c.getString(c.getColumnIndex(KEY_PHOTO_ID)));

        return enabledHamPay;
    }


    //parameter
    public boolean getExistRecentPay(String phone_no) {
        SQLiteDatabase db = this.getReadableDatabase();

        encryptedData = AESHelper.encrypt(mobileKey, serverKey, phone_no);

        String selectQuery = "SELECT  * FROM " + TABLE_RECENT_PAY + " WHERE "
                + KEY_PHONE + " = '" + encryptedData.trim() + "'";
        Log.e(LOG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;

    }

    public boolean getIsExistPurchaseRequest(String purchaseRequestId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CANCELED_PENDING_PAYMENT + " WHERE "
                + KEY_PURCHASE_REQUEST_ID + " = '" + purchaseRequestId.trim() + "'";
        Log.e(LOG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;

    }

    public long createPurchaseRequest(String purchaseRequestId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PURCHASE_REQUEST_ID, purchaseRequestId);
        values.put(KEY_PURCHASE_REQUEST_IS_CANCELED, "0");

        // insert row
        long recent_purchase_request_id = db.insert(TABLE_CANCELED_PENDING_PAYMENT, null, values);

        return recent_purchase_request_id;
    }


    public int updatePurchaseRequest(String purchaseRequestId, String isCanceled) {
        SQLiteDatabase db = this.getWritableDatabase();

        final SQLiteStatement statement = db.compileStatement("UPDATE " + TABLE_CANCELED_PENDING_PAYMENT
                + " SET " + KEY_PURCHASE_REQUEST_IS_CANCELED + "=?"
                + " WHERE " + KEY_PURCHASE_REQUEST_ID + "=?");

        statement.bindString(1, isCanceled);
        statement.bindString(2, purchaseRequestId);

        if (Build.VERSION.SDK_INT >= 11) {
            return statement.executeUpdateDelete();
        }else {
            statement.execute();
            return 1;
        }
    }

    public LatestPurchase getPurchaseRequest(String purchaseRequestId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CANCELED_PENDING_PAYMENT + " WHERE "
                + KEY_PURCHASE_REQUEST_ID + " = '" + purchaseRequestId + "'";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        LatestPurchase latestPurchase = new LatestPurchase();
        latestPurchase.setPurchaseRequestId(c.getString(c.getColumnIndex(KEY_PURCHASE_REQUEST_ID)));
        latestPurchase.setIsCanceled(c.getString(c.getColumnIndex(KEY_PURCHASE_REQUEST_IS_CANCELED)));

        return latestPurchase;
    }

    public List<LatestPurchase> getAllLatestPurchases() {
        List<LatestPurchase> latestPurchases = new ArrayList<LatestPurchase>();
        String selectQuery = "SELECT  * FROM " + TABLE_CANCELED_PENDING_PAYMENT;
        Log.e(LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                LatestPurchase latestPurchase = new LatestPurchase();
                latestPurchase.setPurchaseRequestId(c.getString(c.getColumnIndex(KEY_PURCHASE_REQUEST_ID)));
                latestPurchase.setIsCanceled(c.getString(c.getColumnIndex(KEY_PURCHASE_REQUEST_IS_CANCELED)));

                // adding to todo list
                latestPurchases.add(latestPurchase);
            } while (c.moveToNext());
        }

        return latestPurchases;
    }

    public boolean getExistEnabledHamPay(String cellNember) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ENABLED_HAMPAY + " WHERE "
                + KEY_CELL_NUMBER + " = '" + cellNember + "'";
        Log.e(LOG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;

    }


    public int updateRecentPay(RecentPay recentPay) {
        SQLiteDatabase db = this.getWritableDatabase();

        final SQLiteStatement statement = db.compileStatement("UPDATE " + TABLE_RECENT_PAY
                + " SET " + KEY_MESSAGE + "=?"
                + " WHERE " + KEY_PHONE + "=?");

        encryptedData = AESHelper.encrypt(mobileKey, serverKey, recentPay.getMessage());
        statement.bindString(1, encryptedData);
        statement.bindString(2, recentPay.getPhone());

        if (Build.VERSION.SDK_INT >= 11) {
            return statement.executeUpdateDelete();
        }else {
            statement.execute();
            return 1;
        }
    }

    public List<RecentPay> getAllRecentPays() {
        List<RecentPay> recentPays = new ArrayList<RecentPay>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT_PAY + " LIMIT " + Constants.RECENT_PAY_TO_ONE_LIMIT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                RecentPay recentPay = new RecentPay();
                recentPay.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                recentPay.setStatus((c.getString(c.getColumnIndex(KEY_STATUS))));
                decryptedData = AESHelper.decrypt(mobileKey, serverKey, c.getString(c.getColumnIndex(KEY_NAME)).trim());
                recentPay.setName(decryptedData);
                decryptedData = AESHelper.decrypt(mobileKey, serverKey, c.getString(c.getColumnIndex(KEY_PHONE)).trim());
                recentPay.setPhone(decryptedData);
                decryptedData = AESHelper.decrypt(mobileKey, serverKey, c.getString(c.getColumnIndex(KEY_MESSAGE)).trim());
                recentPay.setMessage(decryptedData);

                // adding to todo list
                recentPays.add(recentPay);
            } while (c.moveToNext());
        }

        return recentPays;
    }

    public List<EnabledHamPay> getAllEnabledHamPay() {
        List<EnabledHamPay> enabledHamPays = new ArrayList<EnabledHamPay>();
        String selectQuery = "SELECT  * FROM " + TABLE_ENABLED_HAMPAY;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                EnabledHamPay enabledHamPay = new EnabledHamPay();
                decryptedData = AESHelper.decrypt(mobileKey, serverKey, c.getString(c.getColumnIndex(KEY_CELL_NUMBER)));
                enabledHamPay.setCellNumber(decryptedData);
                decryptedData = AESHelper.decrypt(mobileKey, serverKey, c.getString(c.getColumnIndex(KEY_DISPLAY_NAME)));
                enabledHamPay.setDisplayName(decryptedData);
                decryptedData = AESHelper.decrypt(mobileKey, serverKey, c.getString(c.getColumnIndex(KEY_PHOTO_ID)));
                enabledHamPay.setPhotoId(decryptedData);

                // adding to todo list
                enabledHamPays.add(enabledHamPay);
            } while (c.moveToNext());
        }

        return enabledHamPays;
    }

    public long createViewedPaymentRequest(String paymentCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIEWED_PAYMENT_REQUEST_CODE, paymentCode);
        long viewed_payment_request_id = db.insert(TABLE_VIEWED_PAYMENT_REQUEST, null, values);
        return viewed_payment_request_id;
    }

    public boolean checkPaymentRequest(String paymentCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_VIEWED_PAYMENT_REQUEST + " WHERE "
                + KEY_VIEWED_PAYMENT_REQUEST_CODE + " = '" + paymentCode + "'";
//        Log.e(LOG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;

    }

}
