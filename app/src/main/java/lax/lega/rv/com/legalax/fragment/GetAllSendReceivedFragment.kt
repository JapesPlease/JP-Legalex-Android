package lax.lega.rv.com.legalax.fragment

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.GetAllSendReceivedAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetAllSendReceivedRequestPojo
import lax.lega.rv.com.legalax.retrofit.Constant

class GetAllSendReceivedFragment : Fragment() {
    lateinit var v: View
    lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var txt_nodata: CustomTextviewBold
    lateinit var sharedPreference: MySharedPreference
    var isLoding = false
    var next_page_url: String? = null
    var recyclerViewState: Parcelable? = null
    val list = ArrayList<GetAllSendReceivedRequestPojo.ReceivedRequest.Datum>()
    lateinit var rl_profile: RelativeLayout
    lateinit var img_profile: CircleImageView
    lateinit var img_profile1: CircleImageView

    var selectedType = 0  //0->recieved 1->sent
    lateinit var textViewReceived: CustomTextviewBold
    lateinit var textViewSent: CustomTextviewBold


    lateinit var relativeLayoutReceieved: RelativeLayout
    lateinit var relativeLayoutSent: RelativeLayout

    lateinit var mySharedPreference: MySharedPreference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.get_all_request_fragment, container, false)

        mySharedPreference = MySharedPreference(activity)
        initt()
        click()
        // setProductRecyclerView()
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            list.clear()
            getNotification(Utils.BASE_URL + Utils.SEND_RECIEVE_BOOKING)
        }
    }

    fun getNotification(url: String?) {
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(url)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetAllSendReceivedRequestPojo::class.java, object : ParsedRequestListener<GetAllSendReceivedRequestPojo> {
                    override fun onResponse(user: GetAllSendReceivedRequestPojo) {
                          Utils.instance.dismissProgressDialog()
                        try {
                            if (user.success.equals(true)) {
                                if (user.receivedRequest.data.size > 0) {
                                    list.addAll(user.receivedRequest.data)
                                    setProductRecyclerView()
                                    product_recycler.visibility = View.VISIBLE
                                    txt_nodata.visibility = View.GONE
                                } else {
                                    product_recycler.visibility = View.GONE
                                    txt_nodata.visibility = View.VISIBLE
                                }
                            } else {
                                Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                            }

                        } catch (e: Exception) {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()
                        }

                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
//                          Utils.instance.dismissProgressDialog()
                        if (anError.errorDetail == "parseError") {
                            Toast.makeText(activity, "No Result Found", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                        }
                    }
                })
    }


    private fun click() {
        rl_profile.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).addToBackStack(null).commit()
        })


        if (mySharedPreference.getString("role").equals("2")) {
            textViewSent.setText("SENT")
            textViewReceived.setText("RECEIVED")
        } else {
            textViewSent.setText("RECEIVED")
            textViewReceived.setText("SENT")
        }


        relativeLayoutSent.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (selectedType == 0) {
                    list.clear()
                    selectedType = 1
                    relativeLayoutSent.background = ContextCompat.getDrawable(activity!!, R.drawable.icn_button_bg)
                    relativeLayoutReceieved.background = ContextCompat.getDrawable(activity!!, R.drawable.inputbox)


                    textViewSent.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    textViewReceived.setTextColor(ContextCompat.getColor(activity!!, R.color.lightGray))

                    getNotification(Utils.BASE_URL + Utils.SEND_RECIEVE_BOOKING_USER)

                }
            }

        })

        relativeLayoutReceieved.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (selectedType == 1) {
                    list.clear()
                    selectedType = 0
                    relativeLayoutSent.background = ContextCompat.getDrawable(activity!!, R.drawable.inputbox)
                    relativeLayoutReceieved.background = ContextCompat.getDrawable(activity!!, R.drawable.icn_button_bg)

                    textViewSent.setTextColor(ContextCompat.getColor(activity!!, R.color.lightGray))
                    textViewReceived.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

                    getNotification(Utils.BASE_URL + Utils.SEND_RECIEVE_BOOKING)

                }
            }

        })


    }

    private fun initt() {
        txt_nodata = v.findViewById<View>(R.id.txt_nodata) as CustomTextviewBold
        sharedPreference = MySharedPreference(activity)
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        rl_profile = v.findViewById<View>(R.id.rl_profile) as RelativeLayout
        img_profile = v.findViewById(R.id.img_profile)
        img_profile1 = v.findViewById(R.id.img_profile1)

        textViewReceived = v.findViewById<View>(R.id.textViewReceived) as CustomTextviewBold
        textViewSent = v.findViewById<View>(R.id.textViewSent) as CustomTextviewBold

        relativeLayoutReceieved = v.findViewById<View>(R.id.relativeLayoutReceieved) as RelativeLayout
        relativeLayoutSent = v.findViewById<View>(R.id.relativeLayoutSent) as RelativeLayout

        Picasso.with(activity).load(Utils.IMAGESURL + sharedPreference.getString("profile_image")).fit().fit().placeholder(R.drawable.icn_user_large).into(img_profile)
        Picasso.with(activity).load(Utils.IMAGESURL + sharedPreference.getString("profile_image")).fit().fit().placeholder(R.drawable.icn_user_large).into(img_profile1)
    }

    fun setProductRecyclerView() {
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        val categoryNameAdapter = GetAllSendReceivedAdapter(activity!!, activity!!, list, selectedType)
        product_recycler.adapter = categoryNameAdapter
        product_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == list.size - 1 && next_page_url != null) {
                    if (!isLoding) {
                        getNotification(next_page_url)
                        recyclerViewState = product_recycler.layoutManager!!.onSaveInstanceState()
                        isLoding = true
                    }
                }
            }
        })
    }
}

