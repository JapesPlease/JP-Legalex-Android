package lax.lega.rv.com.legalax.pojos.mognoPay.pyamentIntent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Card {

@SerializedName("request_three_d_secure")
@Expose
private String requestThreeDSecure;

public String getRequestThreeDSecure() {
return requestThreeDSecure;
}

public void setRequestThreeDSecure(String requestThreeDSecure) {
this.requestThreeDSecure = requestThreeDSecure;
}

}