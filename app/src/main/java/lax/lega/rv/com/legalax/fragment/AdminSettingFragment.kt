package lax.lega.rv.com.legalax.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_purchase_credits.*
import kotlinx.android.synthetic.main.fragment_admin_setting.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.UpdateSettingResponse
import lax.lega.rv.com.legalax.pojos.getSetting.GetSettingResponse
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import retrofit2.Call
import retrofit2.Response

class AdminSettingFragment : Fragment() {


    lateinit var v: View
    lateinit var sharedPreference: MySharedPreference
    lateinit var textViewSetPrice: TextView
    lateinit var textViewSetDescription: TextView
    lateinit var ed_price: EditText
    lateinit var editTextPrivacy: EditText
    lateinit var imageViewBackPress: ImageView
    lateinit var textViewSetPrivacy: TextView
    lateinit var scrollView: ScrollView
    lateinit var linearLayoutLawyerSetting: LinearLayout
    lateinit var editTextDescription: EditText
    var consultationFee = 0.0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_admin_setting, container, false)
        sharedPreference = MySharedPreference(activity)
        findViews(v)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSetting();
    }

    @SuppressLint("ClickableViewAccessibility")
    fun findViews(v: View) {
        textViewSetPrice = v.findViewById(R.id.textViewSetPrice)
        ed_price = v.findViewById(R.id.ed_price)
        editTextPrivacy = v.findViewById(R.id.editTextPrivacy)
        editTextDescription = v.findViewById(R.id.editTextDescription)
        textViewSetPrivacy = v.findViewById(R.id.textViewSetPrivacy)
        imageViewBackPress = v.findViewById(R.id.imageViewBackPress)
        textViewSetDescription = v.findViewById(R.id.textViewSetDescription)
        scrollView = v.findViewById(R.id.scrollView)
        linearLayoutLawyerSetting = v.findViewById(R.id.linearLayoutLawyerSetting)
        textViewSetPrice.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (ed_price.text.toString().length == 0) {
                    showToast(getString(R.string.empty_price))
                } else {
                    updateSetting("points", ed_price.text.toString())
                }
            }

        });
        textViewSetPrivacy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (editTextPrivacy.text.toString().length == 0) {
                    showToast(getString(R.string.empty_privacy))
                } else {
                    updateSetting("privacy_policy", editTextPrivacy.text.toString())
                }
            }

        })

        imageViewBackPress.setOnClickListener { activity!!.supportFragmentManager.popBackStack() }

        linearLayoutLawyerSetting.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, LawyerListSettings()).addToBackStack(null).commit()
        }



        textViewSetDescription.setOnClickListener {
            if (editTextDescription.text.trim().length > 0) {
                updateSetting("video_chat_desc", editTextDescription.text.toString())
            } else {
                Toast.makeText(activity, "Please enter the description", Toast.LENGTH_LONG).show()
            }

        }





        editTextDescription.setOnTouchListener { p0, p1 ->
            scrollView.requestDisallowInterceptTouchEvent(true);
            false;
        }

    }


    private fun getSetting() {
          Utils.instance.dismissProgressDialog()
         Utils.instance.showProgressBar(activity)
        val add_credit = WebService().apiService.getSettings(
                "Bearer " + sharedPreference.getString("access_token")
        )
        add_credit.enqueue(object : retrofit2.Callback<GetSettingResponse> {

            override fun onFailure(call: Call<GetSettingResponse>, t: Throwable) {
                  Utils.instance.dismissProgressDialog()
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show()
               // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<GetSettingResponse>, response: Response<GetSettingResponse>) {
                  Utils.instance.dismissProgressDialog()

                if (response.code()==401){
                    openLogoutPopup(context as Activity)
                }

                if (response.isSuccessful) {
                    if (response.body()!!.data.size > 4) {
                        editTextDescription.setText(response.body()!!.data[4].settingValue)
                        consultationFee = response.body()!!.data[0].settingValue.toDoubleOrNull()!!
                        ed_price.setText(consultationFee.toInt().toString())
                        editTextPrivacy.setText(response.body()!!.data[1].settingValue)
                    }
                }
            }
        })
    }


    private fun updateSetting(fieldName: String, value: String) {
          Utils.instance.dismissProgressDialog()
         Utils.instance.showProgressBar(activity)
        val updateSetting = WebService().apiService.updateSettings("Bearer " + sharedPreference.getString("access_token"), "0", fieldName, value)
        updateSetting.enqueue(object : retrofit2.Callback<UpdateSettingResponse> {

            override fun onFailure(call: Call<UpdateSettingResponse>, t: Throwable) {
                  Utils.instance.dismissProgressDialog()
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_LONG).show()
               // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<UpdateSettingResponse>, response: Response<UpdateSettingResponse>) {
                  Utils.instance.dismissProgressDialog()

                if (response.code()==401){
                    openLogoutPopup(context as Activity)
                }

                if (response.isSuccessful) {
                    editTextPrivacy.setText("")
                    ed_price.setText("")
                    showToast(getString(R.string.setting_update_successfully))
                }
            }
        })
    }

    private fun showToast(s: String) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }
}
