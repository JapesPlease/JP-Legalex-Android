package lax.lega.rv.com.legalax.pojos
import com.google.gson.annotations.SerializedName


data class AddCreditPointsPojo(
    @SerializedName("points")
    val points: Int,
    @SerializedName("success")
    val success: Boolean
)