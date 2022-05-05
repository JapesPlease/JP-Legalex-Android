package lax.lega.rv.com.legalax.adapter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.activities.OpenChatActivity
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.pojos.GetChatDataPojo
import lax.lega.rv.com.legalax.pojos.GetChatHistoryPojo

class InboxListAdapter(private val context: Context, private val list: ArrayList<GetChatHistoryPojo.Response.Users.Datum>) : RecyclerView.Adapter<InboxListAdapter.MyView>() {
    lateinit var itemView: View
    lateinit var mySharedPreference: MySharedPreference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxListAdapter.MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.inboxlist_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: InboxListAdapter.MyView, position: Int) {

        if (list[position].msg_count > 0) {
            holder.textViewBadge.setText(list[position].msg_count.toString());
            holder.textViewBadge.visibility = View.VISIBLE
        } else {
            holder.textViewBadge.visibility = View.GONE
        }


        if ((mySharedPreference.getString("role").equals("2"))) {
            if (list[position].groupchat.equals("1")) {
                holder.txt_name.text = list.get(position).userDetail.name + " " + list.get(position).lastName + " to " + list.get(position).name + " " + list.get(position).lastName
            } else {
                holder.txt_name.text = list.get(position).name + " " + list.get(position).lastName
            }

        } else {
            holder.txt_name.text = list.get(position).name + " " + list.get(position).lastName
        }


        //holder.txt_name.text = list.get(position).name + " " + list.get(position).lastName
        holder.txt_email.text = list.get(position).lastMessage

        if (list.get(position).msg_count > 0) {
            holder.textViewMessageCount.visibility = View.GONE
            holder.textViewMessageCount.setText(list.get(position).msg_count.toString())
        } else {
            holder.textViewMessageCount.visibility = View.GONE
        }


        if (list.get(position).isOnline == 0) {
            holder.iv_online.setImageResource(R.drawable.white_circle)


        } else {
            holder.iv_online.setImageResource(R.drawable.circle_shape)
        }
        holder.ll_profile.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            bundle.putString("image", list[position].profile_image)
            fragment.arguments = bundle
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit()
        }

        holder.ll_main.setOnClickListener {

            if (list.get(position).status == 1) {

                if ((mySharedPreference.getString("role").equals("2"))) {
                    if (list[position].groupchat.equals("1")) {
                        if (list[position].lawyer_no > 0) {
                            val intent = Intent(context, OpenChatActivity::class.java)
                            intent.putExtra("id", list[position].userDetail.id.toString())
                            intent.putExtra("image", list.get(position).profile_image)
                            if ((mySharedPreference.getString("role").equals("2"))) {
                                if (list[position].groupchat.equals("1")) {
                                    intent.putExtra("groupId", list[position].id.toString())
                                    intent.putExtra("id", list[position].userDetail.id.toString())
                                    intent.putExtra("groupChat", "2")
                                    intent.putExtra("name", list.get(position).userDetail.name + " " + list.get(position).lastName + " to " + list.get(position).name + " " + list.get(position).lastName)
                                } else {
                                    intent.putExtra("groupChat", "1")
                                    intent.putExtra("id", list[position].id.toString())
                                    intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                                }
                            } else {
                                if (list[position].groupchat.equals("1")) {
                                    intent.putExtra("groupId", list[position].id.toString())
                                    intent.putExtra("id", list[position].id.toString())
                                    intent.putExtra("groupChat", "2")
                                    intent.putExtra("name", list.get(position).userDetail.name + " " + list.get(position).lastName + " to " + list.get(position).name + " " + list.get(position).lastName)
                                } else {
                                    intent.putExtra("groupChat", "1")
                                    intent.putExtra("id", list[position].id.toString())
                                    intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                                }
                                intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                            }
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "No Lawyer in this group, Lawyers has been shifted to other group, either deleted.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val intent = Intent(context, OpenChatActivity::class.java)
                        intent.putExtra("id", list[position].id.toString())
                        intent.putExtra("image", list.get(position).profile_image)
                        intent.putExtra("groupChat", "1")
                        intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }

                } else if (mySharedPreference.getString("role").equals("4")) {
                    if (list[position].groupchat.equals("1")) {
                        if (list[position].lawyer_no > 0) {
                            val intent = Intent(context, OpenChatActivity::class.java)
                            intent.putExtra("id", list[position].id.toString())
                            intent.putExtra("image", list.get(position).profile_image)
                            intent.putExtra("groupChat", "2")
                            intent.putExtra("groupId", list[position].id.toString())
                            intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "No Lawyer in this group, Lawyers has been shifted to other group, either deleted.", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        val intent = Intent(context, OpenChatActivity::class.java)
                        intent.putExtra("id", list[position].id.toString())
                        intent.putExtra("image", list.get(position).profile_image)
                        intent.putExtra("groupChat", "1")
                        intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                        context.startActivity(intent)
                    }


                    /*val intent = Intent(context, OpenChatActivity::class.java)
                    intent.putExtra("id", list[position].id.toString())
                    intent.putExtra("image", list.get(position).profile_image)
                    intent.putExtra("groupChat", "1")
                    intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)*/
                } else {
                    val intent = Intent(context, OpenChatActivity::class.java)
                    intent.putExtra("id", list[position].id.toString())
                    intent.putExtra("image", list.get(position).profile_image)
                    intent.putExtra("groupChat", "1")
                    intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
                    context.startActivity(intent)
                }

                /*val intent = Intent(context, OpenChatActivity::class.java)
                intent.putExtra("id", list[position].id.toString())
                intent.putExtra("image", list.get(position).profile_image)

                intent.putExtra("name", list.get(position).name + " " + list.get(position).lastName)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)*/
            } else {
                Toast.makeText(context, "This User is not activated yet!", Toast.LENGTH_LONG).show()
            }

        }
        holder.ll_profile.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            bundle.putString("image", list.get(position).profile_image)
            fragment.arguments = bundle
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit()
        }


        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list.get(position).profile_image).fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var ll_main: LinearLayout
        internal var ll_profile: LinearLayout
        internal var txt_name: CustomTextviewBold
        internal var txt_email: CustomTextviewHelvic
        internal var textViewMessageCount: CustomTextviewHelvicNormal
        internal var img_profile: CircleImageView
        lateinit var iv_online: ImageView
        lateinit var textViewBadge: TextView


        init {
            ll_main = view.findViewById<View>(R.id.ll_main) as LinearLayout
            ll_profile = view.findViewById<View>(R.id.ll_profile) as LinearLayout
            txt_name = view.findViewById<View>(R.id.txt_name) as CustomTextviewBold
            txt_email = view.findViewById<View>(R.id.txt_email) as CustomTextviewHelvic
            textViewMessageCount = view.findViewById<View>(R.id.textViewMessageCount) as CustomTextviewHelvicNormal
            img_profile = view.findViewById(R.id.img)
            iv_online = view.findViewById(R.id.iv_online)
            textViewBadge = view.findViewById(R.id.textViewBadge)

            mySharedPreference = MySharedPreference(context)


        }
    }


}
