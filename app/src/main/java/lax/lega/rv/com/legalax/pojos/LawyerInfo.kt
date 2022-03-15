package lax.lega.rv.com.legalax.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LawyerInfo {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("about_us")
    @Expose
    var aboutUs: Any? = null

    @SerializedName("information")
    @Expose
    var information: Any? = null

    @SerializedName("write_something")
    @Expose
    var writeSomething: Any? = null

    @SerializedName("parent_id")
    @Expose
    var parentId: Any? = null

    @SerializedName("age")
    @Expose
    var age: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("role")
    @Expose
    var role: Int? = null

    @SerializedName("facebook_id")
    @Expose
    var facebookId: Any? = null

    @SerializedName("four_digit_code")
    @Expose
    var fourDigitCode: Any? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("profile_image")
    @Expose
    var profileImage: Any? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("email_verified_at")
    @Expose
    var emailVerifiedAt: Any? = null

    @SerializedName("device_type")
    @Expose
    var deviceType: Int? = null

    @SerializedName("fcm_token")
    @Expose
    var fcmToken: String? = null

    @SerializedName("badge")
    @Expose
    var badge: Int? = null

    @SerializedName("chat_badge")
    @Expose
    var chatBadge: Int? = null

    @SerializedName("is_online")
    @Expose
    var isOnline: Int? = null

    @SerializedName("birthday")
    @Expose
    var birthday: Any? = null

    @SerializedName("video_token_ios")
    @Expose
    var videoTokenIos: String? = null

    @SerializedName("points")
    @Expose
    var points: Int? = null

    @SerializedName("vaild_till")
    @Expose
    var vaildTill: Any? = null

    @SerializedName("ordering_num")
    @Expose
    var orderingNum: Int? = null

    @SerializedName("permissions")
    @Expose
    var permissions: Any? = null

    @SerializedName("logout")
    @Expose
    var logout: Int? = null

    @SerializedName("apple_id")
    @Expose
    var appleId: Any? = null

    @SerializedName("video_call_status")
    @Expose
    var videoCallStatus: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("firmid")
    @Expose
    var firmid: Any? = null
}