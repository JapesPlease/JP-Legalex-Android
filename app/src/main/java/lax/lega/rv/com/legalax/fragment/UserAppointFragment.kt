package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.marcohc.robotocalendar.RobotoCalendarView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_appointment_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.custom_timer.TimeWheel.LoopView
import lax.lega.rv.com.legalax.pojos.BookAppointmentPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UserAppointFragment : Fragment(), RobotoCalendarView.RobotoCalendarListener {

    override fun onDayClick(date: Date?) {
        //   Toast.makeText(activity, "onDayClick: $date", Toast.LENGTH_SHORT).show()

        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyy")
        val strDate = sdf.parse(date.toString())

        var day = ""
        if (getMonth(date.toString()).toString().equals("1")) {
            day = "Jan"
        } else if (getMonth(date.toString()).toString().equals("2")) {
            day = "Feb"
        } else if (getMonth(date.toString()).toString().equals("3")) {
            day = "Mar"
        } else if (getMonth(date.toString()).toString().equals("4")) {
            day = "Apr"
        } else if (getMonth(date.toString()).toString().equals("5")) {
            day = "May"
        } else if (getMonth(date.toString()).equals("6")) {
            day = "Jun"
        } else if (getMonth(date.toString()).equals("7")) {
            day = "July"
        } else if (getMonth(date.toString()).equals("8")) {
            day = "Aug"
        } else if (getMonth(date.toString()).equals("9")) {
            day = "Sep"
        } else if (getMonth(date.toString()).equals("10")) {
            day = "Oct"
        } else if (getMonth(date.toString()).equals("11")) {
            day = "Nov"
        } else if (getMonth(date.toString()).equals("12")) {
            day = "Dec"
        }
        val formatter = SimpleDateFormat("yyyy/MM/dd")
        val formatter1 = SimpleDateFormat("MMM dd yyyy")
        val systemDate = Calendar.getInstance().time
        val myDate = formatter1.format(systemDate)
        val selectedDate = formatter1.format(date)
        val convertedDate = formatter1.format(Date())
        if (convertedDate.equals(selectedDate)) {
            var your_date_is_outdated = false
            txt_day.text = "" + formatter.format(date)
            if (txt_time.text.toString().trim() != null) {
                CompareTime(txt_time.text.toString().trim())

            }


        } else {
            if (strDate.toString().contentEquals(day) && strDate.toString()
                            .contentEquals(getDate(date.toString()).toString()) && strDate.toString().contentEquals(getYear(date.toString()).toString())) {
                var your_date_is_outdated = false
                txt_day.text = "" + formatter.format(date)
                if (txt_time.text.toString().trim() != null) {
                    CompareTime(txt_time.text.toString().trim())

                }


            } else {

                if (Date().equals(strDate)) {
                    var your_date_is_outdated = false
                    txt_day.text = "" + formatter.format(date)
                    if (txt_time.text.toString().trim() != null) {
                        CompareTime(txt_time.text.toString().trim())

                    }


                } else {
                    if (Date().after(strDate)) {
                        var your_date_is_outdated = true
                        txt_day.text = ""
                        Toast.makeText(activity, "Please select correct date", Toast.LENGTH_SHORT).show()
                    } else {
                        var your_date_is_outdated = false
                        txt_day.text = "" + formatter.format(date)
                        if (txt_time.text.toString().trim() != null) {
                            CompareTime(txt_time.text.toString().trim())

                        }

                    }
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }


    @Throws(ParseException::class)
    private fun getMonth(date: String): Int {
        val d = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyy").parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        val month = cal.get(Calendar.MONTH)
        return month + 1
    }

    @Throws(ParseException::class)
    private fun getYear(date: String): Int {
        val d = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyy").parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        val year = cal.get(Calendar.YEAR)
        return year
    }

    @Throws(ParseException::class)
    private fun getDate(date: String): Int {
        val d = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyy").parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        val year = cal.get(Calendar.DAY_OF_MONTH)
        return year
    }

    override fun onDayLongClick(date: Date?) {
    }

    override fun onRightButtonClick() {

    }

    override fun onLeftButtonClick() {

    }

    lateinit var ll_calender: LinearLayout
    lateinit var ll_time: LinearLayout
    lateinit var txt_day: TextView
    lateinit var txt_time: TextView
    lateinit var txt_email: CustomTextviewBold
    lateinit var backpress1: ImageView
    lateinit var rl_submit: RelativeLayout
    lateinit var v: View
    lateinit var simpleTimeFormat: SimpleDateFormat

    lateinit var confirmBtn: Button
    lateinit var hourLoopView: LoopView
    lateinit var minLoopView: LoopView
    lateinit var secLoopView: LoopView
    lateinit var timeMeridiemView: LoopView
    lateinit var img_profile: CircleImageView


    private var hourPos = 0
    private var minPos = 0
    private var secPos = 0
    private var timeMeridiemPos = 0
    val hourList = ArrayList<String>()
    val minList = ArrayList<String>()
    val secList = ArrayList<String>()
    val merediumList = ArrayList<String>()

    private val viewTextSize = 25

    internal var minHour: Int = 0
    var myInt: Int = 0
    internal var maxHour: Int = 0
    internal var minMin: Int = 0
    internal var maxMin: Int = 0
    internal var minSec: Int = 0
    internal var maxSec: Int = 0
    lateinit var mySharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.user_appointment_activity, container, false)

        mySharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        init()
        click()
        return v
    }

    private fun click() {
        /* backpress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });*/

        rl_submit.setOnClickListener({
            if (txt_day.text.toString().length == 0) {
                Toast.makeText(activity, "Please select correct date", Toast.LENGTH_LONG).show()
            } else if (txt_time.text.toString().length == 0) {
                Toast.makeText(activity, "Please select time", Toast.LENGTH_LONG).show()
            } else if (edt_concern.text.toString().length == 0) {
                Toast.makeText(activity, "Please Enter concern", Toast.LENGTH_LONG).show()
            } else {

                BookUserAppointment(myInt.toString(), txt_day.text.toString(), txt_time.text.toString(), edt_concern.text.toString())
            }
        })

        txt_day.setOnClickListener { view ->
            /*   final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.Dialog, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        txt_day.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();*/
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val point = Point()
            point.x = location[0]
            point.y = location[1]
            showcommentPopup(activity!!, point)
        }

        txt_time.setOnClickListener { view ->
            // Get Current Time
            /*  final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.Dialog, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String status = "AM";
                        String hour = "", min = "";
                        if (hourOfDay > 11) {
                            status = "PM";
                        }
                        int hour_of_12_hour_format;

                        if (hourOfDay > 11) {
                            hour_of_12_hour_format = hourOfDay - 12;
                        } else {
                            hour_of_12_hour_format = hourOfDay;
                        }
                        if (hour_of_12_hour_format < 10) {
                            hour = "0" + hour_of_12_hour_format;
                        } else {
                            hour = "" + hour_of_12_hour_format;
                        }
                        if (minute < 10) {
                            min = "0" + minute;
                        } else {
                            min = "" + minute;
                        }
                        txt_time.setText(hour + " : " + min + " " + status);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();*/
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val point = Point()
            point.x = location[0]
            point.y = location[1]
            shwTime(activity!!, point)
        }
    }


    private fun BookUserAppointment(id: String, booking_date: String, time: String, concern: String) {
        var paramUserOrLawyerID: String
        var url: String
        if (mySharedPreference.getString("role") == "2") // Role=2=Lawyer, 1 = Admin, 3 = SME, 4 = Regular user
        {
            paramUserOrLawyerID = "user_id"
            url = Utils.BASE_URL + Utils.BOOK_USER
        } else {
            paramUserOrLawyerID = "lawyer_id"
            url = Utils.BASE_URL + Utils.BOOK_LAWYER
        }

          Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(url)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter(paramUserOrLawyerID, id)
                .addBodyParameter("booking_date", booking_date)
                .addBodyParameter("time", time)
                .addBodyParameter("concern", concern)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(BookAppointmentPojo::class.java, object : ParsedRequestListener<BookAppointmentPojo> {
                    override fun onResponse(user: BookAppointmentPojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success != null && user.success) {
                            Toast.makeText(activity, "Record has been added successfully", Toast.LENGTH_LONG).show()
                            val fragmentManager = activity!!.supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.flContent, VideoCallListFragment()).addToBackStack(null).commit()
                        } else {
                            Toast.makeText(activity, "Record has not been added successfully", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    private fun init() {
        ll_time = v.findViewById<View>(R.id.ll_time) as LinearLayout
        img_profile = v.findViewById(R.id.img_profile)
        ll_calender = v.findViewById<View>(R.id.ll_calender) as LinearLayout
        txt_day = v.findViewById<View>(R.id.txt_day) as TextView
        txt_time = v.findViewById<View>(R.id.txt_time) as TextView
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        rl_submit = v.findViewById<View>(R.id.rl_submit) as RelativeLayout
        txt_email = v.findViewById<View>(R.id.txt_email) as CustomTextviewBold
        this.simpleTimeFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        val bundle = this.arguments
        if (bundle != null) {
            myInt = bundle.getInt("lawyer_id", 0)
            txt_email.text = bundle.getString("email", "")
            if (bundle.getString("image") != null) {
                var image = bundle.getString("image")
                Picasso.with(activity).load(Utils.IMAGESURL + image).fit().placeholder(R.drawable.icn_user_large).into(img_profile)

            }
        }
    }

    private fun showcommentPopup(context: Activity, p: Point) {
        val changeStatusPopUp: PopupWindow
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.custom_dialog_calendar, null)
        changeStatusPopUp = PopupWindow(context)
        changeStatusPopUp.contentView = layout
        changeStatusPopUp.width = LinearLayout.LayoutParams.WRAP_CONTENT
        changeStatusPopUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        changeStatusPopUp.isFocusable = true
        val OFFSET_X = 0
        val OFFSET_Y = 20
        changeStatusPopUp.setBackgroundDrawable(BitmapDrawable())
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y)

        val robotoCalendarView = layout.findViewById<RobotoCalendarView>(R.id.robotoCalendarPicker)
        robotoCalendarView.setRobotoCalendarListener(this)

        robotoCalendarView.setShortWeekDays(false)

        robotoCalendarView.showDateTitle(true)

        robotoCalendarView.date = Date()


    }

    private fun shwTime(context: Activity, p: Point) {
        val changeStatusPopUp: PopupWindow
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.activity_time_picker, null)
        changeStatusPopUp = PopupWindow(context)
        changeStatusPopUp.contentView = layout
        changeStatusPopUp.width = LinearLayout.LayoutParams.WRAP_CONTENT
        changeStatusPopUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        changeStatusPopUp.isFocusable = true
        val OFFSET_X = 0
        val OFFSET_Y = 40
        changeStatusPopUp.setBackgroundDrawable(BitmapDrawable())
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y)

        /*  CustomCalendarView mCustomCalendar;
        mCustomCalendar = (CustomCalendarView) layout.findViewById(R.id.activity_main_view_custom_calendar);
        mCustomCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(CalendarDate date) {
                txt_day.setText(""+date);
                //changeStatusPopUp.dismiss();
            }
        });*/
        //Option
        minHour = DEFAULT_MIN_HOUR
        maxHour = DEFAULT_MAX_HOUR
        minMin = DEFAULT_MIN_MIN
        maxMin = DEFAULT_MAX_MIN
        minSec = DEFAULT_MIN_SEC
        maxSec = DEFAULT_MAX_SEC

        val milliseconds = getLongFromyyyyMMdd(strTime)
        val calendar = Calendar.getInstance(Locale.getDefault())

        if (!milliseconds.equals(-1)) {

            calendar.timeInMillis = milliseconds
            hourPos = calendar.get(Calendar.HOUR)
            minPos = calendar.get(Calendar.MINUTE)
            secPos = calendar.get(Calendar.SECOND)

            val date = strTime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (date[1] == "AM") {
                timeMeridiemPos = 0
            } else if (date[1] == "PM") {
                timeMeridiemPos = 1
            }
        }

        initialiseTimeWheel(layout, changeStatusPopUp)

    }

    internal fun onTimePickCompleted(hour: Int, min: Int, sec: Int, meridium: String,
                                     timeDesc: String) {
        Toast.makeText(activity, timeDesc, Toast.LENGTH_SHORT).show()
        Log.e("time", ">>>>>>>>>>>>>>>>" + timeDesc)
        txt_time.text = "" + timeDesc
        CompareTime(timeDesc)


    }

    private fun CompareTime(timeDesc: String) {

        try {
            val sdf = SimpleDateFormat("hh:mm:ss a")


            val systemDate = Calendar.getInstance().time
            val myDate = sdf.format(systemDate)
            //                  txtCurrentTime.setText(myDate);

            val Date1 = sdf.parse(timeDesc)
            val Date2 = sdf.parse(myDate)

            val millse = Date1.time - Date2.time
            val mills = Math.abs(millse)

            val Hours = (mills / (1000 * 60 * 60)).toInt()
            val Mins = (mills / (1000 * 60)).toInt() % 60
            val Secs = ((mills / 1000).toInt() % 60).toLong()

            val diff = Hours.toString() + ":" + Mins + ":" + Secs
            Log.e("Difff", ">>>>>>>>>>>>>" + diff)


            val sdff = SimpleDateFormat("yyyy/MM/dd")
            val currentDate = sdff.format(Date())
            System.out.println(" C DATE is  " + currentDate)


            var selectedate = txt_day.text.toString().trim()
            var comparedate = currentDate
            if (selectedate.equals(comparedate, true)) {
                if (!Date1.after(Date2)) {
                    txt_time.setText("")
                    Toast.makeText(activity, "Not a valid time", Toast.LENGTH_SHORT).show()

                } else {

                }
            }

        } catch (e: Exception) {


        }

    }


    fun initialiseTimeWheel(view: View, changeStatusPopUp: PopupWindow) {

        confirmBtn = view.findViewById<View>(R.id.btn_confirm) as Button
        hourLoopView = view.findViewById<View>(R.id.picker_hour) as LoopView
        minLoopView = view.findViewById<View>(R.id.picker_min) as LoopView
        secLoopView = view.findViewById<View>(R.id.picker_sec) as LoopView
        timeMeridiemView = view.findViewById<View>(R.id.picker_meridiem) as LoopView


        //do not loop,default can loop
        hourLoopView.setNotLoop()
        minLoopView.setNotLoop()
        secLoopView.setNotLoop()
        timeMeridiemView.setNotLoop()

        //set loopview text btnTextsize
        hourLoopView.setTextSize(viewTextSize.toFloat())
        minLoopView.setTextSize(viewTextSize.toFloat())
        secLoopView.setTextSize(viewTextSize.toFloat())
        timeMeridiemView.setTextSize(viewTextSize.toFloat())

        //set checked listen
        hourLoopView.setListener { item -> hourPos = item }
        minLoopView.setListener { item -> minPos = item }
        secLoopView.setListener { item -> secPos = item }

        timeMeridiemView.setListener { item -> timeMeridiemPos = item }

        initPickerViews()

        confirmBtn.setOnClickListener { view ->
            if (view === confirmBtn) {

                val hour = hourPos + 1
                val min = minPos
                val sec = secPos
                val meredium = timeMeridiemPos
                var merediumText = ""
                if (meredium == 0) {
                    merediumText = "am"
                } else if (meredium == 1) {
                    merediumText = "pm"
                }

                val sb = StringBuffer()

                sb.append(hour.toString())
                sb.append(":")
                sb.append(format2LenStr(min))
                sb.append(":")
                sb.append(format2LenStr(sec))
                sb.append(" ")
                sb.append(merediumText)
                onTimePickCompleted(hour, min, sec, merediumText, sb.toString())
                changeStatusPopUp.dismiss()
            }
        }

    }


    private fun initPickerViews() {

        val hourCount = maxHour
        val minCount = maxMin
        val secCount = maxSec

        for (i in 1..hourCount) {
            hourList.add(format2LenStr(i))
        }

        for (j in 0..minCount) {
            minList.add(format2LenStr(j))
        }

        for (k in 0..secCount) {
            secList.add(format2LenStr(k))
        }

        hourLoopView.setArrayList(hourList)
        hourLoopView.setInitPosition(hourPos)

        minLoopView.setArrayList(minList)
        minLoopView.setInitPosition(minPos)

        secLoopView.setArrayList(secList)
        secLoopView.setInitPosition(secPos)

        merediumList.add("AM")
        merediumList.add("PM")
        timeMeridiemView.setArrayList(merediumList)
        timeMeridiemView.setInitPosition(timeMeridiemPos)

    }

    companion object {

        private val DEFAULT_MIN_HOUR = 1
        private val DEFAULT_MIN_MIN = 0
        private val DEFAULT_MIN_SEC = 0
        private val DEFAULT_MAX_HOUR = 12
        private val DEFAULT_MAX_MIN = 59
        private val DEFAULT_MAX_SEC = 59


        fun getLongFromyyyyMMdd(time: String): Long {
            val mFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            var parse: Date? = null
            try {
                parse = mFormat.parse(time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return parse?.time ?: -1
        }

        val strTime: String
            get() {
                val dd = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
                return dd.format(Date())
            }

        fun format2LenStr(num: Int): String {

            return if (num < 10) "0$num" else num.toString()
        }
    }

}
