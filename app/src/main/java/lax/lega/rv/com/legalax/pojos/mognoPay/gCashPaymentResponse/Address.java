
package lax.lega.rv.com.legalax.pojos.mognoPay.gCashPaymentResponse;


import com.google.gson.annotations.SerializedName;


public class Address {

    @SerializedName("city")
    private Object mCity;
    @SerializedName("country")
    private Object mCountry;
    @SerializedName("line1")
    private Object mLine1;
    @SerializedName("line2")
    private Object mLine2;
    @SerializedName("postal_code")
    private Object mPostalCode;
    @SerializedName("state")
    private Object mState;

    public Object getCity() {
        return mCity;
    }

    public void setCity(Object city) {
        mCity = city;
    }

    public Object getCountry() {
        return mCountry;
    }

    public void setCountry(Object country) {
        mCountry = country;
    }

    public Object getLine1() {
        return mLine1;
    }

    public void setLine1(Object line1) {
        mLine1 = line1;
    }

    public Object getLine2() {
        return mLine2;
    }

    public void setLine2(Object line2) {
        mLine2 = line2;
    }

    public Object getPostalCode() {
        return mPostalCode;
    }

    public void setPostalCode(Object postalCode) {
        mPostalCode = postalCode;
    }

    public Object getState() {
        return mState;
    }

    public void setState(Object state) {
        mState = state;
    }

}
