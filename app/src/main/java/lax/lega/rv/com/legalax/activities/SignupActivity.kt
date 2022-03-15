package lax.lega.rv.com.legalax.activities

import android.app.AlertDialog.THEME_HOLO_DARK
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.popup_camera_gallery.view.*
import kotlinx.android.synthetic.main.signup_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomEdittextHelvic
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class SignupActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var btn_continue: Button
    lateinit var sharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector

    // var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mVerificationId: String? = null
    var textLocation: String? = null
    var mAuthforEmail: FirebaseAuth? = null
    var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    var checkstatus: Boolean = false
    lateinit var spn_break: Spinner
    lateinit var spnAge: Spinner
    lateinit var img_close: ImageView
    lateinit var linearLayooutLawFirm: LinearLayout
    lateinit var edt_law_firm_name: CustomEdittextHelvic
    lateinit var viewLawFirmName: View
    internal var break_list = arrayOf("Select Role", "Regular User", "Lawyer", "Law Firm")
    internal var ageList = arrayOf("Select Age", "18-25", "26-35", "36-50", "51 and above")
    var role: Int? = null
    lateinit var ccp: CountryCodePicker
    private var selectedAge = ""

    var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)
        init()
        click()
        setAdapter()
        //  setAdapterAge()
    }

    fun passwordMatcher(edtText: EditText): Boolean {

        val pattern = Pattern.compile("^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}\$")
        val matcher = pattern.matcher(edtText.text.toString())
        val isMatched = matcher.matches()

        if (isMatched) {
            return true
        }

        if (!isMatched) {

//            edtText.setError("error")
//            edtText.error = "" + string
//            edtText.isFocusable = true
            return false
        }
        return true
    }


    private fun click() {

        textViewAge.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            var cyear = c.get(Calendar.YEAR)
            var cmonth = c.get(Calendar.MONTH)
            var cday = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                    this@SignupActivity,
                    THEME_HOLO_DARK,
                    object : DatePickerDialog.OnDateSetListener {
                        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
                            when (month) {
                                0 -> {
                                    selectedAge = "January ${year}"
                                }
                                1 -> {
                                    selectedAge = "February ${year}"
                                }
                                2 -> {
                                    selectedAge = "March ${year}"
                                }
                                3 -> {
                                    selectedAge = "April ${year}"
                                }
                                4 -> {
                                    selectedAge = "May ${year}"
                                }
                                5 -> {
                                    selectedAge = "June ${year}"
                                }
                                6 -> {
                                    selectedAge = "July ${year}"
                                }
                                7 -> {
                                    selectedAge = "August ${year}"
                                }
                                8 -> {
                                    selectedAge = "September ${year}"
                                }
                                9 -> {
                                    selectedAge = "October ${year}"
                                }
                                10 -> {
                                    selectedAge = "November ${year}"
                                }
                                11 -> {
                                    selectedAge = "December ${year}"
                                }
                                else -> selectedAge = ""
                            }
                            textViewAge.setText(selectedAge)
                        }
                    },
                    cyear, cmonth, cday)
            datePickerDialog.getDatePicker().findViewById<NumberPicker>(resources.getIdentifier("day", "id", "android")).setVisibility(View.GONE)
            datePickerDialog.show()

        }


        img_close.setOnClickListener(View.OnClickListener {
            finish()
        })


        btn_continue.setOnClickListener {
            //   Toast.makeText(this@SignupActivity, "code:: " + ccp.selectedCountryCode, Toast.LENGTH_LONG).show()
            var countrycode = ccp.selectedCountryCodeWithPlus
            textLocation = edt_location_sign.text.toString()

            if (!isValidEmail(edt_email_sign.getText().toString().trim())) {
                edt_email_sign.setError("Please enter the valid email address")
                edt_email_sign.requestFocus()
            } else if (edt_pwd_sign.text.length < 8) {
                edt_pwd_sign.setError("Password cannot be less then eight character")
                edt_pwd_sign.requestFocus()
            } else if (edt_firstname_sign.text.length == 0) {
                edt_firstname_sign.setError("Please enter the First name")
                edt_firstname_sign.requestFocus()
            } else if (edt_lastname_sign.text.length == 0) {
                edt_lastname_sign.setError("Please enter the Last name")
                edt_lastname_sign.requestFocus()
            } else if (selectedAge.isEmpty()) {
                Toast.makeText(this@SignupActivity, "Please select any age", Toast.LENGTH_LONG).show()
            }
            /*else if (edt_age_sign.text.length == 0) {
                edt_age_sign.setError("Please enter the Age")
                edt_age_sign.requestFocus()
            } */ else if (edt_location_sign.text.length == 0) {
                edt_location_sign.setError("Please enter the Location")
                edt_location_sign.requestFocus()
            } else if (countrycode.equals("00")) {
                Toast.makeText(this@SignupActivity, "Please select any country code", Toast.LENGTH_LONG).show()
            } else if (edt_number_sign.getText().toString().length == 0) {
                edt_number_sign.setError("Please enter the valid mobile number")
                edt_number_sign.requestFocus()
            } else if (role == 0) {
                Toast.makeText(this@SignupActivity, "Please select any role", Toast.LENGTH_LONG).show()
            } else {
                val connectionDetector = ConnectionDetector(this@SignupActivity)
                connectionDetector.isConnectingToInternet
                if (connectionDetector.isConnectingToInternet === false) run {
                    Toast.makeText(this@SignupActivity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
                    if (edt_location_sign.text.length != 0) {
                        val p = Pattern.compile("([0-9])")
                        val m = p.matcher(textLocation)

                        if (m.find()) {
                            Toast.makeText(this@SignupActivity, "Please enter correct location", Toast.LENGTH_LONG).show()
                            edt_location_sign.setError("Please enter correct location")
                            edt_location_sign.requestFocus()
                        } else {
                            if (edt_firstname_sign.text.toString().equals(edt_lastname_sign.text.toString())) {
                                edt_firstname_sign.setError("First name and last name cannot be same")
                                edt_firstname_sign.requestFocus()
                            } else {
                                if (!passwordMatcher(edt_pwd_sign)) {
                                    edt_pwd_sign.setError("Password must contain one uppercase , digit and special character")
                                    edt_pwd_sign.requestFocus()
                                } else {
                                    if (role == 5) {
                                        if (edt_law_firm_name.text.trim().toString().length == 0) {
                                            edt_law_firm_name.setError("Please enter the law firm name")
                                        } else {
                                            dialogForTerms()
                                        }
                                    } else {
                                        dialogForTerms()
                                    }

                                    /*  CheckRegisteredAPI(edt_firstname_sign.text.toString(), edt_lastname_sign.text.toString(),
                                              edt_email_sign.text.toString(), edt_pwd_sign.text.toString(), edt_age_sign.text.toString(),
                                              edt_location_sign.text.toString(), edt_number_sign.text.toString(), role.toString(),
                                              countrycode)*/
                                }

                            }
                        }
                    }
                }
            }

        }


        /*spnAge.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //"Select Role", "Admin", "Regular User", "SME User", "Lawyer"
                if (position == 0) {

                } else {
                   selectedAge=ageList[position]
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }*/



        spn_break.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //"Select Role", "Admin", "Regular User", "SME User", "Lawyer"
                if (position == 0) {
                    role = 0
                } else {
                    if (position == 1) {
                        role = 4

                    } else if (position == 2) {
                        role = 2

                    } else if (position == 3) {
                        role = 5
                    }

                    //  role = spn_break.selectedItem.toString()
                }


                if (position == 3) {
                    viewLawFirmName.visibility = View.VISIBLE
                    linearLayooutLawFirm.visibility = View.VISIBLE
                } else {
                    viewLawFirmName.visibility = View.GONE
                    linearLayooutLawFirm.visibility = View.GONE
                }
//1-> admin, 2-> lawyer, 3-> SME User, 4-> regular user
                //("Select Role", "Admin", "Regular User", "SME User", "Lawyer")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }

    var user_id: String? = null

    private fun init() {
        img_close = findViewById(R.id.img_close) as ImageView
        spn_break = findViewById(R.id.spn_break) as Spinner
        //spnAge = findViewById(R.id.spnAge) as Spinner
        linearLayooutLawFirm = findViewById(R.id.linearLayooutLawFirm) as LinearLayout
        edt_law_firm_name = findViewById(R.id.edt_law_firm_name) as CustomEdittextHelvic
        viewLawFirmName = findViewById(R.id.viewLawFirmName) as View
        btn_continue = findViewById<View>(R.id.btn_continue) as Button
        ccp = findViewById<View>(R.id.ccp) as CountryCodePicker
        edt_number_sign.movementMethod = ScrollingMovementMethod()

        sharedPreference = MySharedPreference(this@SignupActivity)
        connectionDetector = ConnectionDetector(this@SignupActivity)
        mAuthforEmail = FirebaseAuth.getInstance()
        /*      val user = mAuthforEmail!!.getCurrentUser()
        user_id = mAuthforEmail!!.getUid()*/
    }


    private fun setAdapter() {
        val break_adapter = ArrayAdapter(this, R.layout.spinner_text, break_list)
        break_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_break.adapter = break_adapter
    }

    private fun setAdapterAge() {
        val ageAdapter = ArrayAdapter(this, R.layout.spinner_text, ageList)
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnAge.adapter = ageAdapter
    }


    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun CheckRegisteredAPI(name: String, last_name: String, email: String, password: String, age: String, location: String, phone: String, role: String, countrycode: String) {
        var mainvalue = countrycode + "-" + phone
        Log.e("MainValue", ">>>>>>>>>>>>>>>>" + mainvalue);

        Utils.instance.showProgressBar(this@SignupActivity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.CHECK_REGISTERED)
                .addBodyParameter("name", name)
                .addBodyParameter("last_name", last_name)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("age", age)
                .addBodyParameter("location", location)
                .addBodyParameter("phone", mainvalue)
                .addBodyParameter("role", role)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {

                    override fun onResponse(response: JSONObject) {
                        Utils.instance.dismissProgressDialog()
                        Log.e("Signupresponse", ">>>>>>>>>>>>>>>>>>" + response);
                        try {
                            Log.e("Responseee", ">>>>>>>>>>>>>>>>>>>>>>" + response.get("success"))

//                            if (response != null && response.length() > 0) {
//                                checkstatus = response.getBoolean("success")
//                                if (checkstatus)
//                                {
//                                    emailVerification(email, password, name, last_name, age, location, phone, role, countrycode)
//
//                                } else {
//                                    if (response.optJSONObject("message").length() == 2) {
//                                        Toast.makeText(this@SignupActivity, "The email and phone has already been taken.", Toast.LENGTH_LONG).show()
//
//                                    } else {
//                                        if (response.optJSONObject("message").toString().contains("email")) {
//                                            Toast.makeText(this@SignupActivity, "The email has already been taken.", Toast.LENGTH_LONG).show()
//
//                                        } else {
//                                            Toast.makeText(this@SignupActivity, "The phone has already been taken.", Toast.LENGTH_LONG).show()
//
//                                        }
//                                    }
//
//                                }
//
//
//                            } else {
//                                Toast.makeText(applicationContext, "Something went wrong while connecting to server", Toast.LENGTH_LONG).show()
//                            }
                            if (response.optBoolean("success")) {
                                mobileVerification(name, last_name, email, password, age, location, phone, role, countrycode)

                            } else {
//                                if (response.optJSONObject("message").length() == 2)
//                                {
//
//                                }
//                                else {
                                if (response.optJSONObject("message").toString().contains("email")) {
                                    Toast.makeText(this@SignupActivity, "User with this email or number is already exists.", Toast.LENGTH_LONG).show()

                                } else {
                                    Toast.makeText(this@SignupActivity, "User with this email or number is already exists.", Toast.LENGTH_LONG).show()

                                }
//                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@SignupActivity, "Unable to connect server", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(error: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Toast.makeText(this@SignupActivity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    Log.d("focus", "touchevent")
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    /*  fun signUp(name: String, last_name: String
                 , email: String, password: String
                 , age: String, location: String
                 , phone: String, role: String, countrycode: String) {
          Utils.showLoader(this@SignupActivity)
          AndroidNetworking.post(Utils.BASE_URL + Utils.SIGNUP)
                  .addBodyParameter("name", name)
                  .addBodyParameter("last_name", last_name)
                  .addBodyParameter("email", email)
                  .addBodyParameter("password", password)
                  .addBodyParameter("age", age)
                  .addBodyParameter("location", location)
                  .addBodyParameter("phone", phone)
                  .addBodyParameter("role", role)
                  .setPriority(Priority.HIGH)
                  .build()
                  .getAsObject(SignupPojo::class.java, object : ParsedRequestListener<SignupPojo> {
                      override fun onResponse(user: SignupPojo) {
                          Utils.hideLoader()
                          if (user.success.equals(true)) {
                              sharedPreference.putString("access_token", user.accessToken)
                              sharedPreference.putInt("id", user.user.id)
                              sharedPreference.putString("name", user.user.name.toString())
                              sharedPreference.putString("last_name", user.user.lastName.toString())
                              sharedPreference.putInt("age", user.user.age)
                              sharedPreference.putString("location", user.user.location.toString())
                              sharedPreference.putString("phone", user.user.phone.toString())
                              sharedPreference.putString("role", user.user.role.toString())
                              sharedPreference.putString("email", user.user.email.toString())
                              emailVerification(email, password, name, last_name, email, password, age, location, phone, role, countrycode)
                              //Toast.makeText(this@SignupActivity, "User has been registered successfully.", Toast.LENGTH_LONG).show()

                          } else {
                              Toast.makeText(this@SignupActivity, "User has been registered not successfully.", Toast.LENGTH_LONG).show()

                          }
                      }

                      override fun onError(anError: ANError) {
                          Utils.hideLoader()
                          Toast.makeText(this@SignupActivity, "Unable to connect server", Toast.LENGTH_LONG).show()

                      }
                  })
      }
  */
    fun emailVerification(email: String, password: String, name: String, last_name: String, age: String, location: String, phone: String, role: String, countrycode: String) {
        mAuthforEmail!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult>
        { task ->
            if (task.isSuccessful) {
                mobileVerification(name, last_name, email, password, age, location, phone, role, countrycode)
            } else {
                if (task.exception!!.toString().contains("The email address is already in use by another account")) {
                    edt_email_sign.setError("The email address is already in use by another account.")
                    edt_email_sign.requestFocus()

                }
            }
        })
    }

    private fun mobileVerification(name: String, last_name: String, email: String, password: String, age: String, location: String, phone: String, role: String, countrycode: String) {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.e("Firebase exception", ">>>>>>>>>>>>>>>>>>>" + e.toString());
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.e("Exception", ">>>>>>>>>>>>" + e.toString());
                    Toast.makeText(this@SignupActivity, "Phone number provided is incorrect", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
                mVerificationId = verificationId
                mResendToken = token
                // PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                val intent = Intent(this@SignupActivity, VerificationActivity::class.java)
                intent.putExtra("mVerificationId", mVerificationId)
                intent.putExtra("name", name)
                intent.putExtra("last_name", last_name)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                intent.putExtra("age", age)
                intent.putExtra("location", location)
                intent.putExtra("phone", phone)
                intent.putExtra("role", role)
                intent.putExtra("countrycode", countrycode)
                intent.putExtra("lawFirmName", edt_law_firm_name.text.toString())

                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent)
                finish()
            }
        }
        var phone_number_edt: String? = null
        if (countrycode == "+63") {
            phone_number_edt = if (edt_number_sign.text.toString().trim { it <= ' ' }.length == 11) {
                edt_number_sign.text.toString().trim { it <= ' ' }.substring(1)
            } else {
                edt_number_sign.text.toString().trim { it <= ' ' }
            }
            val phoneNumber = countrycode + phone_number_edt
            //val phoneNumber = "+63" + "9173194744"
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks)
        } else {
            val phoneNumber = countrycode + edt_number_sign.text.toString().trim({ it <= ' ' })
            //val phoneNumber = "+63" + "9173194744"
//            val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
//            try {
//                val swissNumberProto: PhoneNumber = phoneUtil.parse(swissNumberStr, "CH")
//            } catch (e: NumberParseException) {
//                System.err.println("NumberParseException was thrown: " + e.toString())
//            }
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks)
        }
        /* val phoneNumber = countrycode + edt_number_sign.text.toString().trim({ it <= ' ' })
         //val phoneNumber = "+63" + "9173194744"
         PhoneAuthProvider.getInstance().verifyPhoneNumber(
                 phoneNumber,
                 60,String
                 TimeUnit.SECONDS,
                 this,
                 mCallbacks)*/
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuthforEmail!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "signInWithCredential:success")
                        val user = task.result.user
                        val otp = credential.smsCode
                        Toast.makeText(this@SignupActivity, "OTP$otp", Toast.LENGTH_LONG).show()

                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {

                        }
                    }
                }
    }

    var checkBoxAgreeClicked = false
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.textViewTerms -> {
                startActivity(Intent(view.context, WebViewActivity::class.java).putExtra("url", "https://www.legalex.ph/terms-and-conditions/"))
            }

            R.id.textViewPrivacyPolicy -> {
                startActivity(Intent(view.context, WebViewActivity::class.java).putExtra("url", "https://www.legalex.ph/privacy-policy/"))
            }

            R.id.textViewAgree -> {
                if (checkBoxAgreeClicked) {
                    val countrycode = ccp.selectedCountryCodeWithPlus
                    CheckRegisteredAPI(edt_firstname_sign.text.toString(), edt_lastname_sign.text.toString(),
                            //edt_email_sign.text.toString(), edt_pwd_sign.text.toString(), edt_age_sign.text.toString(),
                            edt_email_sign.text.toString(), edt_pwd_sign.text.toString(), selectedAge,
                            edt_location_sign.text.toString(), edt_number_sign.text.toString(), role.toString(),
                            countrycode)
                    dialog!!.dismiss()
                } else {
                    var alertDialog: AlertDialog? = null
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Please click on the checkbox to agree");
                    alertDialogBuilder.setPositiveButton("Ok") { _, _ ->
                        alertDialog?.dismiss()
                    }
                    alertDialog = alertDialogBuilder.create()
                    alertDialog.setOnShowListener {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimary));
                    }
                    alertDialog.show()
                }
            }
            R.id.textViewCancel -> {
                dialog?.dismiss()
            }
        }
    }


    fun dialogForTerms() {
        val dialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.popup_camera_gallery, null)
        val textViewTerms = view.findViewById<TextView>(R.id.textViewTerms)
        val textViewPrivacyPolicy = view.findViewById<TextView>(R.id.textViewPrivacyPolicy)
        val textViewAgree = view.findViewById<TextView>(R.id.textViewAgree)
        val mTextViewCancel = view.findViewById<TextView>(R.id.textViewCancel)
        checkBoxAgreeClicked = false
        textViewTerms.setOnClickListener(this)
        textViewPrivacyPolicy.setOnClickListener(this)
        mTextViewCancel.setOnClickListener(this)
        textViewAgree.setOnClickListener(this)
        view.checkBoxAgree.setOnCheckedChangeListener { buttonView, isChecked ->
            checkBoxAgreeClicked = isChecked
        }
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


}
