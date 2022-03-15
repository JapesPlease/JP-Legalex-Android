package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.settings_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.LoginActivity
import lax.lega.rv.com.legalax.common.CustomTextviewHelvicNormal
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.utils.getVollyError
import org.json.JSONException
import org.json.JSONObject


class SettingsFragment : Fragment() {


    lateinit var btn_signout: Button
    lateinit var backpress1: ImageView
    lateinit var v: View
    lateinit var ll_reset_pwd: LinearLayout
    lateinit var lr_user_agreement: LinearLayout
    lateinit var lr_contact_us: LinearLayout
    lateinit var add_more: LinearLayout
    lateinit var mySharedPreference: MySharedPreference
    lateinit var lr_update_birthday: LinearLayout
    lateinit var lr_complaint: LinearLayout
    lateinit var tv_date_ofbirth: TextView
    lateinit var textViewVersion: CustomTextviewHelvicNormal
    lateinit var imageViewSetting: ImageView
    var birthday = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.settings_activity, container, false)
        FacebookSdk.sdkInitialize(activity)
        mySharedPreference = MySharedPreference(activity)

        GetData()
        init()
        click()

        return v
    }


    private fun GetData() {
        Utils.instance.showProgressBar(activity)
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, Utils.BASE_URL + Utils.GET_USER_DATA,
                Response.Listener<String>
                { response ->
                    Utils.instance.dismissProgressDialog()

                    Log.e("Response", ">>>>>>>>>>>$response")
                    try {
                        val jsonObject = JSONObject(response)
                        val status = jsonObject.getBoolean("success")
                        if (status) {
                            val mainobject = jsonObject.getJSONObject("user")
//                            phone = mainobject.getString("phone")
//                            lastname = mainobject.getString("last_name")
//                            location = mainobject.getString("location")
//                            email = mainobject.getString("email")
//                            age = mainobject.getString("age")
//                            name = mainobject.getString("name")
//                            aboutus = mainobject.getString("about_us")
//                            information = mainobject.getString("information")
                            birthday = mainobject.getString("birthday")
                            Log.i("ROLE", mainobject.getString("role"))
                            if (mainobject.getString("role") == "2") {
                                add_more.visibility = View.GONE
                                view_add_more.visibility = View.GONE
                            }

                            if (birthday != "null") {
                                tv_date_ofbirth.setText(birthday)
                            } else {
                                tv_date_ofbirth.setText("ADD DATE OF BIRTH")
                            }


                        }

                        //                                JSONObject jsonObject = new JSONObject(response);
                        //                                boolean status = jsonObject.getBoolean("success");
                        //                                if (status) {
                        //                                    JSONObject mainobj = jsonObject.getJSONObject("user");
                        //                                    String mainstatus = mainobj.getString("status");
                        //                                    sharedPreference.putInt("status", Integer.parseInt(mainstatus));
                        //
                        ////                                    RecreateActivity();
                        //
                        //
                        //                                }

                    } catch (e: Exception) {
//                          Utils.instance.dismissProgressDialog()
                        Log.e("Exception is", ">>>>>>>>>" + e.toString())
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(volleyError: VolleyError) {
                        Utils.instance.dismissProgressDialog()
                        getVollyError(context as Activity, volleyError)
                        Log.e("VolleyError", ">>>>>>>>>>>>>>>$volleyError")
                        Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                    }
                }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {

                val parms = java.util.HashMap<String, String>()
                parms["user_id"] = mySharedPreference.getInt("id").toString()
                return parms
            }

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Accept", "application/json")
                headers.put("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                return headers
            }
        }
        queue.add(stringRequest)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }


    private fun click() {


        if (MySharedPreference(activity).getString("role") == "1") {
            imageViewSetting.visibility = View.VISIBLE
        } else {
            imageViewSetting.visibility = View.GONE
        }

        imageViewSetting.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, AdminSettingFragment()).addToBackStack(null).commit()
        }

        btn_signout.setOnClickListener {

            MakeUserOffline()
        }
        add_more.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.legalex.ph/plans/"))
            startActivity(browserIntent)
        }
        backpress1.setOnClickListener {
            //            val intent = Intent(activity, MainActivity::class.java)
//            intent.putExtra("f", "")
//            startActivity(intent)
            activity!!.supportFragmentManager.popBackStack()

        }
        ll_reset_pwd.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, ChangePasswordFragment()).addToBackStack(null).commit()

        })
        lr_user_agreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.legalex.ph/terms-and-conditions/"))
            startActivity(browserIntent)
        }
        lr_contact_us.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.legalex.ph/contact-us"))
            startActivity(browserIntent)
        }
        lr_update_birthday.setOnClickListener {
            val fragmentmanger = activity!!.supportFragmentManager
            fragmentmanger.beginTransaction().replace(R.id.flContent, UpdateBirthdayFragment()).commit()
        }

        lr_complaint.setOnClickListener {
            var intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "hello@legalex.ph", null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Complaint");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello Legalex Team!!")
            intent.setPackage("com.google.android.gm");
            if (intent.resolveActivity(activity!!.getPackageManager()) != null)
                startActivity(intent);
            else
                Toast.makeText(activity, "Gmail App is not installed", Toast.LENGTH_SHORT).show();

        }


    }

    private fun MakeUserOffline() {
        val stringRequest = object : StringRequest(Request.Method.POST, Utils.BASE_URL + Utils.ISONLINE_OFFLINE,
                Response.Listener { response ->
                    Log.e("Response", ">>>>>>>>>>>>>" + response!!)
                    try {
                        if (response != null && response.length > 0) {
                            val jsonObject = JSONObject(response)
                            val success = jsonObject.getBoolean("success")

                            if (success) {
                                //                                    Intent i = new Intent("Refresh");
                                //                                    i.putExtra("refresh", "data");
                                //                                    sendBroadcast(i);
                                //                                    fragmentRefreshListener.onRefresh();

                                LogoutUser()

                            } else {
                            }
                        }

                    } catch (e: Exception) {
                        Toast.makeText(activity, "Failed TO Update Status", Toast.LENGTH_SHORT).show()

                        Log.e("Exception is", ">>>>>>>>>>>>>>" + e.toString())
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error", ">>>>>>>>>>>>>>$error")
//                    Toast.makeText(activity, "Failed TO Update Status", Toast.LENGTH_SHORT).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val parms = java.util.HashMap<String, String>()
                parms["is_online"] = "0"
                return parms
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers = java.util.HashMap<String, String>()
                headers["Authorization"] = "Bearer " + MySharedPreference(activity).getString("access_token")
                headers["Accept"] = "application/json"
                return headers
            }
        }
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)

    }


    private fun LogoutUser() {
        Utils.instance.showProgressBar(activity)
        val mySharedPreference = MySharedPreference(activity)
        Log.e("token", ">>>>>>>>>>>>>>>>>>>>" + mySharedPreference.getString("access_token"))

        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(com.android.volley.Request.Method.GET, Utils.logout,
                Response.Listener<String>
                { response ->
                    Utils.instance.dismissProgressDialog()

                    Log.e("Response", ">>>>>>>>>>>" + response.toString());

                    try {
                        val obj = JSONObject(response)
                        val mainobj = obj.getJSONObject("response")
                        val check = mainobj.getString("success")
                        if (check == "true") {
                            GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
                            val nMgr = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            nMgr.cancelAll()
//            LoginManager.getInstance().logOut()
                            val mySharedPreference = MySharedPreference(activity)
                            mySharedPreference.removeAll()
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity!!.finishAffinity()


                        } else {

                            SHowToast("Failed To Logout User")
                        }
                        Log.e("Response", ">>>>>>>>>>>" + response);
                    } catch (e: JSONException) {
//                          Utils.instance.dismissProgressDialog()

                        Log.e("stacktrace", ">>>>>>>>>>>>>" + e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError ->
                    Utils.instance.dismissProgressDialog()
                    getVollyError(context as Activity, volleyError)
                    Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {

                return params
            }

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Accept", "application/json")
                headers.put("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                return headers
            }

        }
        queue.add(stringRequest)
    }

    private fun SHowToast(s: String) {

        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()

    }

    private fun init() {
        btn_signout = v.findViewById<View>(R.id.btn_signout) as Button
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        ll_reset_pwd = v.findViewById<View>(R.id.ll_reset_pwd) as LinearLayout
        add_more = v.findViewById<View>(R.id.add_more) as LinearLayout
        lr_user_agreement = v.findViewById<View>(R.id.lr_user_agreement) as LinearLayout
        lr_contact_us = v.findViewById<View>(R.id.lr_contact_us) as LinearLayout
        lr_update_birthday = v.findViewById<View>(R.id.lr_update_birthday) as LinearLayout
        lr_complaint = v.findViewById<View>(R.id.lr_complaint) as LinearLayout
        tv_date_ofbirth = v.findViewById<View>(R.id.tv_date_ofbirth) as TextView
        imageViewSetting = v.findViewById<View>(R.id.imageViewSetting) as ImageView
        textViewVersion = v.findViewById<View>(R.id.textViewVersionNumber) as CustomTextviewHelvicNormal


        val manager = activity!!.packageManager
        val info = manager.getPackageInfo(activity!!.packageName, PackageManager.GET_ACTIVITIES)
        textViewVersion.setText("Version:" + info.versionName)
    }
}
