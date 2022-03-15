package lax.lega.rv.com.legalax.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.CustomEdittextHelvic
import lax.lega.rv.com.legalax.common.CustomTextviewBold
import java.util.*
import android.text.Editable
import android.text.TextWatcher


/**
 * Created by droidNinja on 29/07/16.
 */
class ImageAdapter(private val context: Context, private val paths: ArrayList<String>,
                   private val pathsName: ArrayList<String>
                   , private val activity: Activity) : RecyclerView.Adapter<ImageAdapter.FileViewHolder>() {
    private var imageSize: Int = 0
    internal var break_list = arrayOf("Select type", "Personal", "Business", "Property", "Finance")
    var role: Int? = null

    init {
        setColumnNumber(context, 3)
    }

    private fun setColumnNumber(context: Context, columnNum: Int) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        val widthPixels = metrics.widthPixels
        imageSize = widthPixels / columnNum
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.uploaded_file_item, parent, false)

        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        setAdapter(holder.spn_break, activity)
//        val path = paths[position]
       // holder.tvName.text = path
        holder.edt_document.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                       count: Int) {
                if (s != "") {

                }
            }


            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (pathsName.size > 0) {
                    if (pathsName[position] == "") {
                        pathsName.add(position, holder.edt_document.text.toString())
                    } else {
                        pathsName.removeAt(position)
                        pathsName.add(position, holder.edt_document.text.toString())
                    }
                } else {
                    pathsName.add(position, holder.edt_document.text.toString())
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return 1
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvName: CustomTextviewBold
        lateinit var spn_break: Spinner
        lateinit var edt_document: CustomEdittextHelvic

        init {
            tvName = itemView.findViewById<View>(R.id.txt_filename) as CustomTextviewBold
            spn_break = itemView.findViewById<View>(R.id.spn_break) as Spinner
            edt_document = itemView.findViewById<View>(R.id.edt_document) as CustomEdittextHelvic

        }
    }

    private fun setAdapter(spn_break: Spinner, activity: Activity) {
        val break_adapter = ArrayAdapter(activity, R.layout.spinner_text, break_list)
        break_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_break.adapter = break_adapter
    }
}
