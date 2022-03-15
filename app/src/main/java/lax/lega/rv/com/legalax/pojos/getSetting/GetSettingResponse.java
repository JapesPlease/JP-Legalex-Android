
package lax.lega.rv.com.legalax.pojos.getSetting;

import java.util.List;
import com.google.gson.annotations.SerializedName;
public class GetSettingResponse {
    @SerializedName("data")
    private List<Datum> mData;
    @SerializedName("success")
    private Boolean mSuccess;

    public List<Datum> getData() {
        return mData;
    }

    public void setData(List<Datum> data) {
        mData = data;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }

}
