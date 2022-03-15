package lax.lega.rv.com.legalax.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_rv_concern_list.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.fragment.ProposalFragment
import lax.lega.rv.com.legalax.interfaces.LegalConcernInterface
import lax.lega.rv.com.legalax.pojos.ProblemData
import lax.lega.rv.com.legalax.utils.getDate


class AdapterConcernList(var activity: Activity, var listLegalConcern: ArrayList<ProblemData>, var fragmentManager: FragmentManager?,var id: Int?,var listener:LegalConcernInterface) : RecyclerView.Adapter<AdapterConcernList.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.item_rv_concern_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(listLegalConcern[position])
    }

    override fun getItemCount(): Int {
        return listLegalConcern.size
    }

    // inner class
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItems(problemData: ProblemData) {

            itemView.tvTitle.text=problemData.title
            itemView.tvCategory.text="CATEGORY: "+problemData.cat_info?.name
            itemView.tvSubmitDate.text="Date submitted: "+ getDate(problemData.created_at.toString())

            if (id==problemData.id){
                itemView.cvMain.setBackgroundResource(R.drawable.bg_green_border)
            }else{
                itemView.cvMain.setBackgroundColor(ContextCompat.getColor(activity,R.color.grey_200))
            }

            itemView.tvView.setOnClickListener {
                val fragmentProposal=ProposalFragment()
                val bundle=Bundle()
                bundle.putString("id",listLegalConcern[adapterPosition].id.toString())
                fragmentProposal.arguments=bundle
                fragmentManager?.beginTransaction()?.replace(R.id.flContent,fragmentProposal)?.addToBackStack(null)?.commit()
            }

//            itemView.viewBackground.setOnClickListener {
//                val dialogDelete=askOption(adapterPosition)
//                dialogDelete?.show()
//            }
        }

    }

    fun updateList(list: ArrayList<ProblemData>) {
        listLegalConcern = list
        notifyDataSetChanged()
    }

//    @SuppressLint("SimpleDateFormat")
//    private fun getDate(dateStr:String):String{
//
//        val simpleDateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date=simpleDateFormat.parse(dateStr)
//      //  val dayOfTheWeek = DateFormat.format("EEE", date) as String // Thursday
//        val day = DateFormat.format("dd", date) as String // 20
//        val monthString = DateFormat.format("MMM", date) as String // Jun
//    //    var monthNumber = DateFormat.format("MM", date) as String // 06
//        val year = DateFormat.format("yyyy", date) as String // 2013
//
//        return "$monthString $day,$year"
//
//    }
    private fun askOption(position: Int): AlertDialog? {
        return AlertDialog.Builder(activity, R.style.AlertDialogTheme) // set message, title, and icon
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.delete)
                .setPositiveButton("Delete") { dialog, _ -> //your deleting code
                    dialog.dismiss()
                    listener.onDelete(position)
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
    }
}