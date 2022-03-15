package lax.lega.rv.com.legalax.utils

import android.app.AlertDialog
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import kotlinx.android.synthetic.main.item_rv_concern_list.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.AdapterConcernList
import lax.lega.rv.com.legalax.fragment.LegalConcernFragment


/**
 * Created by ravi on 29/09/17.
 */
class RecyclerItemTouchHelper(dragDirs: Int, swipeDirs: Int, private val listener: RecyclerItemTouchHelperListener, val adapter: AdapterConcernList) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    var foregroundView: View?=null
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            foregroundView = (viewHolder as AdapterConcernList.MyViewHolder).itemView.viewForeground
            getDefaultUIUtil().onSelected(foregroundView)
        }
    }
    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView,
                                 viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                                 actionState: Int, isCurrentlyActive: Boolean) {
        foregroundView= (viewHolder as AdapterConcernList.MyViewHolder).itemView.viewForeground
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        foregroundView = (viewHolder as AdapterConcernList.MyViewHolder).itemView.viewForeground
        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {
        foregroundView = (viewHolder as AdapterConcernList.MyViewHolder).itemView.viewForeground
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        val dialogDelete= askOption(viewHolder,direction,viewHolder.adapterPosition)
        dialogDelete?.show()
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    interface RecyclerItemTouchHelperListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int)
    }

    private fun askOption(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int): AlertDialog? {
        return AlertDialog.Builder(viewHolder?.itemView?.context, R.style.AlertDialogTheme) // set message, title, and icon
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.delete)
                .setPositiveButton("Delete") { dialog, _ -> //your deleting code
                    dialog.dismiss()
                    listener.onSwiped(viewHolder, direction, viewHolder?.adapterPosition?:-1)
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.dismiss()
                    getDefaultUIUtil().clearView(foregroundView)
                    adapter.notifyDataSetChanged()

                }
                .create()
    }
}