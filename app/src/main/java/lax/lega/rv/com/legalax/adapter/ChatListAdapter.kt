package lax.lega.rv.com.legalax.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.activities.OpenChatActivity
import lax.lega.rv.com.legalax.adapter.ChatListAdapter.MyView
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import lax.lega.rv.com.legalax.common.CustomTextviewHelvic
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.pojos.GetChatHistoryPojo
import lax.lega.rv.com.legalax.retrofit.Constant

class ChatListAdapter(private val context: Context, private val list: ArrayList<GetChatHistoryPojo.Response.Users.Datum>) : RecyclerView.Adapter<MyView>() {
    lateinit var itemView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.chatlist_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.txt_name.text = list.get(position).name + " " + list.get(position).lastName
        holder.txt_email.text = list.get(position).lastMessage


        if (list.get(position).isOnline == 0)
        {
            holder.img_enable.setImageResource(R.drawable.white_circle)
        }
        else {
            holder.img_enable.setImageResource(R.drawable.circle_shape)
        }
        holder.ll_main.setOnClickListener {
            if(list.get(position).status==1){

                if(list.get(position).groupchat.equals("1")){
                    val intent = Intent(context, OpenChatActivity::class.java)
                    intent.putExtra("id", list[position].id.toString())
                    intent.putExtra("image", list.get(position).profile_image)
                    intent.putExtra("groupChat", "2")
                    intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }else{
                    val intent = Intent(context, OpenChatActivity::class.java)
                    intent.putExtra("id", list[position].id.toString())
                    intent.putExtra("image", list.get(position).profile_image)
                    intent.putExtra("groupChat", "1")
                    intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }




            }else{
                Toast.makeText(context,"This user is not activated yet!",Toast.LENGTH_LONG).show();
            }
        }

        holder.ll_profile.setOnClickListener {
            //                Intent intent = new Intent(context, ViewLawyerProfileFragment.class);
            //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //                context.startActivity(intent);
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            fragment.setArguments(bundle)
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit()
        }

        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list.get(position).profile_image).fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)


    }

    override fun getItemCount(): Int {
        return list!!.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var ll_main: LinearLayout
        internal var ll_profile: LinearLayout
        internal var txt_name: CustomTextviewBold
        internal var txt_email: CustomTextviewHelvic
        internal var img_enable: ImageView
        internal var img_profile: CircleImageView

        init {
            ll_main = view.findViewById<View>(R.id.ll_main) as LinearLayout
            txt_name = view.findViewById<View>(R.id.txt_name) as CustomTextviewBold
            txt_email = view.findViewById<View>(R.id.txt_email) as CustomTextviewHelvic
            ll_profile = view.findViewById<View>(R.id.ll_profile) as LinearLayout
            img_enable = view.findViewById<View>(R.id.img_enable) as ImageView
            img_profile = view.findViewById(R.id.img)

        }
    }


}
