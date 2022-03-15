package lax.lega.rv.com.legalax.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 6/5/17.
 */

public class MySharedPreference {
    Context activity;
    SharedPreferences sharedPreferences;
    public static String IS_CHAT_ACTIVE = "is_chat_active";
    //public static String IS_CALL_ENABLE= "CAll_Enable";


    public  static  String PAYMENT_EMAIL="paymentEmail";
    public  static  String PAYMENT_NAME="paymentName";
    public  static  String PAYMENT_PHONE="paymentPhone";
    public  static  String PAYMENT_AMOUNT="paymentAmount";
    public  static  String PAYMENT_CREDITS="paymentCredits";
    public  static  String PAYMENT_SOURCE_ID="paymentSourceId";
    public  static  String PAYMENT_TYPE="paymentType";

    public MySharedPreference(Context activity) {
        this.activity = activity;
        createSharedPreferences();
    }

    public void createSharedPreferences() {
        sharedPreferences = activity.getSharedPreferences("login_detail", Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void removeString(String key) {
        sharedPreferences.edit().remove(key).commit();
    }

    public void removeAll() {
        sharedPreferences.edit().clear().commit();
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0L);
    }

    public long getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }


}
