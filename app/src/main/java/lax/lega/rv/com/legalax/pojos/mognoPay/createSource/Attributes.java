
package lax.lega.rv.com.legalax.pojos.mognoPay.createSource;
import com.google.gson.annotations.SerializedName;


public class Attributes {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("billing")
    private Billing mBilling;
    @SerializedName("created_at")
    private Long mCreatedAt;
    @SerializedName("currency")
    private String mCurrency;
    @SerializedName("livemode")
    private Boolean mLivemode;
    @SerializedName("redirect")
    private Redirect mRedirect;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("type")
    private String mType;
    @SerializedName("updated_at")
    private Long mUpdatedAt;

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

    public Long getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Long createdAt) {
        mCreatedAt = createdAt;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public Boolean getLivemode() {
        return mLivemode;
    }

    public void setLivemode(Boolean livemode) {
        mLivemode = livemode;
    }

    public Redirect getRedirect() {
        return mRedirect;
    }

    public void setRedirect(Redirect redirect) {
        mRedirect = redirect;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Long getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        mUpdatedAt = updatedAt;
    }

}
