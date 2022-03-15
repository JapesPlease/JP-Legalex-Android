package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.pojos.DeletePojo
import lax.lega.rv.com.legalax.pojos.GetAllCommentPojo
import lax.lega.rv.com.legalax.utils.getAnError
import java.util.*


class GetAllCommentAdapter(private val context: Context, private val data: ArrayList<GetAllCommentPojo.Comments.Datum>, private val activity: Activity) : RecyclerView.Adapter<GetAllCommentAdapter.MyView>() {

    lateinit var itemView: View
    lateinit var mySharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetAllCommentAdapter.MyView {
        itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_get_all_comment, parent, false)
        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: GetAllCommentAdapter.MyView, position: Int) {
        if (data[position].userId.equals(MySharedPreference(activity).getInt("id"))) {
            holder.img_delete.visibility = View.VISIBLE
        } else {
            holder.img_delete.visibility = View.INVISIBLE
        }

        holder.img_delete.setOnClickListener {
            val connectionDetector = ConnectionDetector(activity!!.applicationContext)
            connectionDetector.isConnectingToInternet
            if (connectionDetector.isConnectingToInternet === false) run {
                Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {
                deleteNewsFeed(data.get(position).id.toString(), position)
            }  /* if (!connectionDetector.isConnectingToInternet()) {
                val alert = android.support.v7.app.AlertDialog.Builder(context)

                alert.setTitle("Internet connection unavailable.")
                alert.setMessage("You must be connected to an internet connection via WI-FI or Mobile Connection.")
                alert.setPositiveButton("OK") { dialog, whichButton -> context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }

                alert.show()
            } else {

            }*/
        }
        holder.tv_UserName.text = "" + data[position].fullName
        holder.comment.text = "" + data[position].comment

    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var tv_UserName: CustomTextviewBold
        internal var comment: CustomTextviewHelvicNormal
        internal var img_delete: ImageView

        init {
            tv_UserName = view.findViewById(R.id.tv_UserName)
            comment = view.findViewById(R.id.comment)
            img_delete = view.findViewById(R.id.img_delete)
            mySharedPreference = MySharedPreference(activity)
            connectionDetector = ConnectionDetector(context)
        }
    }

    fun deleteNewsFeed(comment_id: String, pos: Int) {
         Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.DELETE_COMMENT)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("comment_id", comment_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(DeletePojo::class.java, object : ParsedRequestListener<DeletePojo> {
                    override fun onResponse(user: DeletePojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            data!!.removeAt(pos)
                            notifyDataSetChanged()
                            Toast.makeText(activity, "Comment has been deleted successfully", Toast.LENGTH_LONG).show()

                        } else {
                            Toast.makeText(activity, "Comment has not been deleted successfully", Toast.LENGTH_LONG).show()

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
