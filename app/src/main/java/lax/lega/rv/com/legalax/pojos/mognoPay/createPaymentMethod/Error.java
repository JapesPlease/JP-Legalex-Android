
package lax.lega.rv.com.legalax.pojos.mognoPay.createPaymentMethod;

import com.google.gson.annotations.SerializedName;

public class Error {

    @SerializedName("code")
    private String mCode;
    @SerializedName("detail")
    private String mDetail;
    @SerializedName("source")
    private Source mSource;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        mDetail = detail;
    }

    public Source getSource() {
        return mSource;
    }

    public void setSource(Source source) {
        mSource = source;
    }

}
