
package lax.lega.rv.com.legalax.pojos.mognoPay.createPaymentMethod;


import com.google.gson.annotations.SerializedName;

import lax.lega.rv.com.legalax.pojos.mognoPay.createSource.Redirect;

@SuppressWarnings("unused")
public class Attributes {

    @SerializedName("billing")
    private Object mBilling;
    @SerializedName("created_at")
    private Long mCreatedAt;
    @SerializedName("details")
    private Details mDetails;
    @SerializedName("livemode")
    private Boolean mLivemode;
    @SerializedName("metadata")
    private Object mMetadata;
    @SerializedName("type")
    private String mType;
    @SerializedName("updated_at")
    private Long mUpdatedAt;

    @SerializedName("next_action")
   // NextAction nextAction;

    public Object getBilling() {
        return mBilling;
    }

    public void setBilling(Object billing) {
        mBilling = billing;
    }

    public Long getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Long createdAt) {
        mCreatedAt = createdAt;
    }

    public Details getDetails() {
        return mDetails;
    }

    public void setDetails(Details details) {
        mDetails = details;
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

    public Object getmBilling() {
        return mBilling;
    }

    public void setmBilling(Object mBilling) {
        this.mBilling = mBilling;
    }

    public Long getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(Long mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public Details getmDetails() {
        return mDetails;
    }

    public void setmDetails(Details mDetails) {
        this.mDetails = mDetails;
    }

    public Boolean getmLivemode() {
        return mLivemode;
    }

    public void setmLivemode(Boolean mLivemode) {
        this.mLivemode = mLivemode;
    }

    public Object getmMetadata() {
        return mMetadata;
    }

    public void setmMetadata(Object mMetadata) {
        this.mMetadata = mMetadata;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public Long getmUpdatedAt() {
        return mUpdatedAt;
    }

    public void setmUpdatedAt(Long mUpdatedAt) {
        this.mUpdatedAt = mUpdatedAt;
    }

   /* public NextAction getNextAction() {
        return nextAction;
    }*/

   /* public void setNextAction(NextAction nextAction) {
        this.nextAction = nextAction;
    }*/
}

