package lax.lega.rv.com.legalax.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
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
import kotlinx.android.synthetic.main.forgot_pwd2_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.ResetPwdPojo

class ForgotPassword2Activity : Activity() {
    lateinit var btn_submit: Button
    lateinit var sharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_pwd2_activity)
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
            if (!edt_four_digit.text.toString().equals(sharedPreference.getInt("fourDigitCode").toString())) {
                Toast.makeText(this@ForgotPassword2Activity, "Four Digit number not matched", Toast.LENGTH_LONG).show()
            } else if (edt_newpwd.text.length < 6) {
                edt_newpwd.setError("Password cannot be less then six character")
                edt_newpwd.requestFocus()
            } else if ((edt_newpwd.text.length == 0) || (!edt_newpwd.text.toString().equals(edt_newcofrmpwd.text.toString()))) {
                edt_newcofrmpwd.setError("Confirm Password not matched")
                edt_newcofrmpwd.requestFocus()
            } else {
                val connectionDetector = ConnectionDetector(this@ForgotPassword2Activity)
                connectionDetector.isConnectingToInternet
                if (connectionDetector.isConnectingToInternet === false) run {
                    Toast.makeText(this@ForgotPassword2Activity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
                    ResetPWDApi(edt_four_digit.text.toString(), edt_newpwd.text.toString(), edt_newcofrmpwd.text.toString())
                }  /*  if (!connectionDetector.isConnectingToInternet()) {
                    val alert = android.support.v7.app.AlertDialog.Builder(this@ForgotPassword2Activity)

                    alert.setTitle("Internet connection unavailable.")
                    alert.setMessage("You must be connected to an internet connection via WI-FI or Mobile Connection.")
                    alert.setPositiveButton("OK") { dialog, whichButton -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }

                    alert.show()
                } else {
                }*/
            }
            //                Intent intent=new Intent(ForgotPassword2Activity.this,AwsomeActivity.class);
            //                startActivity(intent);
        }
    }

    private fun initt() {
        btn_submit = findViewById<View>(R.id.btn_submit) as Button
        sharedPreference = MySharedPreference(this@ForgotPassword2Activity)
        connectionDetector = ConnectionDetector(this@ForgotPassword2Activity)

    }


    fun ResetPWDApi(four_digit_code: String,
                    new_password: String,
                    confirm_password: String) {
         Utils.instance.showProgressBar(this@ForgotPassword2Activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.RESET_PWD)
                .addBodyParameter("four_digit_code", four_digit_code)
                .addBodyParameter("new_password", new_password)
                .addBodyParameter("confirm_password", confirm_password)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ResetPwdPojo::class.java, object : ParsedRequestListener<ResetPwdPojo> {
                    override fun onResponse(user: ResetPwdPojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            Toast.makeText(this@ForgotPassword2Activity, user.response, Toast.LENGTH_LONG).show()
                            val intent = Intent(this@ForgotPassword2Activity, LoginActivity::class.java)
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ForgotPassword2Activity, "Unable to reset password", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        Toast.makeText(this@ForgotPassword2Activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

}
