package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
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
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.adapter.AllLawyerListAdapter
import lax.lega.rv.com.legalax.adapter.ShuffleListAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetLawyerPojo
import lax.lega.rv.com.legalax.pojos.UpdateSettingResponse
import lax.lega.rv.com.legalax.pojos.getSetting.GetSettingResponse
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import retrofit2.Call
import retrofit2.Response
import java.util.*

class LawyerListSettings : Fragment(), SwipeRefreshLayout.OnRefreshListener, ShuffleListAdapter.ClickListener, AllLawyerListAdapter.ClickListener {


    lateinit var backButton: ImageView
    lateinit var sharedPreference: MySharedPreference
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    val list = ArrayList<GetLawyerPojo.Lawyer.Datum>()
    var flag = true
    lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var searchView: SearchView
    lateinit var nodata: CustomTextviewBold
    lateinit var textViewShuffleList: TextView
    lateinit var textViewSetManually: TextView
    lateinit var textViewReset: TextView
    lateinit var switchButton: SwitchCompat
    lateinit var v: View
    lateinit var connectionDetector: ConnectionDetector
    var dialog: AlertDialog? = null
    lateinit var shufflingList: Array<String>
    var selectedCount = 0;
    var selectedIds = "";
    var setManualy = false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.lawyer_list_fragment, container, false)
        shufflingList = resources.getStringArray(R.array.shuffling_list)
        selectedCount = 0;
        init(v)
        clickhandler()

        ImpliemntSwipeToRefresh(v)

        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            if (flag) {
                list.clear()
                getLawyerList()
                flag = false
            }
        }
        setProductRecyclerView(v);
        return v
    }

    override fun onResume() {
        super.onResume()
        getSetting()
    }

    fun clickhandler() {
        backButton.setOnClickListener { activity!!.supportFragmentManager.popBackStack() }

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


        switchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (switchButton.isChecked) {
                    switchButton.isChecked = true;
                    updateSetting("1", "1")
                } else {
                    switchButton.isChecked = false;
                    updateSetting("0", "1")
                }
            }

        })

        textViewShuffleList.setOnClickListener {
            customAlertDialog()
        }

        textViewSetManually.setOnClickListener {
            if (textViewSetManually.text.toString() == "Done") {
                setManualy = false
                updateSetting(selectedIds, "3");
            } else {
                showDialog()
            }
        }


        textViewReset.setOnClickListener {
            updateSetting("0", "0")
        }
    }

    private fun init(view: View) {
        backButton = view.findViewById<View>(R.id.backpress1) as ImageView
        searchView = view.findViewById<View>(R.id.searchView) as SearchView
        sharedPreference = MySharedPreference(activity)
        nodata = view.findViewById<View>(R.id.nodata) as CustomTextviewBold
        textViewShuffleList = view.findViewById<View>(R.id.textViewShuffleList) as TextView
        textViewSetManually = view.findViewById<View>(R.id.textViewSetManually) as TextView
        textViewReset = view.findViewById<View>(R.id.textViewReset) as TextView
        switchButton = view.findViewById<View>(R.id.switchButton) as SwitchCompat
        connectionDetector = ConnectionDetector(activity)
        hideKeyboardForSearchView()
    }


    fun setProductRecyclerView(view: View) {
        product_recycler = view.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        val categoryNameAdapter = AllLawyerListAdapter(list)
        categoryNameAdapter.clickHandler(this)
        product_recycler.adapter = categoryNameAdapter
    }


    private fun ImpliemntSwipeToRefresh(v: View?) {
        mSwipeRefreshLayout = v!!.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)

    }

    override fun onRefresh() {
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            list.clear()
            mSwipeRefreshLayout.setRefreshing(false);
            getLawyerList()
            flag = false
        }

    }

    private fun hideKeyboardForSearchView() {
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Search"
        val imm = MainActivity.activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    fun getLawyerList() {
        selectedCount = 0;
          Utils.instance.showProgressBar(activity)
//        mSwipeRefreshLayout.setRefreshing(true);

        AndroidNetworking.get(Utils.BASE_URL + Utils.GET_LAWYER_LIST)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetLawyerPojo::class.java, object : ParsedRequestListener<GetLawyerPojo> {
                    override fun onResponse(user: GetLawyerPojo) {
                          Utils.instance.dismissProgressDialog()
                        mSwipeRefreshLayout.setRefreshing(false);

                        list.clear()
                        if (user.success.equals(true)) {
                            if (user.lawyer.data.size > 0) {
                                list.addAll(user.lawyer.data)
                                setProductRecyclerView(v)
                                product_recycler.visibility = View.VISIBLE
                                nodata.visibility = View.GONE
                            } else {
                                product_recycler.visibility = View.GONE
                                nodata.visibility = View.VISIBLE
                            }

                        } else {
                            Toast.makeText(MainActivity.activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        mSwipeRefreshLayout.setRefreshing(false);
                        getAnError(context as Activity,anError)

                        Toast.makeText(MainActivity.activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    fun getSearchedLawyerList(url: String?, search: String?) {
        selectedCount = 0;
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
                                setProductRecyclerView(v)
                                product_recycler.visibility = View.VISIBLE
                                nodata.visibility = View.GONE
                            } else {
                                product_recycler.visibility = View.GONE
                                nodata.visibility = View.VISIBLE
                            }

                        } else {
                            Toast.makeText(MainActivity.activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        getAnError(context as Activity,anError)
                        Toast.makeText(MainActivity.activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


    private fun updateSetting(filterValue: String, settingValue: String) {
          Utils.instance.showProgressBar(activity)
        val updateSetting = WebService().apiService.updateSettings("Bearer " + sharedPreference.getString("access_token"), filterValue, "filter_applied,filter_value", settingValue)
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
                getLawyerList();
                textViewSetManually.text = "Set Manually"
            }
        })
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
                Toast.makeText(activity, "Some thing went wrong", Toast.LENGTH_LONG).show()
              //  Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<GetSettingResponse>, response: Response<GetSettingResponse>) {
                  Utils.instance.dismissProgressDialog()
                if (response.isSuccessful) {

                    if (response.code()==401){
                        openLogoutPopup(context as Activity)
                    }

                    if (response.body()!!.data[3].settingValue == "1") {
                        switchButton.isChecked = true
                    } else {
                        switchButton.isChecked = false
                    }
                }
            }
        })
    }

    override fun onClick(position: Int) {
        dialog!!.dismiss()

        updateSetting(shufflingList[position].replace(" ", "").replace("-", "_"), "2");
    }

    override fun lawyerItemClick(position: Int) {

        if (setManualy) {
            selectedCount = selectedCount + 1;
            list.get(position).selectedCount = selectedCount.toString()
            setProductRecyclerView(v);
            if (selectedCount == 1) {
                selectedIds = list.get(position).id.toString();
            } else {
                selectedIds = "$selectedIds,${list.get(position).id}"
            }
        }

    }


    fun customAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val view = LayoutInflater.from(activity).inflate(R.layout.view_shuffle_list, null)
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewShuffleList)
        var textViewCancel = view.findViewById<TextView>(R.id.textViewCancel)


        textViewCancel.setOnClickListener {
            dialog!!.dismiss();
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        var adapter = ShuffleListAdapter(shufflingList)
        adapter.clickHandler(this)
        recyclerView.adapter = adapter
        dialog = dialogBuilder.show()
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setMessage("Please select lawyer by tapping on them")
        dialogBuilder.setPositiveButton("Ok") { dialogInterface, i ->
            textViewSetManually.setText("Done")
            setManualy = true
            dialogInterface.dismiss()
        }
        dialogBuilder.show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity!!,R.color.colorPrimary));

    }

}





