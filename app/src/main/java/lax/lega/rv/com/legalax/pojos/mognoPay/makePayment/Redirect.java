package lax.lega.rv.com.legalax.pojos.mognoPay.makePayment;

import com.google.gson.annotations.SerializedName;

public class Redirect {
@SerializedName("url")
private String url;
@SerializedName("return_url")

private String returnUrl;

public String getUrl() {
return url;
}

public void setUrl(String url) {
this.url = url;
}

public String getReturnUrl() {
return returnUrl;
}

public void setReturnUrl(String returnUrl) {
this.returnUrl = returnUrl;
}

}