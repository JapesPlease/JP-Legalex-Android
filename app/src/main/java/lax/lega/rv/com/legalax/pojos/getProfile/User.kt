package lax.lega.rv.com.legalax.pojos.getProfile

import com.google.gson.annotations.SerializedName

class User {

    @SerializedName("about_us")
    var aboutUs: String? = null
    @SerializedName("age")
    var age: String? = null
    @SerializedName("badge")
    var badge: Long? = null
    @SerializedName("birthday")
    var birthday: Any? = null
    @SerializedName("created_at")
    var createdAt: String? = null
    @SerializedName("device_type")
    var deviceType: Long? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("email_verified_at")
    var emailVerifiedAt: Any? = null
    @SerializedName("facebook_id")
    var facebookId: Any? = null
    @SerializedName("fcm_token")
    var fcmToken: String? = null
    @SerializedName("four_digit_code")
    var fourDigitCode: Any? = null
    @SerializedName("id")
    var id: Long? = null
    @SerializedName("information")
    var information: String? = null
    @SerializedName("is_online")
    var isOnline: Long? = null
    @SerializedName("last_name")
    var lastName: String? = null
    @SerializedName("location")
    var location: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("parent_id")
    var parentId: Any? = null
    @SerializedName("phone")
    var phone: String? = null
    @SerializedName("points")
    var points: Int? = null
    @SerializedName("profile_image")
    var profileImage: String? = null
    @SerializedName("role")
    var role: Long? = null
    @SerializedName("status")
    var status: Long? = null
    @SerializedName("updated_at")
    var updatedAt: String? = null
    @SerializedName("vaild_till")
    var vaildTill: Any? = null
    @SerializedName("video_token_ios")
    var videoTokenIos: Any? = null
    @SerializedName("write_something")
    var writeSomething: Any? = null

}
