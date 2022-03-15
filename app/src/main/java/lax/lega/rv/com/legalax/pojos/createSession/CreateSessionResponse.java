
package lax.lega.rv.com.legalax.pojos.createSession;

import com.google.gson.annotations.SerializedName;

public class CreateSessionResponse {
    @SerializedName("session_id")
    private String mSessionId;
    @SerializedName("success")
    private Boolean mSuccess;
    @SerializedName("token")
    private String mToken;

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

}
