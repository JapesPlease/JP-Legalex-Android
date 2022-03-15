package lax.lega.rv.com.legalax.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.facebook.appevents.AppEventsLogger
import com.paypal.android.sdk.payments.*
import kotlinx.android.synthetic.main.activity_purchase_credits.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.calling.BaseActivity
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.paymongo.CreditCardActivity
import lax.lega.rv.com.legalax.paymongo.GCashActivity
import lax.lega.rv.com.legalax.pojos.AddCreditPointsPojo
import lax.lega.rv.com.legalax.pojos.CurrancyConvertorPojo
import lax.lega.rv.com.legalax.pojos.createSession.CreateSessionResponse
import lax.lega.rv.com.legalax.pojos.getProfile.GetProfileResponse
import lax.lega.rv.com.legalax.pojos.getSetting.GetSettingResponse
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getRetrofitError
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.util.*


class PurchaseCreditsActivity : BaseActivity(), View.OnClickListener {

    var credit_number_price = 0.0
    val PAYPAL_REQUEST_CODE = 123
    var config = PayPalConfiguration()
    lateinit var sharedPreference: MySharedPreference
    var logger: AppEventsLogger? = null
    var inUSD: Double = 0.0
    var usd_return: Boolean = false
    var consultationFee = 0.0
    var points = 0
    var isProposal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_credits)
        logger = AppEventsLogger.newLogger(this)
//        textViewSinglePrice.text = "" + consultationFee
        textViewPurchase.setOnClickListener(this)
        textViewCancel.setOnClickListener(this)
        textView16.setOnClickListener(this)
        isProposal = intent.getBooleanExtra("proposal", false)
        sharedPreference = MySharedPreference(this)
        ed_credit_qunty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.trim().toString().isNotEmpty()) {
                    credit_number_price = consultationFee * ed_credit_qunty.text.toString().toInt()
                    textView12.text = "P $credit_number_price"
                } else {
                    credit_number_price = consultationFee * 0
                    textView12.text = credit_number_price.toString()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        config.environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(Utils.PAYPAL_CLIENT_ID_LIVE)
        config.acceptCreditCards(false)
        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
        getUserProfile()
        getSetting()
    }

    override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.textViewPurchase -> {
                when {
                    ed_credit_qunty.text.toString().trim()
                        .isEmpty() || ed_credit_qunty.text.toString().trim() == "0" -> {
                        Toast.makeText(
                            this@PurchaseCreditsActivity,
                            "Please enter the credits",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        openPaymentOptions()
                        // getConvertCurrency()
                    }
                }
            }

            R.id.textViewCancel -> {
                setResult(2, intent)
                finish()
            }

            R.id.textView16 -> {
                val value = textViewRemainingCredits.text.toString()
                if (value == "0") {
                    Toast.makeText(
                        this@PurchaseCreditsActivity,
                        "Please purchase the credits",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (isProposal) {
                        createSessionToken(
                            intent.getStringExtra("id") ?: "",
                            intent.getStringExtra("image") ?: "",
                            intent.getStringExtra("firstName") ?: "",
                            intent.getStringExtra("lastName") ?: ""
                        )
                    } else {
                        val intent = Intent()
                        setResult(1, intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun getPayment() {
        val payment = PayPalPayment(
            BigDecimal(inUSD.toString()),
            "USD",
            "Credits Fee",
            PayPalPayment.PAYMENT_INTENT_SALE
        )
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                val confirm =
                    data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        val paymentDetails = confirm.toJSONObject().toString(4)
                        Log.e("paymentExample", paymentDetails)

                        addCreditsApi()

                    } catch (e: JSONException) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("paymentExample", "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(
                    "paymentExample",
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
            }
        }

    }

    private fun addCreditsApi() {
        Utils.instance.showProgressBar(this)
        val add_credit = WebService().apiService.add_credit(
            "Bearer " + sharedPreference.getString("access_token"),
            "application/json",
            ed_credit_qunty.text.toString(),
            sharedPreference.getInt("id"),
            "1"
        )

        add_credit.enqueue(object : retrofit2.Callback<AddCreditPointsPojo> {

            override fun onFailure(call: Call<AddCreditPointsPojo>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                getRetrofitError(t, this@PurchaseCreditsActivity)
                Toast.makeText(
                    this@PurchaseCreditsActivity,
                    "Some thing went wrong",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("AddCreditPointsOnError", t.message.toString())
            }

            override fun onResponse(
                call: Call<AddCreditPointsPojo>,
                response: Response<AddCreditPointsPojo>
            ) {
                Utils.instance.dismissProgressDialog()
                ed_credit_qunty.setText("")
                if (response.code() == 401) {
                    openLogoutPopup(this@PurchaseCreditsActivity)
                } else {
                    if (response.isSuccessful) {
                        textViewRemainingCredits.text = response.body()?.points.toString()
                        sharedPreference.putInt("points", response.body()!!.points)

                        Toast.makeText(
                            this@PurchaseCreditsActivity,
                            "Credits Added",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }
        })
    }

    private fun getUserProfile() {
        Utils.instance.dismissProgressDialog()
        Utils.instance.showProgressBar(this)
        val add_credit = WebService().apiService.getUserProfileResponse(
            "Bearer " + sharedPreference.getString("access_token")
        )

        add_credit.enqueue(object : retrofit2.Callback<GetProfileResponse> {

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(
                    this@PurchaseCreditsActivity,
                    "Some thing went wrong",
                    Toast.LENGTH_LONG
                ).show()
                // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(
                call: Call<GetProfileResponse>,
                response: Response<GetProfileResponse>
            ) {
                Utils.instance.dismissProgressDialog()

                if (response.code() == 401) {
                    openLogoutPopup(this@PurchaseCreditsActivity)
                }


                if (response.isSuccessful) {
                    points = response.body()?.user?.points!!
                    textViewRemainingCredits.text = points.toString();
                    sharedPreference.putInt("points", response.body()!!.user!!.points!!)
                }
            }
        })
    }

    private fun getSetting() {
        Utils.instance.dismissProgressDialog()
        Utils.instance.showProgressBar(this)
        val add_credit = WebService().apiService.getSettings(
            "Bearer " + sharedPreference.getString("access_token")
        )

        add_credit.enqueue(object : retrofit2.Callback<GetSettingResponse> {

            override fun onFailure(call: Call<GetSettingResponse>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                getRetrofitError(t, this@PurchaseCreditsActivity)
                Toast.makeText(
                    this@PurchaseCreditsActivity,
                    "Some thing went wrong",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("AddCreditPointsOnError", t.message.toString())
            }

            override fun onResponse(
                call: Call<GetSettingResponse>,
                response: Response<GetSettingResponse>
            ) {
                Utils.instance.dismissProgressDialog()

                if (response.code() == 401) {
                    openLogoutPopup(this@PurchaseCreditsActivity)
                }

                if (response.isSuccessful) {
                    if (response.body()!!.data.size > 4) {
                        textView3.text = response.body()!!.data[4].settingValue
                        consultationFee = response.body()!!.data[0].settingValue.toDoubleOrNull()!!
                    }

                    textViewSinglePrice.text = "P $consultationFee"
//                      textViewConsultationSubtype.setText("P$consultationFee for one consultation with a time limit of 30 mins.")
                }
            }
        })
    }

    private fun getConvertCurrency() {
        Utils.hideKeyboard(this@PurchaseCreditsActivity)
        Utils.instance.showProgressBar(this)
        val call =
            WebService().apiService.getCurrancyConvertor("https://free.currconv.com/api/v7/convert?q=USD_PHP&compact=ultra&apiKey=6f8e6d5a85d94b57b93f")
        call.enqueue(object : Callback<CurrancyConvertorPojo> {
            override fun onFailure(call: Call<CurrancyConvertorPojo>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(
                    this@PurchaseCreditsActivity,
                    "Some thing went wrong",
                    Toast.LENGTH_LONG
                ).show()
                // Log.e("CurrancyConvertor", t.message)
                usd_return = false
            }

            override fun onResponse(
                call: Call<CurrancyConvertorPojo>,
                response: Response<CurrancyConvertorPojo>
            ) {
                Utils.instance.dismissProgressDialog()
                if (response.code() == 401) {
                    openLogoutPopup(this@PurchaseCreditsActivity)
                }
                if (response.isSuccessful) {
                    inUSD = (credit_number_price / response.body()!!.uSDPHP).toDouble()
                    inUSD = Math.round(inUSD * 10.0) / 10.0
                    Log.e("inusd_check", inUSD.toString())
                    getPayment()
                    usd_return = true
                } else {
                    inUSD = (credit_number_price / 51.5)
                    inUSD = Math.round(inUSD * 10.0) / 10.0
                    Log.e("inusd_check", inUSD.toString())
                    getPayment()
                    usd_return = true
                }
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun openPaymentOptions() {
        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(R.color.colorBlackTrans)
        val inflater =
            this.baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.choos_payment_option, null)
        dialog.setContentView(v)
        dialog.create()
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.DialogTheme
        dialog.show()
        val btnPaypal = v.findViewById<View>(R.id.btnPaypal) as Button
        val btnIpay = v.findViewById<View>(R.id.btnIpay) as Button
        val btnCancel = v.findViewById<View>(R.id.btnCancel) as Button

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnPaypal.setOnClickListener {
            dialog.dismiss()
            getConvertCurrency()
        }
        btnIpay.setOnClickListener {
            paymonogPaymentOption();
            dialog.dismiss()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun paymonogPaymentOption() {
        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(R.color.colorBlackTrans)
        val inflater =
            this.baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.choos_paymongo_option, null)
        val btnDebitCard = v.findViewById<Button>(R.id.btnDebitCard)
        val btnGCash = v.findViewById<Button>(R.id.btnGCash)
        val btnGrabPay = v.findViewById<Button>(R.id.btnGrabPay)

        btnDebitCard.setOnClickListener {
            dialog.dismiss()

            startActivity(
                Intent(
                    this@PurchaseCreditsActivity,
                    CreditCardActivity::class.java
                ).putExtra("amount", credit_number_price)
                    .putExtra("credits", ed_credit_qunty.text.toString()).putExtra("isCredit", true)
            )
            ed_credit_qunty.setText("")

        }

        btnGCash.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this@PurchaseCreditsActivity,
                    GCashActivity::class.java
                ).putExtra("amount", credit_number_price)
                    .putExtra("credits", ed_credit_qunty.text.toString()).putExtra("type", "gcash")
                    .putExtra("isCredit", true).putExtra("isCredit", true)
            )
            ed_credit_qunty.setText("")
        }


        btnGrabPay.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this@PurchaseCreditsActivity,
                    GCashActivity::class.java
                ).putExtra("amount", credit_number_price)
                    .putExtra("credits", ed_credit_qunty.text.toString())
                    .putExtra("type", "grab_pay").putExtra("isCredit", true)
                    .putExtra("isCredit", true)
            )
            ed_credit_qunty.setText("")
        }

        dialog.setContentView(v)
        dialog.create()
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    /** * This function assumes logger is an instance of AppEventsLogger and has been * created using AppEventsLogger.newLogger() call.  */
    fun logSubscriptionFinishEvent(
        currency: String?,
        plan: String?,
        status: Boolean,
        valToSum: Double
    ) {
        val params = Bundle()
        params.putString("currency", currency)
        params.putString("plan", plan)
        params.putBoolean("status", status)
        logger?.logEvent("subscription_Finish", 1.0, params)
    }

    fun createSessionToken(
        id: String,
        profileImage: String?,
        name: String,
        lastName: String
    ) {
        val uuid = UUID.randomUUID().toString()
        Utils.instance.showProgressBar(this)
        AndroidNetworking.post(Utils.BASE_URL + Utils.CREATE_SESSION_TOKEN)
            .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .addBodyParameter("reciver_id", id)
            .addBodyParameter("reciver_phn", sharedPreference.getString("phone"))
            .addBodyParameter("reciver_image", sharedPreference.getString("profile_image"))
            .addBodyParameter(
                "reciver_name",
                sharedPreference.getString("name") + " " + sharedPreference.getString("last_name")
            )
            .addBodyParameter("uuid", uuid)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                CreateSessionResponse::class.java,
                object : ParsedRequestListener<CreateSessionResponse> {
                    override fun onResponse(sessionResponse: CreateSessionResponse) {
                        Utils.instance.dismissProgressDialog()

                        if (sessionResponse.success) {
                            val callScreen =
                                Intent(this@PurchaseCreditsActivity, VideoCallActivity::class.java)
                            callScreen.putExtra("id", id)
                            //callScreen.putExtra("lastname", lastname)
                            callScreen.putExtra("image", profileImage)
                            callScreen.putExtra("name", "$name $lastName")
                            callScreen.putExtra("sessionId", sessionResponse.sessionId)
                            callScreen.putExtra("token", sessionResponse.token)
                            callScreen.putExtra("isProposal", true)
                            callScreen.putExtra("callType", "outGoing")
                            callScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(callScreen)
                            finish()
                        } else {
                            if (sessionResponse.message.equals("user is offline")) {
                                showToast(
                                    "The lawyer you are trying to contact is currently offline. They will respond once they log on. If urgent, you may try one of the online lawyers.",
                                    this@PurchaseCreditsActivity
                                )
                            }
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Log.e("Errroror", ">>>>>>>>>>>>>" + anError.errorBody.toString())
                        getAnError(this@PurchaseCreditsActivity, anError)
                        Toast.makeText(
                            this@PurchaseCreditsActivity,
                            "Unable to connect server ",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                })
    }

    private fun showToast(s: String, context: Context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
}




