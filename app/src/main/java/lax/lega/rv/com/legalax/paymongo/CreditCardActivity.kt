package lax.lega.rv.com.legalax.paymongo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_credit_card.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.AcceptBidFragment
import lax.lega.rv.com.legalax.paymongo.adapter.DatePickerAdapter
import lax.lega.rv.com.legalax.pojos.AddCreditPointsPojo
import lax.lega.rv.com.legalax.pojos.mognoPay.cardDetail.Attributes
import lax.lega.rv.com.legalax.pojos.mognoPay.cardDetail.CardDetail
import lax.lega.rv.com.legalax.pojos.mognoPay.cardDetail.Data
import lax.lega.rv.com.legalax.pojos.mognoPay.cardDetail.Details
import lax.lega.rv.com.legalax.pojos.mognoPay.createPaymentMethod.CreatePaymentMethodResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.detailToServer.MakePaymentDetailToServer
import lax.lega.rv.com.legalax.pojos.mognoPay.makePayment.PaymentResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.pyamentIntent.PaymentIntentResponse
import lax.lega.rv.com.legalax.pojos.mognoPay.threedsecure.ThreeDSecurePaymentResponse
import lax.lega.rv.com.legalax.retrofit.PayMongoService
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getRetrofitError
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import retrofit2.Call
import retrofit2.Response

class CreditCardActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerAdapter.ClickHandler {

    var dialog: AlertDialog? = null
    var dialogType = 1 //1->Month 2->year
    private lateinit var datePicker: DatePickerAdapter
    lateinit var sharedPreference: MySharedPreference
    var price = 0.0
    var credits = "0"
    var isCredit = true
    var clientKeyData = ""
    var paymentId = ""
    val month = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
    val year = arrayOf(
        "2020",
        "2021",
        "2022",
        "2023",
        "2024",
        "2025",
        "2026",
        "2027",
        "2028",
        "2029",
        "2030",
        "2031",
        "2032",
        "2033",
        "2034",
        "2035",
        "2036",
        "2037",
        "2038",
        "2039",
        "2040"
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)

        textViewMonthName.setOnClickListener(this)
        textViewYearName.setOnClickListener(this)
        textViewMakePayment.setOnClickListener(this)

        textViewCancel.setOnClickListener { finish() }

        sharedPreference = MySharedPreference(this)

        price = intent.getDoubleExtra("amount", 0.0)
        credits = intent.getStringExtra("credits") ?: ""
        isCredit = intent.getBooleanExtra("isCredit", true)
        textView5.text = "Amount: $price"

    }

    override fun onResume() {
        super.onResume()
        if (clientKeyData.isNotEmpty()) {
            threedSecureConfimationApi()
        }
    }

    private fun dialogForDatePicker(title: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.year_month_selector, null)
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
        val textViewCancel = view.findViewById<TextView>(R.id.textViewCancel)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        datePicker = if (title == "Select Month") {
            DatePickerAdapter(month)
        } else {
            DatePickerAdapter(year)
        }

        datePicker.onCLickListener(this)
        textViewCancel.setOnClickListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = datePicker

        textViewTitle.text = title
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.textViewMonthName -> {
                dialogType = 1
                dialogForDatePicker("Select Month")
            }

            R.id.textViewYearName -> {
                dialogType = 2
                dialogForDatePicker("Select Year")
            }

            R.id.textViewCancel -> {
                dialog?.dismiss()
            }

            R.id.textViewMakePayment -> {

                when {
                    editTextCardNumber.text.isEmpty() -> {
                        Toast.makeText(
                            this@CreditCardActivity,
                            "Please enter the card number",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    textViewMonth.text.isEmpty() -> {
                        Toast.makeText(
                            this@CreditCardActivity,
                            "Please choose the month",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    textViewYear.text.isEmpty() -> {
                        Toast.makeText(
                            this@CreditCardActivity,
                            "Please choose the year",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    editTextCvv.text.isEmpty() -> {
                        Toast.makeText(
                            this@CreditCardActivity,
                            "Please enter the cvv",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        createPaymentMethod()
                    }
                }
            }

        }
    }

    override fun oncLick(position: Int) {
        dialog?.dismiss()
        if (dialogType == 1) {
            textViewMonthName.text = month[position]
        } else {
            textViewYearName.text = year[position]
        }
    }


    private fun createPaymentMethod() {
        Utils.hideKeyboard(this)
        val card = CardDetail()
        val data = Data()

        val details = Details()
        val attributeSet = Attributes()

        details.cardNumber = editTextCardNumber.text.toString()
        details.expMonth = textViewMonthName.text.toString().toLong()
        details.expYear = textViewYearName.text.toString().toLong()
        details.cvc = editTextCvv.text.toString()

        attributeSet.details = details
        attributeSet.type = "card"
        data.attributes = attributeSet

        card.data = data

        val encodedString = Utils.PAYMONGO_PUBLIC_KEY.encode()

        //Utils.instance.dismissProgressDialog()
        Utils.instance.showProgressBar(this)
        val updateSetting =
            PayMongoService().apiService.createPaymentMethod("Basic $encodedString", card)
        updateSetting.enqueue(object : retrofit2.Callback<CreatePaymentMethodResponse> {
            override fun onFailure(call: Call<CreatePaymentMethodResponse>, t: Throwable) {
                // Utils.dismissProgressDialog(
                Toast.makeText(this@CreditCardActivity, "Some thing went wrong", Toast.LENGTH_LONG)
                    .show()
                Log.e("AddCreditPointsOnError", t.message.toString())
            }

            override fun onResponse(
                call: Call<CreatePaymentMethodResponse>,
                response: Response<CreatePaymentMethodResponse>
            ) {
                //   Utils.instance.dismissProgressDialog()
                if (response.code() == 200) {
                    createPaymentIntent(response.body()?.data?.id ?: "")
                } else {
                    Utils.instance.dismissProgressDialog()
                    Toast.makeText(
                        this@CreditCardActivity,
                        "invalid card detail",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun createPaymentIntent(paymentMethodId: String) {
        //   Utils.instance.dismissProgressDialog()
        //   Utils.instance.showProgressBar(this)
        val updateSetting = WebService().apiService.createPaymentIntent(
            "Bearer " + sharedPreference.getString("access_token"),
            price.toInt() * 100
        )
        updateSetting.enqueue(object : retrofit2.Callback<PaymentIntentResponse> {
            override fun onFailure(call: Call<PaymentIntentResponse>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(this@CreditCardActivity, "Some thing went wrong", Toast.LENGTH_LONG)
                    .show()
                Log.e("AddCreditPointsOnError", t.message.toString())
            }

            override fun onResponse(
                call: Call<PaymentIntentResponse>,
                response: Response<PaymentIntentResponse>
            ) {

                /*var responseIs=response.body()!!.string().toString()
                  Log.e("api response is",responseIs)*/

                if (response.code() == 200) {
                    if (response.body()?.data?.errors != null && response.body()?.data?.errors?.size ?: 0 > 0) {
                        Utils.instance.dismissProgressDialog()
                        Toast.makeText(
                            this@CreditCardActivity,
                            response.body()!!.data!!.errors[0].detail,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        makePayment(
                            paymentMethodId,
                            response.body()?.data?.data?.id ?: "",
                            response.body()?.data?.data?.attributes?.clientKey ?: ":"
                        )
                    }
                } else {
                    Utils.instance.dismissProgressDialog()
                    Toast.makeText(
                        this@CreditCardActivity,
                        "Some thing went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun makePayment(paymentMethodId: String, paymentIntentID: String, clientKey: String) {

        val detail = MakePaymentDetailToServer()
        val data = lax.lega.rv.com.legalax.pojos.mognoPay.detailToServer.Data()
        val attributes = lax.lega.rv.com.legalax.pojos.mognoPay.detailToServer.Attributes()
        attributes.clientKey = clientKey
        attributes.paymentMethod = paymentMethodId
        data.attributes = attributes
        detail.data = data


        val encodedString = Utils.PAYMONGO_PUBLIC_KEY.encode()

        //   Utils.instance.dismissProgressDialog()
        //   Utils.instance.showProgressBar(this)
        val updateSetting = PayMongoService().apiService.makePayment(
            "Basic $encodedString",
            paymentIntentID,
            detail
        )
        updateSetting.enqueue(object : retrofit2.Callback<PaymentResponse> {
            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(this@CreditCardActivity, "Some thing went wrong", Toast.LENGTH_LONG)
                    .show()
                Log.e("AddCreditPointsOnError", t.message.toString())
            }

            override fun onResponse(
                call: Call<PaymentResponse>,
                response: Response<PaymentResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.data?.attributes?.nextAction == null) {
                        if (isCredit) {
                            addCreditsApi()
                        } else {
                            AcceptBidFragment.isPaymentSuccess = true
                            Utils.instance.dismissProgressDialog()
                            Toast.makeText(
                                this@CreditCardActivity,
                                "payment success",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }

                    } else {
                        clientKeyData = response.body()?.data?.attributes?.clientKey ?: ""
                        paymentId = response.body()?.data?.id ?: ""
                        Utils.instance.dismissProgressDialog()
                        startActivity(
                            Intent(
                                this@CreditCardActivity,
                                ThreeDSecureActivity::class.java
                            ).putExtra(
                                "url",
                                response.body()?.data?.attributes?.nextAction?.redirect?.url
                            )
                        )
                    }
                } else {
                    Toast.makeText(
                        this@CreditCardActivity,
                        "Invalid card details",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun String.encode(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
    }

    private fun addCreditsApi() {
        Utils.instance.showProgressBar(this)
        val addCredit = WebService().apiService.add_credit(
            "Bearer " + sharedPreference.getString("access_token"),
            "application/json",
            credits,
            sharedPreference.getInt("id"),
            "1"
        )
        addCredit.enqueue(object : retrofit2.Callback<AddCreditPointsPojo> {

            override fun onFailure(call: Call<AddCreditPointsPojo>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                getRetrofitError(t, this@CreditCardActivity)
                Toast.makeText(this@CreditCardActivity, "Some thing went wrong", Toast.LENGTH_LONG)
                    .show()
                Log.e("AddCreditPointsOnError", t.message.toString())
            }

            override fun onResponse(
                call: Call<AddCreditPointsPojo>,
                response: Response<AddCreditPointsPojo>
            ) {
                Utils.instance.dismissProgressDialog()
                // ed_credit_qunty.setText("")
                if (response.code() == 401) {
                    openLogoutPopup(this@CreditCardActivity)
                } else {
                    if (response.isSuccessful) {
                        sharedPreference.putInt("points", response.body()?.points ?: 0)
                        Toast.makeText(this@CreditCardActivity, "Credits Added", Toast.LENGTH_LONG)
                            .show()
                        finish()
                    }
                }
            }
        })
    }


    fun threedSecureConfimationApi() {

        Utils.instance.showProgressBar(this)
        val encodedString = Utils.PAYMONGO_PUBLIC_KEY.encode()
        val updateSetting = PayMongoService().apiService.checkPaymentStatus(
            "Basic $encodedString",
            paymentId,
            clientKeyData
        )
        updateSetting.enqueue(object : retrofit2.Callback<ThreeDSecurePaymentResponse> {
            override fun onFailure(call: Call<ThreeDSecurePaymentResponse>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
            }

            override fun onResponse(
                call: Call<ThreeDSecurePaymentResponse>,
                response: Response<ThreeDSecurePaymentResponse>
            ) {
                Utils.instance.dismissProgressDialog()
                when (response.body()?.data?.attributes?.status) {
                    "succeeded" -> {
                        if (isCredit) {
                            addCreditsApi()
                        } else {
                            Toast.makeText(
                                this@CreditCardActivity,
                                "payment success",
                                Toast.LENGTH_LONG
                            ).show()
                            Utils.instance.dismissProgressDialog()
                            AcceptBidFragment.isPaymentSuccess = true
                            finish()

                        }

                    }
                    "awaiting_payment_method" -> {
                        Toast.makeText(this@CreditCardActivity, "Error Occured", Toast.LENGTH_LONG)
                            .show()
                    }
                    "processing" -> {
                        threedSecureConfimationApi()
                    }
                }
            }
        })

    }
}