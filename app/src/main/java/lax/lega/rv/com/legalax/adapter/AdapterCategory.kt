package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_category.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.interfaces.LegalConcernInterface
import lax.lega.rv.com.legalax.pojos.ProblemCategoryData
import java.util.ArrayList

class AdapterCategory(var activity: Activity,var listCategory: ArrayList<ProblemCategoryData>,var legalConcernInterface: LegalConcernInterface) : RecyclerView.Adapter<AdapterCategory.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.item_category, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(listCategory[position])
    }

    override fun getItemCount(): Int {
        return listCategory.size
    }

    // inner class
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(category: ProblemCategoryData) {

            itemView.tvCategory.text=category.name

            itemView.setOnClickListener { legalConcernInterface.onClick(adapterPosition) }
        }

    }
}