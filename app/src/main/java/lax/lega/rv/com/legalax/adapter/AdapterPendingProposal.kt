package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
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
import kotlinx.android.synthetic.main.item_rv_pending_proposal.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.activities.OpenChatActivity
import lax.lega.rv.com.legalax.activities.PurchaseCreditsActivity
import lax.lega.rv.com.legalax.activities.VideoCallActivity
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.fragment.ViewLawyerProfileFragment
import lax.lega.rv.com.legalax.interfaces.ClickProposalList
import lax.lega.rv.com.legalax.pojos.ProposalList
import lax.lega.rv.com.legalax.pojos.createSession.CreateSessionResponse
import lax.lega.rv.com.legalax.utils.doubleToStringNoDecimal
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getDate
import java.util.*

class AdapterPendingProposal(
    var activity: Activity,
    var clickInterface: ClickProposalList,
    val isPending: Boolean,
    private var listProposal: ArrayList<ProposalList>,
    var sharedPreference: MySharedPreference
) : RecyclerView.Adapter<AdapterPendingProposal.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.item_rv_pending_proposal, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(listProposal[position])
    }

    override fun getItemCount(): Int {
        return listProposal.size
    }

    // inner class
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(proposalList: ProposalList) {

            Picasso.with(MainActivity.activity)
                .load(Utils.IMAGESURL + proposalList.lawyerInfo?.profileImage.toString()).fit()
                .placeholder(R.drawable.icn_user_large).into(itemView.ivUser)
            itemView.tvTitle.text = proposalList.info?.title
            ("P" + doubleToStringNoDecimal(
                proposalList.bid.toString().toDouble()
            )).also { itemView.tvPrice.text = it }

            //getDate(proposalList.lawyerInfo?.createdAt ?: "")

            if (isPending) {
                itemView.tvAccept.visibility = View.VISIBLE
                itemView.ivMessage.visibility = View.GONE
                itemView.ivVideoCall.visibility = View.VISIBLE
                itemView.tvPrice.visibility = View.VISIBLE
                itemView.tvDate.text =
                    proposalList.lawyerInfo?.name + " " + proposalList.lawyerInfo?.lastName + "  " + getDate(
                        proposalList.lawyerInfo?.createdAt ?: ""
                    )

            } else {
                itemView.tvDate.text = "Date Submitted: " + getDate(proposalList.createdAt ?: "")
                itemView.tvAccept.visibility = View.GONE
                itemView.ivMessage.visibility = View.VISIBLE
                itemView.ivVideoCall.visibility = View.VISIBLE
                itemView.tvPrice.visibility = View.GONE
            }


            itemView.tvView.setOnClickListener { clickInterface.onClickView(adapterPosition) }
            itemView.tvAccept.setOnClickListener { clickInterface.onClickAccept(adapterPosition) }

            itemView.ivVideoCall.setOnClickListener {
                val intent = Intent(
                    activity,
                    PurchaseCreditsActivity::class.java
                ).putExtra("proposal", true)
                    .putExtra("id", proposalList.lawyerInfo?.id.toString())
                    .putExtra("image", proposalList.lawyerInfo?.profileImage.toString() ?: "")
                    .putExtra(
                        "firstName", proposalList.lawyerInfo?.name
                            ?: ""
                    )
                    .putExtra("lastName", proposalList.lawyerInfo?.lastName ?: "")
                activity.startActivityForResult(intent, 101)
//                if (proposalList.lawyerInfo?.status == 1) {
//                if (sharedPreference.getInt("points") > 0) {
//                createSessionToken(
//                    proposalList.lawyerInfo?.id.toString(),
//                    proposalList.lawyerInfo?.profileImage.toString(),
//                    proposalList.lawyerInfo?.name
//                        ?: "",
//                    proposalList.lawyerInfo?.lastName
//                        ?: "",
//                    itemView.context,
//                    sharedPreference
//                )
//                } else {
//                    Toast.makeText(itemView.context, "This lawyer don't have credits", Toast.LENGTH_LONG).show()
//                }
//                }
//                else
//                    Toast.makeText(itemView.context, "This user is not activated yet!", Toast.LENGTH_LONG).show()


            }

            itemView.ivMessage.setOnClickListener {
//                if (proposalList.lawyerInfo?.status == 1) {
                val intent = Intent(itemView.context, OpenChatActivity::class.java)
                intent.putExtra("id", proposalList.lawyerInfo?.id.toString())
                intent.putExtra("image", proposalList.lawyerInfo?.profileImage.toString())
                intent.putExtra("groupChat", "1")
                intent.putExtra(
                    "name",
                    proposalList.lawyerInfo?.name + " " + proposalList.lawyerInfo?.lastName
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                itemView.context.startActivity(intent)
//                } else {
//                    Toast.makeText(itemView.context, "This Lawyer is not activated yet!", Toast.LENGTH_LONG).show()
//                }

            }
            itemView.ivUser.setOnClickListener {
                val fragment = ViewLawyerProfileFragment()
                val bundle = Bundle()
                bundle.putString("lawyer_id",proposalList.lawyerInfo?.id.toString())
                bundle.putString("image", proposalList.lawyerInfo?.profileImage.toString() ?: "")

                fragment.setArguments(bundle)
                (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit()
            }
        }

    }

    fun createSessionToken(
        id: String,
        profileImage: String?,
        name: String,
        lastName: String,
        context: Context,
        sharedPreference: MySharedPreference
    ) {
        val uuid = UUID.randomUUID().toString()
        Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.CREATE_SESSION_TOKEN)
            .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
            .addHeaders("Accept", "application/json")
            .addBodyParameter("reciver_id", id)
            .addBodyParameter("reciver_phn", sharedPreference.getString("phone"))
            .addBodyParameter("reciver_image", sharedPreference.getString("profile_image"))
            .addBodyParameter(
                "reciver_name",
                sharedPreference.getString("name") + " " + sharedPreference.getString("last_name")
            )
            .addBodyParameter("uuid", uuid)
            .setPriority(Priority.HIGH)
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
                            callScreen.putExtra("isProposal", true)
                            callScreen.putExtra("callType", "outGoing")
                            callScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(callScreen)
                        } else {
                            if (sessionResponse.message.equals("user is offline")) {
                                showToast(
                                    "The lawyer you are trying to contact is currently offline. They will respond once they log on. If urgent, you may try one of the online lawyers.",
                                    context
                                )
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

    private fun showToast(s: String, context: Context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }
}