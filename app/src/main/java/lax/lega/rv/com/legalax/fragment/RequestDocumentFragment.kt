package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import kotlinx.android.synthetic.main.fragment_request_document.*
import kotlinx.android.synthetic.main.fragment_request_document.ivBack
import kotlinx.android.synthetic.main.fragment_request_document.searchView
import kotlinx.android.synthetic.main.fragment_request_document.tvSelectCategoryD
import kotlinx.android.synthetic.main.fragment_request_document.tvSelectDate
import kotlinx.android.synthetic.main.popup_select_category.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.AdapterCategory
import lax.lega.rv.com.legalax.adapter.AdapterRequestDocument
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.interfaces.LegalConcernInterface
import lax.lega.rv.com.legalax.pojos.ProblemCategoryData
import lax.lega.rv.com.legalax.pojos.ProblemCategoryResponse
import lax.lega.rv.com.legalax.pojos.ProblemData
import lax.lega.rv.com.legalax.pojos.ProblemListResponse
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getDialog

class RequestDocumentFragment : Fragment(), LegalConcernInterface, View.OnClickListener {

    private lateinit var adapter: AdapterRequestDocument
    private var listRequestDocument =ArrayList<ProblemData>()
    private var listCategoryModel =ArrayList<ProblemCategoryData>()
    lateinit var sharedPreference: MySharedPreference
    private var selectedCategoryId=""
    private var selectedCategoryName=""
    private var selectedDate="new"
    private var dialog: Dialog?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_document, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
        setUpRecyclerView()
        getCategory()
    }

    private fun init(){
        sharedPreference = MySharedPreference(activity)
    }

    private fun listeners(){

        ivBack.setOnClickListener(this)
        tvSelectDate.setOnClickListener(this)
        tvSelectCategoryD.setOnClickListener(this)

        // search view listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // collapse the view ?
                //menu.findItem(R.id.menu_search).collapseActionView();
               // Log.e("queryText", query)
                //  filter(query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // search goes here !!
                // listAdapter.getFilter().filter(query);
             //   Log.e("queryText", newText)
                filter(newText.toString())
                return false
            }
        })

    }

    // adapter setup
    private fun setUpRecyclerView(){
        adapter= AdapterRequestDocument(requireActivity(),listRequestDocument,fragmentManager)
        rvRequestDocument.adapter=adapter
    }


    // click of category list
    override fun onClick(position: Int) {
        selectedCategoryId=listCategoryModel[position].id.toString()
        selectedCategoryName=listCategoryModel[position].name.toString()
        tvSelectCategoryD.text=selectedCategoryName
        dialog?.dismiss()
        getDocumentProblemListing()
    }

    override fun onDelete(position: Int) {

    }


    // Local filter list
    fun filter(text: String) {
        val temp: MutableList<ProblemData> = ArrayList()
        for (item in listRequestDocument) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (item.title?.contains(text) == true) {
                temp.add(item)
            }
        }
        //update recyclerview
        adapter.updateList(temp as ArrayList<ProblemData>)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.ivBack->{ activity?.onBackPressed() }

            R.id.tvSelectDate -> {
                displayDateOptionPicker()
            }
            R.id.tvSelectCategoryD -> {
                openCategoryDialog()
            }
        }
    }

    // get category api
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
                            getDocumentProblemListing()
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


    // category popup
    private fun openCategoryDialog() {

        dialog = getDialog(requireContext(),R.layout.popup_select_category,0)

        val breakAdapter = AdapterCategory(requireActivity(), listCategoryModel, this)
        dialog?.rvCategories?.adapter = breakAdapter

        dialog?.tvAllCategory?.setOnClickListener {
            selectedCategoryId=""
            selectedCategoryName="All Categories"
            dialog?.dismiss()
            tvSelectCategoryD.text=selectedCategoryName
            getDocumentProblemListing()
        }

    }


    //get document problem list api
    private fun getDocumentProblemListing(){

        Utils.instance.showProgressBar(activity)
        val androidNetworking= AndroidNetworking.post(Utils.BASE_URL + Utils.GET_DOCUMENT_PROBLEM_LISTING)

        //AndroidNetworking.upload(Utils.BASE_URL + Utils.SUBMIT_PROBLEM)
        androidNetworking.addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
        androidNetworking.addHeaders("Accept", "application/json")
        androidNetworking.addBodyParameter("cat", selectedCategoryId)
        androidNetworking.addBodyParameter("date", selectedDate)


        //.addMultipartFile("video", videoToUpload)
        androidNetworking.setPriority(Priority.HIGH)
        androidNetworking.build()
                .getAsObject(ProblemListResponse::class.java, object : ParsedRequestListener<ProblemListResponse> {
                    override fun onResponse(user: ProblemListResponse) {
                        Utils.instance.dismissProgressDialog()


                        if (user.success == true) {
                            //   Toast.makeText(activity, "Submit problem successfully", Toast.LENGTH_LONG).show()
                            listRequestDocument.clear()
                            listRequestDocument.addAll(user.data ?: emptyList())
                            setUpRecyclerView()

                        } else {
                            Toast.makeText(activity, "Unable to add problem", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


    // date filter popup
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
                    getDocumentProblemListing()
                }
                0 -> {
                    dialog.dismiss()
                    selectedDate="new"
                    getDocumentProblemListing()
                }

            }

            tvSelectDate.text=selectedDate

        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


}