package lax.lega.rv.com.legalax.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.ipay.IPayIH
import com.ipay.IPayIHPayment
import kotlinx.android.synthetic.main.activity_detail_form.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.ipay.*
import lax.lega.rv.com.legalax.pojos.AddCreditPointsPojo
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import retrofit2.Call
import retrofit2.Response

class DetailFormActivity : AppCompatActivity(), View.OnClickListener {


    // new credential
    private val merchantKey = "b6qCfGAFMh"
    private val merchantCode = "PH00970"
    private val country = "PH"
    private val paymentId = ""
    private val currency = "PHP"
    private val lang = "UTF-8"
    private val backendPostURL ="http://merchant.com/backend.php"

  /*private val merchantKey = "YChMzkNuPv"
    private val merchantCode = "PH00582"*/

    private val refNumber="Z001"
  //  private val backendPostURL ="https://sandbox.ipay88.com.ph/epayment/entry.asp"

 //   private val backendPostURL ="https://payment.ipay88.com.ph/epayment/report/"
    lateinit var sharedPreference: MySharedPreference

    private var quantity:String?=null
    private var id:Long?=null

  /*var merchantKey = "Az2QqzFdKz"
    var merchantCode = "M05133"
    var refID = "Z001"
    var country = "MY"
    private val paymentId = ""
    private val currency = "MYR"
    private val lang = "ISO-8859-1"
    var backendPostURL = "www.google.com"*/


    companion object{
      //  var backendPostURL ="http://merchant.com/backend.php"
        var resultTitle: String?=null
        var resultInfo: String?=null
        var resultExtra: String?=null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_form)
        init()
        listeners()
        setFormData()
    }

    private fun init(){
        sharedPreference = MySharedPreference(this)
        id=sharedPreference.getInt("id")
        quantity=intent.getStringExtra("quantity")
    }

    private fun listeners(){
        tvCancel.setOnClickListener(this)
        btnProceed.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(p0: View?) {
        when(p0?.id){

            R.id.tvCancel->{
                finish()
            }

            R.id.btnProceed->{
                checkValidation()
            }
        }
    }

    private fun callPaymentProcess(){

        //permission granted
        try {
            val payment = IPayIHPayment()

            payment.paymentId = paymentId
            payment.currency = currency
            payment.country = country
            payment.refNo = getCurrentTimeStamp()
            payment.lang = lang
            payment.backendPostURL = backendPostURL
            payment.merchantCode = merchantCode
            payment.merchantKey = merchantKey
            payment.amount = "1.00"
         //   payment.amount = intent.getStringExtra("price")
            payment.prodDesc = etDescription.text.toString().trim()
          //  payment.prodDesc ="jhdfhsjkfks"
           // payment.userName = etUserName.text.toString().trim()
            payment.userEmail = etEmail.text.toString().trim()
            payment.userContact = etMobileNumber.text.toString().trim()
            payment.remark = etRemark.text.toString().trim()


//            payment.setTID(txtTerminalId.getText().toString());
//            payment.setXfield1(txtXField1.getText().toString());
//            payment.setENV(IPayIH.ENV_PRODUCTION);
//            payment.prodDesc="test description"
//            payment.amount = intent.getStringExtra("price")
//            payment.responseURL = backendPostURL

            val selectionIntent = IPayIH.getInstance().checkout(payment
                    , this, ResultDelegate(), IPayIH.PAY_METHOD_CREDIT_CARD)
            startActivityForResult(selectionIntent, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 1 || data == null) {
            return
        }

        val resultPaymentMessage = ResultPaymentMessage()
        if (resultTitle != null) {
            resultPaymentMessage.strResultTitle = resultTitle
            resultTitle = null
        }

        if (resultInfo != null) {
            resultPaymentMessage.strResultInfo = resultInfo
            resultInfo = null
        }

        if (resultExtra != null) {
            resultPaymentMessage.strResultExtra = resultExtra
            resultExtra = null
        }

        Log.e("testfairy-checkpoint", "Payment Response Message")
        val resultPaymentObject = ResultPaymentObject(resultPaymentMessage)
        val savePayHisThread = SavePayHisThread(resultPaymentObject, applicationContext)
        savePayHisThread.start()
        val paymentResultMessageDialog = PaymentResultMessageDialog(this,
                resultPaymentMessage)
        paymentResultMessageDialog.show()
        if (resultTitle=="SUCCESS"){
            addCreditsApi()
        }


    }

    private fun setFormData(){
        etUserName.setText(sharedPreference.getString("name"))
        etEmail.setText(sharedPreference.getString("email"))
        etMobileNumber.setText(sharedPreference.getString("phone"))
    }

    private fun getCurrentTimeStamp(): String {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        return ts
    }

  //  @RequiresApi(Build.VERSION_CODES.M)
    private fun checkValidation(){
        if (!isValidEmail(etEmail.text.toString().trim())) {
            etEmail.error = "Please enter the valid email address"
            etEmail.requestFocus()
        } else if (etDescription.text.isEmpty()) {
            etDescription.error = "Please enter description"
            etDescription.requestFocus()
        }
        else{
            /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), PackageManager.PERMISSION_GRANTED)
            }else{*/
                callPaymentProcess()
           // }
        }
    }
    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


    private fun addCreditsApi() {
          Utils.instance.showProgressBar(this)
        val add_credit = WebService().apiService.add_credit(
                "Bearer " + sharedPreference.getString("access_token"),
                "application/json",
                quantity?:"0",
                sharedPreference.getInt("id"),
                "1"
        )

        add_credit.enqueue(object : retrofit2.Callback<AddCreditPointsPojo> {

            override fun onFailure(call: Call<AddCreditPointsPojo>, t: Throwable) {
                  Utils.instance.dismissProgressDialog()
                Toast.makeText(this@DetailFormActivity, "Some thing went wrong", Toast.LENGTH_LONG).show()
               // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<AddCreditPointsPojo>, response: Response<AddCreditPointsPojo>) {
                  Utils.instance.dismissProgressDialog()
                finish()
                if (response.code()==401){
                    openLogoutPopup(this@DetailFormActivity)
                }
            //    ed_credit_qunty.setText("")
                if (response.isSuccessful) {
                  /*  textViewRemainingCredits.text = response.body()?.points.toString()
                    sharedPreference.putInt("points", response.body()!!.points)
*/
                    Toast.makeText(this@DetailFormActivity, "Credits Added", Toast.LENGTH_LONG).show()

                }
            }
        })
    }
}