package lax.lega.rv.com.legalax.pojos.mognoPay.makePayment;

import com.google.gson.annotations.SerializedName;

public class NextAction {

    @SerializedName("type")
    private String type;
    @SerializedName("redirect")
    public Redirect redirect;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(Redirect redirect) {
        this.redirect = redirect;
    }
}