package lax.lega.rv.com.legalax.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class BidList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("problem_id")
    @Expose
    var problemId: Int? = null

    @SerializedName("bid")
    @Expose
    var bid: Double? = null

    @SerializedName("notes")
    @Expose
    var notes: Any? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("info")
    @Expose
    var info: Info? = null

    @SerializedName("is_bid_placed")
    @Expose
    var isBidPlaced: Int? = null

    @SerializedName("cat_info")
    @Expose
    var catInfo: CatInfo? = null

    @SerializedName("user_info")
    @Expose
    var userInfo: UserInfo? = null

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


    @SerializedName("type")
    @Expose
    var type: Int? = null


}