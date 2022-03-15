package lax.lega.rv.com.legalax.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.forgot_pwd_activity.*
import kotlinx.android.synthetic.main.login_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.ForgotPwdPojo
import lax.lega.rv.com.legalax.utils.getAnError


class ForgotPasswordActivity : Activity() {
    lateinit var txtx_forgot: TextView
    lateinit var btn_submit: Button
    lateinit var sharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_pwd_activity)
        initt()
        click()
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

    private fun click() {
        btn_submit.setOnClickListener {
            if (!isValidEmail(ed_email.getText().toString().trim())) {
                Toast.makeText(this@ForgotPasswordActivity, "Please enter email", Toast.LENGTH_LONG).show()

            } else {
                val connectionDetector = ConnectionDetector(this@ForgotPasswordActivity)
                connectionDetector.isConnectingToInternet
                if (connectionDetector.isConnectingToInternet === false) run {
                    Toast.makeText(this@ForgotPasswordActivity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
                    ForgotApi(ed_email.text.toString())
                }   /*if (!connectionDetector.isConnectingToInternet()) {
                    val alert = android.support.v7.app.AlertDialog.Builder(this@ForgotPasswordActivity)

                    alert.setTitle("Internet connection unavailable.")
                    alert.setMessage("You must be connected to an internet connection via WI-FI or Mobile Connection.")
                    alert.setPositiveButton("OK") { dialog, whichButton -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }

                    alert.show()
                } else {

                }*/
            }

        }
        txt_resend_code.setOnClickListener(View.OnClickListener {
            if (!isValidEmail(ed_email.getText().toString().trim())) {
                Toast.makeText(this@ForgotPasswordActivity, "Please enter email", Toast.LENGTH_LONG).show()

            } else {
                val connectionDetector = ConnectionDetector(this@ForgotPasswordActivity)
                connectionDetector.isConnectingToInternet
                if (connectionDetector.isConnectingToInternet === false) run {
                    Toast.makeText(this@ForgotPasswordActivity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
                    ForgotApi(edt_email_login.text.toString())
                }  /* if (!connectionDetector.isConnectingToInternet()) {
                    val alert = android.support.v7.app.AlertDialog.Builder(this@ForgotPasswordActivity)

                    alert.setTitle("Internet connection unavailable.")
                    alert.setMessage("You must be connected to an internet connection via WI-FI or Mobile Connection.")
                    alert.setPositiveButton("OK") { dialog, whichButton -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }

                    alert.show()
                } else {

                }*/
            }
        })
    }

    private fun initt() {
        txtx_forgot = findViewById<View>(R.id.txt_resend_code) as TextView
        btn_submit = findViewById<View>(R.id.btn_submit) as Button
        val spannableStringObject = SpannableString("Resend code")
        spannableStringObject.setSpan(UnderlineSpan(), 0, spannableStringObject.length, 0)
        txtx_forgot.text = spannableStringObject
        sharedPreference = MySharedPreference(this@ForgotPasswordActivity)
        connectionDetector = ConnectionDetector(this@ForgotPasswordActivity)

    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun ForgotApi(email: String) {
        Utils.instance.showProgressBar(this)
        AndroidNetworking.post(Utils.BASE_URL + Utils.FORGOT_PWD)
                .addBodyParameter("email", email)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ForgotPwdPojo::class.java, object : ParsedRequestListener<ForgotPwdPojo> {
                    override fun onResponse(user: ForgotPwdPojo) {
                        Utils.instance.dismissProgressDialog()
                        Log.i("ForgotPassResponse", "${Gson().toJson(user)}")
                        if (user.success.equals(true)) {
                            sharedPreference.putInt("fourDigitCode", user.fourDigitCode)
                            Toast.makeText(this@ForgotPasswordActivity, user.message, Toast.LENGTH_LONG).show()
                            ShowAleartDialog()
                        } else {
                            if (user.message != null)
                                Toast.makeText(this@ForgotPasswordActivity, user.message, Toast.LENGTH_LONG).show()
                            else
                                Toast.makeText(this@ForgotPasswordActivity, "Unable to send code", Toast.LENGTH_LONG).show()
                        }//8352
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(this@ForgotPasswordActivity, anError)
                        Toast.makeText(this@ForgotPasswordActivity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    //
    private fun ShowAleartDialog() {
        val builder = AlertDialog.Builder(this@ForgotPasswordActivity, R.style.Dialog)

        // Set the alert dialog title

        // Display a message on alert dialog
        builder.setTitle("!!!!!")
        builder.setMessage("Four digit code send to your email id if not in email inbox please check spam folder too.")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Ok") { dialog, which ->
            // Do something when user press the positive button
            val intent = Intent(this@ForgotPasswordActivity, ForgotPassword2Activity::class.java)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
            dialog.dismiss()
            finish()

            // Change the app background color
        }


        // Display a negative button on alert dialog


        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}


