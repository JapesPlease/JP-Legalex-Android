package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.editprofile_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.CustomTextviewHelvicNormal
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetUserDetailPojo
import lax.lega.rv.com.legalax.pojos.getSetting.GetSettingResponse
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import retrofit2.Call
import retrofit2.Response

class ProfileFragment : Fragment() {

    lateinit var backpress1: ImageView
    lateinit var v: View
    lateinit var img_feed_gllery: ImageView
    lateinit var rl_edit_profile: RelativeLayout
    lateinit var txt_aboutus: CustomTextviewHelvicNormal
    lateinit var tvAboutUsHead: CustomTextviewBold
    lateinit var img_setting: ImageView
    lateinit var txt_information: CustomTextviewHelvicNormal
    lateinit var linearLayoutPrivacy: LinearLayout
    lateinit var mySharedPreference: MySharedPreference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.editprofile_activity, container, false)
        init()
        getIdDetail(Utils.BASE_URL + Utils.GET_USER_DATA, mySharedPreference.getInt("id").toString())
        return v
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        click()
        getSetting()
    }

    private fun init() {

        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        rl_edit_profile = v.findViewById<View>(R.id.rl_edit_profile) as RelativeLayout
        txt_information = v.findViewById<View>(R.id.txt_information) as CustomTextviewHelvicNormal
        txt_aboutus = v.findViewById<View>(R.id.txt_aboutus) as CustomTextviewHelvicNormal
        tvAboutUsHead = v.findViewById<View>(R.id.tvAboutUsHead) as CustomTextviewBold
        img_setting = v.findViewById<View>(R.id.img_setting) as ImageView
        linearLayoutPrivacy = v.findViewById<View>(R.id.linearLayoutPrivacy) as LinearLayout
        img_feed_gllery = v.findViewById<ImageView>(R.id.img_feed_gllery)
        mySharedPreference = MySharedPreference(activity)

    }

    private fun getIdDetail(url: String, user_id: String?) {
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(url).addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token")).addHeaders("Accept", "application/json").addBodyParameter("user_id", user_id).setPriority(Priority.HIGH).build().getAsObject(GetUserDetailPojo::class.java, object : ParsedRequestListener<GetUserDetailPojo> {
            override fun onResponse(user: GetUserDetailPojo) {
                  Utils.instance.dismissProgressDialog()
                val pojo = Gson().toJson(user)

                Log.e("GetPojoDetail", ">>>>>>>>>>>>>>>>" + user.user.status)
                Log.e("GetPojoDetailPoints", ">>>>>>>>>>>>>>>>" + pojo)
                      if (user.user.role == 2)
                          tvAboutUsHead.text = "ABOUT ME"
                if (user.user.profileImage != null) {
                    Picasso.with(activity).load(Utils.IMAGESURL + user.user.profileImage).fit().placeholder(R.drawable.icn_user_large).into(img_feed_gllery)
                    mySharedPreference.putString("profile_image", user.user.profileImage);
                } else {
                    //     Picasso.with(activity).load("http://13.251.150.219/spree/public/profileimages/" + filelist.get(position).sellerinfo_user_image).placeholder(R.drawable.icn_profile).into(img_profile)
                }

                if (user.user.aboutUs != null) {
                    txt_aboutus.text = user.user.aboutUs
                } else {
                    txt_aboutus.text = "No data available"
                }
                if (user.user.information != null) {
                    txt_information.text = user.user.information
                } else {
                    txt_information.text = "No data available"
                }

                if (user.user.id == mySharedPreference.getInt("id").toInt()) {
                    rl_edit_profile.visibility == View.VISIBLE
                } else {
                    rl_edit_profile.visibility == View.GONE
                }

                //                    name = user.getUser().getName();
                //                    lastname = user.getUser().getLastName();
            }

            override fun onError(anError: ANError) {
                  Utils.instance.dismissProgressDialog()
                getAnError(context as Activity, anError)
                Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

            }
        })
    }

    private fun click() {

        linearLayoutPrivacy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.legalex.ph/privacy-policy"))
            startActivity(browserIntent)
        }


        rl_edit_profile.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, UpdateProfileFragment()).commit()

        }

        textViewNewsFeed.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, NewsFeedFragment()).commit()
        }

        backpress1.setOnClickListener {
            //            val intent = Intent(activity, MainActivity::class.java)
//            startActivity(intent)
//            activity!!.finish()

//            activity!!.onBackPressed()
            activity!!.supportFragmentManager.popBackStack()


        }

        img_setting.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, SettingsFragment()).addToBackStack(null).commit()

        })

    }

    private fun getSetting() {
          Utils.instance.dismissProgressDialog()
          Utils.instance.showProgressBar(activity)
        val add_credit = WebService().apiService.getSettings(
                "Bearer " + mySharedPreference.getString("access_token")
        )

        add_credit.enqueue(object : retrofit2.Callback<GetSettingResponse> {

            override fun onFailure(call: Call<GetSettingResponse>, t: Throwable) {
                  Utils.instance.dismissProgressDialog()
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_LONG).show()
               // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<GetSettingResponse>, response: Response<GetSettingResponse>) {
                  Utils.instance.dismissProgressDialog()

                if (response.code() == 401) {
                    openLogoutPopup(context as Activity)
                }

                if (response.isSuccessful) {
                    /*if (response.body()!!.data.isNotEmpty() && response.body()!!.data.size >= 2)
                        try {
                            textViewPrivacy.text = response.body()!!.data[1].settingValue
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }*/

                }
            }
        })
    }
}
