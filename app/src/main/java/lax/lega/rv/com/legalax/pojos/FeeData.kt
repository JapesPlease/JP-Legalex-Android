package lax.lega.rv.com.legalax.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FeeData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("setting_name")
    @Expose
    var settingName: String? = null

    @SerializedName("setting_value")
    @Expose
    var settingValue: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
}