
package lax.lega.rv.com.legalax.pojos.mognoPay.createSourceDetail;


import com.google.gson.annotations.SerializedName;


public class Data {

    @SerializedName("attributes")
    private Attributes mAttributes;

    public Attributes getAttributes() {
        return mAttributes;
    }

    public void setAttributes(Attributes attributes) {
        mAttributes = attributes;
    }

}
