
package lax.lega.rv.com.legalax.pojos.mognoPay.createSourceDetail;


import com.google.gson.annotations.SerializedName;

public class CreateSourceDetailToServer {

    @SerializedName("data")
    private Data mData;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

}
