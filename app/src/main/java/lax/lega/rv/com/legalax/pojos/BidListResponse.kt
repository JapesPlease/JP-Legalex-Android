package lax.lega.rv.com.legalax.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BidListResponse {

    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("data")
    @Expose
    var data: List<BidList>? = null

}