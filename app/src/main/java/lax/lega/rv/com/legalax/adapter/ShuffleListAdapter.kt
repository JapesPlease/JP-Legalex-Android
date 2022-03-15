package lax.lega.rv.com.legalax.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.CustomEdittextHelvic
import lax.lega.rv.com.legalax.common.CustomTextviewHelvicNormal

class ShuffleListAdapter(shufflingList: Array<String>) : RecyclerView.Adapter<ShuffleListAdapter.ViewHolder>() {
    var shufflingList: Array<String>

    lateinit var  clickListener:ClickListener

    init {
        this.shufflingList = shufflingList
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_shuffle_list, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return shufflingList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName.setText(shufflingList[position])
        holder.textViewName.setOnClickListener{
            clickListener.onClick(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var textViewName: CustomTextviewHelvicNormal

        init {
            textViewName = itemView.findViewById<CustomTextviewHelvicNormal>(R.id.textViewName)
        }
    }

    fun clickHandler(listener:ClickListener){
        clickListener=listener;
    }

    interface ClickListener{
        fun onClick(position:Int);
    }
}