package lax.lega.rv.com.legalax.pojos.getProfile

import com.google.gson.annotations.SerializedName

class GetProfileResponse {

    @SerializedName("success")
    var success: Boolean? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("user")
    var user: User? = null

}
