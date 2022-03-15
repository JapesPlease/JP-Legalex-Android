package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.daimajia.swipe.SwipeLayout
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.activities.OpenChatActivity
import lax.lega.rv.com.legalax.calling.CallScreenActivity
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.CustomTextviewHelvic
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.UserAppointFragment
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.pojos.AddCreditPointsPojo
import lax.lega.rv.com.legalax.pojos.GetUsersPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getVollyError
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call


class AllUserAdapter(private val activity: Activity, private val context: Context,  list: ArrayList<GetUsersPojo.Users.Datum>, private val from: String?) : RecyclerView.Adapter<AllUserAdapter.MyView>() {
    internal lateinit var mySharedPreference: MySharedPreference
    var list: ArrayList<GetUsersPojo.Users.Datum>


    var option=""
    init {
        this.list=list
    }


    lateinit var itemView: View
    lateinit var sharedPreference: MySharedPreference
    var dialog: AlertDialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.alluser_list_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        sharedPreference = MySharedPreference(activity)

        if (mySharedPreference.getString("role").equals("1")) {
            if (!from.equals("call")) {
                holder.bottomwrapper.visibility = View.VISIBLE
                holder.bottomwrapper1.visibility = View.VISIBLE
                holder.swipe_layout.setShowMode(SwipeLayout.ShowMode.PullOut)
                holder.swipe_layout.addDrag(SwipeLayout.DragEdge.Left, holder.swipe_layout.findViewById(R.id.bottom_wrapper1))
//                // holder.swipe_layout.addDrag(SwipeLayout.DragEdge.Left, holder.swipe_layout.findViewById(R.id.bottom_wrapper1))
                holder.swipe_layout.addDrag(SwipeLayout.DragEdge.Right, holder.swipe_layout.findViewById(R.id.bottom_wrapper))


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
                if (list.get(position).status == 0) {
                    holder.tv_activate.setText("Activate")
                    holder.tv_activate.setTag("1")
                    holder.itemView.setPadding(10, 0, 0, 0)
                    holder.rl_chat.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.fade_grey))
                    holder.swipe_layout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.fade_grey))


                } else if (list.get(position).status == 1) {
                    holder.tv_activate.setText("Deactivate")
                    holder.tv_activate.setTag("0")
                    holder.swipe_layout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.MYWhite))
                    holder.rl_chat.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green_300))

                } else {


                }




                holder.tv_activate.setOnClickListener {
                     Utils.instance.showProgressBar(activity)
                    if (holder.tv_activate.getTag() == "1") {
                        ActivateThisUser(list, position, holder)
                    } else {
                        Deactivatethisuser(list, position, holder)
                    }

                }

                holder.tv_add_credits.setOnClickListener {
                    dialogForAddCredit(list.get(position).id)
                    holder.swipe_layout.close()
                }





                holder.tv_delete.setOnClickListener {
                     Utils.instance.showProgressBar(activity)

                    DeletethisUser(list, position, holder);
                }

            } else {
                holder.swipe_layout.isRightSwipeEnabled = false
                holder.swipe_layout.isLeftSwipeEnabled = false


                holder.bottomwrapper.visibility = View.GONE
            }
        } else {
            holder.swipe_layout.isRightSwipeEnabled = false
            holder.swipe_layout.isLeftSwipeEnabled = false
            holder.bottomwrapper.visibility = View.GONE
//            holder.swipe_layout.addSwipeListener(null)
//            holder.swipe_layout.visibility = View.GONE
        }


        holder.txt_name.text = list.get(position).name + " " + list.get(position).lastName
        holder.txt_email.text = list.get(position).location
        if (list.get(position).is_online == "0") {
            holder.iv_online.setImageResource(R.drawable.white_circle)

        } else {
            holder.iv_online.setImageResource(R.drawable.circle_shape)
        }


        if(from.equals("appointment")){
            holder.rl_book.visibility = View.VISIBLE
            holder.rl_chat.visibility = View.GONE
            holder.rl_call.visibility = View.GONE

        }
        else if (from.equals("chat")) {
            holder.rl_chat.visibility = View.VISIBLE
            holder.rl_call.visibility = View.GONE
            holder.rl_book.visibility = View.GONE
        } else {
            holder.rl_chat.visibility = View.GONE
            // holder.rl_call.visibility = View.VISIBLE

            if (mySharedPreference.getString("role").equals("3")) {
                holder.rl_book.visibility = View.VISIBLE
                holder.rl_call.visibility = View.GONE
                holder.rl_call.isEnabled = false
                holder.rl_book.isEnabled = true
            } else if (mySharedPreference.getString("role").equals("4")) {
                holder.rl_book.visibility = View.VISIBLE
                holder.rl_call.visibility = View.GONE
                holder.rl_call.isEnabled = false
                holder.rl_book.isEnabled = true
            } else if (mySharedPreference.getString("role").equals("2")) {
                holder.rl_call.visibility = View.VISIBLE
                holder.rl_book.visibility = View.GONE
                holder.rl_call.isEnabled = true
                holder.rl_book.isEnabled = false
            } else {
                holder.rl_call.visibility = View.VISIBLE
                holder.rl_book.visibility = View.GONE
                holder.rl_call.isEnabled = true
                holder.rl_book.isEnabled = false
            }


        }
        holder.ll_profile.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            bundle.putString("image", list[position].profile_image)
            fragment.arguments = bundle
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit()
        }
        holder.rl_call.setOnClickListener {

            if(list.get(position).status==1){
                val lastname: String
                if (list.get(position).lastName != null) {
                    lastname = list.get(position).lastName.toString()
                } else {
                    lastname = ""
                }
                val callScreen = Intent(context, CallScreenActivity::class.java)
                callScreen.putExtra("id", list.get(position).id.toString())
                callScreen.putExtra("lastname", lastname)
                callScreen.putExtra("image", list.get(position).profile_image)
                callScreen.putExtra("name", list.get(position).name.toString())
                callScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(callScreen)
            }else{
                Toast.makeText(context,context.getString(R.string.user_not_activated_yet),Toast.LENGTH_LONG).show();
            }

            /*val intent = Intent(context, SenderCallActivityAnother::class.java)
            intent.putExtra("id", list[position].id.toString())
            intent.putExtra("from", "")
            intent.putExtra("name", list[position].name.toString())
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)*/
        }

        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list.get(position).profile_image).fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)

        holder.rl_chat.setOnClickListener(View.OnClickListener {
            if(list.get(position).status==1){
                if(option=="2"){
                    val intent = Intent(context, OpenChatActivity::class.java)
                    intent.putExtra("groupId", list[position].id.toString())
                    intent.putExtra("id", list[position].id.toString())
                    intent.putExtra("image", list.get(position).profile_image)
                    intent.putExtra("groupChat", option)
                    intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }else{
                    val intent = Intent(context, OpenChatActivity::class.java)
                    intent.putExtra("id", list[position].id.toString())
                    intent.putExtra("image", list.get(position).profile_image)
                    intent.putExtra("groupChat", option)
                    intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }

            }else{
                if(sharedPreference.getString("role").equals("4")){
                    Toast.makeText(context, "Lawyer is not yet verified", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "This user is not activated yet!", Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(context, "Lawyer is not yet verified", Toast.LENGTH_LONG).show();
                //Toast.makeText(context,context.getString(R.string.user_not_activated_yet),Toast.LENGTH_LONG).show();
            }

        })

        holder.rl_book.setOnClickListener(View.OnClickListener {
            val fragment = UserAppointFragment()
            val bundle = Bundle()
            bundle.putInt("lawyer_id", list[position].id)
            bundle.putString("image", list.get(position).profile_image)
            bundle.putString("email", list[position].name + " " + list.get(position).lastName)
            fragment.arguments = bundle
            (itemView.context as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flContent, fragment).addToBackStack(null).commit()
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

    private fun DeletethisUser(list: ArrayList<GetUsersPojo.Users.Datum>, position: Int, holder: AllUserAdapter.MyView) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, Constant.DELETE,
                Response.Listener<String> { response ->
                      Utils.instance.dismissProgressDialog()

                    Log.e("Response", ">>>>>>>>>>>" + response.toString());

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
                        Log.e("Response", ">>>>>>>>>>>" + response);
                    } catch (e: JSONException) {
                          Utils.instance.dismissProgressDialog()

                        Log.e("stacktrace", ">>>>>>>>>>>>>" + e.toString())
                        e.printStackTrace()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(volleyError: VolleyError) {
                        Log.e("Response", ">>>>>>>>>>>" + Gson().toJson(volleyError));

                        Utils.instance.dismissProgressDialog()
                        getVollyError(context as Activity,volleyError)
                        Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                    }
                }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("delete_type", "d_user")
                params.put("user_id", list.get(position).id.toString())
                Log.e("Parentid", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + list.get(position).id.toString())
                return params
            }

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Accept", "application/json")
                headers.put("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                Log.e("TOKEN", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sharedPreference.getString("access_token"))

                return headers
            }

        }
//        queue.getCache().clear();

        queue.add(stringRequest)


    }

    private fun ActivateThisUser(list: ArrayList<GetUsersPojo.Users.Datum>, position: Int, holder: AllUserAdapter.MyView) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, Constant.DELETE,
                Response.Listener<String> { response ->
                      Utils.instance.dismissProgressDialog()

                    Log.e("Response", ">>>>>>>>>>>" + response.toString());

                    try {
                        val obj = JSONObject(response)
                        val check = obj.getString("success")
                        if (check == "true") {
                            list.get(position).status = 1;
                            SHowToast("User Activated Successfully")
                            notifyDataSetChanged()
                            notifyItemChanged(position)
                        } else {

                            SHowToast("Failed To Delete User")
                        }
                        Log.e("Response", ">>>>>>>>>>>" + response);
                    } catch (e: JSONException) {
//                          Utils.instance.dismissProgressDialog()
                        Log.e("stacktrace", ">>>>>>>>>>>>>" + e.toString())
                        e.printStackTrace()
                    }
                },

                object : Response.ErrorListener {

                    override fun onErrorResponse(volleyError: VolleyError) {
                          Utils.instance.dismissProgressDialog()
                        SHowToast("InError")

                        getVollyError(context as Activity,volleyError)
                        Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                    }
                }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("delete_type", "activate")
                params.put("user_id", list.get(position).id.toString())
                Log.e("Parentid", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + list.get(position).id.toString())
                return params
            }

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Accept", "application/json")
                headers.put("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                return headers
            }

        }
        queue.add(stringRequest)

    }

    private fun SHowToast(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()


    }

    private fun Deactivatethisuser(list: ArrayList<GetUsersPojo.Users.Datum>, position: Int, holder: AllUserAdapter.MyView) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(com.android.volley.Request.Method.POST, Constant.DELETE,
                Response.Listener<String> { response ->
                      Utils.instance.dismissProgressDialog()

                    Log.e("Response", ">>>>>>>>>>>" + response.toString());

                    try {
                        val obj = JSONObject(response)
                        val check = obj.getString("success")
                        if (check == "true") {
                            list.get(position).status = 0
                            SHowToast("User Deactivated Successfully")
                            notifyDataSetChanged()
                            notifyItemChanged(position)

                        } else {

                            SHowToast("Failed To Delete User")
                        }
                        Log.e("Response", ">>>>>>>>>>>" + response);
                    } catch (e: JSONException) {
                          Utils.instance.dismissProgressDialog()
                        Log.e("stacktrace", ">>>>>>>>>>>>>" + e.toString())
                        e.printStackTrace()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(volleyError: VolleyError) {
                          Utils.instance.dismissProgressDialog()

                        getVollyError(context as Activity,volleyError)
                        Toast.makeText(context, volleyError.message, Toast.LENGTH_LONG).show()
                    }
                }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("delete_type", "deactivate")
                params.put("user_id", list.get(position).id.toString())
                Log.e("Parentid", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + list.get(position).id.toString())
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                headers.put("Accept", "application/json")
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
        lateinit var swipe_layout: SwipeLayout
        lateinit var tv_activate: TextView
        lateinit var tv_add_credits: TextView
        lateinit var tv_delete: TextView
        lateinit var bottomwrapper: LinearLayout
        lateinit var bottomwrapper1: LinearLayout
        lateinit var iv_online: ImageView

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
            tv_add_credits = view.findViewById<View>(R.id.tv_add_credits) as TextView
            tv_delete = view.findViewById<View>(R.id.tvDelete) as TextView
            bottomwrapper = view.findViewById<View>(R.id.bottom_wrapper) as LinearLayout
            bottomwrapper1 = view.findViewById<View>(R.id.bottom_wrapper) as LinearLayout
            iv_online = view.findViewById<View>(R.id.iv_online) as ImageView

            img_profile = view.findViewById(R.id.img)

        }
    }




    fun dialogForAddCredit(id: Int) {
        val dialogBuilder =  AlertDialog.Builder(context!!)
        val view = LayoutInflater.from(context).inflate(R.layout.view_add_credits, null)
        var editTextCredits=view.findViewById<EditText>(R.id.editTextCredits)
        var btn_done=view.findViewById<Button>(R.id.btn_done)

        btn_done.setOnClickListener {
            Utils.hideKeyboard(context as Activity);
            if(editTextCredits.text.trim().isEmpty()){
                Toast.makeText(context,context.getString(R.string.empty_credits),Toast.LENGTH_LONG).show()
            }else{
                dialog!!.dismiss()
                addCreditsApi(editTextCredits.text.toString(),id);
            }
        }

        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(true)
        dialog = dialogBuilder.show()
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun addCreditsApi(points: String, id: Int) {
         Utils.instance.showProgressBar(activity)
        val add_credit = WebService().apiService.add_credit(
                "Bearer " + sharedPreference.getString("access_token"),
                "application/json",
                points,
                id.toLong(),
                "1"
        )

        add_credit.enqueue(object : retrofit2.Callback<AddCreditPointsPojo> {
            override fun onResponse(call: Call<AddCreditPointsPojo>, response: retrofit2.Response<AddCreditPointsPojo>) {
                  Utils.instance.dismissProgressDialog()
                Utils.hideKeyboard(context as Activity);

                if (response.code()==401){
                    openLogoutPopup(context)
                }else{
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Credits Added", Toast.LENGTH_LONG).show()
                    }
                }

            }

            override fun onFailure(call: Call<AddCreditPointsPojo>, t: Throwable) {
                  Utils.instance.dismissProgressDialog()
              //  Log.e("AddCreditPointsOnError", t.message)
            }

        })
    }


    fun customNotifyChange(list: ArrayList<GetUsersPojo.Users.Datum>, option: String, from: String?){
        this.list=list
        this.option=option
        notifyDataSetChanged()
    }
}

