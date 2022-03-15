
package lax.lega.rv.com.legalax.pojos.mognoPay.createPaymentMethod;

import com.google.gson.annotations.SerializedName;


public class Details {

    @SerializedName("exp_month")
    private Long mExpMonth;
    @SerializedName("exp_year")
    private Long mExpYear;
    @SerializedName("last4")
    private String mLast4;

    public Long getExpMonth() {
        return mExpMonth;
    }

    public void setExpMonth(Long expMonth) {
        mExpMonth = expMonth;
    }

    public Long getExpYear() {
        return mExpYear;
    }

    public void setExpYear(Long expYear) {
        mExpYear = expYear;
    }

    public String getLast4() {
        return mLast4;
    }

    public void setLast4(String last4) {
        mLast4 = last4;
    }

}
