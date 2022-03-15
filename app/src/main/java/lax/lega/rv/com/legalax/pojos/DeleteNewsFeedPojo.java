package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteNewsFeedPojo {

@SerializedName("success")
@Expose
private Boolean success;
@SerializedName("response")
@Expose
private String response;

public Boolean getSuccess() {
return success;
}

public void setSuccess(Boolean success) {
this.success = success;
}

public String getResponse() {
return response;
}

public void setResponse(String response) {
this.response = response;
}

}