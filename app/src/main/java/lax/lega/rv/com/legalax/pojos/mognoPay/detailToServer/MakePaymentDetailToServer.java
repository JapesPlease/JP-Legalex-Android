
package lax.lega.rv.com.legalax.pojos.mognoPay.detailToServer;


import com.google.gson.annotations.SerializedName;
public class MakePaymentDetailToServer {

    @SerializedName("data")
    private Data mData;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

}
