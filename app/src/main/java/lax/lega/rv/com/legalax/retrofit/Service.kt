package lax.lega.rv.com.legalax.retrofit

import com.google.gson.JsonElement
import lax.lega.rv.com.legalax.pojos.*
import lax.lega.rv.com.legalax.pojos.getProfile.GetProfileResponse
import lax.lega.rv.com.legalax.pojos.getSetting.GetSettingResponse
import lax.lega.rv.com.legalax.pojos.lawyerCategories.GetLawyerCategories
import lax.lega.rv.com.legalax.pojos.mognoPay.cardDetail.CardDetail
import lax.lega.rv.com.legalax.pojos.mognoPay.createPaymentMethod.CreatePaymentMethodResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.createSource.CreateSourceResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.createSourceDetail.CreateSourceDetailToServer
import lax.lega.rv.com.legalax.pojos.mognoPay.detailToServer.MakePaymentDetailToServer
import lax.lega.rv.com.legalax.pojos.mognoPay.gCashPaymentResponse.GCashPaymentResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.makePayment.PaymentResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.pyamentIntent.PaymentIntentResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.threedsecure.ThreeDSecurePaymentResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by user on 5/2/18.
 */

interface Service {
//    @Multipart
//    @POST("document-upload")
//    fun add_event(@Header("Authorization") Authorization: String,
//                  @Header("Accept") Accept: String
//                  , @Part("type") title: RequestBody
//                  , @Part("title[]") type: ArrayList<RequestBody>
//                  , @Part avatar: ArrayList<MultipartBody.Part>): Call<JsonElement>

    @Multipart
    @POST("document-upload")
    fun add_event(@Header("Authorization") Authorization: String,
                  @Header("Accept") Accept: String, @Part("type") title: RequestBody, @Part("title[]") type: ArrayList<RequestBody>, @Part("file_name") fileName: RequestBody, @Part("desc[]") desc: ArrayList<RequestBody>
    ): Call<JsonElement>

    @Multipart
    @POST("send-message")
    fun uploadImageOnChat(@Header("Authorization") Authorization: String,
                          @Header("Accept") Accept: String, @Part("receiver_id") receiver_id: RequestBody, @Part("group_chat") group_chat: RequestBody, @Part avatar: MultipartBody.Part): Call<SendMessagePojo>

    @FormUrlEncoded
    @POST("change-password")
    fun change_pwd(@Header("Authorization") Authorization: String,
                   @Header("Accept") Accept: String, @Field("old_password") old_password: String, @Field("new_password") new_password: String, @Field("confirm_password") confirm_password: String): Call<JsonElement>

    @FormUrlEncoded

    @POST("updatePoints")
    fun add_credit(@Header("Authorization") Authorization: String,
                   @Header("Accept") Accept: String, @Field("Points") points: String, @Field("userId") userId: Long, @Field("add") add: String): Call<AddCreditPointsPojo>

    @FormUrlEncoded
    @POST("saveVideoCall")
    fun saveVideoCall(@Header("Authorization") Authorization: String,
                      @Field("receiver_id") receiver_id: String): Call<ResponseBody>


    @GET
    fun getCurrancyConvertor(@Url url: String): Call<CurrancyConvertorPojo>


    @GET("get-profile")
    fun getUserProfileResponse(@Header("Authorization") Authorization: String): Call<GetProfileResponse>

    @GET("getSettings")
    fun getSettings(@Header("Authorization") Authorization: String): Call<GetSettingResponse>

    @FormUrlEncoded
    @POST("updateSettings")
    fun updateSettings(@Header("Authorization") Authorization: String, @Field("filter_value") filter_value: String, @Field("setting_name") setting_name: String, @Field("setting_value") setting_value: String): Call<UpdateSettingResponse>


    @POST("payment_methods")
    fun createPaymentMethod(@Header("Authorization") Authorization: String, @Body data: CardDetail): Call<CreatePaymentMethodResponse>


    @FormUrlEncoded
    @POST("payment/intent")
    fun createPaymentIntent(@Header("Authorization") Authorization: String, @Field("amount") amount: Int): Call<PaymentIntentResponse>


    @POST("payment_intents/{paymentIntentId}/attach")
    fun makePayment(@Header("Authorization") Authorization: String, @Path("paymentIntentId") paymentIntentId: String, @Body detail: MakePaymentDetailToServer): Call<PaymentResponse>


    @POST("sources")
    fun createSource(@Header("Authorization") Authorization: String, @Body detail: CreateSourceDetailToServer): Call<CreateSourceResponse>


    @FormUrlEncoded
    @POST("create/payment/by/source")
    fun gcashPayment(@Header("Authorization") Authorization: String, @Header("Accept") Accept: String, @Field("amount") amount: Long, @Field("source_id") source_id: String, @Field("points") points: String, @Field("is_credit") isCredit: String): Call<GCashPaymentResponse>

    @FormUrlEncoded
    @POST("chats")
    fun getChats(@Header("Authorization") Authorization: String, @Header("Accept") Accept: String, @Field("receiver_id") receiver_id: String, @Field("group_chat") group_chat: String): Call<GetChatDataPojo>

    @FormUrlEncoded
    @POST("update/user/video/status")
    fun updateVideoStatus(@Header("Authorization") Authorization: String, @Field("status") status: String): Call<ResponseBody>

    @GET("get/lawyer/cat")
    fun getAllLawyerCategory(@Header("Authorization") Authorization: String): Call<GetLawyerCategories>

    @GET("get/lawyer/selected/cat")
    fun getSelectedCategory(@Header("Authorization") Authorization: String): Call<GetLawyerCategories>


    @GET
    fun getUserList(@Header("Authorization") Authorization: String, @Url url: String): Call<GetUsersPojo>

    @FormUrlEncoded
    @POST("update/lawyer/cat")
    fun updateSelectedCategories(@Header("Authorization") Authorization: String, @Field("cat_ids[]") list: List<String>): Call<ResponseBody>

    @GET("payment_intents/{paymentIntentId}")
    fun checkPaymentStatus(@Header("Authorization") Authorization: String, @Path("paymentIntentId") paymentIntentId: String, @Query("client_key") client_key: String): Call<ThreeDSecurePaymentResponse>

}