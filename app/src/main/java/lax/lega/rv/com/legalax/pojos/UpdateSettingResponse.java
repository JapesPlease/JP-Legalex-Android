
package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.SerializedName;
public class UpdateSettingResponse {

    @SerializedName("success")
    private Boolean mSuccess;

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }

}
