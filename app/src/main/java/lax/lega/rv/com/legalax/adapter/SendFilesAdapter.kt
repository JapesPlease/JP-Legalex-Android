package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.fragment.DocumentFragment
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.pojos.GetUsersPojo
import lax.lega.rv.com.legalax.pojos.SendDocumentPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError

class SendFilesAdapter(private val context: Context, private val activity: Activity,
                       private val document_id: String?,
                       private val list: ArrayList<GetUsersPojo.Users.Datum>) : RecyclerView.Adapter<SendFilesAdapter.MyView>() {

    lateinit var itemView: View
    internal var mySharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SendFilesAdapter.MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.send_file_item, parent, false)

        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: SendFilesAdapter.MyView, position: Int) {
        holder.txt_name.text=list.get(position).name+" "+list.get(position).lastName
        holder.txt_email.text=list.get(position).email

        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + list.get(position).profile_image).fit().placeholder(R.drawable.icn_user_large).into(holder.img_profile)

        holder.ll_profile.setOnClickListener {
            val fragment = ViewLawyerProfileFragment()
            val bundle = Bundle()
            bundle.putString("lawyer_id", list[position].id.toString())
            fragment.setArguments(bundle)
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit()

        }

        holder.rl_sendFile.setOnClickListener(View.OnClickListener {
            val connectionDetector = ConnectionDetector(activity!!.applicationContext)
            connectionDetector.isConnectingToInternet
            if (connectionDetector.isConnectingToInternet === false) run {
                Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {
                if(list.get(position).status==1){
                    SendDocument(list!!.get(position).id.toString(), document_id , position)
                }else{
                    Toast.makeText(context,"This user is not activated yet!",Toast.LENGTH_LONG).show()
                }

            }
        })

    } fun SendDocument(receiver_id: String, document_id: String?, pos: Int) {
         Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.SHARE_DOCUMENT)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("receiver_id", receiver_id)
                .addBodyParameter("document_id", document_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(SendDocumentPojo::class.java, object : ParsedRequestListener<SendDocumentPojo> {
                    override fun onResponse(user: SendDocumentPojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "Document has been send successfully", Toast.LENGTH_LONG).show()
                            (itemView.context as FragmentActivity).supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.flContent, DocumentFragment(false)).addToBackStack(null).commit()
                        } else {
                            Toast.makeText(activity, "Document has not been send successfully", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    override fun getItemCount(): Int {
        return list!!.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var ll_profile: LinearLayout
        internal var rl_sendFile: RelativeLayout

        internal lateinit var txt_name: CustomTextviewBold
        internal lateinit var txt_email: CustomTextviewHelvic
        internal var img_profile: CircleImageView

        init {
            ll_profile = view.findViewById<View>(R.id.ll_profile) as LinearLayout
            rl_sendFile = view.findViewById<View>(R.id.rl_sendFile) as RelativeLayout
            txt_name = view.findViewById<View>(R.id.txt_name) as CustomTextviewBold
            txt_email = view.findViewById<View>(R.id.txt_email) as CustomTextviewHelvic
            img_profile = view.findViewById(R.id.img)

        }
    }


    init {
        mySharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(context)

    }
}
