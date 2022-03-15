
package lax.lega.rv.com.legalax.pojos.mognoPay.gCashPaymentResponse;


import com.google.gson.annotations.SerializedName;

public class Source {

    @SerializedName("id")
    private String mId;
    @SerializedName("type")
    private String mType;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
