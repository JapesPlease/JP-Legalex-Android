package lax.lega.rv.com.legalax.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.verification_code_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.SignupPojo
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.logRegistrationEvent


class VerificationActivity : Activity() {

    lateinit var btn_submit: Button
    var mAuthforEmail: FirebaseAuth? = null
    lateinit var sharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector
    var email: String = ""
    var password: String = ""
    var logger: AppEventsLogger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_code_activity)
        initt()
        click()

    }

    private fun click() {
        btn_submit.setOnClickListener {
            val mVerificationId: String
            mVerificationId = intent.getStringExtra("mVerificationId").toString()
            // val otp = ed1_veri.text.toString() + ed2.text.toString() + ed3.text.toString() + ed4.text.toString() + ed5.text.toString() + ed6.text.toString()
            val otp = ed1_veri.text.toString()
            if (otp.length != 6) {
                Toast.makeText(this, "OTP is of six digits", Toast.LENGTH_LONG).show()

            } else if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_LONG).show()

            } else {
                val credential = PhoneAuthProvider.getCredential(mVerificationId, otp)
                signInWithPhoneAuthCredential(credential)

             /*   signUp(intent.getStringExtra("name"), intent.getStringExtra("last_name")
                        , intent.getStringExtra("email"), intent.getStringExtra("password")
                        , intent.getStringExtra("age"), intent.getStringExtra("location")
                        , tv_number.text.toString(), intent.getStringExtra("role"))*/
            }

            /*
               if(otp.length<6){
                   Toast.makeText(this@VerificationActivity,"Please enter all digits",Toast.LENGTH_LONG).show()
               }else{
                   val credential = PhoneAuthProvider.getCredential(mVerificationId, otp)
                   signInWithPhoneAuthCredential(credential)
               }
   */

        }

    }

    private fun initt() {
        logger = AppEventsLogger.newLogger(this)
        btn_submit = findViewById<View>(R.id.btn_submit) as Button
        mAuthforEmail = FirebaseAuth.getInstance()
        sharedPreference = MySharedPreference(this@VerificationActivity)
        connectionDetector = ConnectionDetector(this@VerificationActivity)
        var phone_number_edt: String? = null
        email = intent.getStringExtra("email").toString()
        password = intent.getStringExtra("password").toString()
        if (intent.getStringExtra("countrycode") == "+63") {
            if ( intent.getStringExtra("phone") != null && intent.getStringExtra("phone")!!.length == 11) {
                phone_number_edt = intent.getStringExtra("phone")!!.substring(1)
            } else {
                phone_number_edt = intent.getStringExtra("phone")
            }
            tv_number.text = intent.getStringExtra("countrycode") + "-" + phone_number_edt

        } else {
            tv_number.text = intent.getStringExtra("countrycode") + "-" + intent.getStringExtra("phone")


        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuthforEmail?.signInWithCredential(credential)
                ?.addOnCompleteListener(this)
                { task ->
                    if (task.isSuccessful) {
                        //                        val intent = Intent(this@VerificationActivity, MainActivity::class.java)
                        //                        utils.forwardOverridePendingTransitionEnter()
                        //                        startActivity(intent)
                        //                        finish()
                        val connectionDetector = ConnectionDetector(this@VerificationActivity)
                        connectionDetector.isConnectingToInternet
                        if (connectionDetector.isConnectingToInternet === false) run {
                            Toast.makeText(this@VerificationActivity, "No Internet Connection", Toast.LENGTH_LONG).show()
                        } else {
                            if (intent.getStringExtra("role").equals("5")) {
                                val addresses = listOf("hr@legalex.ph").toTypedArray()
                                val subject = "Request to add in Law Firm"
                                val text = "Law Firm Name=${intent.getStringExtra("lawFirmName")}\n\nname=${intent.getStringExtra("name")}\n\nphone=${intent.getStringExtra("phone")}\n\nlast name=${intent.getStringExtra("name")}\n\nemail=${intent.getStringExtra("email")}\n\nage=${intent.getStringExtra("age")}\n\npassword=${intent.getStringExtra("password")}\n\nlocation=${intent.getStringExtra("location")}"
                                latestExampleEmailCreation(addresses, subject, text)
                                finish()
                            } else {
                                signUp(intent.getStringExtra("name").toString(), intent.getStringExtra("last_name").toString()
                                        , intent.getStringExtra("email").toString(), intent.getStringExtra("password").toString()
                                        , intent.getStringExtra("age").toString(), intent.getStringExtra("location").toString()
                                        , tv_number.text.toString(), intent.getStringExtra("role").toString())
                            }
                        }
                        /* if (!connectionDetector.isConnectingToInternet()) {
                                    val alert = android.support.v7.app.AlertDialog.Builder(this@VerificationActivity)

                                    alert.setTitle("Internet connection unavailable.")
                                    alert.setMessage("You must be connected to an internet connection via WI-FI or Mobile Connection.")
                                    alert.setPositiveButton("OK") { dialog, whichButton -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }

                                    alert.show()
                                } else {

                                }*/
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this@VerificationActivity, "Invalid verification code", Toast.LENGTH_SHORT).show()
                        }
                    }
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

    fun signUp(name: String, last_name: String
               , email: String, password: String
               , age: String, location: String
               , phone: String, role: String) {
        Log.e("Phonenumber", ">>>>>>>>>>>>>>>>>" + tv_number.text.toString())
        Utils.instance.showProgressBar(this@VerificationActivity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.SIGNUP)
                .addBodyParameter("name", name)
                .addBodyParameter("last_name", last_name)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("age", age)
                .addBodyParameter("location", location)
                .addBodyParameter("phone", tv_number.text.toString())
                .addBodyParameter("role", role)
                .addBodyParameter("birthday", "ENTER YOUR BIRTH DATE")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(SignupPojo::class.java, object : ParsedRequestListener<SignupPojo> {
                    override fun onResponse(user: SignupPojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success == true) {
                            sharedPreference.putString("access_token", user.accessToken)
                            sharedPreference.putInt("id", user.user.id)
                            sharedPreference.putString("name", user.user.name.toString())
                            sharedPreference.putString("last_name", user.user.lastName.toString())
                            sharedPreference.putString("age", user.user.age)
                            sharedPreference.putString("location", user.user.location.toString())
                            if (user.user.phone == null) {
                                sharedPreference.putString("phone", "")
                            } else {
                                sharedPreference.putString("phone", user.user.phone.toString())
                            }
                            sharedPreference.putString("role", user.user.role.toString())
                            sharedPreference.putString("email", user.user.email.toString())
                            if (user.user.fcm_token == null) {
                                sharedPreference.putString("sp_fcm", "")
                            } else {
                                sharedPreference.putString("sp_fcm", user.user.fcm_token.toString())
                            }
                            if (user.user.profile_image != null) {
                                sharedPreference.putString("profile_image", user.user.profile_image)

                            }
                            sharedPreference.putInt("status", user.user.status)

                            logRegistrationEvent(logger!!,intent.getStringExtra("name").toString(), intent.getStringExtra("last_name").toString()
                                    , intent.getStringExtra("email").toString()
                                    , intent.getStringExtra("age").toString(), intent.getStringExtra("location").toString()
                                    , tv_number.text.toString(), intent.getStringExtra("role").toString(), intent.getStringExtra("countrycode").toString(),"simple")

                            DeleteUserFromFirebase()

                        } else {
                            Toast.makeText(this@VerificationActivity, "User registered not successful.", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(this@VerificationActivity,anError)
                        Toast.makeText(this@VerificationActivity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    private fun DeleteUserFromFirebase() {
         Utils.instance.showProgressBar(this@VerificationActivity)

//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        val authCredential = EmailAuthProvider.getCredential(email,password)
//
//        firebaseUser?.reauthenticate(authCredential)?.addOnCompleteListener {
//            firebaseUser.delete().addOnCompleteListener { task ->
//                if (task.isSuccessful()) {
//                      Utils.instance.dismissProgressDialog()
//
//                    Log.e("UserrrrrrrDeleteed", "User account deleted.")
//                    val intent = Intent(this@VerificationActivity, MainActivity::class.java)
//                    intent.putExtra("f", "")
////                            DeleteAllAccountsFromFirebase()
//                    utils.forwardOverridePendingTransitionEnter()
//                    startActivity(intent)
//                    finish()
//                    Log.e("Deleted", "User account deleted!")
//                } else{
//                      Utils.instance.dismissProgressDialog()
//                    Toast.makeText(this@VerificationActivity,"Failed TO Signup please restart App",Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
        Log.e("Deleting", "ingreso a deleteAccount")
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        Log.e("CurrentUser", ">>>>>>>>>>>>>>>" + currentUser.toString())
        currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Workssssss Fine", "OK! Works fine!")
                  Utils.instance.dismissProgressDialog()

                Log.e("UserrrrrrrDeleteed", "User account deleted.")

                Log.e("Deleted", "User account deleted!")



                val intent = Intent(this@VerificationActivity, MainActivity::class.java)
                intent.putExtra("f", "")
//                            DeleteAllAccountsFromFirebase()
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                startActivity(intent)
                finish()

            } else {
                  Utils.instance.dismissProgressDialog()
                Toast.makeText(this@VerificationActivity, "Failed TO Signup please restart App", Toast.LENGTH_SHORT).show()
//                }
            }
        }

    }

//    private fun DeleteAllAccountsFromFirebase()
//    {
//         val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            //You need to get here the token you saved at logging-in time.
//            val token = "userSavedToken"
//            //You need to get here the password you saved at logging-in time.
//            val password = "userSavedPassword"
//
//            val credential: AuthCredential
//
//            //This means you didn't have the token because user used like Facebook Sign-in method.
//            if (token == null) {
//                credential = EmailAuthProvider.getCredential(user.email!!, password)
//            } else {
//                //Doesn't matter if it was Facebook Sign-in or others. It will always work using GoogleAuthProvider for whatever the provider.
//                credential = GoogleAuthProvider.getCredential(token, null)
//            }
//
//            //We have to reauthenticate user because we don't know how long
//            //it was the sign-in. Calling reauthenticate, will update the
//            //user login and prevent FirebaseException (CREDENTIAL_TOO_OLD_LOGIN_AGAIN) on user.delete()
//            user.reauthenticate(credential)
//                    .addOnCompleteListener(object : OnCompleteListener<Void> {
//                        override fun onComplete(@NonNull task: Task<Void>) {
//                            //Calling delete to remove the user and wait for a result.
//                            user.delete().addOnCompleteListener(object : OnCompleteListener<Void> {
//                                override fun onComplete(@NonNull task: Task<Void>) {
//                                    if (task.isSuccessful()) {
//                                        //Ok, user remove
//                                    } else {
//                                        //Handle the exception
//                                        task.getException()
//                                    }
//                                }
//                            })
//                        }
//                    })
//
//
//        }



    private fun latestExampleEmailCreation(
            addresses: Array<String>, subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }



}
