package lax.lega.rv.com.legalax.pojos.lawyerCategories

import com.google.gson.annotations.SerializedName

class Response {
    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

}