
package lax.lega.rv.com.legalax.pojos.mognoPay.detailToServer;


import com.google.gson.annotations.SerializedName;


public class Attributes {

    @SerializedName("client_key")
    private String mClientKey;
    @SerializedName("payment_method")
    private String mPaymentMethod;

    public String getClientKey() {
        return mClientKey;
    }

    public void setClientKey(String clientKey) {
        mClientKey = clientKey;
    }

    public String getPaymentMethod() {
        return mPaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

}
