package lax.lega.rv.com.legalax.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.paypal.android.sdk.payments.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_accept_bid.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.paymongo.CreditCardActivity
import lax.lega.rv.com.legalax.paymongo.GCashActivity
import lax.lega.rv.com.legalax.pojos.BidResponse
import lax.lega.rv.com.legalax.pojos.CurrancyConvertorPojo
import lax.lega.rv.com.legalax.pojos.GetFeeResponse
import lax.lega.rv.com.legalax.pojos.ProposalList
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import kotlin.math.roundToInt

class AcceptBidFragment : Fragment(), View.OnClickListener {

    private var proposal: ProposalList? = null
    lateinit var sharedPreference: MySharedPreference
    var creditNumberPrice = 0.0
    var inUSD: Double = 0.0
    private var config = PayPalConfiguration()
    var usdReturn: Boolean = false
    private val PAYPAL_REQUEST_CODE = 123

    companion object {
        var isPaymentSuccess = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_accept_bid, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
        setData()
        getFeeApi()
    }

    private fun init() {
        isPaymentSuccess = false
        proposal = ProposalFragment.proposal
        sharedPreference = MySharedPreference(activity)
    }

    private fun listeners() {
        ivBack.setOnClickListener(this)
        tvAccept.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        //tvCategory.visibility=View.GONE
        tvName.text = proposal?.lawyerInfo?.name
        tvTitle.text = proposal?.info?.title
        tvDescription.text = proposal?.notes
        tvAmount.text = "Amount : "
        tvAmountCount.text = "P " + proposal?.bid.toString()
        if (proposal?.bid != null && proposal?.bid == 0.0)
            tvAccept.text = requireActivity().resources.getString(R.string.accept)
        tvLocation.text = proposal?.lawyerInfo?.location.toString()
        tvCatName.text = proposal?.info?.catInfo?.name

        //tvDownload.text="P"+ doubleToStringNoDecimal(proposal?.bid.toString().toDouble())
        //dialogCreditCardEntry.tvCategory.text="Category : "+proposalList.info?.catInfo?.name
        tvDateSubmitted.text = proposal?.createdAt.toString()
//                getDate(proposal?.createdAt.toString())
        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + proposal?.lawyerInfo?.profileImage.toString()).fit().placeholder(R.drawable.icn_user_large).into(ivUser)

    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                requireActivity().onBackPressed()
            }
            R.id.tvAccept -> {
                if (tvAccept.text.toString().equals(requireActivity().resources.getString(R.string.accept)))
                    acceptBidApi(proposal?.id.toString())
                else
                    openPaymentOptions()
            }
        }
    }

    // Accept bid api
    private fun acceptBidApi(bidId: String) {
        if (activity != null)
            Utils.instance.showProgressBar(requireActivity())
        AndroidNetworking.post(Utils.BASE_URL + Utils.ACCEPT_BID)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("bid_id", bidId)
                //.addBodyParameter("bid_amount", amount)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(BidResponse::class.java, object : ParsedRequestListener<BidResponse> {
                    override fun onResponse(user: BidResponse) {

                        Utils.instance.dismissProgressDialog()
                        if (user.success == true) {
                            if (activity != null) {
                                // Toast.makeText(requireActivity(), user.message, Toast.LENGTH_LONG).show()
                                requireActivity().onBackPressed()
                            }
                        } else {
                            if (activity != null)
                                Toast.makeText(requireActivity(), "Unable to accept bid", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        if (activity != null)
                            Toast.makeText(requireActivity(), "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun openPaymentOptions() {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(R.color.colorBlackTrans)
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.choos_payment_option, null)
        dialog.setContentView(v)
        dialog.create()
        dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.DialogTheme
        dialog.show()
        val btnPaypal = v.findViewById<View>(R.id.btnPaypal) as Button
        val btnIpay = v.findViewById<View>(R.id.btnIpay) as Button
        val btnCancel = v.findViewById<View>(R.id.btnCancel) as Button

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnPaypal.visibility = View.GONE

        btnPaypal.setOnClickListener {
            dialog.dismiss()
            getConvertCurrency()
        }
        btnIpay.setOnClickListener {
            payMongoPaymentOption()
            dialog.dismiss()
        }
    }


    private fun getConvertCurrency() {
        Utils.hideKeyboard(requireActivity())
        Utils.instance.showProgressBar(requireActivity())
        val call = WebService().apiService.getCurrancyConvertor("https://free.currconv.com/api/v7/convert?q=USD_PHP&compact=ultra&apiKey=6f8e6d5a85d94b57b93f")
        call.enqueue(object : Callback<CurrancyConvertorPojo> {
            override fun onFailure(call: Call<CurrancyConvertorPojo>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(requireActivity(), "Some thing went wrong", Toast.LENGTH_LONG).show()
                Log.e("CurrancyConvertor", t.message.toString())
                usdReturn = false
            }

            override fun onResponse(call: Call<CurrancyConvertorPojo>, response: Response<CurrancyConvertorPojo>) {
                Utils.instance.dismissProgressDialog()
                if (response.code() == 401) {
                    openLogoutPopup(requireActivity())
                }
                if (response.isSuccessful) {
                    inUSD = (creditNumberPrice / response.body()!!.uSDPHP)
                    inUSD = (inUSD * 10.0).roundToInt() / 10.0
                    Log.e("inusd_check", inUSD.toString())
                    getPayment()
                    usdReturn = true
                } else {
                    inUSD = (creditNumberPrice / 51.5)
                    inUSD = (inUSD * 10.0).roundToInt() / 10.0
                    Log.e("inusd_check", inUSD.toString())
                    getPayment()
                    usdReturn = true
                }
            }

        })
    }

    private fun getPayment() {
        Log.i("FinalAmount", "${BigDecimal(inUSD.toString())}")
        val payment = PayPalPayment(BigDecimal(inUSD.toString()), "USD", "Credits Fee", PayPalPayment.PAYMENT_INTENT_SALE)
        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        val paymentDetails = confirm.toJSONObject().toString(4)
                        Log.e("paymentExample", paymentDetails)

                        acceptBidApi(proposal?.id.toString())

                    } catch (e: JSONException) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("paymentExample", "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun payMongoPaymentOption() {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(R.color.colorBlackTrans)
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.choos_paymongo_option, null)
        val btnDebitCard = v.findViewById<Button>(R.id.btnDebitCard)
        val btnGCash = v.findViewById<Button>(R.id.btnGCash)
        val btnGrabPay = v.findViewById<Button>(R.id.btnGrabPay)
        val btnCancel = v.findViewById<Button>(R.id.btnCancel)

        btnDebitCard.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(requireContext(), CreditCardActivity::class.java).putExtra("amount", creditNumberPrice).putExtra("credits", "0").putExtra("isCredit", false))
        }

        btnGCash.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(requireContext(), GCashActivity::class.java).putExtra("amount", creditNumberPrice).putExtra("credits", "0").putExtra("type", "gcash").putExtra("isCredit", false))

        }


        btnGrabPay.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(requireContext(), GCashActivity::class.java).putExtra("amount", creditNumberPrice).putExtra("credits", "0").putExtra("type", "grab_pay").putExtra("isCredit", false))

        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(v)
        dialog.create()
        dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    // get Bid List Api
    private fun getFeeApi() {
        //  Utils.showLoader(activity)
        Utils.instance.showProgressBar(requireActivity())
        AndroidNetworking.get(Utils.BASE_URL + Utils.GET_FEE)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetFeeResponse::class.java, object : ParsedRequestListener<GetFeeResponse> {
                    override fun onResponse(getFeeResponse: GetFeeResponse) {
                        //   Utils.hideLoader()

                        if (getFeeResponse.success == true) {
                            Utils.instance.dismissProgressDialog()
                            // Toast.makeText(requireContext(),getFeeResponse.data?.settingValue.toString(),Toast.LENGTH_SHORT).show()
                            calculateAmount(getFeeResponse.data?.settingValue?.toDouble() ?: 0.0)
                        }
                    }

                    override fun onError(anError: ANError) {
                        //   Utils.hideLoader().
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

    @SuppressLint("SetTextI18n")
    private fun calculateAmount(extraPrice: Double) {
        val amount = proposal?.bid

        val percentageAmount = (amount?.times(extraPrice))?.div(100)

        val totalPrice = amount?.plus(percentageAmount ?: 0.0)
        creditNumberPrice = totalPrice ?: 0.0
        tvExtraCharge.text = "Extra Charge :"
        if (amount != 0.0)
            tvExtraChargeAmount.text = "$extraPrice%"
        else
            tvExtraChargeAmount.text = "0.0%"

        tvTotalCharge.text = "Total Amount :"
        tvTotalChargeAmount.text = " P $totalPrice"
    }


    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            if (isPaymentSuccess) {
                if (proposal != null)
                    acceptBidApi(proposal?.id.toString())
            }
        }, 1000)

    }

}