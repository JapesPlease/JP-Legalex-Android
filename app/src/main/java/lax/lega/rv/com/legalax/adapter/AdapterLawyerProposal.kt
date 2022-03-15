package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_proposal_lawyer.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.activities.OpenChatActivity
import lax.lega.rv.com.legalax.activities.VideoCallActivity
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.interfaces.ClickProposalList
import lax.lega.rv.com.legalax.pojos.BidList
import lax.lega.rv.com.legalax.pojos.createSession.CreateSessionResponse
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getDate
import java.util.*

class AdapterLawyerProposal(var activity: Activity, var clickInterface: ClickProposalList, var isPending: Boolean, private var listBid: ArrayList<BidList>, var sharedPreference: MySharedPreference) : RecyclerView.Adapter<AdapterLawyerProposal.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.item_proposal_lawyer, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(listBid[position])
    }

    override fun getItemCount(): Int {
        return listBid.size
    }

    // inner class
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(bid: BidList) {

            if (isPending) {
                setPendingData(itemView, bid)
            } else {
                setApproveData(itemView, bid)
            }

            itemView.tvBid.setOnClickListener { clickInterface.onClickAccept(adapterPosition) }
            itemView.tvView.setOnClickListener { clickInterface.onClickView(adapterPosition) }
            itemView.ivVideoCall.setOnClickListener {

               // if (bid.info?.userInfo?.status == 1) {

//                    if (bid.info?.userInfo?.points?:0>0){
                    createSessionToken(bid.info?.userInfo?.id.toString(), bid.info?.userInfo?.profileImage.toString(), bid.info?.userInfo?.name
                            ?: "", bid.info?.userInfo?.lastName
                            ?: "", itemView.context, sharedPreference)
//                    }else{
//                        Toast.makeText(itemView.context, "This user don't have credits", Toast.LENGTH_LONG).show()
//                    }

//                } else {
//                    Toast.makeText(itemView.context, "This user is not activated yet!", Toast.LENGTH_LONG).show()
//                }

            }

            itemView.ivMessage.setOnClickListener {

               // if (bid.info?.userInfo?.status == 1) {
                    val intent = Intent(itemView.context, OpenChatActivity::class.java)
                    intent.putExtra("id", bid.info?.userInfo?.id.toString())
                    intent.putExtra("image", bid.info?.userInfo?.profileImage)
                    intent.putExtra("groupChat", "1")
                    intent.putExtra("name", bid.info?.userInfo?.name + " " + bid.info?.userInfo?.lastName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    itemView.context.startActivity(intent)
//                } else {
//                    Toast.makeText(itemView.context, "This user is not activated yet!", Toast.LENGTH_LONG).show()
//                }

            }
        }

    }

    private fun setPendingData(itemView: View, bid: BidList) {
        itemView.ivMessage.visibility = View.GONE
        itemView.ivVideoCall.visibility = View.GONE
        itemView.tvBid.visibility = View.VISIBLE
        itemView.tvDate.text = bid.userInfo?.name + " " + bid.userInfo?.lastName + "  " + getDate(bid.createdAt
                ?: "")

        itemView.tvTitle.text = bid.title
        itemView.tvName.text = bid.userInfo?.name
        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + bid.userInfo?.profileImage.toString()).fit().placeholder(R.drawable.icn_user_large).into(itemView.ivUser)


        if (bid.isBidPlaced == 1) {
            itemView.tvBid.visibility = View.GONE
            itemView.tvSubmit.visibility = View.VISIBLE

        } else {
            itemView.tvBid.visibility = View.VISIBLE
            itemView.tvSubmit.visibility = View.GONE
        }

    }

    private fun setApproveData(itemView: View, bid: BidList) {
        itemView.ivMessage.visibility = View.VISIBLE
        itemView.ivVideoCall.visibility = View.VISIBLE
        itemView.tvBid.visibility = View.GONE
        itemView.tvDate.text = "Date Submitted: " + getDate(bid.createdAt ?: "")
        itemView.tvTitle.text = bid.info?.title
        itemView.tvName.text = bid.info?.userInfo?.name
        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + bid.info?.userInfo?.profileImage.toString()).fit().placeholder(R.drawable.icn_user_large).into(itemView.ivUser)
    }

    fun createSessionToken(id: String, profileImage: String?, name: String, lastName: String, context: Context, sharedPreference: MySharedPreference) {
        val uuid = UUID.randomUUID().toString()
        Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.CREATE_SESSION_TOKEN)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("reciver_id", id)
                .addBodyParameter("reciver_phn", sharedPreference.getString("phone"))
                .addBodyParameter("reciver_image", sharedPreference.getString("profile_image"))
                .addBodyParameter("reciver_name", sharedPreference.getString("name") + " " + sharedPreference.getString("last_name"))
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
                            callScreen.putExtra("name", "$name $lastName")
                            callScreen.putExtra("sessionId", sessionResponse.sessionId)
                            callScreen.putExtra("token", sessionResponse.token)
                            callScreen.putExtra("callType", "outGoing")
                            callScreen.putExtra("isProposal", true)
                            callScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(callScreen)
                        } else {
                            if (sessionResponse.message.equals("user is offline")) {
                                showToast("The lawyer you are trying to contact is currently offline. They will respond once they log on. If urgent, you may try one of the online lawyers.", context)
                            }
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Log.e("Errroror", ">>>>>>>>>>>>>" + anError.errorBody.toString())
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server ", Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun showToast(s: String, context: Context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
}