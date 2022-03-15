package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.app.Dialog
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
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.daimajia.swipe.SwipeLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.activities.OpenChatActivity
import lax.lega.rv.com.legalax.activities.VideoCallActivity
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.CustomTextviewHelvic
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.UserAppointFragment
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.pojos.GetUsersPojo
import lax.lega.rv.com.legalax.pojos.createSession.CreateSessionResponse
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getVollyError
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AllVideoUserAdapter(
    private val activity: Activity,
    private val context: Context,
    private val list: ArrayList<GetUsersPojo.Users.Datum>,
    private val from: String?
) : RecyclerView.Adapter<AllVideoUserAdapter.MyView>() {
    internal lateinit var mySharedPreference: MySharedPreference

    lateinit var itemView: View
    lateinit var sharedPreference: MySharedPreference


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {

        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.alluser_video_list_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        sharedPreference = MySharedPreference(activity)

        if (mySharedPreference.getString("role").equals("1")) {
            if (!from.equals("call")) {
                holder.bottomwrapper.visibility = View.VISIBLE
                holder.swipe_layout.showMode = SwipeLayout.ShowMode.PullOut

                // Drag From Left
//            holder.swipe_layout.addDrag(SwipeLayout.DragEdge.Left, holder.swipe_layout.findViewById(R.id.bottom_wrapper1))

                // Drag From Right
                holder.swipe_layout.addDrag(
                    SwipeLayout.DragEdge.Right,
                    holder.swipe_layout.findViewById(R.id.bottom_wrapper)
                )


                // Handling different events when swiping
                holder.swipe_layout.addSwipeListener(object : SwipeLayout.SwipeListener {
                    override fun onClose(layout: SwipeLayout) {
                        //when the SurfaceView totally cover the BottomView.
                    }

                    override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {

                    }

                    override fun onStartOpen(layout: SwipeLayout) {

                    }

                    override fun onOpen(layout: SwipeLayout) {
                        //when the BoadvantagesttomView totally show.
                    }

                    override fun onStartClose(layout: SwipeLayout) {

                    }

                    override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {
                        //when user's hand released.
                    }
                })
                Log.e("Status", ">>>>>>>>>>>>>>>" + list.get(position).status);
                when (list[position].status) {
                    0 -> {
                        holder.tv_activate.text = "Activate"
                        holder.tv_activate.tag = "1"
                        holder.itemView.setPadding(10, 0, 0, 0)
                        holder.rl_chat.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.fade_grey
                            )
                        )
                        holder.swipe_layout.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.fade_grey
                            )
                        )

                    }
                    1 -> {
                        holder.tv_activate.text = "Deactivate"
                        holder.tv_activate.tag = "0"
                        holder.swipe_layout.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.MYWhite
                            )
                        )
                        holder.rl_chat.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.green_300
                            )
                        )

                    }
                    else -> {


                    }
                }




                holder.tv_activate.setOnClickListener {
                    if (holder.tv_activate.tag == "1") {

                        Utils.instance.showProgressBar(activity)
                        ActivateThisUser(list, position, holder)
                    } else {

                        Utils.instance.showProgressBar(activity)
                        Deactivatethisuser(list, position, holder)
                    }

                }
                holder.tv_delete.setOnClickListener {
                    Utils.instance.showProgressBar(activity)

                    DeletethisUser(list, position, holder);
                }

            } else {
                holder.swipe_layout.isRightSwipeEnabled = false


                holder.bottomwrapper.visibility = View.GONE
            }

        } else {

            holder.swipe_layout.isRightSwipeEnabled = false


            holder.bottomwrapper.visibility = View.GONE
//            holder.swipe_layout.addSwipeListener(null)
//            holder.swipe_layout.visibility = View.GONE
        }


        holder.txt_name.text = list[position].name + " " + list[position].lastName
        holder.txt_email.text = list[position].location
        if (list[position].is_online == "0") {
            holder.iv_online.setImageResource(R.drawable.white_circle)

        } else {
            holder.iv_online.setImageResource(R.drawable.circle_shape)
        }


        if (from.equals("chat")) {
            holder.rl_chat.visibility = View.VISIBLE
            holder.rl_call.visibility = View.GONE
            holder.rl_book.visibility = View.GONE
        } else {
            holder.rl_chat.visibility = View.GONE
            // holder.rl_call.visibility = View.VISIBLE

            when {
                mySharedPreference.getString("role").equals("3") -> {
                    holder.rl_book.visibility = View.VISIBLE
                    holder.rl_call.visibility = View.GONE
                    holder.rl_call.isEnabled = false
                    holder.rl_book.isEnabled = true
                }
                mySharedPreference.getString("role").equals("4") -> {
                    holder.rl_book.visibility = View.VISIBLE
                    holder.rl_call.visibility = View.VISIBLE
                    holder.rl_call.isEnabled = true
                    holder.rl_book.isEnabled = true
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
                    holder.rl_book.isEnabled = false
                }
            }


        }
        holder.rl_call.setOnClickListener {


            if (list[position].status == 1) {

                val lastname = if (list[position].lastName != null) {
                    list[position].lastName.toString()
                } else {
                    ""
                }
                val profileImage = if (list[position].profile_image != null) {
                    list[position].profile_image.toString()
                } else {
                    ""
                }

                val phone = if (list[position].phone != null) {
                    list[position].phone.toString()
                } else {
                    ""
                }

                if (sharedPreference.getString("role").equals("4")) {
                    if (sharedPreference.getInt("points") > 0) {
                        createSessionToken(
                            list[position].id.toString(),
                            profileImage,
                            list[position].name.toString(),
                            lastname
                        )
                    } else {
                        SHowToast("You don't have credits")
                    }
                } else {
                    if (list[position].points > 0) {
                        createSessionToken(
                            list[position].id.toString(),
                            profileImage,
                            list[position].name.toString(),
                            lastname
                        )
                    } else {
                        SHowToast("This user does not have credits")
                    }
                }
            } else {
                if (sharedPreference.getString("role").equals("4")) {
                    Toast.makeText(context, "Lawyer is not yet verified", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "This user is not activated yet!", Toast.LENGTH_LONG)
                        .show();
                }
            }


            /* val callScreen = Intent(context, CallScreenActivity::class.java)
             callScreen.putExtra("id", list.get(position).id.toString())
             callScreen.putExtra("lastname", lastname)
             callScreen.putExtra("image", list.get(position).profile_image)
             callScreen.putExtra("name", list.get(position).name.toString())
             callScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             context.startActivity(callScreen)*/

            /*val intent = Intent(context, SenderCallActivityAnother::class.java)
            intent.putExtra("id", list[position].id.toString())
            intent.putExtra("from", "")
            intent.putExtra("name", list[position].name.toString())
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)*/
        }

        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list.get(position).profile_image)
            .fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)

        holder.rl_chat.setOnClickListener(View.OnClickListener {

            if (list[position].status == 1) {
                val intent = Intent(context, OpenChatActivity::class.java)
                intent.putExtra("id", list[position].id.toString())
                intent.putExtra("image", list[position].profile_image)
                intent.putExtra("name", list[position].name + " " + list[position].lastName)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "This user is not activated yet!", Toast.LENGTH_LONG)
                    .show();
            }


        })
        holder.ll_profile.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            bundle.putString("image", list[position].profile_image)
            fragment.arguments = bundle
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment).addToBackStack(null).commit()
        }


        holder.rl_book.setOnClickListener(View.OnClickListener {
            if (list[position].status == 1) {
                if (list[position].videoCallStatus == 0) {
                    val dialog = Dialog(context)
                    dialog.setContentView(R.layout.contact_us_dialog)
                    val textView = dialog.findViewById<TextView>(R.id.tv_ok)
                    val tvConfirm = dialog.findViewById<TextView>(R.id.tvConfirm)
                    val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
                    val llBook = dialog.findViewById<LinearLayout>(R.id.llBook)
                    textView.visibility = View.GONE
                    llBook.visibility = View.VISIBLE
                    val tvPopupMsg = dialog.findViewById<TextView>(R.id.tvPopupMsg)
                    tvPopupMsg.text = context.resources.getText(R.string.book_click)
                    tvConfirm.setOnClickListener {
                        dialog.dismiss()
                        val fragment = UserAppointFragment()
                        val bundle = Bundle()
                        bundle.putInt("lawyer_id", list[position].id)
                        bundle.putString("image", list[position].profile_image)
                        bundle.putString(
                            "email",
                            list[position].name + " " + list[position].lastName
                        )
                        fragment.arguments = bundle
                        (itemView.context as FragmentActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.flContent, fragment).addToBackStack(null).commit()
                    }
                    tvCancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                } else {
                    val fragment = UserAppointFragment()
                    val bundle = Bundle()
                    bundle.putInt("lawyer_id", list[position].id)
                    bundle.putString("image", list[position].profile_image)
                    bundle.putString("email", list[position].name + " " + list[position].lastName)
                    fragment.arguments = bundle
                    (itemView.context as FragmentActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.flContent, fragment).addToBackStack(null).commit()
                }

            } else {
                Toast.makeText(context, "Lawyer is not yet verified", Toast.LENGTH_LONG).show();
            }
        })
        /* holder.rl_chat.setOnClickListener(View)
         {

         }
         holder.rl_book.setOnClickListener
         {
             val fragment = UserAppointFragment()
             val bundle = Bundle()
             bundle.putInt("lawyer_id", list[position].id)
             bundle.putString("email", list[position].name + " " + list.get(position).lastName)
             fragment.setArguments(bundle)
             (itemView.context as FragmentActivity).supportFragmentManager
                     .beginTransaction()
                     .replace(R.id.flContent, fragment).addToBackStack(null).commit()
         }
     */
    }

    private fun DeletethisUser(
        list: ArrayList<GetUsersPojo.Users.Datum>,
        position: Int,
        holder: MyView
    ) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Method.POST, Constant.DELETE,
            Response.Listener<String> { response ->
                Utils.instance.dismissProgressDialog()

                Log.e("Response", ">>>>>>>>>>>$response");

                try {
                    val obj = JSONObject(response)
                    val check = obj.getString("success")
                    if (check == "true") {
                        list.removeAt(position);
                        notifyItemRemoved(position)
                        SHowToast("User Deleted Successfully")
                        notifyDataSetChanged()
                    } else {

                        SHowToast("Failed To Delete User")
                    }
                    Log.e("Response", ">>>>>>>>>>>$response");
                } catch (e: JSONException) {
                    Utils.instance.dismissProgressDialog()

                    Log.e("stacktrace", ">>>>>>>>>>>>>$e")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError ->
                Utils.instance.dismissProgressDialog()
                getVollyError(context as Activity, volleyError)
                Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
            }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["delete_type"] = "d_user"
                params["user_id"] = list[position].id.toString()
                Log.e("Parentid", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + list[position].id.toString())
                return params
            }

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer " + sharedPreference.getString("access_token")
                return headers
            }

        }
//        queue.getCache().clear();

        queue.add(stringRequest)


    }

    private fun ActivateThisUser(
        list: ArrayList<GetUsersPojo.Users.Datum>,
        position: Int,
        holder: MyView
    ) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest =
            object : StringRequest(com.android.volley.Request.Method.POST, Constant.DELETE,
                Response.Listener<String> { response ->

                    Utils.instance.dismissProgressDialog()

                    Log.e("Response", ">>>>>>>>>>>$response");

                    try {
                        val obj = JSONObject(response)
                        val check = obj.getString("success")
                        if (check == "true") {
                            list[position].status = 1;
                            SHowToast("User Activated Successfully")
                            notifyDataSetChanged()
                            notifyItemChanged(position)
                        } else {

                            SHowToast("Failed To Delete User")
                        }
                        Log.e("Response", ">>>>>>>>>>>$response");
                    } catch (e: JSONException) {
//                          Utils.instance.dismissProgressDialog()

                        Log.e("stacktrace", ">>>>>>>>>>>>>$e")
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { volleyError ->
                    Utils.instance.dismissProgressDialog()
                    SHowToast("InError")
                    getVollyError(context as Activity, volleyError)
                    Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                }) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["delete_type"] = "activate"
                    params["user_id"] = list[position].id.toString()
                    Log.e(
                        "Parentid",
                        ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + list.get(position).id.toString()
                    )
                    return params
                }

                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] =
                        "Bearer " + sharedPreference.getString("access_token")
                    return headers
                }

            }
        queue.add(stringRequest)

    }

    private fun SHowToast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    private fun Deactivatethisuser(
        list: ArrayList<GetUsersPojo.Users.Datum>,
        position: Int,
        holder: MyView
    ) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest =
            object : StringRequest(com.android.volley.Request.Method.POST, Constant.DELETE,
                Response.Listener<String> { response ->
                    Utils.instance.dismissProgressDialog()

                    Log.e("Response", ">>>>>>>>>>>$response");

                    try {
                        val obj = JSONObject(response)
                        val check = obj.getString("success")
                        if (check == "true") {
                            list[position].status = 0
                            SHowToast("User Deactivated Successfully")
                            notifyDataSetChanged()
                            notifyItemChanged(position)

                        } else {

                            SHowToast("Failed To Delete User")
                        }
                        Log.e("Response", ">>>>>>>>>>>$response");
                    } catch (e: JSONException) {
//                          Utils.instance.dismissProgressDialog()

                        Log.e("stacktrace", ">>>>>>>>>>>>>$e")
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { volleyError ->
                    Utils.instance.dismissProgressDialog()
                    getVollyError(context as Activity, volleyError)
                    Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                }) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["delete_type"] = "deactivate"
                    params["user_id"] = list[position].id.toString()
                    Log.e(
                        "Parentid",
                        ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + list[position].id.toString()
                    )
                    return params
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] =
                        "Bearer " + sharedPreference.getString("access_token")
                    headers["Accept"] = "application/json"
                    return headers
                }

            }
        queue.add(stringRequest)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var ll_profile: LinearLayout
        internal var rl_chat: RelativeLayout
        internal var rl_book: RelativeLayout
        internal var rl_call: RelativeLayout
        internal var txt_name: CustomTextviewBold
        internal var txt_email: CustomTextviewHelvic
        internal var img_profile: CircleImageView
        var swipe_layout: SwipeLayout
        var tv_activate: TextView
        var tv_delete: TextView
        var bottomwrapper: LinearLayout
        var iv_online: ImageView

        init {
            mySharedPreference = MySharedPreference(activity)
            ll_profile = view.findViewById<View>(R.id.ll_profile) as LinearLayout
            rl_book = view.findViewById<View>(R.id.rl_book) as RelativeLayout
            rl_chat = view.findViewById<View>(R.id.rl_chat) as RelativeLayout
            rl_call = view.findViewById<View>(R.id.rl_call) as RelativeLayout
            txt_name = view.findViewById<View>(R.id.txt_name) as CustomTextviewBold
            txt_email = view.findViewById<View>(R.id.txt_email) as CustomTextviewHelvic
            swipe_layout = view.findViewById<View>(R.id.swipe) as SwipeLayout
            tv_activate = view.findViewById<View>(R.id.tv_activate) as TextView
            tv_delete = view.findViewById<View>(R.id.tvDelete) as TextView
            bottomwrapper = view.findViewById<View>(R.id.bottom_wrapper) as LinearLayout
            iv_online = view.findViewById<View>(R.id.iv_online) as ImageView
            img_profile = view.findViewById(R.id.img)

        }
    }


    private fun createSessionToken(
        id: String,
        profileImage: String?,
        name: String,
        lastName: String
    ) {
        val uuid = UUID.randomUUID().toString()
        Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.CREATE_SESSION_TOKEN)
            .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .addBodyParameter("reciver_id", id)
            .addBodyParameter("reciver_phn", mySharedPreference.getString("phone"))
            .addBodyParameter("reciver_image", sharedPreference.getString("profile_image"))
            .addBodyParameter(
                "reciver_name",
                sharedPreference.getString("name") + " " + sharedPreference.getString("last_name")
            )
            .addBodyParameter("uuid", uuid)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObject(
                CreateSessionResponse::class.java,
                object : ParsedRequestListener<CreateSessionResponse> {
                    override fun onResponse(sessionResponse: CreateSessionResponse) {
                        Utils.instance.dismissProgressDialog()

                        if (sessionResponse.success) {
                            val callScreen = Intent(context, VideoCallActivity::class.java)
                            callScreen.putExtra("id", id)
                            //callScreen.putExtra("lastname", lastname)
                            callScreen.putExtra("image", profileImage)
                            callScreen.putExtra("name", "$name $lastName")
                            callScreen.putExtra("sessionId", sessionResponse.sessionId)
                            callScreen.putExtra("token", sessionResponse.token)
                            callScreen.putExtra("callType", "outGoing")
                            callScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(callScreen)
                        } else {
                            if (sessionResponse.message.equals("user is offline")) {
                                val dialog = Dialog(context)
                                dialog.setContentView(R.layout.contact_us_dialog)
                                val textView = dialog.findViewById<TextView>(R.id.tv_ok)
                                val tvPopupMsg = dialog.findViewById<TextView>(R.id.tvPopupMsg)
                                tvPopupMsg.text = "The lawyer you are trying to contact is currently offline. They will respond once they log on. If urgent, you may try one of the online lawyers."
                                textView.setOnClickListener { dialog.dismiss() }
                                dialog.show()
//                                SHowToast("The lawyer you are trying to contact is currently offline. They will respond once they log on. If urgent, you may try one of the online lawyers.")
                            }
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Log.e("Errroror", ">>>>>>>>>>>>>" + anError.errorBody.toString())
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server ", Toast.LENGTH_LONG)
                            .show()
                    }
                })
    }


}