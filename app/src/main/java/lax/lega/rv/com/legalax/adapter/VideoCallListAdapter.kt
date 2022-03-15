package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.activities.VideoCallActivity
import lax.lega.rv.com.legalax.calling.CallScreenActivity
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.CustomTextviewHelvic
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.UserAppointFragment
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.pojos.GetCallHistoryPojo
import lax.lega.rv.com.legalax.pojos.createSession.CreateSessionResponse
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError
import java.util.*
import kotlin.collections.ArrayList

class VideoCallListAdapter(private val context: Context, private val activity: Activity, private val list: ArrayList<GetCallHistoryPojo.Users.Datum>) : RecyclerView.Adapter<VideoCallListAdapter.MyView>() {

    lateinit var itemView: View
    var listener: OnItemClickListener? = null
    internal var mySharedPreference: MySharedPreference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        itemView = LayoutInflater.from(parent.context).inflate(R.layout.videcalllist_item, parent, false)
        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {

        when {
            mySharedPreference.getString("role").equals("3") -> {
                holder.rl_book.visibility = View.VISIBLE
                holder.rl_call.visibility = View.GONE
                holder.rl_call.isEnabled = false
                holder.rl_book.isEnabled = true
            }
            mySharedPreference.getString("role").equals("4") -> {
                holder.rl_book.visibility = View.GONE
                holder.rl_call.visibility = View.VISIBLE
                holder.rl_call.isEnabled = true
                holder.rl_book.isEnabled = false
            }
            mySharedPreference.getString("role").equals("2") -> {
                holder.rl_call.visibility = View.VISIBLE
                holder.rl_book.visibility = View.GONE
                holder.rl_call.isEnabled = true
                holder.rl_book.isEnabled = false
            }
            else -> {
                holder.rl_call.visibility = View.VISIBLE
                holder.rl_book.visibility = View.GONE
                holder.rl_call.isEnabled = true
                holder.rl_book.isEnabled = true
            }
        }
        holder.rl_book.setOnClickListener {

            if (list[position].status == 1) {
                val fragment = UserAppointFragment()
                val bundle = Bundle()
                bundle.putInt("lawyer_id", list[position].id)
                bundle.putString("image", list[position].profile_image)
                bundle.putString("email", list[position].name + " " + list[position].lastName)
                fragment.arguments = bundle
                (itemView.context as FragmentActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit()
            } else {
                Toast.makeText(context, "This user is not activated yet!", Toast.LENGTH_LONG).show();
            }
        }

        if (list[position].status == 0) {
            holder.linearLayoutTransparent.visibility = View.VISIBLE
        } else {
            holder.linearLayoutTransparent.visibility = View.GONE
        }

        holder.ll_profile.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            bundle.putString("image", list[position].profile_image)
            fragment.arguments = bundle
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit()
        }

        holder.txt_name.text = list[position].name + " " + list[position].lastName
        if (list[position].is_online == "0") {
            holder.iv_online.setImageResource(R.drawable.white_circle)

        } else {
            holder.iv_online.setImageResource(R.drawable.circle_shape)

        }

        holder.rl_call.setOnClickListener {

            val phone: String

            val lastname: String = if (list[position].lastName != null) {
                list[position].lastName.toString()
            } else {
                ""
            }
            val profileImage: String = if (list[position].profile_image != null) {
                list[position].profile_image.toString()
            } else {
                ""
            }

            /* if (list.get(position).phone != null) {
                 phone = list.get(position).phone.toString()
             } else {
                 phone = ""
             }*/
            /*val callScreen = Intent(context, CallScreenActivity::class.java)
            callScreen.putExtra("id", list.get(position).id.toString())
            callScreen.putExtra("image", list.get(position).profile_image)
            Log.e("SendingImage", ">>>>>>>>>>>>>>>>" + list.get(position).profile_image)
            callScreen.putExtra("lastname", list.get(position).lastName.toString())
            callScreen.putExtra("name", list.get(position).name.toString())
            callScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(callScreen)*/
            if (list[position].status == 1) {
                if (mySharedPreference.getString("role").equals("4")) {
                    if (mySharedPreference.getInt("points") > 0) {
                        createSessionToken(list[position].id.toString(), profileImage, list[position].name.toString(), lastname)
                    } else {
                        Toast.makeText(context, "You don't have credits", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (list[position].points > 0) {
                        createSessionToken(list[position].id.toString(), profileImage, list[position].name.toString(), lastname);
                    } else {
                        Toast.makeText(context, "This user does not have credits", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                if (mySharedPreference.getString("role").equals("4")) {
                    Toast.makeText(context, "Lawyer is not yet verified", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "This user is not activated yet!", Toast.LENGTH_LONG).show();
                }
            }
//            if(list.get(position).points>0){
//                createSessionToken(list.get(position).id.toString(),profileImage,list.get(position).name.toString(),lastname);
//            }else{
//                Toast.makeText(context,"This user does not have credits",Toast.LENGTH_LONG).show();
//            }
        }


        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list[position].profile_image).fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)



        if (list[position].callByYou.equals("yes")) {
            holder.img_type.setImageResource(R.mipmap.diagonal_arrow)
            holder.txt_email.text = "Called"
        } else {
            holder.img_type.setImageResource(R.mipmap.diagonal_arrow_down)
            holder.txt_email.text = "Received"
        }


    }

    init {
        mySharedPreference = MySharedPreference(activity)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick()
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var rl_book: RelativeLayout
        internal var rl_call: RelativeLayout
        internal var ll_profile: LinearLayout
        internal var txt_name: CustomTextviewBold
        internal var txt_email: CustomTextviewHelvic
        internal var img_type: ImageView
        internal var img_profile: CircleImageView
        lateinit var iv_online: ImageView
        lateinit var linearLayoutTransparent: LinearLayout

        init {
            rl_book = view.findViewById<View>(R.id.rl_book) as RelativeLayout
            rl_call = view.findViewById<View>(R.id.rl_call) as RelativeLayout
            ll_profile = view.findViewById<View>(R.id.ll_profile) as LinearLayout
            txt_name = view.findViewById<View>(R.id.txt_name) as CustomTextviewBold
            txt_email = view.findViewById<View>(R.id.txt_email) as CustomTextviewHelvic
            img_type = view.findViewById<View>(R.id.img_type) as ImageView
            img_profile = view.findViewById(R.id.img);
            iv_online = view.findViewById(R.id.iv_online);
            linearLayoutTransparent = view.findViewById(R.id.linearLayoutTransparent);


        }

        fun bind(listener: OnItemClickListener) {
            rl_book.setOnClickListener {
                listener.onItemClick()
            }
        }
    }


    fun createSessionToken(id: String, profileImage: String?, name: String, lastName: String) {
        var uuid = UUID.randomUUID().toString()
         Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.CREATE_SESSION_TOKEN)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("reciver_id", id)
                .addBodyParameter("reciver_phn", mySharedPreference.getString("phone"))
                .addBodyParameter("reciver_image", mySharedPreference.getString("profile_image"))
                .addBodyParameter("reciver_name", mySharedPreference.getString("name") + " " + mySharedPreference.getString("last_name"))
                .addBodyParameter("uuid", uuid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObject(CreateSessionResponse::class.java, object : ParsedRequestListener<CreateSessionResponse> {
                    override fun onResponse(sessionResponse: CreateSessionResponse) {
                          Utils.instance.dismissProgressDialog()
                        if (sessionResponse.success) {
                            val callScreen = Intent(context, VideoCallActivity::class.java)
                            callScreen.putExtra("id", id)
                            //callScreen.putExtra("lastname", lastname)
                            callScreen.putExtra("image", profileImage)
                            callScreen.putExtra("name", name + " " + lastName)
                            callScreen.putExtra("sessionId", sessionResponse.sessionId)
                            callScreen.putExtra("token", sessionResponse.token)
                            callScreen.putExtra("callType", "outGoing")
                            callScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(callScreen)
                        } else {

                        }

                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Log.e("Errroror", ">>>>>>>>>>>>>" + anError.errorBody.toString())
                        Toast.makeText(activity, "Unable to connect server ", Toast.LENGTH_LONG).show()
                    }
                })
    }
}