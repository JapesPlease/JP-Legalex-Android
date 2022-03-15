package lax.lega.rv.com.legalax.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProposalList {

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
    var notes: String? = null

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

    @SerializedName("lawyer_info")
    @Expose
    var lawyerInfo: LawyerInfo? = null
}