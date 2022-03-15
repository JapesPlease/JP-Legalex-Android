
package lax.lega.rv.com.legalax.pojos.mognoPay.createSource;
import com.google.gson.annotations.SerializedName;
public class Redirect {
    @SerializedName("checkout_url")
    private String mCheckoutUrl;
    @SerializedName("failed")
    private String mFailed;
    @SerializedName("success")
    private String mSuccess;

    public String getCheckoutUrl() {
        return mCheckoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        mCheckoutUrl = checkoutUrl;
    }

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
