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
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.Gson
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.ExpandableListAdapter
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.pojos.AcceptedAppointmentPojo
import lax.lega.rv.com.legalax.pojos.DeleteNewsFeedPojo
import lax.lega.rv.com.legalax.pojos.ExpandableListData
import lax.lega.rv.com.legalax.pojos.GetAllSendReceivedRequestPojo
import lax.lega.rv.com.legalax.utils.getAnError
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AppoinmentFragment : Fragment(), ExpandableListAdapter.ClickHandler {
    var selectedType = 0  //0->recieved 1->sent
    var from = ""
    lateinit var v: View
    lateinit var img_add: ImageView

    //lateinit var rl_submit: RelativeLayout
    lateinit var ll_booked: LinearLayout
    lateinit var relativeLayoutReceieved: RelativeLayout
    lateinit var relativeLayoutSent: RelativeLayout
    lateinit var txt_nodata: CustomTextviewBold
    lateinit var textViewTitle: CustomTextviewHelvicNormal
    lateinit var textViewReceived: CustomTextviewBold
    lateinit var textViewSent: CustomTextviewBold
    lateinit var ll_lawyerBooking: LinearLayout

    //lateinit var txt_selected_date: CustomTextviewHelvicNormal
    lateinit var sharedPreference: MySharedPreference
    lateinit var calenderView: MaterialCalendarView
    lateinit var expandableListVIew: NonScrollExpandableListView
    var isLoding = false
    var next_page_url: String? = null
    var recyclerViewState: Parcelable? = null

    // lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    val listAccepted = ArrayList<AcceptedAppointmentPojo.List.Datum>()
    val listRejected = ArrayList<AcceptedAppointmentPojo.List.Datum>()
    val listPending = ArrayList<AcceptedAppointmentPojo.List.Datum>()


    var expandableListData: MutableList<ExpandableListData> = ArrayList();
    var navigationMainAdapter: ExpandableListAdapter? = null
    lateinit var handler: ExpandableListAdapter.ClickHandler;
    lateinit var mySharedPreference: MySharedPreference


    //val list_S = ArrayList<String>()

    var pendingDates: MutableList<CalendarDay> = ArrayList();
    var approvedDates: MutableList<CalendarDay> = ArrayList();


    var selectedDate: MutableList<CalendarDay> = ArrayList();

    //   lateinit var calendarView: CalendarView
    var name_s: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.appointment_activity, container, false)


        mySharedPreference = MySharedPreference(activity)
        initt()
        click()
        handler = this
        //setProductRecyclerView()
        return v
    }

    private fun click() {
        ll_booked.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, GetAllSendReceivedFragment())
                .addToBackStack(null).commit()
        }
// Commented on 01-July-2020 by varinder
//        img_add.setOnClickListener {
//            val fragmentManager = activity!!.supportFragmentManager
//            fragmentManager.beginTransaction().replace(R.id.flContent, GetAllSendReceivedFragment()).addToBackStack(null).commit()
//        }


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
                    selectedType = 1
                    selectedDate.clear()
                    calenderView.clearSelection()
                    relativeLayoutSent.background =
                        ContextCompat.getDrawable(activity!!, R.drawable.icn_button_bg)
                    relativeLayoutReceieved.background =
                        ContextCompat.getDrawable(activity!!, R.drawable.inputbox)


                    textViewSent.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    textViewReceived.setTextColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.lightGray
                        )
                    )

                    getNotification(Utils.BASE_URL + Utils.SEND_RECIEVE_BOOKING_USER)

                }
            }

        })

        relativeLayoutReceieved.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (selectedType == 1) {
                    selectedDate.clear()
                    calenderView.clearSelection()
                    selectedType = 0
                    relativeLayoutSent.background =
                        ContextCompat.getDrawable(activity!!, R.drawable.inputbox)
                    relativeLayoutReceieved.background =
                        ContextCompat.getDrawable(activity!!, R.drawable.icn_button_bg)


                    textViewSent.setTextColor(ContextCompat.getColor(activity!!, R.color.lightGray))
                    textViewReceived.setTextColor(ContextCompat.getColor(activity!!, R.color.white))


                    getNotification(Utils.BASE_URL + Utils.SEND_RECIEVE_BOOKING)

                }
            }

        })
    }

    private fun initt() {
        img_add = v.findViewById<ImageView>(R.id.img_add)


        //txt_selected_date = v.findViewById<View>(R.id.txt_selected_date) as CustomTextviewHelvicNormal
        txt_nodata = v.findViewById<View>(R.id.txt_nodata) as CustomTextviewBold
        textViewReceived = v.findViewById<View>(R.id.textViewReceived) as CustomTextviewBold
        textViewSent = v.findViewById<View>(R.id.textViewSent) as CustomTextviewBold
        textViewTitle = v.findViewById<View>(R.id.textViewTitle) as CustomTextviewHelvicNormal
        ll_lawyerBooking = v.findViewById<View>(R.id.ll_lawyerBooking) as LinearLayout
        relativeLayoutReceieved =
            v.findViewById<View>(R.id.relativeLayoutReceieved) as RelativeLayout
        relativeLayoutSent = v.findViewById<View>(R.id.relativeLayoutSent) as RelativeLayout
        sharedPreference = MySharedPreference(activity)
        ll_booked = v.findViewById<View>(R.id.ll_booked) as LinearLayout
        //rl_submit = v.findViewById<View>(R.id.rl_submit_cal) as RelativeLayout
        //product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        expandableListVIew =
            v.findViewById<NonScrollExpandableListView>(R.id.expandableListVIew) as NonScrollExpandableListView


        // txt_selected_date.setText("No date selected")

        if (sharedPreference.getString("role").equals("2")) {
            textViewTitle.setText("All Requests")
            ll_lawyerBooking.visibility = View.VISIBLE
        } else {
            textViewTitle.setText("Book Appointment")
            ll_lawyerBooking.visibility = View.GONE
        }


        if (sharedPreference.getString("role").equals("1")) {
            ll_booked.visibility = View.GONE

        } else {
            ll_booked.visibility = View.VISIBLE

        }
        calenderView =
            v.findViewById<MaterialCalendarView>(R.id.calendarView) as MaterialCalendarView
        calenderView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE;



        calenderView.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                /*var selectedDates = "";
                if (selected) {
                    selectedDate.add(date)
                } else {
                    selectedDate.remove(date)
                }
                for (i in selectedDate.indices) {
                    var dateFormatter = SimpleDateFormat("yyyy-MM-dd")
                    var date = dateFormatter.format(selectedDate.get(i).date)
                    if (i == 0) {
                        selectedDates = date
                    } else {
                        selectedDates = date + "," + selectedDates
                    }
                }
                if (selectedType == 0) {
                    getBookedAppointments(Utils.BASE_URL + Utils.ACCEPT_BOOKING, selectedDates)
                } else {
                    getBookedAppointments(Utils.BASE_URL + Utils.ACCEPT_BOOKING_SENT, selectedDates)
                } //date.get*/
            }

        })

        ll_lawyerBooking.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val fragment = UserListFragment()
                val args = Bundle()
                args.putString("from", "appointment")
                //  val fragobj = UserListFragment()
                fragment.arguments = args
                //fragment.setArguments(bundle)
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flContent, fragment).addToBackStack(null).commit()
            }

        })
        requireActivity().registerReceiver(
            lawyerOnlineOffline,
            IntentFilter("lawyer_online_offline")
        )
    }

    private val lawyerOnlineOffline = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onResume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(lawyerOnlineOffline)
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
            //list.clear()
            getNotification(Utils.BASE_URL + Utils.SEND_RECIEVE_BOOKING)
        }

        expandableListData.clear()
        listPending.clear()
        listAccepted.clear()
        listRejected.clear()

        for (i in 0..2) {
            when (i) {
                0 -> {
                    var data = ExpandableListData();
                    data.itemName = "Accepted Appointments"
                    data.data = listAccepted
                    expandableListData.add(data)
                }

                1 -> {
                    var data = ExpandableListData();
                    data.itemName = "Pending Appointments"
                    data.data = listPending
                    expandableListData.add(data)
                }

                2 -> {

                    var data = ExpandableListData();
                    data.itemName = "Rejected Appointments"
                    data.data = listRejected
                    expandableListData.add(data)
                }

            }
        }
        navigationMainAdapter = ExpandableListAdapter(activity!!, expandableListData)
        navigationMainAdapter!!.clickListener(this)
        expandableListVIew.setAdapter(navigationMainAdapter)
    }


    override fun removeClick(groupPosition: Int, childPosition: Int) {
        if (selectedType == 1) {
            deleteNewsFeedWithUser(
                expandableListData.get(groupPosition).data!!.get(childPosition).id.toString(),
                groupPosition,
                childPosition
            );
        } else {
            deleteNewsFeed(
                expandableListData.get(groupPosition).data!!.get(childPosition).id.toString(),
                groupPosition,
                childPosition
            );
        }
    }


    fun getNotification(url: String?) {

        val timeStampFormat = SimpleDateFormat("yyyy-MM-dd")
        val myDate = Date()
        val currentDate = timeStampFormat.format(myDate)


        Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(url)
            .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                GetAllSendReceivedRequestPojo::class.java,
                object : ParsedRequestListener<GetAllSendReceivedRequestPojo> {
                    override fun onResponse(user: GetAllSendReceivedRequestPojo) {
                        Utils.instance.dismissProgressDialog()
                        Log.i("Appointment","${Gson().toJson(user)}")
                        try {
                            if (user.success.equals(true)) {


                                pendingDates.clear()
                                approvedDates.clear()

                                if (user.receivedRequest.data.size > 0) {
                                    for (i in user.receivedRequest.data.indices) {
                                        if (user.receivedRequest.data[i].status == 0) {
                                            var time =
                                                DateUtils.getTimeStamp(user.receivedRequest.data[i].bookingDate)
                                            var caldenderDat = CalendarDay.from(Date(time))
                                            pendingDates.add(caldenderDat)
                                        } else if (user.receivedRequest.data[i].status == 1) {
                                            var time =
                                                DateUtils.getTimeStamp(user.receivedRequest.data[i].bookingDate)
                                            var caldenderDat = CalendarDay.from(Date(time))
                                            approvedDates.add(caldenderDat)
                                        }
                                    }
                                    calenderView.addDecorator(
                                        EventDecorator(
                                            ContextCompat.getDrawable(
                                                activity!!,
                                                R.drawable.bg_blue_circle
                                            ), pendingDates, activity!!
                                        )
                                    )
                                    calenderView.addDecorator(
                                        EventDecorator(
                                            ContextCompat.getDrawable(
                                                activity!!,
                                                R.drawable.bg_red_circle
                                            ), approvedDates, activity!!
                                        )
                                    )

                                    if (selectedType == 0) {
                                        getBookedAppointments(
                                            Utils.BASE_URL + Utils.ACCEPT_BOOKING,
                                            currentDate
                                        )
                                    } else {
                                        getBookedAppointments(
                                            Utils.BASE_URL + Utils.ACCEPT_BOOKING_SENT,
                                            currentDate
                                        )
                                    }

                                } else {

                                }
                            } else {

                                Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG)
                                    .show()

                            }

                        } catch (e: Exception) {
                            Utils.instance.dismissProgressDialog()
                            if (activity != null)
                                Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG)
                                    .show()
                            else
                                Log.e("data", "" + e.message)
                        }

                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        if (anError.errorDetail == "parseError") {

                            Toast.makeText(activity, "No Result Found", Toast.LENGTH_LONG).show()


                        } else {
                            Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG)
                                .show()
                        }

                        getAnError(context as Activity, anError)
                    }
                })

    }


    fun deleteNewsFeed(id: String, groupPosition: Int, childPosition: Int) {
        Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.DELETE_APPOINTMENT)
            .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .addBodyParameter("id", id)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                DeleteNewsFeedPojo::class.java,
                object : ParsedRequestListener<DeleteNewsFeedPojo> {
                    override fun onResponse(user: DeleteNewsFeedPojo) {
                        Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            expandableListData.get(groupPosition).data!!.removeAt(childPosition)
                            navigationMainAdapter =
                                ExpandableListAdapter(activity!!, expandableListData)
                            navigationMainAdapter!!.clickListener(handler)
                            expandableListVIew.setAdapter(navigationMainAdapter)
                        } else {
                            Toast.makeText(
                                activity,
                                "Record has not been deleted successfully",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG)
                            .show()

                    }
                })
    }

    fun deleteNewsFeedWithUser(id: String, groupPosition: Int, childPosition: Int) {
        Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.DELETE_APPOINTMENT)
            .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .addBodyParameter("id", id)
            .addBodyParameter("booking_type", "user")
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                DeleteNewsFeedPojo::class.java,
                object : ParsedRequestListener<DeleteNewsFeedPojo> {
                    override fun onResponse(user: DeleteNewsFeedPojo) {
                        Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            expandableListData.get(groupPosition).data!!.removeAt(childPosition)
                            navigationMainAdapter =
                                ExpandableListAdapter(activity!!, expandableListData)
                            navigationMainAdapter!!.clickListener(handler)
                            expandableListVIew.setAdapter(navigationMainAdapter)
                        } else {
                            Toast.makeText(
                                activity,
                                "Record has not been deleted successfully",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG)
                            .show()

                    }
                })
    }


    fun getBookedAppointments(url: String?, searchedDate: String?) {
        /*Log.e("Searchdate", ">>>>>>>>>>>>>>>>>" + searchedDate);
        if (searchedDate == null || searchedDate == "") {
            return

        }*/
        //list.clear()
        //Utils.showProgressBar(activity)
        AndroidNetworking.get(url + "?" + "dates=" + searchedDate)
            .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(AcceptedAppointmentPojo::class.java,
                object : ParsedRequestListener<AcceptedAppointmentPojo> {
                    override fun onResponse(user: AcceptedAppointmentPojo) {
                        Utils.instance.dismissProgressDialog()
                        expandableListData.clear()
                        listPending.clear()
                        listAccepted.clear()
                        listRejected.clear()
                        if (user.success.equals(true)) {
                            if (user.list.data.size > 0) {
                                // list.addAll(user.list.data)
                                expandableListVIew.visibility = View.VISIBLE
                                txt_nodata.visibility = View.GONE

                                for (i in user.list.data.indices) {
                                    if (user.list.data.get(i).status.toString().equals("0")) {
                                        listPending.add(user.list.data.get(i))
                                    } else if (user.list.data.get(i).status.toString()
                                            .equals("1")
                                    ) {
                                        listAccepted.add(user.list.data.get(i))
                                    } else {
                                        listRejected.add(user.list.data.get(i))
                                    }
                                }

                                for (i in 0..2) {
                                    when (i) {
                                        0 -> {
                                            var data = ExpandableListData();
                                            data.itemName = "Accepted Appointments"
                                            data.data = listAccepted
                                            expandableListData.add(data)
                                        }

                                        1 -> {
                                            var data = ExpandableListData();
                                            data.itemName = "Pending Appointments"
                                            data.data = listPending
                                            expandableListData.add(data)
                                        }

                                        2 -> {

                                            var data = ExpandableListData();
                                            data.itemName = "Rejected Appointments"
                                            data.data = listRejected
                                            expandableListData.add(data)
                                        }

                                    }
                                }

                                navigationMainAdapter =
                                    ExpandableListAdapter(activity!!, expandableListData)
                                navigationMainAdapter!!.clickListener(handler);
                                expandableListVIew.setAdapter(navigationMainAdapter)
                            } else {

                                for (i in 0..2) {
                                    when (i) {
                                        0 -> {
                                            var data = ExpandableListData();
                                            data.itemName = "Accepted Appointments"
                                            data.data = listAccepted
                                            expandableListData.add(data)
                                        }

                                        1 -> {
                                            var data = ExpandableListData();
                                            data.itemName = "Pending Appointments"
                                            data.data = listPending
                                            expandableListData.add(data)
                                        }

                                        2 -> {

                                            var data = ExpandableListData();
                                            data.itemName = "Rejected Appointments"
                                            data.data = listRejected
                                            expandableListData.add(data)
                                        }

                                    }
                                }

                                /* expandableListVIew.visibility = View.GONE
                                 txt_nodata.visibility = View.VISIBLE*/
                            }
                            if (user.list.getNextPageUrl() != null) {
                                next_page_url = user.list.getNextPageUrl().toString()
                            } else {
                                next_page_url = null
                            }
                            /* isLoding = false
                             product_recycler.adapter!!.notifyDataSetChanged()*/
                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        if (anError.errorDetail == "parseError") {
                            Toast.makeText(activity, "No Result Found", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG)
                                .show()
                        }
                        getAnError(context as Activity, anError)

                    }
                })
    }

    /* fun setProductRecyclerView() {
         RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
         product_recycler.layoutManager = RecyclerViewLayoutManager1
         val categoryNameAdapter = BookedAppointmentAdapter(activity!!, activity!!, list)
         product_recycler.adapter = categoryNameAdapter

         product_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
             override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                 super.onScrollStateChanged(recyclerView, newState)
                 if ((recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == list.size - 1 && next_page_url != null) {
                     if (!isLoding) {
                         getBookedAppointments(next_page_url, "")
                         recyclerViewState = product_recycler.layoutManager!!.onSaveInstanceState()
                         isLoding = true
                     }
                 }
             }
         })
     }*/

}




