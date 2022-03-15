package lax.lega.rv.com.legalax.pojos.mognoPay.pyamentIntent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentIntentResponse {

@SerializedName("data")
@Expose
private Data data;
@SerializedName("success")
@Expose
private Boolean success;

public Data getData() {
return data;
}

public void setData(Data data) {
this.data = data;
}

public Boolean getSuccess() {
return success;
}

public void setSuccess(Boolean success) {
this.success = success;
}

}