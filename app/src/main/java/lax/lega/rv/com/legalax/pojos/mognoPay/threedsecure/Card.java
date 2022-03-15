
package lax.lega.rv.com.legalax.pojos.mognoPay.threedsecure;
import com.google.gson.annotations.SerializedName;


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
