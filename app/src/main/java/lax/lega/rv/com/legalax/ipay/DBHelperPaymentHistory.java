package lax.lega.rv.com.legalax.ipay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tonyb on 3/17/2016.
 */
public class DBHelperPaymentHistory extends SQLiteOpenHelper {
    public static final String DB_NAME = "payhisdb";
    public static final String NAME_TBL = "tbl_payhis";

    public static final String STR_TRANSID = "transid";
    public static final String STR_REFNO = "refno";
    public static final String STR_AMOUNT = "amount";
    public static final String STR_REMARK = "remrk";
    public static final String STR_ERRDESC = "errd";

    public static final String STR_INFO = "info";
    public static final String STR_TIT = "tit";
    public static final String STR_TIME = "time";

    public static final int DB_VERSION = 1;

    static DBHelperPaymentHistory dbHelperPaymentHisInstance;

    public static final String[] COLS = {STR_TRANSID, STR_REFNO, STR_AMOUNT, STR_REMARK, STR_ERRDESC
            , STR_INFO, STR_TIT};

    private static final String STR_CREATEDB = "CREATE TABLE " + NAME_TBL + " (" + STR_TRANSID + " TEXT,"
            + STR_REFNO + " TEXT,"
            + STR_AMOUNT + " TEXT,"
            + STR_REMARK + " TEXT,"
            + STR_ERRDESC + " TEXT,"
            + STR_INFO + " TEXT,"
            + STR_TIT + " TEXT,"
            + STR_TIME + " TEXT"
            + ")";

    private SQLiteDatabase mDb;


    public DBHelperPaymentHistory(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * @param context
     * @return
     */
    public static DBHelperPaymentHistory getInstance(Context context) {
        if (dbHelperPaymentHisInstance == null) {
            dbHelperPaymentHisInstance = new DBHelperPaymentHistory(context);
        }
        return dbHelperPaymentHisInstance;
    }

    /**
     * @return
     */
    private boolean openDB() {
        if (dbHelperPaymentHisInstance != null) {
            mDb = dbHelperPaymentHisInstance.getWritableDatabase();
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STR_CREATEDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * @param object
     */
    public void insertPaymentHis(ResultPaymentObject object) {
        if (openDB()) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(STR_TRANSID, object.getStrTransID());
                contentValues.put(STR_REFNO, object.getStrRefNo());
                contentValues.put(STR_AMOUNT, String.valueOf(object.getdAmount()));
                contentValues.put(STR_REMARK, object.getStrRemark());
                contentValues.put(STR_ERRDESC, object.getErrDesc());
                contentValues.put(STR_INFO, object.getStrInfo());
                contentValues.put(STR_TIT, object.getStrTit());
                contentValues.put(STR_TIME, object.getlTime());
                mDb.insert(NAME_TBL, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void close() {
        if (dbHelperPaymentHisInstance != null) {
            mDb.close();
        }
    }


    /**
     * @param id
     * @return
     */
    public boolean deleteItemWithTransID(String id) {
        if (openDB()) {
            return mDb.delete(NAME_TBL, STR_TRANSID + " = ?", new String[]{id}) > 0;
        } else {
            return false;
        }
    }

    /**
     * @return
     */
    public Cursor getAllData() {
        Cursor retCur = null;
        if (openDB()) {
            String getAllSQL = "SELECT * FROM " + NAME_TBL;
            retCur = mDb.rawQuery(getAllSQL, null);
        }

        return retCur;
    }
}
