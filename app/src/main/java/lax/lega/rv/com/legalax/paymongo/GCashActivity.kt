package lax.lega.rv.com.legalax.paymongo

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_credit_card.textView5
import kotlinx.android.synthetic.main.activity_credit_card.textViewCancel
import kotlinx.android.synthetic.main.activity_g_cash.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.AcceptBidFragment
import lax.lega.rv.com.legalax.pojos.mognoPay.createSource.CreateSourceResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.createSourceDetail.*
import lax.lega.rv.com.legalax.pojos.mognoPay.gCashPaymentResponse.GCashPaymentResponse
import lax.lega.rv.com.legalax.retrofit.PayMongoService
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import retrofit2.Call
import retrofit2.Response

class GCashActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var sharedPreference: MySharedPreference
    var price = 0.0
    var credits = "0"
    var type = ""
    var isCredit = true

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_g_cash)
        textViewMakePayment.setOnClickListener(this)
        textViewCancel.setOnClickListener { finish() }
        sharedPreference = MySharedPreference(this)
        isCredit = intent.getBooleanExtra("isCredit", false)
        val data = intent.data
        if (data != null) {
            editTextName.setText(sharedPreference.getString(MySharedPreference.PAYMENT_NAME))
            editTextEmail.setText(sharedPreference.getString(MySharedPreference.PAYMENT_EMAIL))
            editTextPhoneNumber.setText(sharedPreference.getString(MySharedPreference.PAYMENT_PHONE))
            textView5.text = sharedPreference.getString(MySharedPreference.PAYMENT_AMOUNT)
            price = sharedPreference.getString(MySharedPreference.PAYMENT_AMOUNT).toDouble()
            credits = sharedPreference.getString(MySharedPreference.PAYMENT_CREDITS)
            type = sharedPreference.getString(MySharedPreference.PAYMENT_TYPE)


            if (data.toString() == "legalexsuccess://") {
                if (isCredit) {
                    makePaymentApi(isCredit)
                } else {
                    AcceptBidFragment.isPaymentSuccess = true
                    makePaymentApi(isCredit)
                    // Toast.makeText(this, "Payment success", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Payment Declined", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            price = intent.getDoubleExtra("amount", 0.0)
            textView5.text = "Amount: $price"
            credits = intent.getStringExtra("credits") ?: ""

            type = intent.getStringExtra("type") ?: ""
            if (type == "gcash") {
                textViewPaymentType.text = "GCash"
            } else {
                textViewPaymentType.text = "Grab Pay"
            }
        }
    }


    private fun createSource() {
        Utils.hideKeyboard(this)
        val detail = CreateSourceDetailToServer()
        val data = Data()
        val attributes = Attributes()
        attributes.amount = price.toLong() * 100

        val billing = Billing()
        billing.name = editTextName.text.toString()
        billing.email = editTextEmail.text.toString()
        billing.phone = editTextPhoneNumber.text.toString()


        val redirect = Redirect()
        redirect.failed = Utils.PAYMONGO_FAIL_URL
        redirect.success = Utils.PAYMONGO_SUCCESS_URL

        attributes.billing = billing
        attributes.redirect = redirect
        attributes.currency = "PHP"
        attributes.type = type

        data.attributes = attributes
        detail.data = data


        val encodedString = Utils.PAYMONGO_PUBLIC_KEY.encode()

        Utils.instance.showProgressBar(this)
        val updateSetting = PayMongoService().apiService.createSource("Basic $encodedString", detail)
        updateSetting.enqueue(object : retrofit2.Callback<CreateSourceResponse> {
            override fun onFailure(call: Call<CreateSourceResponse>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Log.e("AddCreditPointsOnError", t.message.toString())
            }

            override fun onResponse(call: Call<CreateSourceResponse>, response: Response<CreateSourceResponse>) {
//                Log.i("AddCreditPointsOn", "${Gson().toJson(response.errorBody())}")
                if (response.code() == 200) {
                    Utils.instance.dismissProgressDialog()
                    sharedPreference.putString(MySharedPreference.PAYMENT_EMAIL, editTextEmail.text.toString())
                    sharedPreference.putString(MySharedPreference.PAYMENT_NAME, editTextName.text.toString())
                    sharedPreference.putString(MySharedPreference.PAYMENT_PHONE, editTextPhoneNumber.text.toString())
                    sharedPreference.putString(MySharedPreference.PAYMENT_AMOUNT, price.toString())
                    sharedPreference.putString(MySharedPreference.PAYMENT_CREDITS, credits)
                    sharedPreference.putString(MySharedPreference.PAYMENT_SOURCE_ID, response.body()!!.data.id)
                    sharedPreference.putString(MySharedPreference.PAYMENT_TYPE, textViewPaymentType.text.toString())
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(response.body()!!.data.attributes.redirect.checkoutUrl))
                    startActivity(browserIntent)
//                    startActivity(Intent(this@GCashActivity,WebViewActivity::class.java).putExtra("url",response.body()!!.data.attributes.redirect.checkoutUrl))
                } else {
                    Utils.instance.dismissProgressDialog()
                    Toast.makeText(this@GCashActivity, "Please enter valid email and phone number.", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.textViewMakePayment -> {
                when {
                    editTextName.text.isEmpty() -> {
                        Toast.makeText(this@GCashActivity, "Please enter the name", Toast.LENGTH_LONG).show()
                    }
                    editTextEmail.text.isEmpty() -> {
                        Toast.makeText(this@GCashActivity, "Please enter the email", Toast.LENGTH_LONG).show()
                    }
                    editTextPhoneNumber.text.isEmpty() -> {
                        Toast.makeText(this@GCashActivity, "Please enter the phone number", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setMessage("Please do not kill or exit app and make sure to click on the \"Process Payment\" button at the end to complete payment");
                        alertDialogBuilder.setPositiveButton("ok"
                        ) { _, _ ->
                            createSource()
                        }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.setOnShowListener { alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.resources.getColor(R.color.colorPrimary)); }
                        alertDialog.show()
                    }
                }
            }
        }
    }

    fun String.encode(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
    }


    private fun makePaymentApi(isCredit: Boolean) {
        try {
            Utils.instance.showProgressBar(this)
            var isCre = if (isCredit) "1" else "0"

            val addCredit = WebService().apiService.gcashPayment(
                    "Bearer " + sharedPreference.getString("access_token"),
                    "application/json",
                    price.toLong() * 100,
                    sharedPreference.getString(MySharedPreference.PAYMENT_SOURCE_ID),
                    credits, isCre
            )

            addCredit.enqueue(object : retrofit2.Callback<GCashPaymentResponse> {
                override fun onFailure(call: Call<GCashPaymentResponse>, t: Throwable) {
                    Utils.instance.dismissProgressDialog()
                    AcceptBidFragment.isPaymentSuccess = false
                    Toast.makeText(this@GCashActivity, "FAILURE:  ${t.message}", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<GCashPaymentResponse>, response: Response<GCashPaymentResponse>) {
                    Utils.instance.dismissProgressDialog()
                    if (response.code() == 401) {
                        openLogoutPopup(this@GCashActivity)
                    } else {
                        if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                            onBackPressed()
                        } else {
                            AcceptBidFragment.isPaymentSuccess = false
                            Toast.makeText(this@GCashActivity, "Payment failed any money debited will be refunded in 5-7 working days.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        } catch (e: Exception) {
            AcceptBidFragment.isPaymentSuccess = false
            Toast.makeText(this@GCashActivity, "CATCH: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}