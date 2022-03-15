package lax.lega.rv.com.legalax.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_show_document.*
import lax.lega.rv.com.legalax.R


class ShowDocumentFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_document, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
    }

    private fun init(){

//        val mParams: ConstraintLayout.LayoutParams = rlRequestDocument.layoutParams as ConstraintLayout.LayoutParams
//        mParams.height = rlRequestDocument.width
//        rlRequestDocument.layoutParams = mParams
//        rlRequestDocument.postInvalidate()

    }

    private fun listeners(){
        rlRequestDocument.setOnClickListener(this)
        rlReviewDocument.setOnClickListener(this)
        ivBack.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.rlRequestDocument -> {
                fragmentManager?.beginTransaction()?.replace(R.id.flContent, DocumentFragment(true))?.addToBackStack(null)?.commit()
            }

            R.id.rlReviewDocument -> {
                val transaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.flContent, SubmitLegalProblemFragment(getString(R.string.review_document_)))
                transaction.addToBackStack(null)
                transaction.commit()

            }
            R.id.ivBack -> {
                activity?.onBackPressed()
            }
        }
    }

}