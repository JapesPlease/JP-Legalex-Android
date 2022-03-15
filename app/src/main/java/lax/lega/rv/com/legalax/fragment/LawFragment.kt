package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.LawyerListAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetLawyerPojo
import lax.lega.rv.com.legalax.pojos.lawyerCategories.GetLawyerCategories
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import retrofit2.Call
import retrofit2.Response

class LawFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    var selectedCategoryId = "20"
    var searchQuery = ""


    override fun onRefresh() {
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
//            if (flag) {
            list.clear()
            mSwipeRefreshLayout.isRefreshing = false

            if (searchView.query != null) {
                getSearchedLawyerList(
                    Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1",
                    searchView.query.toString(),
                    option.toString()
                )
            } else {
                getLawyerList(Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1", option.toString())
            }

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
    lateinit var rl_profile: LinearLayout
    lateinit var ll_root_layout: LinearLayout
    lateinit var rl_view_list: RelativeLayout
    lateinit var searchView: SearchView
    var isType = true
    var flag = true
    var isLoding = false
    var next_page_url: String? = null
    var recyclerViewState: Parcelable? = null
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    lateinit var relativeLayoutLawyer: RelativeLayout
    lateinit var relativeLayoutLawFirm: RelativeLayout


    lateinit var textViewLawFirm: CustomTextviewBold
    lateinit var textViewLawyer: CustomTextviewBold

    var selectedType = 0  //0->lawyer 1->lawfirm
    var option = 1    // 1->lawyers 2-> law firm

    lateinit var imageViewRed: ImageView


    lateinit var dialog: BottomSheetDialog
    var array = arrayOfNulls<String>(0)
    var allCategoriesList: MutableList<lax.lega.rv.com.legalax.pojos.lawyerCategories.Response> =
        ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.law_fragment, container, false)
        initt()
        setupUI(rll_main)
        click()
        ImpliemntSwipeToRefresh(v)
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            if (flag) {
                list.clear()
                getLawyerList(Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1", option.toString())
                flag = false
            }
        }
        setProductRecyclerView()

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

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(lawyerOnlineOffline)
    }

    private fun ImpliemntSwipeToRefresh(v: View?) {
        mSwipeRefreshLayout = v!!.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )

    }

    private fun hideKeyboardForSearchView() {
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Search"
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }


    private fun click() {
        rl_view_list.setOnClickListener {
            if (sharedPreference.getString("role") == "4") {
                val dialogView: View = layoutInflater.inflate(R.layout.bottom_sheet, null)

                var textViewSearch = dialogView.findViewById<TextView>(R.id.textViewSearch)
                var textViewCancel = dialogView.findViewById<TextView>(R.id.textViewCancel)

                var picker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)
                // var picker1 = dialogView.findViewById<NumberPicker>(R.id.numberPicker1)

                textViewCancel.setOnClickListener {
                    dialog.dismiss()
                }

                textViewSearch.setOnClickListener {


                    if (allCategoriesList.get(picker.value).id.toString() == "20") {
                        imageViewRed.visibility = View.GONE
                    } else {
                        selectedCategoryId = allCategoriesList.get(picker.value).id.toString()
                        imageViewRed.visibility = View.VISIBLE
                    }


                    selectedCategoryId = allCategoriesList.get(picker.value).id.toString()

                    getSearchedLawyerList(
                        Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1",
                        searchView.query.toString(),
                        option.toString()
                    )


                    //searchView.setQuery(searchView.query,true)
                    //  getLawyerList(Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1", option.toString())
                    dialog.dismiss()
                }

                picker.setMinValue(0)
                picker.setMaxValue(array.size - 1)
                picker.setDisplayedValues(array!!)


                dialog = BottomSheetDialog(activity!!)
                dialog.setContentView(dialogView)
                dialog.show()
            }
        }


        relativeLayoutLawFirm.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (selectedType == 0) {
                    option = 2
                    selectedType = 1
                    relativeLayoutLawFirm.background =
                        ContextCompat.getDrawable(activity!!, R.drawable.icn_button_bg)
                    relativeLayoutLawyer.background =
                        ContextCompat.getDrawable(activity!!, R.drawable.inputbox)


                    textViewLawFirm.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    textViewLawyer.setTextColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.lightGray
                        )
                    )

                    list.clear()
                    getLawyerList(
                        Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1",
                        option.toString()
                    )

                }
            }

        })
        relativeLayoutLawyer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (selectedType == 1) {
                    selectedType = 0
                    option = 1
                    relativeLayoutLawFirm.background =
                        ContextCompat.getDrawable(activity!!, R.drawable.inputbox)
                    relativeLayoutLawyer.background =
                        ContextCompat.getDrawable(activity!!, R.drawable.icn_button_bg)

                    textViewLawFirm.setTextColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.lightGray
                        )
                    )
                    textViewLawyer.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

                    list.clear()
                    getLawyerList(
                        Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1",
                        option.toString()
                    )
                }
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                hideKeyboardForSearchView()
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                list.clear()
                searchQuery = s
                getSearchedLawyerList(
                    Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1",
                    s,
                    option.toString()
                )
                return false
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (searchView.query.isEmpty()) {
                    list.clear()
                    getSearchedLawyerList(
                        Utils.BASE_URL + Utils.GET_LAWYER_LIST + "?page=1",
                        "",
                        option.toString()
                    )
                }
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
        rl_view_list = v.findViewById<View>(R.id.rl_view_list) as RelativeLayout
        rl_profile = v.findViewById<View>(R.id.rl_profile) as LinearLayout
        rll_main = v.findViewById<View>(R.id.rll_main) as LinearLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        sharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        nodata = v.findViewById<View>(R.id.nodata) as CustomTextviewBold

        imageViewRed = v.findViewById<View>(R.id.imageViewRed) as ImageView

        relativeLayoutLawyer = v.findViewById<View>(R.id.relativeLayoutLawyer) as RelativeLayout
        relativeLayoutLawFirm = v.findViewById<View>(R.id.relativeLayoutLawFirm) as RelativeLayout

        textViewLawFirm = v.findViewById<View>(R.id.textViewLawFirm) as CustomTextviewBold
        textViewLawyer = v.findViewById<View>(R.id.textViewLawyer) as CustomTextviewBold

        connectionDetector.isConnectingToInternet

        if (sharedPreference.getString("role") == "1") {
            rl_profile.visibility = View.GONE
        } else if (sharedPreference.getString("role") == "2") {
            rl_profile.visibility = View.GONE
        } else if (sharedPreference.getString("role") == "3") {
            rl_profile.visibility = View.GONE
        } else if (sharedPreference.getString("role") == "4") {
            rl_profile.visibility = View.VISIBLE
        }

        hideKeyboardForSearchView()

    }

    fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is SearchView) {
            view.setOnTouchListener { v, event ->
                Utils.hideKeyboard(activity)
                false
            }
        }
        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    fun getLawyerList(url: String?, option: String) {
        Utils.instance.showProgressBar(activity)
//        mSwipeRefreshLayout.setRefreshing(true);

        AndroidNetworking.get("$url&option=$option")
            .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(GetLawyerPojo::class.java, object : ParsedRequestListener<GetLawyerPojo> {
                override fun onResponse(user: GetLawyerPojo) {
                    Utils.instance.dismissProgressDialog()
                    mSwipeRefreshLayout.isRefreshing = false

                    if (user.success == true) {
                        if (user.lawyer.data.size > 0) {
                            list.addAll(user.lawyer.data)
                            setProductRecyclerView()
                            product_recycler.visibility = View.VISIBLE
                            nodata.visibility = View.GONE
                        } else {
                            product_recycler.visibility = View.GONE
                            nodata.visibility = View.VISIBLE
                        }

//                            if (user.lawyer.nextPageUrl != null) {
//                                next_page_url = user.lawyer.nextPageUrl.toString()
//                            } else {
//                                next_page_url = null
//                            }
//                            isLoding = false

                    } else {
                        Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(anError: ANError) {
                    Utils.instance.dismissProgressDialog()
                    mSwipeRefreshLayout.isRefreshing = false;
                    getAnError(context as Activity, anError)
                    Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                }
            })
    }

    fun getSearchedLawyerList(url: String?, search: String?, option: String) {
        AndroidNetworking.get("$url&search=$search&option=$option&cat_id=$selectedCategoryId")
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
                        Toast.makeText(activity, "No data is fetched", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(anError: ANError) {
                    getAnError(context as Activity, anError)
                    Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                }
            })
    }

    fun setProductRecyclerView() {
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        val categoryNameAdapter = LawyerListAdapter(activity!!, list, option)
        product_recycler.adapter = categoryNameAdapter


//        product_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if ((recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == list.size - 1 && next_page_url != null) {
//                    if (!isLoding) {
//                        if (searchView.query != null) {
//                            getSearchedLawyerList(next_page_url, searchView.query.toString(), option.toString())
//                        } else {
//                            getLawyerList(next_page_url, option.toString())
//                        }
//                        recyclerViewState = product_recycler.layou tManager!!.onSaveInstanceState()
//                        isLoding = true
//                    }
//                }
//            }
//        })


    }

    override fun onResume() {
        super.onResume()
        hideKeyboardForSearchView()
//        searchView.setQuery("", false);
        ll_root_layout.requestFocus()

        getAllLawyerCatgory()


    }

    private fun getAllLawyerCatgory() {
        Utils.instance.dismissProgressDialog()
        Utils.instance.showProgressBar(activity!!)
        val api = WebService().apiService.getAllLawyerCategory(
            "Bearer " + sharedPreference.getString("access_token")
        )

        api.enqueue(object : retrofit2.Callback<GetLawyerCategories> {

            override fun onFailure(call: Call<GetLawyerCategories>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(activity!!, "Some thing went wrong", Toast.LENGTH_LONG).show()
                //  Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(
                call: Call<GetLawyerCategories>,
                response: Response<GetLawyerCategories>
            ) {
                Utils.instance.dismissProgressDialog()
                allCategoriesList = response.body()!!.response!!
                array = arrayOfNulls<String>(response.body()!!.response!!.size)
                for (i in response.body()!!.response!!.indices) {
                    array[i] = response.body()!!.response!![i].name
                }
            }
        })
    }

}
