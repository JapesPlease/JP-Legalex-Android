package lax.lega.rv.com.legalax.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.CustomTextviewHelvicNormal
import lax.lega.rv.com.legalax.pojos.lawyerCategories.Response

class SelectedCategoryAdapter(var arraySelectedCategories: MutableList<Response>) : RecyclerView.Adapter<SelectedCategoryAdapter.ViewHolder>() {
  lateinit var  handler:ClickHandler
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_selectedategory, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arraySelectedCategories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewCategoryName.setText(arraySelectedCategories[position].name)
        holder.relativeLayoutCross.setOnClickListener {
            handler.onCrossClick(arraySelectedCategories[position])
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewCategoryName: CustomTextviewHelvicNormal
        var relativeLayoutCross: RelativeLayout

        init {
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName)
            relativeLayoutCross = itemView.findViewById(R.id.relativeLayoutCross)
        }
    }


    interface  ClickHandler{
        fun onCrossClick(category:Response)
    }

    fun clickListener(handler:ClickHandler){
        this.handler=handler
    }

    fun customNotifiy(arraySelectedCategories: MutableList<Response>) {
        this.arraySelectedCategories=arraySelectedCategories
        notifyDataSetChanged()

    }

}