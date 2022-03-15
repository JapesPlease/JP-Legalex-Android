package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.ChatListAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetChatHistoryPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError

class ChatListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
//            if (flag) {

            list.clear()
            mSwipeRefreshLayout.isRefreshing = false
            getChatList(Utils.BASE_URL + Utils.GET_CHAT_HISTORY)
            flag = false
//            }

        }

    }

    lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var nodata: CustomTextviewBold
    lateinit var rl_click: RelativeLayout
    lateinit var rl_profile: RelativeLayout
    lateinit var backpress1: ImageView
    lateinit var v: View
    lateinit var sharedPreference: MySharedPreference
    val list = ArrayList<GetChatHistoryPojo.Response.Users.Datum>()
    lateinit var connectionDetector: ConnectionDetector
    lateinit var rl_view_list: RelativeLayout
    lateinit var searchView: SearchView
    lateinit var textViewLawFirm: TextView
    lateinit var textViewLawyer: TextView
    lateinit var ll_root_layout: RelativeLayout
    lateinit var relativeLayoutLawyer: RelativeLayout
    lateinit var relativeLayoutLawFirm: RelativeLayout
    lateinit var ll_top: LinearLayout
    lateinit var rll_main: RelativeLayout
    var isType = false
    var recyclerViewState: Parcelable? = null
    var isLoding = false
    var next_page_url: String? = null
    var flag = true
    lateinit var img_profile: CircleImageView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    var selectedType=0  //0->lawyer 1->lawfirm


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.chat_list_fragment, container, false)
        ll_root_layout = v.findViewById<View>(R.id.ll_root_layout) as RelativeLayout
        rll_main = v.findViewById<View>(R.id.rll_main) as RelativeLayout
        rl_profile = v.findViewById<View>(R.id.rl_profile) as RelativeLayout

        ll_top = v.findViewById<View>(R.id.ll_top) as LinearLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView

        initt()
        click()
        setProductRecyclerView()
        ImplimentSwipeToRefresh(v)


        setupUI(rll_main)
        requireActivity().registerReceiver(
            lawyerOnlineOffline,
            IntentFilter("lawyer_online_offline")
        )
        return v
    }

    private val lawyerOnlineOffline = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onRefresh()
        }
    }


    private fun hideKeyboardForSearchView()
    {
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
        if (view is ViewGroup)
        {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }

        hideKeyboardForSearchView()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        isType = true
        requireActivity().unregisterReceiver(lawyerOnlineOffline)
    }

    private fun ImplimentSwipeToRefresh(v: View) {
        mSwipeRefreshLayout = v!!.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)
    }

    override fun onResume() {
        super.onResume()

//        searchView.setQuery("", false);
        hideKeyboardForSearchView()
        ll_root_layout.requestFocus();

        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {

            if (flag) {
                list.clear()
                getChatList(Utils.BASE_URL + Utils.GET_CHAT_HISTORY)
                flag = false
            }
        }
    }

    private fun click() {
        rl_profile.setOnClickListener( {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).addToBackStack(null).commit()
        })




        backpress1.setOnClickListener { activity!!.finish() }
        rl_click.setOnClickListener( {
            val args = Bundle()
            args.putString("from", "chat")
            val fragobj = UserListFragment()
            fragobj.arguments = args

            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, fragobj).addToBackStack(null).commit()


        })



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                val search_term = searchView.getQuery().toString();
                list.clear();
                searchView.setIconifiedByDefault(false);
                searchView.setIconified(false);
                searchView.clearFocus();
                Utils.hideKeyboard(activity)
                hideKeyboardForSearchView()
                getLawyerListSearch(Utils.BASE_URL + Utils.GET_CHAT_HISTORY, s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean
            {
                if (searchView.getQuery().length == 0) {
                    list.clear()
                    getLawyerListSearch(Utils.BASE_URL + Utils.GET_CHAT_HISTORY, s)

                } else {
                    list.clear();
                    getLawyerListSearch(Utils.BASE_URL + Utils.GET_CHAT_HISTORY, s)
                }
                return false
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (searchView.query.length == 0) {
                    list.clear();
                    getLawyerListSearch(Utils.BASE_URL + Utils.GET_CALL_HISTORY, "");
                }
                return false
            }
        })


        product_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == list.size - 1 && next_page_url != null) {
                    if (!isLoding) {
                        getChatList(next_page_url)
                        recyclerViewState = product_recycler.layoutManager!!.onSaveInstanceState()
                        isLoding = true
                    }
                }
            }
        })
    }

    private fun initt()
    {
        sharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        rl_view_list = v.findViewById<View>(R.id.rl_view_list) as RelativeLayout
        rl_click = v.findViewById<View>(R.id.rl_click) as RelativeLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        nodata = v.findViewById<View>(R.id.nodata) as CustomTextviewBold
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        img_profile = v.findViewById(R.id.img_profile)
        Picasso.with(activity).load(Utils.IMAGESURL + sharedPreference.getString("profile_image")).fit().placeholder(R.drawable.icn_user_large).into(img_profile)

    }


    fun setProductRecyclerView() {
        val categoryNameAdapter = ChatListAdapter(activity!!, list)
        product_recycler.adapter = categoryNameAdapter
    }

    fun getChatList(url: String?)
    {
        //Utils.showLoader(activity)
        list.clear()
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(url)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetChatHistoryPojo::class.java, object : ParsedRequestListener<GetChatHistoryPojo> {
                    override fun onResponse(user: GetChatHistoryPojo) {

                        //  Utils.hideLoader()
                          Utils.instance.dismissProgressDialog()
                        mSwipeRefreshLayout.isRefreshing = false
                        if (user.response.success == true) {
                            if (user.response.users.data.size > 0)
                            {
                                val args = Bundle()
                                args.putString("from", "chat")
                                val fragobj = UserListFragment()
                                fragobj.arguments = args
                                val fragmentManager = activity!!.supportFragmentManager
                                fragmentManager.beginTransaction().replace(R.id.flContent, fragobj).commit()
                            } else {
//                                product_recycler.visibility = View.GONE
//                                nodata.visibility = View.VISIBLE
                                val args = Bundle()
                                args.putString("from", "chat")
                                val fragobj = UserListFragment()
                                fragobj.arguments = args
                                val fragmentManager = activity!!.supportFragmentManager
                                fragmentManager.beginTransaction().replace(R.id.flContent, fragobj).commit()

                            }
                            if (user.response.users.getNextPageUrl() != null) {
                                next_page_url = user.response.users.getNextPageUrl().toString()
                            } else {
                                next_page_url = null
                            }
                            isLoding = false
                            product_recycler.adapter!!.notifyDataSetChanged()
                        } else {
                            product_recycler.visibility = View.GONE
                            nodata.visibility = View.VISIBLE
                            //    Toast.makeText(activity, "No data is available", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        //   Utils.hideLoader()
                         Utils.instance.dismissProgressDialog()
                        mSwipeRefreshLayout.isRefreshing = false
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

    fun getLawyerListSearch(url: String?, search: String?) {
        AndroidNetworking.get(url + "?" + "search=" + search)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetChatHistoryPojo::class.java, object : ParsedRequestListener<GetChatHistoryPojo> {
                    override fun onResponse(user: GetChatHistoryPojo) {
                        if (user.response.success.equals(true)) {
                            list.clear()
                            list.addAll(user.response.users.data)
//                            RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
//                            product_recycler.layoutManager = RecyclerViewLayoutManager1
//                            val categoryNameAdapter = ChatListAdapter(activity!!, list)
//                            product_recycler.adapter = categoryNameAdapter
                            product_recycler.adapter!!.notifyDataSetChanged()
                        } else {
                            product_recycler.visibility = View.GONE
                            nodata.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(anError: ANError)
                    {
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }


}
