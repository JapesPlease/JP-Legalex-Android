package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import kotlinx.android.synthetic.main.fragment_legal_concern.*
import kotlinx.android.synthetic.main.popup_select_category.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.AdapterCategory
import lax.lega.rv.com.legalax.adapter.AdapterConcernList
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.interfaces.LegalConcernInterface
import lax.lega.rv.com.legalax.pojos.*
import lax.lega.rv.com.legalax.utils.RecyclerItemTouchHelper
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.getDialog


class LegalConcernFragment : Fragment(), View.OnClickListener, LegalConcernInterface, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private lateinit var adapter: AdapterConcernList
    lateinit var sharedPreference: MySharedPreference
    private var listLegalConcern = ArrayList<ProblemData>()
    private var listCategoryModel = ArrayList<ProblemCategoryData>()
    var selectedCategoryId = ""
    var selectedCategoryName = ""
    var selectedDate = "new"
    var dialog: Dialog? = null
    var id: Int? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_legal_concern, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
        getCategory()
    }

    private fun init() {
        id = arguments?.getInt("ID")
    }

    private fun listeners() {

        sharedPreference = MySharedPreference(activity)
        clDates.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        clCategory.setOnClickListener(this)
        tvSelectDate.setOnClickListener(this)
        tvSelectCategoryD.setOnClickListener(this)
        ivFilter.setOnClickListener(this)

        // search view listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // collapse the view ?
                //menu.findItem(R.id.menu_search).collapseActionView();
                //Log.e("queryText", query)
                //  filter(query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // search goes here !!
                // listAdapter.getFilter().filter(query);
              //  Log.e("queryText", newText)
                filter(newText.toString())
                return false
            }
        })

    }

    // Adapter setup
    private fun setUpRecyclerView() {
        adapter = AdapterConcernList(requireActivity(), listLegalConcern, fragmentManager, id, this)
        rvConcernList.adapter = adapter

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this, adapter)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvConcernList)

    }


    // Local filter list
    fun filter(text: String) {
        val temp: MutableList<ProblemData> = ArrayList()
        for (item in listLegalConcern) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (item.title?.contains(text) == true) {
                temp.add(item)
            }
        }
        //update recyclerview
        adapter?.updateList(temp as ArrayList<ProblemData>)
    }


    // Get Problem List api
    private fun getProblemListing() {

        Utils.instance.showProgressBar(activity)
        val androidNetworking = AndroidNetworking.post(Utils.BASE_URL + Utils.GET_PROBLEM_LISTING)

        //AndroidNetworking.upload(Utils.BASE_URL + Utils.SUBMIT_PROBLEM)
        androidNetworking.addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
        androidNetworking.addHeaders("Accept", "application/json")
        androidNetworking.addBodyParameter("cat", selectedCategoryId)
        androidNetworking.addBodyParameter("date", selectedDate)


        //    .addMultipartFile("video", videoToUpload)
        androidNetworking.setPriority(Priority.HIGH)
        androidNetworking.build()
                .getAsObject(ProblemListResponse::class.java, object : ParsedRequestListener<ProblemListResponse> {
                    override fun onResponse(user: ProblemListResponse) {
                        Utils.instance.dismissProgressDialog()


                        if (user.success == true) {
                            //   Toast.makeText(activity, "Submit problem successfully", Toast.LENGTH_LONG).show()
                            listLegalConcern.clear()
                            listLegalConcern.addAll(user.data ?: emptyList())
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


    // date Filter popup
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
                    getProblemListing()
                }
                0 -> {
                    dialog.dismiss()
                    selectedDate = "new"
                    getProblemListing()
                }

            }

            tvSelectDate.text = selectedDate

        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    // show Category List popup
    private fun openCategoryDialog() {

        dialog = getDialog(requireContext(), R.layout.popup_select_category, 0)

        val breakAdapter = AdapterCategory(requireActivity(), listCategoryModel, this)
        dialog?.rvCategories?.adapter = breakAdapter

        dialog?.tvAllCategory?.setOnClickListener {
            selectedCategoryId = ""
            selectedCategoryName = "All Categories"
            dialog?.dismiss()
            tvSelectCategoryD.text = selectedCategoryName
            getProblemListing()
        }

    }

    // get category list api
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



                            getProblemListing()
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


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.clDates, R.id.tvSelectDate -> {
                displayDateOptionPicker()
            }
            R.id.ivBack -> {
                activity?.onBackPressed()
            }
            R.id.tvSelectCategoryD -> {
                openCategoryDialog()
            }
            R.id.ivFilter -> {
                if (constraintFilter.visibility == View.VISIBLE)
                    constraintFilter.visibility = View.GONE
                else
                    constraintFilter.visibility = View.VISIBLE
            }
        }
    }

    // on category click
    override fun onClick(position: Int) {
        selectedCategoryId = listCategoryModel[position].id.toString()
        selectedCategoryName = listCategoryModel[position].name.toString()
        tvSelectCategoryD.text = selectedCategoryName
        dialog?.dismiss()
        getProblemListing()
    }

    // For delete item
    override fun onDelete(position: Int) {
        deleteItem(listLegalConcern[position].id.toString(), position)
    }

    // delete item api
    private fun deleteItem(id: String, position: Int) {
        Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(Utils.BASE_URL + Utils.DELETE_PROBLEM + id)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetLawyerPojo::class.java, object : ParsedRequestListener<GetLawyerPojo> {
                    override fun onResponse(user: GetLawyerPojo) {
                        Utils.instance.dismissProgressDialog()
                        if (user.success == true) {
                            listLegalConcern.removeAt(position)
                            adapter?.notifyDataSetChanged()

                        } else {
                            Toast.makeText(activity, "unable to delete", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {

        Log.e("on swipe", "..... done")
        deleteItem(listLegalConcern[position].id.toString(), position)
//        viewHolder?.itemView?.ivDelete?.setOnClickListener {
//            val dialogDelete=askOption(position)
//            dialogDelete?.show()
//
//        }

    }

    private fun askOption(position: Int): AlertDialog? {
        return AlertDialog.Builder(activity, R.style.AlertDialogTheme) // set message, title, and icon
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.delete)
                .setPositiveButton("Delete") { dialog, _ -> //your deleting code
                    dialog.dismiss()
                    deleteItem(listLegalConcern[position].id.toString(), position)
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
    }

}