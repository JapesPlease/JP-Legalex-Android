package lax.lega.rv.com.legalax.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ForgotPwdPojo implements Serializable {

@SerializedName("message")
@Expose
private String message;
@SerializedName("four_digit_code")
@Expose
private Integer fourDigitCode;
@SerializedName("success")
@Expose
private Boolean success;

public String getMessage() {
return message;
}

public void setMessage(String message) {
this.message = message;
}

public Integer getFourDigitCode() {
return fourDigitCode;
}

public void setFourDigitCode(Integer fourDigitCode) {
this.fourDigitCode = fourDigitCode;
}

public Boolean getSuccess() {
return success;
}

public void setSuccess(Boolean success) {
this.success = success;
}

}