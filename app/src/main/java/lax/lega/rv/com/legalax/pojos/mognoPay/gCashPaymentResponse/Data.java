
package lax.lega.rv.com.legalax.pojos.mognoPay.gCashPaymentResponse;


import com.google.gson.annotations.SerializedName;


public class Data {

    @SerializedName("attributes")
    private Attributes mAttributes;
    @SerializedName("data")
    private Data mData;
    @SerializedName("id")
    private String mId;
    @SerializedName("type")
    private String mType;

    public Attributes getAttributes() {
        return mAttributes;
    }

    public void setAttributes(Attributes attributes) {
        mAttributes = attributes;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

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
