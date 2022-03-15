package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.CustomTextviewHelvicNormal
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetUserDetailPojo
import lax.lega.rv.com.legalax.pojos.WriteSomethingPojo
import lax.lega.rv.com.legalax.utils.getAnError

class ViewLawyerProfileFragment : Fragment() {
    lateinit var backpress1: ImageView
    lateinit var v: View
    lateinit var mySharedPreference: MySharedPreference
    lateinit var txt_name: CustomTextviewBold
    lateinit var txt_loc: CustomTextviewHelvicNormal
    lateinit var txt_aboutme: CustomTextviewHelvicNormal

    lateinit var txt_info: CustomTextviewHelvicNormal
    lateinit var edt_something: EditText
    lateinit var img_setting: ImageView
    lateinit var img_profile: CircleImageView
    lateinit var llSpecifications: LinearLayout
    lateinit var tvSpecifications: CustomTextviewHelvicNormal
    val image = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.view_lawyer_profile_activity, container, false)
        init()
        click()
        getIdDetail(Utils.BASE_URL + Utils.GET_USER_DATA, arguments!!.getString("lawyer_id"))

        Picasso.with(activity).load(Utils.IMAGESURL + arguments!!.getString("image")).fit()
            .placeholder(R.drawable.icn_user_large).into(img_profile)
        return v
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }


    private fun click() {
        backpress1.setOnClickListener {
            if (edt_something.text.toString().equals("")) {
//                val intent = Intent(activity, MainActivity::class.java)
//                intent.putExtra("f", "")
//                startActivity(intent)
                activity!!.supportFragmentManager.popBackStack()

            } else {
                UpdateAboutYourself(edt_something.text.toString())

            }
        }
        img_setting.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, SettingsFragment())
                .addToBackStack(null).commit()

        })
    }


    fun UpdateAboutYourself(write_something: String) {
        Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.WRITE_SOMETHING)
            .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .addBodyParameter("write_something", write_something)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                WriteSomethingPojo::class.java,
                object : ParsedRequestListener<WriteSomethingPojo> {
                    override fun onResponse(user: WriteSomethingPojo) {
                        Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
//                            val intent = Intent(activity, MainActivity::class.java)
//                            intent.putExtra("f", "")
//                            startActivity(intent)
                            activity!!.supportFragmentManager.popBackStack()

                        } else {

                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                    }
                })
    }


    private fun getIdDetail(url: String, user_id: String?) {
        Utils.instance.showProgressBar(activity)
        try {
            AndroidNetworking.post(url)
                .addHeaders(
                    "Authorization",
                    "Bearer " + mySharedPreference.getString("access_token")
                )
                .addHeaders("Accept", "application/json")
                .addBodyParameter("user_id", user_id).setPriority(Priority.HIGH).build()
                .getAsObject(
                    GetUserDetailPojo::class.java,
                    object : ParsedRequestListener<GetUserDetailPojo> {
                        override fun onResponse(user: GetUserDetailPojo) {
                            Utils.instance.dismissProgressDialog()
                            if (user.user == null) {

                            } else {
                                Log.i("LAWYERDATA", "${Gson().toJson(user)}")
                                txt_name.text = user.user.name + " " + user.user.lastName
                                txt_loc.text = user.user.location
                                txt_aboutme.text = user.user.aboutUs
                                txt_info.text = user.user.information
                                val string = StringBuilder()
                                if (user.user.role == 2) {
                                    llSpecifications.visibility = View.VISIBLE
                                    if (user.user.lawyer_selected_cat.isNotEmpty()) {
                                        for (cat in user.user.lawyer_selected_cat)
                                            string.append(cat.cat_info.name + "\n")
                                        tvSpecifications.text = string.toString()
                                    }
                                } else
                                    llSpecifications.visibility = View.GONE


                            }

                            //                    name = user.getUser().getName();
                            //                    lastname = user.getUser().getLastName();
                        }

                        override fun onError(anError: ANError) {
                            Utils.instance.dismissProgressDialog()
                            getAnError(context as Activity, anError)
                            Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG)
                                .show()

                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun init() {
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        img_setting = v.findViewById<View>(R.id.img_setting) as ImageView
        img_profile = v.findViewById<View>(R.id.img_profile) as CircleImageView
        mySharedPreference = MySharedPreference(activity)
        txt_name = v.findViewById<View>(R.id.txt_name) as CustomTextviewBold
        txt_loc = v.findViewById<View>(R.id.txt_loc) as CustomTextviewHelvicNormal
        txt_aboutme = v.findViewById<View>(R.id.txt_aboutme) as CustomTextviewHelvicNormal
        tvSpecifications = v.findViewById<View>(R.id.tvSpecifications) as CustomTextviewHelvicNormal
        llSpecifications = v.findViewById<View>(R.id.llSpecifications) as LinearLayout
        txt_info = v.findViewById<View>(R.id.txt_info) as CustomTextviewHelvicNormal
        edt_something = v.findViewById<View>(R.id.edt_something) as EditText
        System.out.println("data" + mySharedPreference.getString("write_something"))
        /**/
        if (mySharedPreference.getString("write_something").equals(null)) {
            edt_something.setText("No data available")

        } else {
            edt_something.setText(mySharedPreference.getString("write_something"))

        }
    }

}
