package lax.lega.rv.com.legalax.pojos.mognoPay.pyamentIntent;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

@SerializedName("amount")
@Expose
private Integer amount;
@SerializedName("currency")
@Expose
private String currency;
@SerializedName("description")
@Expose
private Object description;
@SerializedName("statement_descriptor")
@Expose
private String statementDescriptor;
@SerializedName("status")
@Expose
private String status;
@SerializedName("livemode")
@Expose
private Boolean livemode;
@SerializedName("client_key")
@Expose
private String clientKey;
@SerializedName("created_at")
@Expose
private Integer createdAt;
@SerializedName("updated_at")
@Expose
private Integer updatedAt;
@SerializedName("last_payment_error")
@Expose
private Object lastPaymentError;
@SerializedName("payment_method_allowed")
@Expose
private List<String> paymentMethodAllowed = null;
@SerializedName("payments")
@Expose
private List<Object> payments = null;
@SerializedName("next_action")
@Expose
private Object nextAction;
@SerializedName("payment_method_options")
@Expose
private PaymentMethodOptions paymentMethodOptions;
@SerializedName("metadata")
@Expose
private Object metadata;

public Integer getAmount() {
return amount;
}

public void setAmount(Integer amount) {
this.amount = amount;
}

public String getCurrency() {
return currency;
}

public void setCurrency(String currency) {
this.currency = currency;
}

public Object getDescription() {
return description;
}

public void setDescription(Object description) {
this.description = description;
}

public String getStatementDescriptor() {
return statementDescriptor;
}

public void setStatementDescriptor(String statementDescriptor) {
this.statementDescriptor = statementDescriptor;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public Boolean getLivemode() {
return livemode;
}

public void setLivemode(Boolean livemode) {
this.livemode = livemode;
}

public String getClientKey() {
return clientKey;
}

public void setClientKey(String clientKey) {
this.clientKey = clientKey;
}

public Integer getCreatedAt() {
return createdAt;
}

public void setCreatedAt(Integer createdAt) {
this.createdAt = createdAt;
}

public Integer getUpdatedAt() {
return updatedAt;
}

public void setUpdatedAt(Integer updatedAt) {
this.updatedAt = updatedAt;
}

public Object getLastPaymentError() {
return lastPaymentError;
}

public void setLastPaymentError(Object lastPaymentError) {
this.lastPaymentError = lastPaymentError;
}

public List<String> getPaymentMethodAllowed() {
return paymentMethodAllowed;
}

public void setPaymentMethodAllowed(List<String> paymentMethodAllowed) {
this.paymentMethodAllowed = paymentMethodAllowed;
}

public List<Object> getPayments() {
return payments;
}

public void setPayments(List<Object> payments) {
this.payments = payments;
}

public Object getNextAction() {
return nextAction;
}

public void setNextAction(Object nextAction) {
this.nextAction = nextAction;
}

public PaymentMethodOptions getPaymentMethodOptions() {
return paymentMethodOptions;
}

public void setPaymentMethodOptions(PaymentMethodOptions paymentMethodOptions) {
this.paymentMethodOptions = paymentMethodOptions;
}

public Object getMetadata() {
return metadata;
}

public void setMetadata(Object metadata) {
this.metadata = metadata;
}

}