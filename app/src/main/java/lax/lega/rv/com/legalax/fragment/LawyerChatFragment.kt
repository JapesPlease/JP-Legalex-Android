package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.ChatForLawyerAdapter
import lax.lega.rv.com.legalax.adapter.LawyerListAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetLawyerPojo
import lax.lega.rv.com.legalax.utils.getAnError

class LawyerChatFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
//            if (flag) {

            list.clear()
            mSwipeRefreshLayout.setRefreshing(false);
            getLawyerList()
            flag = false
//            }

        }
    }

    lateinit var v: View
    lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var sharedPreference: MySharedPreference
    val list = ArrayList<GetLawyerPojo.Lawyer.Datum>()
    lateinit var connectionDetector: ConnectionDetector
    lateinit var nodata: CustomTextviewBold
    lateinit var rll_main: LinearLayout
    lateinit var ll_root_layout: LinearLayout
    lateinit var rl_view_list: RelativeLayout
    lateinit var searchView: SearchView
    var isType = true
    var flag = true
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.lawyerforchatfragment, container, false)
        initt()
        setupUI(rll_main)
        click()
        ImplimentSwipeToRwefresh()
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            if (flag)
            {
                list.clear()
                getLawyerList()
                flag = false
            }
        }
        setProductRecyclerView()

        return v
    }

    private fun ImplimentSwipeToRwefresh() {

        mSwipeRefreshLayout = v.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)

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

    private fun click() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                hideKeyboardForSearchView()
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                list.clear()
                getSearchedLawyerList(Utils.BASE_URL + Utils.GET_LAWYER_LIST, s)
                return false
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun initt() {
        ll_root_layout = v.findViewById<View>(R.id.ll_root_layout) as LinearLayout
        rll_main = v.findViewById<View>(R.id.rll_main) as LinearLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        sharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        nodata = v.findViewById<View>(R.id.nodata) as CustomTextviewBold
        connectionDetector.isConnectingToInternet

        hideKeyboardForSearchView()

    }

    fun setupUI(view: View)
    {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is SearchView) {
            view.setOnTouchListener(object : View.OnTouchListener
            {
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

    fun getLawyerList() {
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(Utils.BASE_URL + Utils.GET_LAWYER_LIST)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetLawyerPojo::class.java, object : ParsedRequestListener<GetLawyerPojo> {
                    override fun onResponse(user: GetLawyerPojo) {
                          Utils.instance.dismissProgressDialog()
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (user.success.equals(true))
                        {
                            if (user.lawyer.data.size > 0) {
                                list.addAll(user.lawyer.data)
                                setProductRecyclerView()
                                product_recycler.visibility = View.VISIBLE
                                nodata.visibility = View.GONE
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

    fun getSearchedLawyerList(url: String?, search: String?)
    {
//        AndroidNetworking.cancel("search")
        AndroidNetworking.get(url + "?" + "search=" + search)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH).setTag("search")
                .build()
                .getAsObject(GetLawyerPojo::class.java, object : ParsedRequestListener<GetLawyerPojo> {
                    override fun onResponse(user: GetLawyerPojo) {
                        if (user.success.equals(true)) {
                            if (user.lawyer.data.size > 0) {
                                list.clear();
                                list.addAll(user.lawyer.data)
                                setProductRecyclerView()
                                product_recycler.visibility = View.VISIBLE
                                nodata.visibility = View.GONE
                            } else {
                                product_recycler.visibility = View.GONE
                                nodata.visibility = View.VISIBLE
                            }

                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    fun setProductRecyclerView() {
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        val categoryNameAdapter = ChatForLawyerAdapter(activity!!, list)
        product_recycler.adapter = categoryNameAdapter
    }


    override fun onResume() {
        super.onResume()
        hideKeyboardForSearchView()
//        searchView.setQuery("", false);
        ll_root_layout.requestFocus();

    }

}
