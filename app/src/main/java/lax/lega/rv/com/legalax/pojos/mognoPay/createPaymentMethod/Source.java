
package lax.lega.rv.com.legalax.pojos.mognoPay.createPaymentMethod;
import com.google.gson.annotations.SerializedName;

public class Source {

    @SerializedName("attribute")
    private String mAttribute;
    @SerializedName("pointer")
    private String mPointer;

    public String getAttribute() {
        return mAttribute;
    }

    public void setAttribute(String attribute) {
        mAttribute = attribute;
    }

    public String getPointer() {
        return mPointer;
    }

    public void setPointer(String pointer) {
        mPointer = pointer;
    }

}
