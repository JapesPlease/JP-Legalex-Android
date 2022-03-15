package lax.lega.rv.com.legalax.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_proposal.*
import kotlinx.android.synthetic.main.popup_bid.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.adapter.AdapterPendingProposal
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.interfaces.ClickProposalList
import lax.lega.rv.com.legalax.pojos.BidResponse
import lax.lega.rv.com.legalax.pojos.ProposalList
import lax.lega.rv.com.legalax.pojos.ProposalListResponse
import lax.lega.rv.com.legalax.utils.doubleToStringNoDecimal
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getDate
import lax.lega.rv.com.legalax.utils.getDialog

class ProposalFragment : Fragment(), View.OnClickListener, ClickProposalList {

    private var selectedTab=1
    private lateinit var adapter: AdapterPendingProposal
    lateinit var sharedPreference: MySharedPreference
    private var listProposal=ArrayList<ProposalList>()
    private var selectedType="0"
    private var selectedDate="new"
    private var search=""
    private var id=""

    companion object{
        var proposal:ProposalList?=null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proposal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
        setUpRecyclerView(true)
        getBidList(id)
    }

    private fun init(){
        sharedPreference = MySharedPreference(activity)
        id= arguments?.getString("id","").toString()
    }

    private fun listeners(){
        clPending.setOnClickListener(this)
        clApproved.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        ivFilter.setOnClickListener(this)



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                search=s
                hideKeyboardForSearchView()
                Utils.hideKeyboard(activity)
                getBidList(id)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                search=s
                getBidList(id)
                return false
            }
        })
    }

    // Hide keyboard during search
    private fun hideKeyboardForSearchView() {
        searchView.isIconifiedByDefault = false
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Search"
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }


    // Adapter recycler view
    private fun setUpRecyclerView(isPending:Boolean){
        adapter= AdapterPendingProposal(requireActivity(),this,isPending,listProposal,sharedPreference)
        rvPendingConcern.adapter=adapter
        Utils.instance.dismissProgressDialog()
    }


    // on click buttons
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.ivBack->{
               requireActivity().onBackPressed()
            }
            R.id.clPending->{
                selectedTab=1
                setTabSelect()
            }
            R.id.clApproved->{
                selectedTab=2
                setTabSelect()
            }
            R.id.ivFilter->{
                displayDateOptionPicker()
            }
        }
    }

    private fun setTabSelect(){
        when(selectedTab){
            // pending tab select
            1->{
                clPending.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
                tvPending.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))

                clApproved.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorTransparent))
                tvApproved.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                selectedType="0"
                getBidList(id)

            }
            // approove tab select
            2->{
                clPending.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorTransparent))
                tvPending.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))

                clApproved.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
                tvApproved.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                selectedType="1"
                getBidList(id)

            }
        }

    }

    override fun onClickItem(position: Int) {

    }

    override fun onClickView(position: Int) {
        openBidPopup(listProposal[position])
    }
    override fun onClickAccept(position: Int) {

        proposal=listProposal[position]
        fragmentManager?.beginTransaction()?.replace(R.id.flContent,AcceptBidFragment())?.addToBackStack(null)?.commit()
       // acceptBidApi(listProposal[position].id.toString())
    }


    // For Date Filter popup
    private fun displayDateOptionPicker() {

        val pickerItems = arrayOf(
                requireContext().getString(R.string.recent),
                requireContext().getString(R.string.oldest),
                requireContext().getString(android.R.string.cancel))

        val builder = AlertDialog.Builder(context)
        builder.setTitle(requireContext().getString(R.string.dialog_select_your_choice))
        builder.setItems(pickerItems) { dialog, which ->
            when (which) {
                2 -> {
                    dialog.dismiss()
                }
//                2 -> {
//                    selectedDate = ""
//                    tvSelectDate.text = ""
//                    getProblemListing()
//                }
                1 -> {
                    dialog.dismiss()
                    selectedDate="old"
                    getBidList(id)
                }
                0 -> {
                    dialog.dismiss()
                    selectedDate="new"
                    getBidList(id)
                }

            }


        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    // Bid Detail popup
    @SuppressLint("SetTextI18n")
    private fun openBidPopup(proposalList: ProposalList) {
        val dialogCreditCardEntry = getDialog(requireContext(),R.layout.popup_bid,0)
      //  dialogCreditCardEntry.tvCategory.visibility=View.GONE
        dialogCreditCardEntry.tvName.text=proposalList.lawyerInfo?.name
        dialogCreditCardEntry.tvTitle.text=proposalList.info?.title
        dialogCreditCardEntry.tvDescription.text=proposalList.notes.toString()
        dialogCreditCardEntry.ivDownload.visibility = View.GONE
        dialogCreditCardEntry.tvDownload.text="P"+doubleToStringNoDecimal(proposalList.bid.toString().toDouble())
       // dialogCreditCardEntry.tvLocation.text = proposalList.lawyerInfo?.location.toString()
        dialogCreditCardEntry.tvCategory.text="Category: "+proposalList.info?.catInfo?.name
        dialogCreditCardEntry.tvDateSubmitted.text= "Date Submitted: "+getDate(proposalList.createdAt ?: "")
        Picasso.with(MainActivity.activity).load(Utils.IMAGESURL +proposalList.lawyerInfo?.profileImage.toString()).fit().placeholder(R.drawable.icn_user_large).into(dialogCreditCardEntry.ivUser)

    }


    // get Bid List Api
    private fun getBidList(problemID:String) {
        //  Utils.showLoader(activity)
        Log.i("APIHIT","${Utils.BASE_URL + Utils.GET_BID_LIST_USER_SIDE + selectedType+"&problem_id="+problemID+"&search="+search+"&date="+selectedDate}")
        Log.i("ACCESSTOKEN","${"Bearer " + sharedPreference.getString("access_token")}")
        Utils.instance.showProgressBar(requireActivity())
        AndroidNetworking.get(Utils.BASE_URL + Utils.GET_BID_LIST_USER_SIDE + selectedType+"&problem_id="+problemID+"&search="+search+"&date="+selectedDate)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ProposalListResponse::class.java, object : ParsedRequestListener<ProposalListResponse> {
                    override fun onResponse(user: ProposalListResponse) {
                        //   Utils.hideLoader()
                            Log.i("ProposalList","${Gson().toJson(user)}")
                        if (user.success==true) {
                            listProposal.clear()
                            listProposal.addAll(user.data?: emptyList())

                            if (listProposal.isEmpty()){
                                tvNoData.visibility=View.VISIBLE
                                rvPendingConcern.visibility=View.GONE
                                Utils.instance.dismissProgressDialog()
                            }else{
                                tvNoData.visibility=View.GONE
                                rvPendingConcern.visibility=View.VISIBLE
                                checkTab()
                            }

                        }
                    }

                    override fun onError(anError: ANError) {
                        //   Utils.hideLoader().
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

        private fun checkTab(){
            if (selectedType=="0")
            {
                setUpRecyclerView(true)
            }else{
                setUpRecyclerView(false)
            }
        }


    override fun onResume() {
        super.onResume()
        getBidList(id)
    }

    // Accept bid api
    private fun acceptBidApi(bidId: String) {
        Utils.instance.showProgressBar(requireActivity())
        AndroidNetworking.post(Utils.BASE_URL + Utils.ACCEPT_BID)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("bid_id", bidId)
                //.addBodyParameter("bid_amount", amount)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(BidResponse::class.java, object : ParsedRequestListener<BidResponse> {
                    override fun onResponse(user: BidResponse) {
                        Utils.instance.dismissProgressDialog()
                        if (user.success==true) {
                            Toast.makeText(requireContext(), user.message, Toast.LENGTH_LONG).show()
                            requireActivity().onBackPressed()
                        } else {
                            Toast.makeText(requireContext(), "Unable to accept bid", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Toast.makeText(requireContext(), "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

}