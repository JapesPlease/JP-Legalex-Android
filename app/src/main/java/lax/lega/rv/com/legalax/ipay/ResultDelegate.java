package lax.lega.rv.com.legalax.ipay;

import android.util.Log;

import com.ipay.IPayIHResultDelegate;

import java.io.Serializable;

import lax.lega.rv.com.legalax.activities.DetailFormActivity;


/**
 * Created by pnduy on 3/9/2016.
 */
public class ResultDelegate implements IPayIHResultDelegate, Serializable {
    private static final long serialVersionUID = 10001L;
    private static final String TAG = ResultDelegate.class.getSimpleName();

    @Override
    public void onPaymentSucceeded(String TransId, String RefNo, String Amount,
                                   String Remark, String AuthCode) {
        DetailFormActivity.Companion.setResultTitle("SUCCESS");
        DetailFormActivity.Companion.setResultInfo("You have successfully completed your transaction.");

        String extra = "";
        extra = extra + "TransId\t= " + TransId + "\n";
        extra = extra + "RefNo\t\t= " + RefNo + "\n";
        extra = extra + "Amount\t= " + Amount + "\n";
        extra = extra + "Remark\t= " + Remark + "\n";
        extra = extra + "AuthCode\t= " + AuthCode + "\n";
//        extra = extra + "CCName\t= " + CCName + "\n";
//        extra = extra + "CCNo\t= " + CCNo + "\n";
//        extra = extra + "S_bankname\t= " + S_bankname + "\n";
//        extra = extra + "S_country\t= " + S_country;
        DetailFormActivity.Companion.setResultTitle(extra);
        Log.d(TAG, "Remark: " + Remark);
    }

    @Override
    public void onPaymentFailed(String TransId, String RefNo, String Amount,
                                String Remark, String ErrDesc) {

        DetailFormActivity.Companion.setResultTitle("FAILURE");
        DetailFormActivity.Companion.setResultInfo(ErrDesc);

        String extra = "";
        extra = extra + "TransId\t= " + TransId + "\n";
        extra = extra + "RefNo\t\t= " + RefNo + "\n";
        extra = extra + "Amount\t= " + Amount + "\n";
        extra = extra + "Remark\t= " + Remark + "\n";
        extra = extra + "ErrDesc\t= " + ErrDesc + "\n";
//        extra = extra + "CCName\t= " + CCName + "\n";
//        extra = extra + "CCNo\t= " + CCNo + "\n";
//        extra = extra + "S_bankname\t= " + S_bankname + "\n";
//        extra = extra + "S_country\t= " + S_country;
        DetailFormActivity.Companion.setResultExtra(extra);
        Log.d(TAG, "ErrDesc: " + ErrDesc);
        Log.d(TAG, "Remark: " + Remark);
    }

    @Override
    public void onPaymentCanceled(String TransId, String RefNo, String Amount,
                                  String Remark, String ErrDesc) {


        DetailFormActivity.Companion.setResultTitle("CANCELED");
        DetailFormActivity.Companion.setResultInfo("The transaction has been cancelled.");

        String extra = "";
        extra = extra + "TransId\t= " + TransId + "\n";
        extra = extra + "RefNo\t\t= " + RefNo + "\n";
        extra = extra + "Amount\t= " + Amount + "\n";
        extra = extra + "Remark\t= " + Remark + "\n";
        extra = extra + "ErrDesc\t= " + ErrDesc + "\n";
//        extra = extra + "CCName\t= " + CCName + "\n";
//        extra = extra + "CCNo\t= " + CCNo + "\n";
//        extra = extra + "S_bankname\t= " + S_bankname + "\n";
//        extra = extra + "S_country\t= " + S_country;
        DetailFormActivity.Companion.setResultExtra(extra);
        Log.d(TAG, "ErrDesc: " + ErrDesc);
        Log.d(TAG, "Remark: " + Remark);

    }

    @Override
    public void onRequeryResult(String MerchantCode, String RefNo,
                                String Amount, String Result) {
        DetailFormActivity.Companion.setResultTitle("Requery Result");
        DetailFormActivity.Companion.setResultInfo("");

        String extra = "";
        extra = extra + "MerchantCode\t= " + MerchantCode + "\n";
        extra = extra + "RefNo\t\t= " + RefNo + "\n";
        extra = extra + "Amount\t= " + Amount + "\n";
        extra = extra + "Result\t= " + Result;
        DetailFormActivity.Companion.setResultExtra(extra);

    }

    @Override
    public void onConnectionError(String merchantCode, String merchantKey,
                                  String RefNo, String Amount, String Remark, String lang, String country) {

        DetailFormActivity.Companion.setResultTitle("CONNECTION ERROR");
        DetailFormActivity.Companion.setResultInfo("The transaction has been unsuccessful.");

        String extra = "";
        extra = extra + "Merchant Code\t= " + merchantCode + "\n";
        extra = extra + "RefNo\t\t= " + RefNo + "\n";
        extra = extra + "Amount\t= " + Amount + "\n";
        extra = extra + "Remark\t= " + Remark + "\n";
        extra = extra + "Language\t= " + lang + "\n";
        extra = extra + "Country\t= " + country + "\n";
//        extra = extra + "ErrDesc\t= " + "Had connection error while connecting to IPay server";
        DetailFormActivity.Companion.setResultExtra(extra);
    }
}
