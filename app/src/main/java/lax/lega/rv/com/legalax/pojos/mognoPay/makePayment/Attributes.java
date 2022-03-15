
package lax.lega.rv.com.legalax.pojos.mognoPay.makePayment;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("client_key")
    private String mClientKey;
    @SerializedName("created_at")
    private Long mCreatedAt;
    @SerializedName("currency")
    private String mCurrency;
    @SerializedName("description")
    private Object mDescription;
    @SerializedName("last_payment_error")
    private Object mLastPaymentError;
    @SerializedName("livemode")
    private Boolean mLivemode;
    @SerializedName("metadata")
    private Object mMetadata;
    @SerializedName("next_action")
    private NextAction mNextAction;
    @SerializedName("payment_method_allowed")
    private List<String> mPaymentMethodAllowed;
    @SerializedName("payment_method_options")
    private PaymentMethodOptions mPaymentMethodOptions;
    @SerializedName("statement_descriptor")
    private String mStatementDescriptor;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("updated_at")
    private Long mUpdatedAt;

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
    }

    public String getClientKey() {
        return mClientKey;
    }

    public void setClientKey(String clientKey) {
        mClientKey = clientKey;
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

    public Object getDescription() {
        return mDescription;
    }

    public void setDescription(Object description) {
        mDescription = description;
    }

    public Object getLastPaymentError() {
        return mLastPaymentError;
    }

    public void setLastPaymentError(Object lastPaymentError) {
        mLastPaymentError = lastPaymentError;
    }

    public Boolean getLivemode() {
        return mLivemode;
    }

    public void setLivemode(Boolean livemode) {
        mLivemode = livemode;
    }

    public Object getMetadata() {
        return mMetadata;
    }

    public void setMetadata(Object metadata) {
        mMetadata = metadata;
    }

    public NextAction getNextAction() {
        return mNextAction;
    }

    public void setNextAction(NextAction nextAction) {
        mNextAction = nextAction;
    }

    public List<String> getPaymentMethodAllowed() {
        return mPaymentMethodAllowed;
    }

    public void setPaymentMethodAllowed(List<String> paymentMethodAllowed) {
        mPaymentMethodAllowed = paymentMethodAllowed;
    }

    public PaymentMethodOptions getPaymentMethodOptions() {
        return mPaymentMethodOptions;
    }

    public void setPaymentMethodOptions(PaymentMethodOptions paymentMethodOptions) {
        mPaymentMethodOptions = paymentMethodOptions;
    }

    public String getStatementDescriptor() {
        return mStatementDescriptor;
    }

    public void setStatementDescriptor(String statementDescriptor) {
        mStatementDescriptor = statementDescriptor;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public Long getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        mUpdatedAt = updatedAt;
    }

}
