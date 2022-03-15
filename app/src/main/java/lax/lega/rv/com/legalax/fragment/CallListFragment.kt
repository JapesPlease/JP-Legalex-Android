package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.CallListAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetCallHistoryPojo
import lax.lega.rv.com.legalax.utils.getAnError


class CallListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh()
    {

        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
//            if (flag) {

            list.clear()
            mSwipeRefreshLayout.setRefreshing(false);
            getLawyerList(Utils.BASE_URL + Utils.GET_CALL_HISTORY)
            flag = false
//            }

        }
    }

    lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var backpress1: ImageView
    lateinit var rl_view_list: RelativeLayout
    lateinit var utils: Utils
    lateinit var v: View
    val list = ArrayList<GetCallHistoryPojo.Users.Datum>()
    lateinit var sharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector
    lateinit var nodata: CustomTextviewBold
    lateinit var rl_click: RelativeLayout
    lateinit var rll_main: RelativeLayout
    lateinit var ll_root_layout: LinearLayout
    lateinit var searchView: SearchView
    var isType = false
    var isLoding = false
    var next_page_url: String? = null
    var recyclerViewState: Parcelable? = null
    var flag = true
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.call_list_activity, container, false)
        rll_main = v.findViewById<View>(R.id.rll_main) as RelativeLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        ll_root_layout = v.findViewById<View>(R.id.ll_root_layout) as LinearLayout
        init()
        click()
        setProductRecyclerView()
        ImplimentSwipeToRefresh(v)

        setupUI(rll_main)
        return v
    }

    private fun ImplimentSwipeToRefresh(v: View?) {
        mSwipeRefreshLayout = v!!.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)


    }

    public fun UpdateFragment(){
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            /*if (flag) {
// alreadyHit=true;
                dataList.clear();
                hitGetHomeData(UrlConstants.URL_GET_HOME_DATA);
                flag = false;

            }*/
            if (flag) {
                list.clear()
                getLawyerList(Utils.BASE_URL + Utils.GET_CALL_HISTORY)
                flag = false
            }

        }

    }



    private fun hideKeyboardForSearchView() {
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Search"
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
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

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }


    override fun onResume() {
        super.onResume()
//        searchView.setQuery("", false);
        hideKeyboardForSearchView()
        val filter = IntentFilter("Refresh")
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(reciever, filter)
        ll_root_layout.requestFocus();
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            /*if (flag) {
// alreadyHit=true;
                dataList.clear();
                hitGetHomeData(UrlConstants.URL_GET_HOME_DATA);
                flag = false;

            }*/
            if (flag) {
                list.clear()
                getLawyerList(Utils.BASE_URL + Utils.GET_CALL_HISTORY)
                flag = false
            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        isType = true

    }

    val reciever = object : BroadcastReceiver()
    {
        override fun onReceive(contxt: Context?, intent: Intent?) {

            if (intent?.getStringExtra("refresh").equals("data"))
            {
                getLawyerList(Utils.BASE_URL + Utils.GET_CALL_HISTORY)


            }
        }
    }

    private fun click() {
        rl_click.setOnClickListener {
            val args = Bundle()
            args.putString("from", "call")
            val fragobj = UserListFragment()
            fragobj.arguments = args

            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, fragobj).addToBackStack(null).commit()

        }

        backpress1.setOnClickListener { activity!!.finish() }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                val search_term = searchView.getQuery().toString();
                list.clear();
                hideKeyboardForSearchView()
                Utils.hideKeyboard(activity)
                getLawyerListSearch(Utils.BASE_URL + Utils.GET_CALL_HISTORY, s);
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {

                if (searchView.getQuery().length == 0) {
                    list.clear();
                    getLawyerListSearch(Utils.BASE_URL + Utils.GET_CALL_HISTORY, s)

                } else {
                    list.clear()
                    getLawyerListSearch(Utils.BASE_URL + Utils.GET_CALL_HISTORY, s)

                }
//                isType = false
//                if (isType == false) {
                // getLawyerListSearch(Utils.BASE_URL + Utils.GET_CALL_HISTORY, s)
                // }
                return false
            }
        })


        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (searchView.query.length == 0) {
                    list.clear()
                    getLawyerListSearch(Utils.BASE_URL + Utils.GET_CALL_HISTORY, "");
                }
                return false
            }
        })

        product_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == list.size - 1 && next_page_url != null) {
                    if (!isLoding) {
                        getLawyerList(next_page_url)
                        recyclerViewState = product_recycler.layoutManager!!.onSaveInstanceState()
                        isLoding = true
                    }
                }
            }
        })
    }


    private fun init() {
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        rl_view_list = v.findViewById<View>(R.id.rl_view_list) as RelativeLayout
        sharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        nodata = v.findViewById<View>(R.id.nodata) as CustomTextviewBold
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        rl_click = v.findViewById<View>(R.id.rl_click) as RelativeLayout

    }


    fun setProductRecyclerView() {
        val categoryNameAdapter = CallListAdapter(activity!!.applicationContext, activity!!, list)
        product_recycler.adapter = categoryNameAdapter

    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(reciever);

        super.onPause()
    }


    fun getLawyerList(url: String?) {

         Utils.instance.showProgressBar(activity)
//        mSwipeRefreshLayout.setRefreshing(true);
        AndroidNetworking.get(url)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetCallHistoryPojo::class.java, object : ParsedRequestListener<GetCallHistoryPojo> {
                    override fun onResponse(user: GetCallHistoryPojo) {
                          Utils.instance.dismissProgressDialog()
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (user.success.equals(true)) {
                            if (user.users.data.size > 0)
                            {
                                list.clear()

                                list.addAll(user.users.data)
                                product_recycler.visibility = View.VISIBLE
                                nodata.visibility = View.GONE
                                if (user.users.getNextPageUrl() != null) {
                                    next_page_url = user.users.getNextPageUrl().toString()
                                } else {
                                    next_page_url = null
                                }
                                isLoding = false
                                product_recycler.adapter!!.notifyDataSetChanged()
                            } else {
                                product_recycler.visibility = View.GONE
                                nodata.visibility = View.VISIBLE
                            }

                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        mSwipeRefreshLayout.setRefreshing(false);
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

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
                .getAsObject(GetCallHistoryPojo::class.java, object : ParsedRequestListener<GetCallHistoryPojo> {
                    override fun onResponse(user: GetCallHistoryPojo) {
                        //   Utils.hideLoader()

                        if (user.success.equals(true)) {
                            list.clear()
                            list.addAll(user.users.data)
                            RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
                            product_recycler.layoutManager = RecyclerViewLayoutManager1
                            val categoryNameAdapter = CallListAdapter(activity!!.applicationContext, activity!!, list)
                            product_recycler.adapter = categoryNameAdapter
                        } else {
                            product_recycler.visibility = View.GONE
                            nodata.visibility = View.VISIBLE
                            //    Toast.makeText(activity, "No data is available", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        //   Utils.hideLoader()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

}
