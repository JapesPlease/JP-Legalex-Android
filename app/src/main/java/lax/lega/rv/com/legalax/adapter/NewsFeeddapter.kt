package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.EditNewsFeedFragment
import lax.lega.rv.com.legalax.fragment.GetCommentFragment
import lax.lega.rv.com.legalax.fragment.OpenNewsFeedFragment
import lax.lega.rv.com.legalax.pojos.DeleteNewsFeedPojo
import lax.lega.rv.com.legalax.pojos.GetNewsFeedPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError
import java.util.*


/**
 * Created by user on 12/5/18.
 */

class NewsFeeddapter(private val context: Context, private val list: ArrayList<GetNewsFeedPojo.Response>?, private val activity: Activity) : RecyclerView.Adapter<NewsFeeddapter.MyView>() {

    lateinit var itemView: View
    internal var mySharedPreference: MySharedPreference
    var connectionDetector: ConnectionDetector

    init {
        mySharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(context)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsFeeddapter.MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.newsfeed_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: NewsFeeddapter.MyView, position: Int) {
//        val spannableStringObject = SpannableString(list!![position].description)
//        spannableStringObject.setSpan(UnderlineSpan(), 0, spannableStringObject.length, 0)
        holder.txt_data.text = list!![position].title

        if (mySharedPreference.getString("role") == "1") {
            holder.img_edit.visibility = View.VISIBLE
            holder.img_comment.visibility = View.VISIBLE
            holder.img_delete.visibility = View.VISIBLE
        } else {
            holder.img_edit.visibility = View.INVISIBLE
            holder.img_comment.visibility = View.VISIBLE
            holder.img_delete.visibility = View.INVISIBLE
        }

        holder.ll_main.setOnClickListener {
            val response = list!![position]
            val args = Bundle()
            args.putSerializable("list", list)
            args.putInt("size", position)
            val fragobj = OpenNewsFeedFragment()
            fragobj.arguments = args
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragobj).addToBackStack(null).commit()
        }

        if (list[position].image == null || list[position].image == "")
        {
            holder.img_data.visibility=View.GONE
            holder.flVideo.visibility=View.VISIBLE
            Glide.with(context).load("http://server4.rvtechnologies.in/JP-Legalex-IOS_Backend/public/" + list[position].video_thumb).into(holder.ivThumbnail)
        //    val bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(Utils.BASE_URL_VIDEO+list[position].video, MediaStore.Video.Thumbnails.MINI_KIND)
        //    Picasso.with(context).load(Constant.NEWSFEEDURL + list[position].image).rotate(90f).into(holder.img_data)
        //    holder.ivThumbnail.setImageBitmap(bitmap)
        }else{
            holder.img_data.visibility=View.VISIBLE
            holder.flVideo.visibility=View.GONE
            Glide.with(context).load(Constant.NEWSFEEDURL + list[position].image).into(holder.img_data)
        }


        holder.img_edit.setOnClickListener {
            val response = list[position]
            val args = Bundle()
            args.putSerializable("list", list)
            args.putInt("size", position)
            val fragobj = EditNewsFeedFragment()
            fragobj.arguments = args
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragobj).addToBackStack(null).commit()
        }

        holder.img_delete.setOnClickListener {
            val connectionDetector = ConnectionDetector(activity.applicationContext)
            connectionDetector.isConnectingToInternet
            if (connectionDetector.isConnectingToInternet === false) run {
                Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {
                deleteNewsFeed(list!![position].id.toString(), position)
            }

        }

        holder.img_comment.setOnClickListener {
            val response = list[position]
            val args = Bundle()
            args.putSerializable("list", list)
            args.putInt("size", position)
            val fragobj = GetCommentFragment()
            fragobj.arguments = args

            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragobj).addToBackStack(null).commit()

        }
    }


    override fun getItemCount(): Int {
        return list!!.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var ll_main: LinearLayout
        internal var txt_data: TextView
        internal var img_data: ImageView
        internal var img_edit: ImageView
        internal var ivThumbnail: ImageView
        internal var img_comment: ImageView
        internal var img_delete: ImageView
        internal var flVideo: FrameLayout

        init {
            ll_main = view.findViewById<View>(R.id.ll_main) as LinearLayout
            txt_data = view.findViewById<View>(R.id.txt_data) as TextView
            img_data = view.findViewById<View>(R.id.img_data) as ImageView
            img_edit = view.findViewById<View>(R.id.img_edit) as ImageView
            ivThumbnail = view.findViewById<View>(R.id.ivThumbnail) as ImageView
            img_comment = view.findViewById<View>(R.id.img_comment) as ImageView
            img_delete = view.findViewById<View>(R.id.img_delete) as ImageView
            flVideo = view.findViewById<View>(R.id.flVideo) as FrameLayout
        }
    }

    fun deleteNewsFeed(id: String, pos: Int) {
         Utils.instance.showProgressBar(activity)
         AndroidNetworking.post(Utils.BASE_URL + Utils.DELETE_NEWSFEED)
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
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }
}
