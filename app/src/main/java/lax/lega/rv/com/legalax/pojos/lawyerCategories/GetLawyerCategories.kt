package lax.lega.rv.com.legalax.pojos.lawyerCategories

import com.google.gson.annotations.SerializedName

class GetLawyerCategories {
    @SerializedName("response")
    var response: MutableList<Response>? = null

    @SerializedName("success")
    var success: Boolean? = null

}