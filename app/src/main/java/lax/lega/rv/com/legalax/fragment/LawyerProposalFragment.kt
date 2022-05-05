package lax.lega.rv.com.legalax.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.sinch.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_lawyer_proposal.*
import kotlinx.android.synthetic.main.popup_bid.*
import kotlinx.android.synthetic.main.popup_filter.*
import kotlinx.android.synthetic.main.popup_select_category.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.adapter.AdapterCategory
import lax.lega.rv.com.legalax.adapter.AdapterLawyerProposal
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.interfaces.ClickProposalList
import lax.lega.rv.com.legalax.interfaces.LegalConcernInterface
import lax.lega.rv.com.legalax.pojos.BidList
import lax.lega.rv.com.legalax.pojos.BidListResponse
import lax.lega.rv.com.legalax.pojos.ProblemCategoryData
import lax.lega.rv.com.legalax.pojos.ProblemCategoryResponse
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getDate
import lax.lega.rv.com.legalax.utils.getDialog


class LawyerProposalFragment : Fragment(), View.OnClickListener, ClickProposalList, LegalConcernInterface {

    private var selectedTab = 1
    private var selectedType = "0"
    private lateinit var adapter: AdapterLawyerProposal
    lateinit var sharedPreference: MySharedPreference
    private var isPending = true
    private var listBid = ArrayList<BidList>()
    private var listCategoryModel = ArrayList<ProblemCategoryData>()
    private var selectedCategoryId = ""
    private var selectedCategoryName = ""
    private var search = ""
    private var selectedDate = "new"
    private var dialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lawyer_proposal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
        setUpRecyclerView()
        getCategory()
        getLawyerListSearch()
        if (arguments != null) {
            if (arguments!!.getString("bid").equals("bid_accept"))
                clApproved.performClick()
            else
                clPending.performClick()

        }
        requireActivity().registerReceiver(bidAccepted, IntentFilter("bid_accept"))
    }

    private val bidAccepted = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (selectedTab == 1) {
                selectedTab = 1
                setTabSelect()
            } else {
                selectedTab = 2
                setTabSelect()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(bidAccepted)
    }

    private fun init() {
        sharedPreference = MySharedPreference(activity)
    }

    private fun listeners() {
        clPending.setOnClickListener(this)
        clApproved.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        ivFilter.setOnClickListener(this)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                search = s
                hideKeyboardForSearchView()
                Utils.hideKeyboard(activity)
                getLawyerListSearch()
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {

                search = s
                getLawyerListSearch()

                return false
            }
        })
    }

    private fun hideKeyboardForSearchView() {
        searchView.isIconifiedByDefault = false
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Search"
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    private fun setUpRecyclerView() {
        adapter = AdapterLawyerProposal(requireActivity(), this, isPending, listBid, sharedPreference)
        rvPendingConcern.adapter = adapter
        Utils.instance.dismissProgressDialog()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                requireActivity().onBackPressed()
            }
            R.id.clPending -> {
                selectedTab = 1

                setTabSelect()
            }
            R.id.clApproved -> {
                selectedTab = 2

                setTabSelect()
            }
            R.id.ivFilter -> {
                openFilterPopup()
            }
            /* R.id.tvSelectDate -> {
                 displayDateOptionPicker()
             }
             R.id.tvSelectCategoryD -> {
                 openCategoryDialog()
             }*/
        }
    }

    private fun setTabSelect() {
        when (selectedTab) {
            1 -> {
                //for Pending Tab
                selectedType = "0"
                clPending.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                tvPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                clApproved.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorTransparent))
                tvApproved.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                getLawyerListSearch()

            }
            2 -> {
                // For Approved Tab
                selectedType = "1"
                clPending.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorTransparent))
                tvPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                clApproved.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                tvApproved.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                getLawyerListSearch()
            }
        }

    }

    //on click
    override fun onClickItem(position: Int) {

    }

    // on Click of view
    override fun onClickView(position: Int) {
        openBidPopup(listBid[position])
    }

    override fun onClickAccept(position: Int) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.flContent, ProposalBiddingFragment(listBid[position]))
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Get bid List api
    private fun getLawyerListSearch() {
        //  Log.e("user id is",sharedPreference.getInt("id").toString())
        Log.i("APIHIT", "${Utils.BASE_URL + Utils.GET_BID_LIST + selectedType + "&search=" + search + "&cat=" + selectedCategoryId + "&date=" + selectedDate}")

        Log.i("ACCESSTOKEN", "${"Bearer " + sharedPreference.getString("access_token")}")
        Utils.instance.showProgressBar(requireActivity())
        AndroidNetworking.get(Utils.BASE_URL + Utils.GET_BID_LIST + selectedType + "&search=" + search + "&cat=" + selectedCategoryId + "&date=" + selectedDate)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(BidListResponse::class.java, object : ParsedRequestListener<BidListResponse> {
                    override fun onResponse(user: BidListResponse) {
                        //   Utils.hideLoader()

                        if (user.success == true) {
                            listBid.clear()
                            listBid.addAll(user.data ?: emptyList())
                            if (listBid.isEmpty()) {
                                if (tvNoData != null) {
                                    tvNoData.visibility = View.VISIBLE
                                    rvPendingConcern.visibility = View.GONE
                                }
                                Utils.instance.dismissProgressDialog()
                            } else {
                                if (tvNoData != null) {
                                    tvNoData.visibility = View.GONE
                                    rvPendingConcern.visibility = View.VISIBLE
                                    checkTab()
                                }
                            }
                        }
                    }

                    override fun onError(anError: ANError) {
                        //   Utils.hideLoader()
                        Log.i("LawyerPro",anError.localizedMessage)
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun checkTab() {
        if (selectedType == "0") {
            isPending = true
            setUpRecyclerView()
        } else {
            isPending = false
            setUpRecyclerView()
        }
    }


    // bid popup
    @SuppressLint("SetTextI18n")
    private fun openBidPopup(bidList: BidList) {
        val dialogCreditCardEntry = getDialog(requireContext(), R.layout.popup_bid, 0)

        if (isPending) {
            dialogCreditCardEntry.tvName.text = bidList.userInfo?.name
            dialogCreditCardEntry.tvTitle.text = bidList.title
            dialogCreditCardEntry.tvDescription.text = bidList.desc
            dialogCreditCardEntry.tvCategory.text = "Category: " + bidList.catInfo?.name
            dialogCreditCardEntry.tvDateSubmitted.text = "Date Submitted: " + getDate(bidList.createdAt
                    ?: "")
            // dialogCreditCardEntry.tvLocation.text = bidList.userInfo?.location
            Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + bidList.userInfo?.profileImage).fit().placeholder(R.drawable.icn_user_large).into(dialogCreditCardEntry.ivUser)
        } else {
            dialogCreditCardEntry.tvName.text = bidList.info?.userInfo?.name
            dialogCreditCardEntry.tvTitle.text = bidList.info?.title
            dialogCreditCardEntry.tvDescription.text = bidList.info?.desc
            dialogCreditCardEntry.tvCategory.text = "Category: " + bidList.info?.catInfo?.name
            dialogCreditCardEntry.tvDateSubmitted.text = "Date Submitted: " + getDate(bidList.createdAt
                    ?: "")
            //dialogCreditCardEntry.tvLocation.text = bidList.userInfo?.location
            Picasso.with(MainActivity.activity).load(Utils.IMAGESURL + bidList.info?.userInfo?.profileImage).fit().placeholder(R.drawable.icn_user_large).into(dialogCreditCardEntry.ivUser)
        }


        dialogCreditCardEntry.tvDownload.setOnClickListener {


            if (bidList.media == null || bidList.media == "") {
                Toast.makeText(requireActivity(), "file not uploaded", Toast.LENGTH_SHORT).show()
            } else {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requireActivity().requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
                } else {

                    val url = Utils.SERVER_URL + bidList.media
                    val request = DownloadManager.Request(Uri.parse(url))
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    request.setTitle("Downloading")
                    request.setDescription("Downloading" + bidList.media)
                    request.setVisibleInDownloadsUi(false)
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Legalex Doc/" + "/" + bidList.media)
                    val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    var downloadID = downloadManager.enqueue(request)
                    Toast.makeText(activity, "Downloading Started", Toast.LENGTH_SHORT).show()

                }
            }


        }

    }


    // Filter popup
    private fun openFilterPopup() {
        val dialogFilter = getDialog(requireContext(), R.layout.popup_filter, 0)

        dialogFilter.tvSelectDate.text = selectedDate
        dialogFilter.tvSelectCategoryD.text = selectedCategoryName

        dialogFilter.tvSelectDate.setOnClickListener {
            dialogFilter.dismiss()
            displayDateOptionPicker()
        }
        dialogFilter.tvSelectCategoryD.setOnClickListener {
            dialogFilter.dismiss()
            openCategoryDialog()
        }

        dialogFilter.show()
    }

    // Show category popup
    private fun openCategoryDialog() {

        dialog = getDialog(requireContext(), R.layout.popup_select_category, 0)

        val breakAdapter = AdapterCategory(requireActivity(), listCategoryModel, this)
        dialog?.rvCategories?.adapter = breakAdapter

        dialog?.tvAllCategory?.setOnClickListener {
            selectedCategoryId = ""
            selectedCategoryName = "All Categories"
            dialog?.dismiss()
            //  tvSelectCategoryD.text=selectedCategoryName
            getLawyerListSearch()
        }

    }

    // Date Filter popup
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
                    selectedDate = "old"
                    getLawyerListSearch()
                }
                0 -> {
                    dialog.dismiss()
                    selectedDate = "new"
                    getLawyerListSearch()
                }

            }

            //   tvSelectDate.text=selectedDate

        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    // get Category Api
    private fun getCategory() {

        Utils.instance.showProgressBar(activity)

        AndroidNetworking.get(Utils.BASE_URL + Utils.GET_PROBLEM_CATEGORY)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ProblemCategoryResponse::class.java, object : ParsedRequestListener<ProblemCategoryResponse> {
                    override fun onResponse(user: ProblemCategoryResponse) {

                        Utils.instance.dismissProgressDialog()

                        if (user.success == true) {

                            if (user.data?.isNotEmpty() == true) {
                                listCategoryModel.clear()
                                listCategoryModel.addAll(user.data ?: emptyList())
                            }


                            //  getProblemListing()
                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    // click for category popup
    override fun onClick(position: Int) {
        selectedCategoryId = listCategoryModel[position].id.toString()
        selectedCategoryName = listCategoryModel[position].name.toString()
        //    tvSelectCategoryD.text=selectedCategoryName
        dialog?.dismiss()
        getLawyerListSearch()
    }


    // For delete item
    override fun onDelete(position: Int) {

    }

    override fun onResume() {
        super.onResume()
        getLawyerListSearch()
    }

}