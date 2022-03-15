
package lax.lega.rv.com.legalax.pojos.mognoPay.createPaymentMethod;


import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CreatePaymentMethodResponse {

    @SerializedName("data")
    private Data mData;

    @SerializedName("errors")
    private List<Error> mErrors;

    public List<Error> getErrors() {
        return mErrors;
    }


    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

}
