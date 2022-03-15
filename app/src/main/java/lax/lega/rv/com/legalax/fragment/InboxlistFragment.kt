package lax.lega.rv.com.legalax.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.ChatListAdapter
import lax.lega.rv.com.legalax.adapter.InboxListAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetChatDataPojo
import lax.lega.rv.com.legalax.pojos.GetChatHistoryPojo

class InboxlistFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
//            if (flag) {
            list.clear()
            setProductRecyclerView()
            mSwipeRefreshLayout.isRefreshing = false
            getLawyerList(Utils.BASE_URL + Utils.GET_CHAT_HISTORY)
            // flag = false
//            }

        }
    }

    lateinit var v: View
    lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var rl_settings: RelativeLayout
    lateinit var sharedPreference: MySharedPreference
    val list = ArrayList<GetChatHistoryPojo.Response.Users.Datum>()
    lateinit var nodata: CustomTextviewBold
    var isLoding = false
    var next_page_url: String? = null
    lateinit var searchView: SearchView
    var recyclerViewState: Parcelable? = null
    lateinit var rll_main: LinearLayout
    lateinit var ll_root_layout: LinearLayout
    lateinit var img_profile: CircleImageView
    var isType = true
    var flag = true
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    lateinit var reciever:BroadcastReceiver


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.inboxlist_fragment, container, false)
        ll_root_layout = v.findViewById<View>(R.id.ll_root_layout) as LinearLayout
        img_profile = v.findViewById(R.id.img_profile)
        rll_main = v.findViewById<View>(R.id.rll_main) as LinearLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        setupUI(rll_main)
        initt()
        click()
        ImplimentSwipeToRefreshView()

        return v
    }


    private fun ImplimentSwipeToRefreshView() {
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun hideKeyboardForSearchView() {
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Search"

        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is SearchView) {
            view.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    Utils.hideKeyboard(activity)
                    return false
                }
            })
        }

        hideKeyboardForSearchView()

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(reciever)
    }

    override fun onResume() {
        super.onResume()
//        searchView.setQuery("", false);
        hideKeyboardForSearchView()
        ll_root_layout.requestFocus()

        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            if (!isLoding) {
                list.clear()
                setProductRecyclerView()
                isLoding = true
                getLawyerList(Utils.BASE_URL + Utils.GET_CHAT_HISTORY)
            }
            /* if (flag)
             {
                 list.clear()
                 getLawyerList(Utils.BASE_URL + Utils.GET_CHAT_HISTORY)
             }*/
        }
        registerBroadCast()
    }

    fun getLawyerList(url: String?) {
       var token= sharedPreference.getString("access_token")
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(url)
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetChatHistoryPojo::class.java, object : ParsedRequestListener<GetChatHistoryPojo> {
                    override fun onResponse(user: GetChatHistoryPojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.response.success == true) {
                            if (user.response.users.data.size > 0) {
                                list.clear()
                                list.addAll(user.response.users.data)
                                product_recycler.visibility = View.VISIBLE
                                nodata.visibility = View.GONE
                            } else {
                                product_recycler.visibility = View.GONE
                                nodata.visibility = View.VISIBLE
//                                val fragment = LawyerChatFragment()
//                                (activity)!!.supportFragmentManager
//                                        .beginTransaction()
//                                        .replace(R.id.flContent, fragment).addToBackStack(null).commit()
                            }
                        } else {
                            product_recycler.visibility = View.GONE
                            nodata.visibility = View.VISIBLE
                        }
                        isLoding = false
                        setProductRecyclerView()
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        isLoding = false
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }


    private fun click() {
        rl_settings.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, SettingsFragment()).addToBackStack(null).commit()
        }

        img_profile.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).addToBackStack(null).commit()

        })

        product_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == list.size - 1 && next_page_url != null) {
                    if (!isLoding) {
                        getLawyerList(next_page_url)
                        recyclerViewState = product_recycler.layoutManager!!.onSaveInstanceState()
                        isLoding = true
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                isType = false
                if (isType == false) {
                    if (!isLoding) {
                        list.clear()
                        setProductRecyclerView()
                        isLoding = true
                        getLawyerListSearch(Utils.BASE_URL + Utils.GET_CHAT_HISTORY, s)
                    }
                }
                return false
            }
        })
    }

    fun getLawyerListSearch(url: String?, search: String?) {
        //  Utils.showLoader(activity)
        AndroidNetworking.get(url + "?" + "search=" + search)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetChatHistoryPojo::class.java, object : ParsedRequestListener<GetChatHistoryPojo> {
                    override fun onResponse(user: GetChatHistoryPojo) {
                        //   Utils.hideLoader()
                        if (user != null) {
                            if (user.response.success == true) {

                                if (user.response.users.data.size > 0) {
                                    list.clear()
                                    list.addAll(user.response.users.data)
                                    RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
                                    product_recycler.layoutManager = RecyclerViewLayoutManager1
                                    val categoryNameAdapter = ChatListAdapter(activity!!, list)
                                    product_recycler.adapter = categoryNameAdapter

                                    product_recycler.visibility = View.VISIBLE
                                    nodata.visibility = View.GONE

                                } else {
                                    product_recycler.visibility = View.GONE
                                    nodata.visibility = View.VISIBLE
                                }
                            } else {
                                product_recycler.visibility = View.GONE
                                nodata.visibility = View.VISIBLE
                                //    Toast.makeText(activity, "No data is available", Toast.LENGTH_LONG).show()
                            }
                        }
                        isLoding = false
                    }

                    override fun onError(anError: ANError) {
                        //   Utils.hideLoader()
                        isLoding = false
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun initt() {
        rl_settings = v.findViewById<View>(R.id.rl_settings) as RelativeLayout
        sharedPreference = MySharedPreference(activity)
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        nodata = v.findViewById<View>(R.id.nodata) as CustomTextviewBold
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        Picasso.with(activity).load(Utils.IMAGESURL + sharedPreference.getString("profile_image")).fit().placeholder(R.drawable.icn_user_large).into(img_profile)
//        searchView.setQuery("", false)
        hideKeyboardForSearchView()
    }

    fun setProductRecyclerView() {
        val categoryNameAdapter = InboxListAdapter(activity!!, list)
        product_recycler.adapter = categoryNameAdapter
    }
    fun registerBroadCast(){
        var intentFilter = IntentFilter("refreshList")
        reciever = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                getLawyerList(Utils.BASE_URL + Utils.GET_CHAT_HISTORY)
            }
        }
        activity!!.registerReceiver(reciever, intentFilter)
    }
}
