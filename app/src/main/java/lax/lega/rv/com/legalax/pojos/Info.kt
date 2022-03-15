package lax.lega.rv.com.legalax.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Info {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("cat")
    @Expose
    var cat: Int? = null

    @SerializedName("media")
    @Expose
    var media: String? = null

    @SerializedName("desc")
    @Expose
    var desc: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("type")
    @Expose
    var type: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("is_bid_placed")
    @Expose
    var isBidPlaced: Int? = null

    @SerializedName("cat_info")
    @Expose
    var catInfo: CatInfo? = null

    @SerializedName("user_info")
    @Expose
    var userInfo: UserInfo? = null
}