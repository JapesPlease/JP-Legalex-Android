package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.fragment.GetAllSendReceivedFragment
import lax.lega.rv.com.legalax.pojos.DeletePojo
import lax.lega.rv.com.legalax.pojos.GetAllSendReceivedRequestPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError

class GetAllSendReceivedAdapter(private val activity: Activity, private val context: Context, private val list: ArrayList<GetAllSendReceivedRequestPojo.ReceivedRequest.Datum>, private val selectedType: Int) : RecyclerView.Adapter<GetAllSendReceivedAdapter.MyView>() {
    lateinit var itemView: View
    lateinit var mySharedPreference: MySharedPreference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetAllSendReceivedAdapter.MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.getall_sendreceived_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: GetAllSendReceivedAdapter.MyView, position: Int) {


        if (mySharedPreference.getString("role").equals("2")) {

            if (selectedType == 0) {
                if (list.get(position).status.toString().equals("0")) {
                    holder.rl_accept.visibility = View.VISIBLE
                    holder.rl_reject.visibility = View.VISIBLE
                    holder.txt_accepted.text = "Accept"
                    holder.txt_rejected.text = "Reject"
                } else if (list.get(position).status.toString().equals("1")) {
                    holder.rl_accept.visibility = View.VISIBLE
                    holder.rl_reject.visibility = View.GONE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                } else if (list.get(position).status.toString().equals("-1")) {
                    holder.rl_accept.visibility = View.GONE
                    holder.rl_reject.visibility = View.VISIBLE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                } else {
                    holder.rl_accept.visibility = View.INVISIBLE
                    holder.rl_reject.visibility = View.INVISIBLE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                }
            } else {
                if (list.get(position).status.toString().equals("0")) {
                    holder.rl_accept.visibility = View.VISIBLE
                    holder.rl_reject.visibility = View.GONE
                    holder.txt_accepted.text = "Pending"
                    holder.txt_rejected.text = "Reject"
                } else if (list.get(position).status.toString().equals("1")) {
                    holder.rl_accept.visibility = View.VISIBLE
                    holder.rl_reject.visibility = View.GONE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                } else if (list.get(position).status.toString().equals("-1")) {
                    holder.rl_accept.visibility = View.GONE
                    holder.rl_reject.visibility = View.VISIBLE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                } else {
                    holder.rl_accept.visibility = View.INVISIBLE
                    holder.rl_reject.visibility = View.INVISIBLE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                }
            }

        } else {
            if(selectedType==0){
                if (list.get(position).status.toString().equals("0")) {
                    //Commented on 1-July-2020 by Varinder
                    holder.rl_accept.visibility = View.VISIBLE
                    holder.rl_reject.visibility = View.GONE
                    holder.txt_accepted.text = "Pending"
                    holder.txt_rejected.text = "Reject"
                    /* holder.rl_accept.visibility = View.VISIBLE
                     holder.rl_reject.visibility = View.VISIBLE
                     holder.txt_accepted.text = "Accept"
                     holder.txt_rejected.text = "Reject"*/
                } else if (list.get(position).status.toString().equals("1")) {
                    holder.rl_accept.visibility = View.VISIBLE
                    holder.rl_reject.visibility = View.GONE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                } else if (list.get(position).status.toString().equals("-1")) {
                    holder.rl_accept.visibility = View.GONE
                    holder.rl_reject.visibility = View.VISIBLE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                } else {
                    holder.rl_accept.visibility = View.INVISIBLE
                    holder.rl_reject.visibility = View.INVISIBLE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                }
            }else{
                if (list.get(position).status.toString().equals("0")) {
                    //Commented on 1-July-2020 by Varinder
                   /* holder.rl_accept.visibility = View.VISIBLE
                    holder.rl_reject.visibility = View.GONE
                    holder.txt_accepted.text = "Pending"
                    holder.txt_rejected.text = "Reject"*/
                     holder.rl_accept.visibility = View.VISIBLE
                     holder.rl_reject.visibility = View.VISIBLE
                     holder.txt_accepted.text = "Accept"
                     holder.txt_rejected.text = "Reject"
                } else if (list.get(position).status.toString().equals("1")) {
                    holder.rl_accept.visibility = View.VISIBLE
                    holder.rl_reject.visibility = View.GONE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                } else if (list.get(position).status.toString().equals("-1")) {
                    holder.rl_accept.visibility = View.GONE
                    holder.rl_reject.visibility = View.VISIBLE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                } else {
                    holder.rl_accept.visibility = View.INVISIBLE
                    holder.rl_reject.visibility = View.INVISIBLE
                    holder.txt_accepted.text = "Accepted"
                    holder.txt_rejected.text = "Rejected"
                }
            }

        }

        holder.txt_name.text = list.get(position).user?.name + " " + list.get(position).user?.lastName
        var timeIs = DateUtils.Convert24to12(list.get(position).time)

        holder.textViewTime.text = list.get(position).bookingDate + " at " + timeIs
        if (mySharedPreference.getString("role").toString().equals("2")) {
            holder.txt_desc.text = list.get(position).user?.name + " " + "sent you request"
        } else {
            holder.txt_desc.text = list.get(position).user?.name + " " + "request send"
        }
        holder.rl_accept.setOnClickListener(View.OnClickListener {
            if (holder.txt_accepted.text.toString().equals("Accept", true)) {

                if (mySharedPreference.getString("role").equals("2")) {
                    Accept_rejct(Utils.BASE_URL + Utils.ACCEPT_REJECT, list.get(position).user?.id.toString(), list.get(position).id.toString(), "1")

                } else {
                    Accept_rejct(Utils.BASE_URL + Utils.ACCEPT_REJECT_USER, list.get(position).user?.id.toString(), list.get(position).id.toString(), "1")

                }
            }

        })
        holder.rl_reject.setOnClickListener(View.OnClickListener {
            if (mySharedPreference.getString("role").equals("2")) {
                if (holder.txt_rejected.text.toString().equals("Reject", true)) {
                    Accept_rejct(Utils.BASE_URL + Utils.ACCEPT_REJECT, list.get(position).user.id.toString(), list.get(position).id.toString(), "-1")
                }
            } else {
                Accept_rejct(Utils.BASE_URL + Utils.ACCEPT_REJECT_USER, list.get(position).user.id.toString(), list.get(position).id.toString(), "-1")
            }

        })

        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list.get(position).user.profile_image).fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)


        /* holder.rl_accept.setOnClickListener(View.OnClickListener {
             Accept_rejct(list.get(position).user.id.toString(),list.get(position).id.toString(),"1" }
         holder.rl_reject.setOnClickListener(View.OnClickListener {
             Accept_rejct(list.get(position).user.id.toString(),list.get(position).id.toString(),"-1" }

     })*/
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var txt_accepted: CustomTextviewBold
        internal var txt_rejected: CustomTextviewBold
        internal var txt_name: CustomTextviewBold
        internal var txt_desc: CustomTextviewHelvic
        internal var rl_accept: RelativeLayout
        internal var rl_reject: RelativeLayout
        internal var img_profile: CircleImageView
        internal var textViewTime: TextView

        init {
            txt_name = view.findViewById<View>(R.id.txt_name) as CustomTextviewBold
            txt_accepted = view.findViewById<View>(R.id.txt_accepted) as CustomTextviewBold
            txt_rejected = view.findViewById<View>(R.id.txt_rejected) as CustomTextviewBold
            txt_desc = view.findViewById<View>(R.id.txt_desc) as CustomTextviewHelvic
            rl_accept = view.findViewById<View>(R.id.rl_accept) as RelativeLayout
            rl_reject = view.findViewById<View>(R.id.rl_reject) as RelativeLayout
            textViewTime = view.findViewById<View>(R.id.textViewTime) as TextView
            img_profile = view.findViewById(R.id.img)
            mySharedPreference = MySharedPreference(activity)

        }
    }

    private fun Accept_rejct(url: String, user_id: String, booking_id: String, status: String) {

        var paramUserOrLawyerID: String = if (mySharedPreference.getString("role") == "2") // Role=2=Lawyer, 1 = Admin, 3 = SME, 4 = Regular user
            "user_id"
        else
            "lawyer_id"

         Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(url)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter(paramUserOrLawyerID, user_id)
                .addBodyParameter("booking_id", booking_id)
                .addBodyParameter("status", status)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(DeletePojo::class.java, object : ParsedRequestListener<DeletePojo> {
                    override fun onResponse(user: DeletePojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            val fragment = GetAllSendReceivedFragment()
                            (itemView.context as FragmentActivity).supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.flContent, fragment).addToBackStack(null).commit()
                        } else {
                            Toast.makeText(activity, "Error in request", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

}
