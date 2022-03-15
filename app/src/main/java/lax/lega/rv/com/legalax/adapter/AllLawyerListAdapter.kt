package lax.lega.rv.com.legalax.adapter

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.CustomTextviewHelvic
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.pojos.GetLawyerPojo
import lax.lega.rv.com.legalax.retrofit.Constant

class AllLawyerListAdapter(private val list: ArrayList<GetLawyerPojo.Lawyer.Datum>) : RecyclerView.Adapter<AllLawyerListAdapter.ViewHolder>() {
    lateinit var itemView: View
    lateinit var clickListener: ClickListener
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_all_lawyer_list, null)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txt_name.text = list.get(position).name + " " + list.get(position).lastName
        holder.txt_email.text = list.get(position).location

        if (list.get(position).selectedCount == null||list.get(position).selectedCount=="0") {
            holder.textViewCount.visibility = View.GONE
        } else {
            holder.textViewCount.visibility = View.VISIBLE
            holder.textViewCount.setText(list.get(position).selectedCount)
        }

        if (list.get(position).is_online == "0") {
            holder.ivonline.setImageResource(R.drawable.white_circle)

        } else {
            holder.ivonline.setImageResource(R.drawable.circle_shape)
        }
        if (list.get(position).status == 0) {
            holder.mainlayout.visibility = View.GONE
            holder.mainlayout.setLayoutParams(RecyclerView.LayoutParams(0, 0))
        }
        holder.ll_profile.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            bundle.putString("image", list.get(position).profile_image)

            fragment.setArguments(bundle)
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit()
        }
        holder.rl_view.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("image", list.get(position).profile_image)

            bundle.putString("lawyer_id", list[position].id.toString())
            fragment.setArguments(bundle)
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit()
        }
        holder.mainlayout.setOnClickListener {
            if (list.get(position).selectedCount == null) {
                clickListener.lawyerItemClick(position);
            }
        }

        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list.get(position).profile_image).fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var ll_profile: LinearLayout
        internal var txt_name: CustomTextviewBold
        internal var textViewCount: CustomTextviewBold
        internal var txt_email: CustomTextviewHelvic
        internal var rl_view: RelativeLayout
        internal var img_profile: CircleImageView
        internal var ivonline: ImageView
        internal var mainlayout: RelativeLayout

        init {
            ll_profile = view.findViewById<View>(R.id.ll_profile) as LinearLayout
            txt_name = view.findViewById<View>(R.id.txt_name) as CustomTextviewBold
            textViewCount = view.findViewById<View>(R.id.textViewCount) as CustomTextviewBold
            txt_email = view.findViewById<View>(R.id.txt_email) as CustomTextviewHelvic
            rl_view = view.findViewById<View>(R.id.rl_view) as RelativeLayout
            img_profile = view.findViewById(R.id.img);
            ivonline = view.findViewById(R.id.iv_online);
            mainlayout = view.findViewById<View>(R.id.mainview) as RelativeLayout


        }
    }


    fun clickHandler(listener: ClickListener) {
        clickListener = listener;
    }

    interface ClickListener {
        fun lawyerItemClick(position: Int);
    }
}