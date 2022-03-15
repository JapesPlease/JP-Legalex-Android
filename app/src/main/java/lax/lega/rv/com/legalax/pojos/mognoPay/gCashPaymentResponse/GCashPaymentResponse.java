
package lax.lega.rv.com.legalax.pojos.mognoPay.gCashPaymentResponse;


import com.google.gson.annotations.SerializedName;

public class GCashPaymentResponse {

//    @SerializedName("data")
//    private Object mData;
    @SerializedName("success")
    private Boolean mSuccess;

//    public Object getData() {
//        return mData;
//    }
//
//    public void setData(Data data) {
//        mData = data;
//    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }

}
