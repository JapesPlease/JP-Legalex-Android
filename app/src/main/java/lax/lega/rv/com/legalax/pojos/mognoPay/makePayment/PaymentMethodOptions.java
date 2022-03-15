
package lax.lega.rv.com.legalax.pojos.mognoPay.makePayment;


import com.google.gson.annotations.SerializedName;


public class PaymentMethodOptions {

    @SerializedName("card")
    private Card mCard;

    public Card getCard() {
        return mCard;
    }

    public void setCard(Card card) {
        mCard = card;
    }

}
