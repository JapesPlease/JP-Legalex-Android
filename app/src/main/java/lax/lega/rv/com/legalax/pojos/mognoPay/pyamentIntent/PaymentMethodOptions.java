package lax.lega.rv.com.legalax.pojos.mognoPay.pyamentIntent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentMethodOptions {

@SerializedName("card")
@Expose
private Card card;

public Card getCard() {
return card;
}

public void setCard(Card card) {
this.card = card;
}

}