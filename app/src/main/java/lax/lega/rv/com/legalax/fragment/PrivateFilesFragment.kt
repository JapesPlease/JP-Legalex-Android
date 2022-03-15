package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import kotlinx.android.synthetic.main.private_file_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.PrivateFilesAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.ReceivedDocumentPojo
import lax.lega.rv.com.legalax.utils.getAnError

class PrivateFilesFragment : Fragment() {
    lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var v: View
    lateinit var sharedPreference: MySharedPreference
    val list = ArrayList<ReceivedDocumentPojo.Documents.Datum>()
    lateinit var searchView: SearchView
    lateinit var ll_root_layout: LinearLayout
    lateinit var rll_main: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.private_file_activity, container, false)
        initt()
        click()
        setupUI(rll_main)
        return v
    }

    private fun click() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean
            {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean
            {
                list.clear()
                //if(!s.equals("")) {
                getSearchedReceivedDocument(Utils.BASE_URL + Utils.RECEIVED_DOCUMENT, s)
                // }
                return false
            }
        })

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    fun setupUI(view: View)
    {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is SearchView)
        {
            view.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    Utils.hideKeyboard(activity)
                    return false
                }
            })
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun initt()
    {
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        sharedPreference = MySharedPreference(activity)
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        ll_root_layout = v.findViewById<View>(R.id.ll_root_layout) as LinearLayout
        rll_main = v.findViewById<View>(R.id.rll_main) as LinearLayout

    }


    fun setProductRecyclerView()
    {
//        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        val categoryNameAdapter = PrivateFilesAdapter(activity!!, list)
        product_recycler.adapter = categoryNameAdapter
    }

    override fun onResume()
    {
        super.onResume()
        searchView.setQuery("", false);
        ll_root_layout.requestFocus();
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            list.clear()
            getReceivedDocument("")
        }
    }


    fun getReceivedDocument(type: String) {
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(Utils.BASE_URL + Utils.RECEIVED_DOCUMENT)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ReceivedDocumentPojo::class.java, object : ParsedRequestListener<ReceivedDocumentPojo> {
                    override fun onResponse(user: ReceivedDocumentPojo) {
                          Utils.instance.dismissProgressDialog()

                        if (user.success.equals(true)) {
                            if (user.documents.data.size > 0) {
                                list.addAll(user.documents.data)
                                setProductRecyclerView()
                                product_recycler.visibility = View.VISIBLE
                                txt_nodata.visibility = View.GONE
                            } else {
                                product_recycler.visibility = View.GONE
                                txt_nodata.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    fun getSearchedReceivedDocument(url: String?, search: String?)
    {
        //Utils.showLoader(activity)
        AndroidNetworking.get(url + "?" + "search=" + search)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ReceivedDocumentPojo::class.java, object : ParsedRequestListener<ReceivedDocumentPojo> {
                    override fun onResponse(user: ReceivedDocumentPojo) {
                        //   Utils.instance.dismissProgressDialog()

                        if (user.success.equals(true)) {
                            if (user.documents.data.size > 0) {
                                list.addAll(user.documents.data)
                                setProductRecyclerView()
                                product_recycler.visibility = View.VISIBLE
                                txt_nodata.visibility = View.GONE
                            } else {
                                product_recycler.visibility = View.GONE
                                txt_nodata.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        //   Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


}
