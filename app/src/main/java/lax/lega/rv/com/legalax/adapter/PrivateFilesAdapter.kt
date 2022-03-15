package lax.lega.rv.com.legalax.adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.CustomTextviewHelvicNormal
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.pojos.ReceivedDocumentPojo
import lax.lega.rv.com.legalax.retrofit.Constant

class PrivateFilesAdapter(private val context: Context, private val list: ArrayList<ReceivedDocumentPojo.Documents.Datum>) : RecyclerView.Adapter<PrivateFilesAdapter.MyView>() {

    lateinit var itemView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateFilesAdapter.MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.private_files_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: PrivateFilesAdapter.MyView, position: Int) {
        holder.rl_profile.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            fragment.setArguments(bundle)
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit()
            //                Intent intent=new Intent(context, ViewLawyerProfileFragment.class);
            //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //                context.startActivity(intent);
        }
        if (list!!.get(position).document.title == null) {
            holder.txt_title.text = "No title available"

        } else {
            holder.txt_title.text = list!!.get(position).document.title.toString()
        }
        holder.txt_sendername.text = list!!.get(position).senderUser.name + " " + list!!.get(position).senderUser.lastName
        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list.get(position).senderUser.profile_image).fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var rl_profile: RelativeLayout
        internal var txt_title: CustomTextviewBold
        internal var txt_sendername: CustomTextviewHelvicNormal
        internal var img_profile: CircleImageView

        init {
            rl_profile = view.findViewById<View>(R.id.rl_profile) as RelativeLayout
            txt_title = view.findViewById<View>(R.id.txt_title) as CustomTextviewBold
            txt_sendername = view.findViewById<View>(R.id.txt_sendername) as CustomTextviewHelvicNormal

            img_profile = view.findViewById(R.id.img)
        }
    }


}
