
package lax.lega.rv.com.legalax.pojos.mognoPay.cardDetail;


import com.google.gson.annotations.SerializedName;


public class CardDetail {

    @SerializedName("data")
    private Data mData;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

}
