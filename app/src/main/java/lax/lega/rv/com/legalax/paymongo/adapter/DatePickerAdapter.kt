package lax.lega.rv.com.legalax.paymongo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import lax.lega.rv.com.legalax.R

class DatePickerAdapter(year: Array<String>) : RecyclerView.Adapter<DatePickerAdapter.ViewHolder>() {

    var year: Array<String>;
    lateinit var handler:ClickHandler
    init {
        this.year=year;
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_date_picker, viewGroup,false)
        return ViewHolder(view);
    }

    override fun getItemCount(): Int {
        return  year.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName.setText(year[position])
        holder.textViewName.setOnClickListener{
            handler.oncLick(position);
        }
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName:TextView
        init {
            textViewName=itemView.findViewById(R.id.textViewName)
        }


    }


    interface  ClickHandler{
        fun oncLick(position: Int)
    }

    fun onCLickListener(handler:ClickHandler){
        this.handler=handler;
    }
}