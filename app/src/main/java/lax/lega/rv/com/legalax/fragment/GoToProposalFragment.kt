package lax.lega.rv.com.legalax.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_go_to_proposal.*
import lax.lega.rv.com.legalax.R


class GoToProposalFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_go_to_proposal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init(){

        val id=arguments?.getInt("id")?:-1

        tvGo.setOnClickListener {
            val legalFragment=LegalConcernFragment()
            val bundle =Bundle()
            bundle.putInt("ID",id)
            legalFragment.arguments=bundle
            requireFragmentManager().beginTransaction().replace(R.id.flContent, legalFragment).addToBackStack(null).commit()
        }
    }
}