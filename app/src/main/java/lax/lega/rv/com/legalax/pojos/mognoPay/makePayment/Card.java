
package lax.lega.rv.com.legalax.pojos.mognoPay.makePayment;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Card {

    @SerializedName("request_three_d_secure")
    private String mRequestThreeDSecure;

    public String getRequestThreeDSecure() {
        return mRequestThreeDSecure;
    }

    public void setRequestThreeDSecure(String requestThreeDSecure) {
        mRequestThreeDSecure = requestThreeDSecure;
    }

}
