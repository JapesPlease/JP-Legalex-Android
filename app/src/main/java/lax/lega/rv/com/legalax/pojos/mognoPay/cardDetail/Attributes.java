
package lax.lega.rv.com.legalax.pojos.mognoPay.cardDetail;


import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("details")
    private Details mDetails;
    @SerializedName("type")
    private String mType;

    public Details getDetails() {
        return mDetails;
    }

    public void setDetails(Details details) {
        mDetails = details;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
