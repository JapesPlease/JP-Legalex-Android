package lax.lega.rv.com.legalax.ipay;

import android.content.Context;

/**
 * Created by tonyb on 3/21/2016.
 */
public class SavePayHisThread extends Thread {
    ResultPaymentObject paymentObject;
    Context savePayHisContext;
    DBHelperPaymentHistory dbHelperPaymentHistory;

    public SavePayHisThread(ResultPaymentObject object, Context context) {
        paymentObject = object;
        savePayHisContext = context;
        dbHelperPaymentHistory = DBHelperPaymentHistory.getInstance(context);
    }

    @Override
    public void run() {
        paymentObject.setlTime(System.currentTimeMillis());
        dbHelperPaymentHistory.insertPaymentHis(paymentObject);
    }
}
