package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckFBPojo {

@SerializedName("response")
@Expose
private Boolean response;

public Boolean getResponse() {
return response;
}

public void setResponse(Boolean response) {
this.response = response;
}

}