package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_rv_request_document.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.fragment.LegalConcernFragment
import lax.lega.rv.com.legalax.pojos.ProblemData

class AdapterRequestDocument(var activity: Activity, var listRequestDocument: ArrayList<ProblemData>,var fragmentManager: FragmentManager?) : RecyclerView.Adapter<AdapterRequestDocument.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.item_rv_request_document, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(listRequestDocument[position])
    }

    override fun getItemCount(): Int {
        return listRequestDocument.size
    }

    // inner class
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(problemData: ProblemData) {
            itemView.tvTitle.text=problemData.title
            itemView.tvDescription.text=problemData.desc

            itemView.tvRequest.setOnClickListener {
                val legalFragment=LegalConcernFragment()
                val bundle =Bundle()
                bundle.putInt("ID",problemData.id?:-2)
                legalFragment.arguments=bundle
                fragmentManager?.beginTransaction()?.replace(R.id.flContent, legalFragment)?.addToBackStack(null)?.commit()
            }

        }

    }
    fun updateList(list: ArrayList<ProblemData>) {
        listRequestDocument = list
        notifyDataSetChanged()
    }
}