package lax.lega.rv.com.legalax.pojos.mognoPay.createSourceDetail;
import com.google.gson.annotations.SerializedName;
public class Redirect {

    @SerializedName("failed")
    private String mFailed;
    @SerializedName("success")
    private String mSuccess;

    public String getFailed() {
        return mFailed;
    }

    public void setFailed(String failed) {
        mFailed = failed;
    }

    public String getSuccess() {
        return mSuccess;
    }

    public void setSuccess(String success) {
        mSuccess = success;
    }

}
