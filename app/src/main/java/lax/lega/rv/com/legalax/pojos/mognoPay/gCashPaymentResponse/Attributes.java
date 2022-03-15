
package lax.lega.rv.com.legalax.pojos.mognoPay.gCashPaymentResponse;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class Attributes {

    @SerializedName("access_url")
    private Object mAccessUrl;
    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("available_at")
    private Long mAvailableAt;
    @SerializedName("balance_transaction_id")
    private String mBalanceTransactionId;
    @SerializedName("billing")
    private Billing mBilling;
    @SerializedName("created_at")
    private Long mCreatedAt;
    @SerializedName("currency")
    private String mCurrency;
    @SerializedName("description")
    private Object mDescription;
    @SerializedName("disputed")
    private Boolean mDisputed;
    @SerializedName("external_reference_number")
    private Object mExternalReferenceNumber;
    @SerializedName("fee")
    private Long mFee;
    @SerializedName("livemode")
    private Boolean mLivemode;
    @SerializedName("net_amount")
    private Long mNetAmount;
    @SerializedName("paid_at")
    private Long mPaidAt;
    @SerializedName("payout")
    private Object mPayout;
    @SerializedName("refunds")
    private List<Object> mRefunds;
    @SerializedName("source")
    private Source mSource;
    @SerializedName("statement_descriptor")
    private String mStatementDescriptor;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("updated_at")
    private Long mUpdatedAt;

    public Object getAccessUrl() {
        return mAccessUrl;
    }

    public void setAccessUrl(Object accessUrl) {
        mAccessUrl = accessUrl;
    }

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
    }

    public Long getAvailableAt() {
        return mAvailableAt;
    }

    public void setAvailableAt(Long availableAt) {
        mAvailableAt = availableAt;
    }

    public String getBalanceTransactionId() {
        return mBalanceTransactionId;
    }

    public void setBalanceTransactionId(String balanceTransactionId) {
        mBalanceTransactionId = balanceTransactionId;
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

    public Object getDescription() {
        return mDescription;
    }

    public void setDescription(Object description) {
        mDescription = description;
    }

    public Boolean getDisputed() {
        return mDisputed;
    }

    public void setDisputed(Boolean disputed) {
        mDisputed = disputed;
    }

    public Object getExternalReferenceNumber() {
        return mExternalReferenceNumber;
    }

    public void setExternalReferenceNumber(Object externalReferenceNumber) {
        mExternalReferenceNumber = externalReferenceNumber;
    }

    public Long getFee() {
        return mFee;
    }

    public void setFee(Long fee) {
        mFee = fee;
    }

    public Boolean getLivemode() {
        return mLivemode;
    }

    public void setLivemode(Boolean livemode) {
        mLivemode = livemode;
    }

    public Long getNetAmount() {
        return mNetAmount;
    }

    public void setNetAmount(Long netAmount) {
        mNetAmount = netAmount;
    }

    public Long getPaidAt() {
        return mPaidAt;
    }

    public void setPaidAt(Long paidAt) {
        mPaidAt = paidAt;
    }

    public Object getPayout() {
        return mPayout;
    }

    public void setPayout(Object payout) {
        mPayout = payout;
    }

    public List<Object> getRefunds() {
        return mRefunds;
    }

    public void setRefunds(List<Object> refunds) {
        mRefunds = refunds;
    }

    public Source getSource() {
        return mSource;
    }

    public void setSource(Source source) {
        mSource = source;
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
