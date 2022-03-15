
package lax.lega.rv.com.legalax.pojos.mognoPay.cardDetail;
import com.google.gson.annotations.SerializedName;

public class Details {

    @SerializedName("card_number")
    private String mCardNumber;
    @SerializedName("cvc")
    private String mCvc;
    @SerializedName("exp_month")
    private Long mExpMonth;
    @SerializedName("exp_year")
    private Long mExpYear;

    public String getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(String cardNumber) {
        mCardNumber = cardNumber;
    }

    public String getCvc() {
        return mCvc;
    }

    public void setCvc(String cvc) {
        mCvc = cvc;
    }

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

}
