package lax.lega.rv.com.legalax.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Base64
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
import com.androidnetworking.interfaces.ParsedRequestListener
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.hbb20.CountryCodePicker
import com.sinch.android.rtc.SinchError
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.login_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.calling.BaseActivity
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.CheckFBPojo
import lax.lega.rv.com.legalax.pojos.FbLoginPojo
import lax.lega.rv.com.legalax.pojos.LoginPojo
import lax.lega.rv.com.legalax.utils.getDialog
import lax.lega.rv.com.legalax.utils.logRegistrationEvent
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Pattern


class LoginActivity : BaseActivity(), SinchService.StartFailedListener {

    lateinit var btn_login: TextView
    lateinit var txt_signup: TextView
    lateinit var txtx_forgot: TextView
    lateinit var sharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector
    lateinit var callbackManager: CallbackManager
    lateinit var profile: Profile
    lateinit var loginButton: LoginButton
    var accessToken: AccessToken? = null
    var id: String? = null
    var userImage_pic: String? = null
    var name: String? = null
    var email: String? = null
    var gender: String? = null
    var birthday: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var textLocation: String? = null
    var logger: AppEventsLogger? = null
    var dialog: AlertDialog? = null

    val permission = arrayOf(Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            // Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    lateinit var builder: AlertDialog.Builder

    var requestCodeSetting = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    FacebookSdk.sdkInitialize(this@LoginActivity)
        setContentView(R.layout.login_activity)
        callbackManager = CallbackManager.Factory.create()

        CheckFacebookstatus()
        GenrateKeyHas()
        init()
        HideKeyboard(parent_layout)
        //setFacebookListener()
        click()

    }

    private fun GenrateKeyHas() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.e("MyKetyHassss", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("Exception", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("Exception", "printHashKey()", e)
        }
    }

    private fun HideKeyboard(parent_layout: LinearLayout) {
        parent_layout.setOnClickListener {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
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


    private fun CheckFacebookstatus() {
//        profile = Profile.getCurrentProfile()
//        if (profile != null) {
//            Toast.makeText(applicationContext, "Loggedin", Toast.LENGTH_SHORT).show()
//            LoginManager.getInstance().logOut()
//            GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
//        } else {
//            // user has not logged in
//        }

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
        if (checkPermission(permission) > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permission,
                    1001)
        }

        btn_login.setOnClickListener {
            if (!isValidEmail(edt_email_login.text.toString())) {
                Toast.makeText(this@LoginActivity, "Please enter email", Toast.LENGTH_LONG).show()
            } else if (edt_pwd_login.text.isEmpty() || edt_pwd_login.text.length < 6) {
                Toast.makeText(this@LoginActivity, "Wrong password", Toast.LENGTH_LONG).show()
            } else {
                val connectionDetector = ConnectionDetector(this@LoginActivity)
                connectionDetector.isConnectingToInternet
                if (connectionDetector.isConnectingToInternet == false) run {
                    Toast.makeText(this@LoginActivity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
//                    if (!passwordMatcher(edt_pwd_login)) {
//                        edt_pwd_login.setError("Password must contain one uppercase , digit and special character")
//                        edt_pwd_login.requestFocus()
//                    } else {
                    LoginAPI(edt_email_login.text.toString(), edt_pwd_login.text.toString(), Utils.LOGIN)
                    //  }
                }
            }
        }

        txtx_forgot.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        txt_signup.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
            //   }
        }

        btn_fb.setOnClickListener {
            Log.e("Clicked", ">>>>>>" + "clickeddddddddddddddddddddddddddddddddddddddddd")
            //            Log.e("Accesstoken", ">>>>>>>>>>>>>>>>>" + sharedPreference.getString("access_token"))
            if (sharedPreference.getString("access_token").equals("")) {
                /*try {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
                        Log.e("InsideClicked", ">>>>>>" + "clickeddddddddddddddddddddddddddddddddddddddddd")

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                loginButton.performClick()
//            } else {
                Log.e("INISDELOGINBUTTON", ">>>>>>" + "clickeddddddddddddddddddddddddddddddddddddddddd")*/
                setFacebookListener()
//
            }
            Log.e("Outsidecalled", ">>>>>>" + "clickeddddddddddddddddddddddddddddddddddddddddd")

        }


    }

    private fun setFacebookListener() {

        val loginmanager = LoginManager.getInstance()
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null) {
            loginmanager.logOut()
        }
        loginmanager.logInWithReadPermissions(
                this,
                listOf("public_profile", "email")
        )
//        val permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile", "AccessToken")
        //   loginmanager.setReadPermissions(Arrays.asList("email,public_profile"));

        loginmanager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                println("onSuccess")
//                Log.e("LOginresult",">>>>>>>>>>>>>"+)


                val accessToken = loginResult.accessToken.token
                Log.e("accessToken", accessToken)

                val request = GraphRequest.newMeRequest(loginResult.accessToken)
                { `object`, response ->
                    Log.e("LoginActivity", response.toString())
                    id = `object`.optString("id")
                    Log.e("id", ">>>>>>>>>>>>$id")
                    try {
                        val profile_pic = URL("https://graph.facebook.com/" + "" + "/picture?width=150&height=150")
                        //   img_value = new URL("https://graph.facebook.com/" + id + "/picture?type=large");

                        Log.i("profile_pic", profile_pic.toString() + "")
                        userImage_pic = profile_pic.toString()

                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    }
                    name = `object`.optString("name")?:""
                    email = `object`.optString("email") ?:""
                    gender = `object`.optString("gender")?:""
                    birthday = `object`.optString("birthday")?:""
                    firstname = `object`.optString("first_name")?:""
                    lastname = `object`.optString("last_name")?:""
                    if (id?.length != 0) {
                        val connectionDetector = ConnectionDetector(this@LoginActivity)
                        connectionDetector.isConnectingToInternet
                        if (connectionDetector.isConnectingToInternet === false) run {
                            Toast.makeText(this@LoginActivity, "No Internet Connection", Toast.LENGTH_LONG).show()
                        } else {
                            isRegistered(`object`.optString("id"), `object`.optString("last_name"), `object`.optString("email"))
                        }
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday,first_name,last_name")
                request.parameters = parameters
                request.executeAsync()

                //  loginPresenter.hitFbAPi();
            }

            override fun onCancel() {
                try {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onError(exception: FacebookException) {
                println("onError")
                try {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //   Log.v("LoginActivity", exception.getCause().toString());
            }
        })
    }

    fun isRegistered(facebook_id: String, last_name: String, email: String) {
        Log.e("Sending Id", ">>>>>>>>>>>>>>>>>>>>>$facebook_id")
        Utils.instance.showProgressBar(this@LoginActivity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.FB_ISREGESTERD)
                .addBodyParameter("facebook_id", facebook_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(CheckFBPojo::class.java, object : ParsedRequestListener<CheckFBPojo> {
                    override fun onResponse(user: CheckFBPojo) {
                        Utils.instance.dismissProgressDialog()
                        Log.e("Responsee", ">>>>>>>>>>" + user.response);
                        if (user.response == true) {
                            FBLoginAPI(facebook_id)
                        } else {

                            DialogShow(last_name, facebook_id, email)
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Toast.makeText(this@LoginActivity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun init() {
        // loginButton = findViewById(R.id.login_button) as LoginButton
        logger = AppEventsLogger.newLogger(this)
        btn_login = findViewById<View>(R.id.btn_login) as TextView
        txt_signup = findViewById<View>(R.id.txt_signup) as TextView
        txtx_forgot = findViewById<View>(R.id.txtx_forgot) as TextView
        sharedPreference = MySharedPreference(this@LoginActivity)
        connectionDetector = ConnectionDetector(this@LoginActivity)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeSetting) {
            ActivityCompat.requestPermissions(
                    this,
                    permission,
                    1001)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun LoginAPI(email: String, password: String, endPointLogin: String) {
        Utils.instance.showProgressBar(this@LoginActivity)

        Log.e("login url is", Utils.BASE_URL + endPointLogin)
        AndroidNetworking.post(Utils.BASE_URL + endPointLogin)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(LoginPojo::class.java, object : ParsedRequestListener<LoginPojo> {
                    override fun onResponse(user: LoginPojo) {
                        Utils.instance.dismissProgressDialog()
                        Log.e("Response", ">>>>>>>>>>>" + user.message)
                        Log.e("Status", ">>>>>>>>>>>" + user.success)
                        if (user.success.equals(true)) {
                            Log.e("AccessToken", ">>>>>>>>>>>>>>" + user.accessToken)
                            sharedPreference.putString("access_token", user.accessToken)
                            sharedPreference.putString("write_something", user.user.writeSomething)
                            sharedPreference.putInt("id", user.user.id)
                            sharedPreference.putString("name", user.user.name.toString())
                            sharedPreference.putString("last_name", user.user.lastName.toString())

                            if (user.user.age != null && !user.user.age.equals("")) {
                                sharedPreference.putString("age", user.user.age)
                            } else {
                                sharedPreference.putString("age", "0")
                            }

                            if (user.user.location != null)
                                sharedPreference.putString("location", user.user.location.toString())
                            else
                                sharedPreference.putString("location", "")
                            if (user.user.phone == null) {
                                sharedPreference.putString("phone", "")
                            } else {
                                sharedPreference.putString("phone", user.user.phone.toString())
                            }
                            sharedPreference.putString("role", user.user.role.toString())
                            //  sharedPreference.putString("role", "2")
                            sharedPreference.putString("email", user.user.email.toString())
                            if (user.user.fcmToken == null && user.user.fcmToken != "") {
                                sharedPreference.putString("sp_fcm", "")
                            } else {
                                sharedPreference.putString("sp_fcm", user.user.fcmToken.toString())
                            }
                            if (user.user.profileImage != null && user.user.profileImage != "") {
                                sharedPreference.putString("profile_image", user.user.profileImage.toString())
                            }
                            sharedPreference.putInt("status", user.user.status)
                            sharedPreference.putInt("points", user.user.points)
                            sharedPreference.putString("videoCallStatus", user.user.video_call_status.toString())

                            loginClicked()
                        } else {

                            if (user.message == "You can not login. Because you are already loggedin with another device.") {

                                openForcefullyLofinPopup(this@LoginActivity)

                            }
                            Toast.makeText(this@LoginActivity, user.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Log.e("Exception", ">>>>>>>>>>>>>>>>>>>>>" + anError.errorBody);
                        Toast.makeText(this@LoginActivity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }


    fun FBSignupAPI(name: String, last_name: String, email: String, facebook_id: String, role: String, age: String, location: String, phone: String, mDialogView: Dialog) {

        Utils.instance.showProgressBar(this@LoginActivity)
        Log.e("MainValue", ">>>>>>>>>>>>>>>>" + phone);
        AndroidNetworking.post(Utils.BASE_URL + Utils.FB_LOGIN)
                .addBodyParameter("name", name)
                .addBodyParameter("last_name", last_name)
                .addBodyParameter("email", email)
                .addBodyParameter("facebook_id", facebook_id)
                .addBodyParameter("role", role)
                .addBodyParameter("age", age)
                .addBodyParameter("location", location)
                .addBodyParameter("phone", "")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(FbLoginPojo::class.java, object : ParsedRequestListener<FbLoginPojo> {
                    override fun onResponse(user: FbLoginPojo) {
                        Utils.instance.dismissProgressDialog()
                        mDialogView.dismiss()
                        if (user.success == true) {
                            Log.e("LoginResponse", ">>>>>>>>>>>>" + user)
                            sharedPreference.putString("access_token", user.accessToken)
                            sharedPreference.putInt("id", user.user.id)
                            sharedPreference.putString("name", user.user.name.toString())
                            sharedPreference.putString("last_name", user.user.lastName.toString())
//                            sharedPreference.putInt("age", user.user.age)
                            if (user.user.age != null && !user.user.age.equals("")) {
                                sharedPreference.putString("age", user.user.age!!)

                            } else {
                                sharedPreference.putString("age", "0")
                            }
                            if (user.user.location != null)
                                sharedPreference.putString("location", user.user.location.toString())
                            else
                                sharedPreference.putString("location", "")
                            if (user.user.phone == null) {
                                sharedPreference.putString("phone", "")
                            } else {
                                sharedPreference.putString("phone", user.user.phone.toString())
                            }
                            sharedPreference.putString("role", user.user.role.toString())
                            sharedPreference.putString("email", user.user.email.toString())
                            sharedPreference.putInt("status", user.user.status)
                            Log.e("SharedStatus", ">>>>>>>>>>" + sharedPreference.getInt("status"))
                            Toast.makeText(this@LoginActivity, "User has been Login successfully.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("f", "")

                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                            startActivity(intent)
                            finish()
                        } else {
                            try {
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    LoginManager.getInstance().logOut()
                                    GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()

                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            Toast.makeText(this@LoginActivity, "User has been Login not successfully.", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        mDialogView.dismiss()
                        try {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        Toast.makeText(this@LoginActivity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


    fun FBLoginAPI(facebook_id: String) {
        Utils.instance.showProgressBar(this@LoginActivity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.FB_LOGIN)
                .addBodyParameter("facebook_id", facebook_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(FbLoginPojo::class.java, object : ParsedRequestListener<FbLoginPojo> {
                    override fun onResponse(user: FbLoginPojo) {
                        Utils.instance.dismissProgressDialog()
                        if (user.success == true) {
                            sharedPreference.putString("access_token", user.accessToken)
                            sharedPreference.putInt("id", user.user.id)
                            sharedPreference.putString("name", user.user.name.toString())
                            sharedPreference.putString("last_name", user.user.lastName.toString())
//                            sharedPreference.putInt("age", user.user.age)
                            if (user.user.age != null && !user.user.age.equals("")) {
                                sharedPreference.putString("age", user.user.age)
                            } else {
                                sharedPreference.putString("age", "0")
                            }
                            if (user.user.location != null)
                                sharedPreference.putString("location", user.user.location.toString())
                            else
                                sharedPreference.putString("location", "")
                            if (user.user.phone == null) {
                                sharedPreference.putString("phone", "")
                            } else {
                                sharedPreference.putString("phone", user.user.phone.toString())
                            }
                            sharedPreference.putString("role", user.user.role.toString())
                            sharedPreference.putString("email", user.user.email.toString())
                            sharedPreference.putInt("status", user.user.status)

                            logRegistrationEvent(logger!!, sharedPreference.getString("name"), sharedPreference.getString("last_name"), sharedPreference.getString("email"), sharedPreference.getString("age"), sharedPreference.getString("location"), sharedPreference.getString("phone"), sharedPreference.getString("role"), "", "facebook")

                            Toast.makeText(this@LoginActivity, "User has been Login successfully.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("f", "")
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "User has been Login not successfully.", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Toast.makeText(this@LoginActivity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }


    fun DialogShow(last_name: String, fb_id: String, email: String) {
        val break_list = arrayOf("Select Role", "Regular User", "Lawyer")
        var role: Int? = null
//        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
//        val mBuilder = AlertDialog.Builder(this)
//                .setView(mDialogView)
//        val mAlertDialog = mBuilder.show()

        val mDialogView = getDialog(this, R.layout.custom_dialog, 0)


        val break_adapter = ArrayAdapter(this, R.layout.spinner_text, break_list)
        break_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mDialogView.spn_role?.adapter = break_adapter

        lateinit var ccp: CountryCodePicker
        ccp = mDialogView.findViewById<View>(R.id.ccp) as CountryCodePicker
        Log.e("emailll", ">>>>>>>>>>>>>>>>" + email);
        Log.e("FAcebookid", ">>>>>>>>>>>>>" + fb_id);
        if (this.email != null) {
            mDialogView.edt_email.setText(this.email)
            mDialogView.edt_email.isClickable = false;
        }
        mDialogView.edt_number_sign_dialog.movementMethod = ScrollingMovementMethod();


        mDialogView.edt_age_custom.setOnClickListener {
            var selectedAge = ""

            val c: Calendar = Calendar.getInstance()
            val cyear = c.get(Calendar.YEAR)
            val cmonth = c.get(Calendar.MONTH)
            val cday = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                    this@LoginActivity,
                    android.app.AlertDialog.THEME_HOLO_DARK,
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
                            mDialogView.edt_age_custom.setText(selectedAge)
                        }
                    },
                    cyear, cmonth, cday)
            datePickerDialog.datePicker.findViewById<NumberPicker>(resources.getIdentifier("day", "id", "android")).setVisibility(View.GONE)
            datePickerDialog.show()
        }

        mDialogView.iv_back.setOnClickListener {
            try {
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mDialogView.dismiss()

        }

        mDialogView.spn_role.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == 0) {
                    role = 0
                } else {
                    if (position == 1) {
                        role = 4

                    } else if (position == 2) {
                        role = 2

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}

        }

        mDialogView.tvTermsCondition.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java).putExtra("url", "https://www.legalex.ph/terms-and-conditions/"))
        }

        mDialogView.tvPrivacy.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java).putExtra("url", "https://www.legalex.ph/privacy-policy/"))
        }

        mDialogView.btn_continue_dialog.setOnClickListener {
            textLocation = mDialogView.edt_loc_cus.text.toString()
            /*  if (email != null) {
                  mDialogView.edt_email.setText(email)
                  mDialogView.edt_email.isEnabled = false
              }
              if (mDialogView.edt_name.text.toString().length == 0) {
                  Toast.makeText(this@LoginActivity, "Please enter first name", Toast.LENGTH_LONG).show()
              } else if (mDialogView.edt_lastname.text.toString().length == 0) {
                  Toast.makeText(this@LoginActivity, "Please enter last name", Toast.LENGTH_LONG).show()
              } else if (!isValidEmail(mDialogView.edt_email.text.toString())) {
                  Toast.makeText(this@LoginActivity, "Please enter email", Toast.LENGTH_LONG).show()

              } else if (mDialogView.edt_age_custom.text.toString().length == 0) {
                  Toast.makeText(this@LoginActivity, "Please choose age", Toast.LENGTH_LONG).show()

              } else if (mDialogView.edt_loc_cus.text.toString().length == 0) {
                  Toast.makeText(this@LoginActivity, "Please enter location", Toast.LENGTH_LONG).show()

              } else*/

            if (role == 0) {
                Toast.makeText(this@LoginActivity, "Please select any role", Toast.LENGTH_LONG).show()

            } else if (!mDialogView.checkBox.isChecked) {
                Toast.makeText(this@LoginActivity, "Please click on Checkbox to agree", Toast.LENGTH_LONG).show()
            } else {

//                val firstname = mDialogView.edt_name.text.toString()
//                val lastname = mDialogView.edt_lastname.text.toString()
//                val email = mDialogView.edt_email.text.toString()
                val password = "1234566"
                val code = ""
                val phonenumber = mDialogView.edt_number_sign_dialog.text.toString()
                val rolee = role.toString()
                val age = mDialogView.edt_age_custom.text.toString()
                val loc = mDialogView.edt_loc_cus.text.toString()
                val fbid = fb_id

                CheckRegister(firstname ?: "", lastname
                        ?: "", email, password, code, phonenumber, rolee, age, loc, fbid, mDialogView)


//                if (mDialogView.edt_loc_cus.text.isNotEmpty()) {
//                    val p = Pattern.compile("([0-9])")
//                    val m = p.matcher(textLocation)
//
//                    if (m.find()) {
//                        Toast.makeText(this@LoginActivity, "Please enter correct location", Toast.LENGTH_LONG).show()
//                        edt_location_sign.setError("Please enter correct location")
//                        edt_location_sign.requestFocus()
//                    } else {
//                        var countrycode = "+" + ccp.selectedCountryCode
//                        if (countrycode == "00") {
//                            Toast.makeText(this@LoginActivity, "Please select any country code", Toast.LENGTH_LONG).show()
//                        } else {
//                            val firstname = mDialogView.edt_name.text.toString()
//                            val lastname = mDialogView.edt_lastname.text.toString()
//                            val email = mDialogView.edt_email.text.toString()
//                            val password = "1234566"
//                            val code = countrycode
//                            val phonenumber = mDialogView.edt_number_sign_dialog.text.toString()
//                            val rolee = role.toString()
//                            val age = mDialogView.edt_age_custom.text.toString()
//                            val loc = mDialogView.edt_loc_cus.text.toString()
//                            val fbid = fb_id
//
//                            CheckRegister(firstname, lastname, email, password, code, phonenumber, rolee, age, loc, fbid, mDialogView)
//
//
//                        }
//                    }
//                }
            }
        }

    }

    private fun CheckRegister(firstname: String, lastname: String, email: String, password: String, code: String, phonenumber: String, rolee: String, age: String, loc: String, fbid: String, mDialogView: Dialog) {


        var mainvalue = if (phonenumber == "") {
            ""
        } else {
            code + "-" + phonenumber
        }

        //   var mainvalue = code + "-" + phonenumber

        Log.e("MainValue", ">>>>>>>>>>>>>>>>" + mainvalue)

        Utils.instance.showProgressBar(this@LoginActivity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.CHECK_REGISTERED)
                .addBodyParameter("name", firstname)
                .addBodyParameter("last_name", lastname)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("facebook_id", fbid)
                .addBodyParameter("age", age)
                .addBodyParameter("location", loc)
                .addBodyParameter("phone", mainvalue)
                .addBodyParameter("role", rolee)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {

                    override fun onResponse(response: JSONObject) {
                        Utils.instance.dismissProgressDialog()

                        Log.e("Signupresponse", ">>>>>>>>>>>>>>>>>>$response")
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
                                FBSignupAPI(firstname, lastname, email, fbid, rolee, age, loc, code + "-" + phonenumber, mDialogView)
                            } else {
//                                if (response.optJSONObject("message").length() == 2)
//                                {
//
//                                }
//                                else {
                                if (response.optJSONObject("message").toString().contains("email")) {
                                    Toast.makeText(this@LoginActivity, "The email has already been taken.", Toast.LENGTH_LONG).show()

                                } else {
                                    Toast.makeText(this@LoginActivity, "The phone has already been taken.", Toast.LENGTH_LONG).show()

                                }
//                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, "Unable to connect server", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(error: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Toast.makeText(this@LoginActivity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


    override fun onBackPressed() {
        try {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServiceConnected() {
        getSinchServiceInterface().setStartListener(this)
    }

    override fun onStartFailed(error: SinchError) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onStarted() {
        openPlaceCallActivity()
    }

    private fun loginClicked() {
        if (checkPermission(permission) > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permission,
                    1001
            )
        } else {
            if (MySharedPreference(this@LoginActivity).getInt("id").toString() != getSinchServiceInterface().userName) {
                getSinchServiceInterface().stopClient()
            }

            if (!getSinchServiceInterface().isStarted) {
                getSinchServiceInterface().startClient(MySharedPreference(this@LoginActivity).getInt("id").toString())
            } else {
                openPlaceCallActivity()
            }
        }

    }

    private fun openPlaceCallActivity() {
        Toast.makeText(this@LoginActivity, "User has been Login successfully.", Toast.LENGTH_LONG).show()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("f", "")
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        startActivity(intent)
        finish()

    }


    fun openForcefullyLofinPopup(activity: Activity) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage("You cannot log in because you are already logged in with another device. Click Use Here to use this ID here");
        alertDialogBuilder.setPositiveButton("Use here") { _, _ ->
            LoginAPI(edt_email_login.text.toString(), edt_pwd_login.text.toString(), Utils.LOGIN_HERE)
        }
        alertDialogBuilder.setNegativeButton("Cancel") { _, _ ->
        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.resources.getColor(R.color.colorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.resources.getColor(R.color.colorPrimary));
        }

        alertDialog.show()
    }

    fun checkPermission(permission: Array<String>): Int {
        var permissionNeeded = 0
        if (Build.VERSION.SDK_INT >= 23) {
            for (i in permission.indices) {
                val result = ContextCompat.checkSelfPermission(this!!, permission[i])
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded++
                }
            }
        }
        return permissionNeeded
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {

            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED || grantResults[3] == PackageManager.PERMISSION_DENIED || grantResults[4] == PackageManager.PERMISSION_DENIED) {
                showSettingsDialog();
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    //  val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                    val showRationale2 = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                    val showRationale3 = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    val showRationale4 = shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                    val showRationale5 = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    val showRationale6 = shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)
                    if (/*!showRationale ||*/ !showRationale2 || !showRationale3 || !showRationale4 || !showRationale5 || showRationale6) {
                        showSettingsDialog();
                    }


                }
            }
        }
    }

    fun showSettingsDialog() {
        builder = AlertDialog.Builder(this@LoginActivity, R.style.Dialog)
        builder.setTitle("Need Permissions")
        builder.setCancelable(false)
        builder.setMessage("This app needs permission to use its features. You can grant them in app settings. If already given all permission then click cancel.")
        builder.setPositiveButton("GOTO SETTINGS") { dialogInterface, i -> openSettings() }
        builder.setNegativeButton("Cancel") { dialogInterface, i ->

            ActivityCompat.requestPermissions(
                    this,
                    permission,
                    1001)

        }
        builder.show()
    }

    fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, requestCodeSetting)
    }

    fun dialogForTerms(firstname: String, lastname: String, email: String, password: String, code: String, phonenumber: String, rolee: String, age: String, loc: String, fbid: String, mDialogView: Dialog) {
        val dialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.popup_camera_gallery, null)
        val textViewTerms = view.findViewById<TextView>(R.id.textViewTerms)
        val textViewPrivacyPolicy = view.findViewById<TextView>(R.id.textViewPrivacyPolicy)
        val textViewAgree = view.findViewById<TextView>(R.id.textViewAgree)
        val mTextViewCancel = view.findViewById<TextView>(R.id.textViewCancel)
        textViewTerms.setOnClickListener {
            startActivity(Intent(view.context, WebViewActivity::class.java).putExtra("url", "https://www.legalex.ph/terms-and-conditions/"))
        }
        textViewPrivacyPolicy.setOnClickListener {
            startActivity(Intent(view.context, WebViewActivity::class.java).putExtra("url", "https://www.legalex.ph/privacy-policy/"))
        }
        mTextViewCancel.setOnClickListener {
            dialog?.dismiss()
        }
        textViewAgree.setOnClickListener {
            dialog?.dismiss()
            CheckRegister(firstname ?: "", lastname
                    ?: "", email, password, code, phonenumber, rolee, age, loc, fbid, mDialogView)
        }
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}

