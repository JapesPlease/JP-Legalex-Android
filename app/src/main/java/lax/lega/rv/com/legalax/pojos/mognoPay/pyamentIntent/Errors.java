package lax.lega.rv.com.legalax.pojos.mognoPay.pyamentIntent;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public  class Errors {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("detail")
    @Expose
    private String detail;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
