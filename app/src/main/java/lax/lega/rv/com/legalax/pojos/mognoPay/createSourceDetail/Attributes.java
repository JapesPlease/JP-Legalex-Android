
package lax.lega.rv.com.legalax.pojos.mognoPay.createSourceDetail;


import com.google.gson.annotations.SerializedName;


public class Attributes {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("billing")
    private Billing mBilling;
    @SerializedName("currency")
    private String mCurrency;
    @SerializedName("redirect")
    private Redirect mRedirect;
    @SerializedName("type")
    private String mType;

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
    }

    public Billing getBilling() {
        return mBilling;
    }

    public void setBilling(Billing billing) {
        mBilling = billing;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public Redirect getRedirect() {
        return mRedirect;
    }

    public void setRedirect(Redirect redirect) {
        mRedirect = redirect;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
