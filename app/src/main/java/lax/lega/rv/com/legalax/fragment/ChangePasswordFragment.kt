package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomEditextHelvicItalic
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.ChangePwdPojo
import lax.lega.rv.com.legalax.pojos.ResetPwdPojo
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.openLogoutPopup

class ChangePasswordFragment : Fragment() {
    lateinit var v: View
    lateinit var edt_current: CustomEditextHelvicItalic
    lateinit var edt_newpwd: CustomEditextHelvicItalic
    lateinit var edt_newcofrmpwd: CustomEditextHelvicItalic
    lateinit var btn_submit: Button
    lateinit var sharedPreference: MySharedPreference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.change_pwd_fragment, container, false)
        initt()
        click()
        return v
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun click() {
        btn_submit.setOnClickListener(View.OnClickListener {
            if (edt_current.text.length<6) {
                edt_current.setError("Current password cannot be less then six chararcter")
                edt_current.requestFocus()
            } else if (edt_newpwd.text.length < 6) {
                edt_newpwd.setError("Password cannot be less then six character")
                edt_newpwd.requestFocus()
            } else if ((edt_newpwd.text.length == 0) || (!edt_newpwd.text.toString().equals(edt_newcofrmpwd.text.toString()))) {
                edt_newcofrmpwd.setError("Confirm Password not matched")
                edt_newcofrmpwd.requestFocus()
            } else {
                val connectionDetector = ConnectionDetector(activity)
                connectionDetector.isConnectingToInternet
                if (connectionDetector.isConnectingToInternet === false) run {
                    Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
                    ChangePwdApi(edt_current.text.toString(), edt_newpwd.text.toString(), edt_newcofrmpwd.text.toString())
                }
            }
        })


    }

    fun ChangePwdApi(old_password: String,
                     new_password: String,
                     confirm_password: String) {
          Utils.instance.showProgressBar(activity)
        val addPostcall = WebService().apiService
                .change_pwd("Bearer " + sharedPreference.getString("access_token")
                        ,"application/json"
                        , old_password
                        , new_password
                        , confirm_password)
        addPostcall.enqueue(object : retrofit2.Callback<JsonElement>
        {
            override fun onFailure(call: retrofit2.Call<JsonElement>?, t: Throwable?) {
                  Utils.instance.dismissProgressDialog()
                Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

            }

            override fun onResponse(call: retrofit2.Call<JsonElement>?,
                                    response: retrofit2.Response<JsonElement>?) {
                  Utils.instance.dismissProgressDialog()


                if (response?.code()==401){ openLogoutPopup(context as Activity)
                }else{
                    if (response?.body()?.asJsonObject?.get("success")?.asBoolean == true)
                    {
                        Toast.makeText(activity, "Password has been changed successfully.", Toast.LENGTH_LONG).show()
//                    val fragmentManager = activity!!.supportFragmentManager
//
//                    fragmentManager.beginTransaction().replace(R.id.flContent, SettingsFragment()).addToBackStack(null).commit()
//                    activity!!.onBackPressed()
                        activity!!.onBackPressed();


                    } else {
                        Toast.makeText(activity, "Unable to change password", Toast.LENGTH_LONG).show()
                    }
                }

            }


        })
       /* AndroidNetworking
                .post(Utils.BASE_URL + Utils.CHANGE_PWD)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("old_password", old_password)
                .addBodyParameter("new_password", new_password)
                .addBodyParameter("confirm_password", confirm_password)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ResetPwdPojo::class.java,
                        object : ParsedRequestListener<ResetPwdPojo> {
                    override fun onResponse(user: ResetPwdPojo) {
                        Utils.hideLoader()
                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "Password has been changed successfully.", Toast.LENGTH_LONG).show()
                            val fragmentManager = activity!!.supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.flContent, DocumentFragment()).addToBackStack(null).commit()
                        } else {
                            Toast.makeText(activity, "Unable to change password", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.hideLoader()
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })*/
    }


    private fun initt() {
        edt_current = v.findViewById<CustomEditextHelvicItalic>(R.id.edt_current)
        edt_newpwd = v.findViewById<CustomEditextHelvicItalic>(R.id.edt_newpwd)
        edt_newcofrmpwd = v.findViewById<CustomEditextHelvicItalic>(R.id.edt_newcofrmpwd)
        btn_submit = v.findViewById<Button>(R.id.btn_submit)
        sharedPreference = MySharedPreference(activity)

    }

}
