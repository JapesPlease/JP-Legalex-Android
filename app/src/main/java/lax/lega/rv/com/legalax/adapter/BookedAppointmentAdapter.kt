package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.pojos.AcceptedAppointmentPojo
import lax.lega.rv.com.legalax.pojos.DeleteNewsFeedPojo
import lax.lega.rv.com.legalax.utils.getAnError
import java.text.SimpleDateFormat
import java.util.*

class BookedAppointmentAdapter(private val activity: Activity, private val context: Context, private val list: ArrayList<AcceptedAppointmentPojo.List.Datum>) : RecyclerView.Adapter<BookedAppointmentAdapter.MyView>() {
    lateinit var itemView: View
    lateinit var mySharedPreference: MySharedPreference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedAppointmentAdapter.MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.booked_appointment_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: BookedAppointmentAdapter.MyView, position: Int) {
        if (list.get(position).status.toString().equals("0")) {
            holder.txt_appointmnet_type.text = "Pending Appointment"
            holder.txt_appointmnet_type.setTextColor(Color.parseColor("#2A2A2A"))
        } else if (list.get(position).status.toString().equals("1")) {
            holder.txt_appointmnet_type.text = "Confirmed Appointment"
            holder.txt_appointmnet_type.setTextColor(Color.parseColor("#64B084"))
        } else if (list.get(position).status.toString().equals("-1")) {
            holder.txt_appointmnet_type.text = "Rejected Appointment"
            holder.txt_appointmnet_type.setTextColor(Color.parseColor("#2A2A2A"))
        } else {
            holder.txt_appointmnet_type.text = "No status"
            holder.txt_appointmnet_type.setTextColor(Color.parseColor("#2A2A2A"))
        }

        if(mySharedPreference.getString("role").equals("1")) {
           holder.txt_name.text = list.get(position).reguserFullname + " booked " + list.get(position).lawyerFullname
        }else{
            holder.txt_name.text = list.get(position).user.name + " " + list.get(position).user.lastName
        }

        holder.txt_concern.text = list.get(position).concern
        holder.txt_time.text = list.get(position).bookingDate+" at "+DateUtils.Convert24to12(list.get(position).time)
        holder.txt_place.text = list.get(position).user.location

        var spf = SimpleDateFormat("yyyy-MM-dd")
        val newDate = spf.parse(list.get(position).bookingDate)
        spf = SimpleDateFormat("dd MMMM yyyy")
        val date = spf.format(newDate)
        holder.txt_date.text = date.toString()


        val current = Date()
        if (newDate.before(current)) {
            holder.rl_appointment.visibility == View.VISIBLE
        } else {
            holder.rl_appointment.visibility == View.GONE
        }

        holder.rl_appointment.setOnClickListener(View.OnClickListener {
            deleteNewsFeed(list!!.get(position).id.toString(),position)
        })
    }

    fun deleteNewsFeed(id: String, pos: Int)
    {
         Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.DELETE_APPOINTMENT)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("id", id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(DeleteNewsFeedPojo::class.java, object : ParsedRequestListener<DeleteNewsFeedPojo> {
                    override fun onResponse(user: DeleteNewsFeedPojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            list!!.removeAt(pos)
                            notifyDataSetChanged()
                            Toast.makeText(activity, "Record has been deleted successfully", Toast.LENGTH_LONG).show()

                        } else {
                            Toast.makeText(activity, "Record has not been deleted successfully", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


    override fun getItemCount(): Int
    {
        return list.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal lateinit var txt_appointmnet_type: CustomTextviewHelivicItalic
        internal lateinit var txt_name: CustomTextviewBold
        internal lateinit var txt_date: CustomTextviewBold
        internal lateinit var txt_concern: CustomTextviewHelvicNormal
        internal lateinit var txt_time: CustomTextviewHelvicNormal
        internal lateinit var txt_place: CustomTextviewHelvicNormal
        internal lateinit var rl_appointment: RelativeLayout

        init {
           // txt_appointmnet_type = view.findViewById<View>(R.id.txt_appointmnet_type) as CustomTextviewHelivicItalic
            txt_name = view.findViewById<View>(R.id.txt_name) as CustomTextviewBold
            txt_date = view.findViewById<View>(R.id.txt_date) as CustomTextviewBold
            txt_concern = view.findViewById<View>(R.id.txt_concern) as CustomTextviewHelvicNormal
            txt_time = view.findViewById<View>(R.id.txt_time) as CustomTextviewHelvicNormal
            txt_place = view.findViewById<View>(R.id.txt_place) as CustomTextviewHelvicNormal
            rl_appointment = view.findViewById<View>(R.id.rl_appointment) as RelativeLayout
            mySharedPreference = MySharedPreference(activity)

        }
    }


}

