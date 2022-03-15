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
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
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
import lax.lega.rv.com.legalax.adapter.AllVideoUserAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetUsersPojo
import lax.lega.rv.com.legalax.pojos.lawyerCategories.GetLawyerCategories
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import retrofit2.Call
import retrofit2.Response
import java.util.*

class UserVideoListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (!connectionDetector.isConnectingToInternet) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
//          if (flag) {
            list.clear()
            mSwipeRefreshLayout.isRefreshing = false
            getAlluser(Utils.BASE_URL + Utils.ALL_USER)
            flag = false
//         }
        }

    }

    lateinit var product_recycler: RecyclerView
    lateinit var img_profile: CircleImageView
    lateinit var textViewTitle: CustomTextviewBold
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var backpress1: ImageView
    lateinit var v: View
    lateinit var sharedPreference: MySharedPreference
    val list = ArrayList<GetUsersPojo.Users.Datum>()
    lateinit var connectionDetector: ConnectionDetector
    var from: String? = null
    lateinit var searchView: SearchView
    lateinit var rll_main: LinearLayout
    lateinit var rl_profile: RelativeLayout
    lateinit var rl_click: RelativeLayout
    lateinit var ll_root_layout: RelativeLayout
    lateinit var rl_view_list: RelativeLayout
    lateinit var imageViewRed: ImageView
    var isType = true
    var flag = true
    var isLoding = false
    var next_page_url: String? = null
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    var allCategoriesList: MutableList<lax.lega.rv.com.legalax.pojos.lawyerCategories.Response> =
        ArrayList()
    var selectedCategoryId = "20"

    var toast: Toast? = null

    lateinit var dialog: BottomSheetDialog

    //var lawyerList:MutableList<String> = arrayListOf()

    var array = arrayOfNulls<String>(0)


    var recyclerViewState: Parcelable? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.alluser_video_fragment, container, false)

        isType = true
        setupUI(v.findViewById(R.id.ll_main))
        // rll_main = v.findViewById<View>(R.id.ll_main) as LinearLayout
        ll_root_layout = v.findViewById<View>(R.id.ll_root_layout) as RelativeLayout
        rl_profile = v.findViewById<View>(R.id.rl_profile) as RelativeLayout
        rl_click = v.findViewById<View>(R.id.rl_click) as RelativeLayout
        rl_view_list = v.findViewById<View>(R.id.rl_view_list) as RelativeLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        //

        initt()

        click()
        setProductRecyclerView()
        ImplimentSwipeToRefresh(v)

//        (activity as MainActivity).fragmentRefreshListener = MainActivity.FragmentRefreshListener {
//            Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show()
//
//            getAlluser(Utils.BASE_URL + Utils.ALL_USER)
//
//        }


        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        /*if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            if (flag) {

                list.clear()
                getAlluser(Utils.BASE_URL + Utils.ALL_USER)
                flag = false
            }

        }*/
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


    private fun ImplimentSwipeToRefresh(v: View) {
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
//        mSwipeRefreshLayout.post {
//            mSwipeRefreshLayout.isRefreshing = true
//
//            // Fetching data from server
//            val connectionDetector = ConnectionDetector(activity!!.applicationContext)
//            connectionDetector.isConnectingToInternet
//            if (connectionDetector.isConnectingToInternet === false) run {
//                Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
//            } else {
//                if (flag) {
//
//                    list.clear()
//                    getAlluser(Utils.BASE_URL + Utils.ALL_USER)
//                    flag = false
//                }
//
//            }
//        }
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
//        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0);
    }


    fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is SearchView) {
            view.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (activity != null) {
//                        hideSoftKeyboard(activity)
                        val imm =
                            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view!!.windowToken, 0);
                    }
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


    private fun click() {
        rl_view_list.setOnClickListener {
            if (sharedPreference.getString("role") == "4") {
                val dialogView: View = layoutInflater.inflate(R.layout.bottom_sheet, null)

                var textViewSearch = dialogView.findViewById<TextView>(R.id.textViewSearch)
                var textViewCancel = dialogView.findViewById<TextView>(R.id.textViewCancel)

                var picker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)

                textViewCancel.setOnClickListener {
                    dialog.dismiss()
                }

                textViewSearch.setOnClickListener {
                    if (allCategoriesList.get(picker.value).id.toString() == "20") {
                        imageViewRed.visibility = View.GONE
                    } else {
                        imageViewRed.visibility = View.VISIBLE
                    }
                    selectedCategoryId = allCategoriesList.get(picker.value).id.toString()
                    list.clear()
                    getAlluserSearch(
                        Utils.BASE_URL + Utils.ALL_USER + "?page=1",
                        searchView.query.toString()
                    )
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



        rl_click.setOnClickListener {
            val fragobj = VideoCallListFragment()
            var fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, fragobj).addToBackStack(null)
                .commit()

            /*replaceFrgamnet(new VideoCallListFragment());
            img_home.setImageResource(R.drawable.icn_home);
            img_chat.setImageResource(R.drawable.icn_message);
            img_law.setImageResource(R.drawable.icn_law);
            img_appoint.setImageResource(R.drawable.icn_calender);*/
        }


        rl_profile.setOnClickListener({
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment())
                .addToBackStack(null).commit()

        })
        backpress1.setOnClickListener { activity!!.finish() }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                list.clear()
                hideKeyboardForSearchView()
                Utils.hideKeyboard(activity)
                getAlluserSearch(Utils.BASE_URL + Utils.ALL_USER, s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (searchView.getQuery().length == 0) {
                    list.clear()

                    getAlluserSearch(Utils.BASE_URL + Utils.ALL_USER, s)

                } else {
                    list.clear()
                    getAlluserSearch(Utils.BASE_URL + Utils.ALL_USER, s)

                }
                return false
            }
        })


        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (searchView.query.length == 0) {
                    list.clear()
                    getAlluserSearch(Utils.BASE_URL + Utils.ALL_USER, "");
                }
                return false
            }
        })
        product_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == list.size - 1 && next_page_url != null) {
                    if (!isLoding) {
                        getAlluser(next_page_url)
                        recyclerViewState = product_recycler.layoutManager!!.onSaveInstanceState()
                        isLoding = true
                    }
                }
            }
        })
    }


    private fun initt() {
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        imageViewRed = v.findViewById<View>(R.id.imageViewRed) as ImageView
        img_profile = v.findViewById<View>(R.id.img_profile) as CircleImageView
        textViewTitle = v.findViewById<View>(R.id.textViewTitle) as CustomTextviewBold
        sharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        from = arguments!!.getString("from")
        Log.e("FromIs", ">>>>>>>>>>>>>>>>>" + from)
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1

        hideKeyboardForSearchView()

        Picasso.with(activity)
            .load(Constant.PROFILEPICTUREURL + sharedPreference.getString("profile_image")).fit()
            .placeholder(R.drawable.icn_user_large).into(img_profile)

        if (sharedPreference.getString("role").equals("1")) {
            textViewTitle.setText("ALL USER LIST")
        } else if (sharedPreference.getString("role").equals("2")) {
            textViewTitle.setText("All REGULAR USER LIST")
        } else if (sharedPreference.getString("role").equals("3")) {
            textViewTitle.setText("All LAWYER LIST")
        } else if (sharedPreference.getString("role").equals("4")) {
            textViewTitle.setText("All LAWYER LIST")
        }
    }


    fun setProductRecyclerView() {
        val categoryNameAdapter = AllVideoUserAdapter(activity!!, activity!!, list, from)
        product_recycler.adapter = categoryNameAdapter
    }


    /* fun getAlluser(url: String?) {
 //        mSwipeRefreshLayout.setRefreshing(true);

         Utils.instance.showProgressBar(activity)
         AndroidNetworking.get(url+"?cat_id=$selectedCategoryId")
                 .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                 .addHeaders("Accept", "application/json")
                 .setPriority(Priority.HIGH)
                 .build()
                 .getAsObject(GetUsersPojo::class.java, object : ParsedRequestListener<GetUsersPojo> {
                     override fun onResponse(user: GetUsersPojo) {
                           Utils.instance.dismissProgressDialog()
                         mSwipeRefreshLayout.isRefreshing = false

                         if (user.status == true) {
                             if (user.users.data.size > 0) {
                                 //list.clear()
                                 list.addAll(user.users.data)
                                 product_recycler.visibility = View.VISIBLE
                                 if (user.users.getNextPageUrl() != null) {
                                     next_page_url = user.users.getNextPageUrl().toString()
                                 } else {
                                     next_page_url = null
                                 }
                                 isLoding = false
                                 product_recycler.adapter!!.notifyDataSetChanged()
                             } else {
                                 if (toast != null) {
                                     toast!!.cancel()
                                 }
                                 toast = Toast.makeText(activity, "No Data available", Toast.LENGTH_LONG);
                                 toast!!.show()
                                 product_recycler.visibility = View.GONE
                             }

                         } else {
                             if (toast != null) {
                                 toast!!.cancel()
                             }
                             toast = Toast.makeText(activity, "No Data available", Toast.LENGTH_LONG);
                             toast!!.show()
                         }
                     }

                     override fun onError(anError: ANError) {
                           Utils.instance.dismissProgressDialog()
                         mSwipeRefreshLayout.isRefreshing = false
                         getAnError(context as Activity, anError)
                         Log.e("Error", ">>>>>>>>>>>>>" + anError.errorBody.toString())
                         Toast.makeText(activity, "Unable to connect server ", Toast.LENGTH_LONG).show()

                     }
                 })
     }*/

    private fun getAlluser(url: String?) {
        Utils.instance.showProgressBar(activity)
        val api = WebService().apiService.getUserList(
            "Bearer " + sharedPreference.getString("access_token"),
            url!!
        )

        api.enqueue(object : retrofit2.Callback<GetUsersPojo> {

            override fun onFailure(call: Call<GetUsersPojo>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(activity!!, "Some thing went wrong", Toast.LENGTH_LONG).show()
                // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<GetUsersPojo>, response: Response<GetUsersPojo>) {
                Log.e("response is", response.toString())
                Utils.instance.dismissProgressDialog()
                if (response.body()!!.status == true) {
                    if (response.body()!!.users.data.size > 0) {
                        //list.clear()
                        list.addAll(response.body()!!.users.data)
                        product_recycler.visibility = View.VISIBLE
                        if (response.body()!!.users.getNextPageUrl() != null) {
                            next_page_url = response.body()!!.users.getNextPageUrl().toString()
                        } else {
                            next_page_url = null
                        }
                        isLoding = false
                        product_recycler.adapter!!.notifyDataSetChanged()
                    } else {
                        if (toast != null) {
                            toast!!.cancel()
                        }
                        toast = Toast.makeText(activity, "No Data available", Toast.LENGTH_LONG);
                        toast!!.show()
                        product_recycler.visibility = View.GONE
                    }

                } else {
                    if (toast != null) {
                        toast!!.cancel()
                    }
                    toast = Toast.makeText(activity, "No Data available", Toast.LENGTH_LONG);
                    toast!!.show()
                }

            }
        })
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
                //Log.e("AddCreditPointsOnError", t.message)
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


    fun getAlluserSearch(url: String?, search: String?) {
        AndroidNetworking.get(url + "?" + "search=" + search + "&cat_id=$selectedCategoryId")
            .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(GetUsersPojo::class.java, object : ParsedRequestListener<GetUsersPojo> {
                override fun onResponse(user: GetUsersPojo) {
                    if (user.status.equals(true)) {
                        Log.e("Called", ">>>>>>" + "called");
                        if (user.users.data.size > 0) {

                            list.clear()

                            list.addAll(user.users.data)
                            //    setProductRecyclerView()
                            product_recycler.visibility = View.VISIBLE
                            if (user.users.getNextPageUrl() != null) {
                                next_page_url = user.users.getNextPageUrl().toString()
                            } else {
                                next_page_url = null
                            }
                            isLoding = false
                            product_recycler.adapter!!.notifyDataSetChanged()
//
//     product_recycler.adapter = categoryNameAdapter
                        } else {
                            product_recycler.visibility = View.GONE

                            if (toast != null) {
                                toast!!.cancel()
                            }
                            toast =
                                Toast.makeText(activity, "No Data available", Toast.LENGTH_LONG);
                            toast!!.show()
                        }

                    } else {
                        Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                    }
                }

                override fun onError(anError: ANError) {
                    getAnError(context as Activity, anError)
                    Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                }
            })
    }


    override fun onResume() {
        super.onResume()
//        searchView.setQuery("", false);
        activity!!.registerReceiver(reciever, IntentFilter("Refresh"))

        hideKeyboardForSearchView()
        ll_root_layout.requestFocus();

        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            list.clear()
            getAlluser(Utils.BASE_URL + Utils.ALL_USER)
            getAllLawyerCatgory()
        }
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(reciever)


    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(lawyerOnlineOffline)
    }

    val reciever = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Toast.makeText(activity, "called", Toast.LENGTH_SHORT).show()

            if (intent?.getStringExtra("refresh").equals("data")) {
                Toast.makeText(activity, "called", Toast.LENGTH_SHORT).show()
                getAlluser(Utils.BASE_URL + Utils.ALL_USER)
            }
        }

    }

}