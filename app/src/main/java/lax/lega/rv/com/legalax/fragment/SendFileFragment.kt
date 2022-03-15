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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.SendFilesAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetUsersPojo
import lax.lega.rv.com.legalax.utils.getAnError

class SendFileFragment : Fragment() {
    lateinit var product_recycler: RecyclerView
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var v: View
    lateinit var sharedPreference: MySharedPreference
    val list = ArrayList<GetUsersPojo.Users.Datum>()
    var document_id: String? = null
    lateinit var rll_main: LinearLayout
    lateinit var ll_root_layout: LinearLayout
    lateinit var searchView: SearchView
    lateinit var backpress1:ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.send_file_activity, container, false)
        rll_main = v.findViewById<View>(R.id.rll_main) as LinearLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        ll_root_layout = v.findViewById<View>(R.id.ll_root_layout) as LinearLayout
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView

        setupUI(rll_main)

        initt()
        click()
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            list.clear()
            getAlluser()
        }
        return v
    }

    private fun click() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                getAlluserSearch(Utils.BASE_URL + Utils.ALL_USER, s)
                return false
            }
        })
        backpress1.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }
    }
    fun setupUI(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is SearchView) {
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


    override fun onResume() {
        super.onResume()
        searchView.setQuery("", false);
        ll_root_layout.requestFocus();
    }

    private fun initt() {
        document_id = arguments!!.getString("document_id")
        sharedPreference = MySharedPreference(activity)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    fun setProductRecyclerView() {
        product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        product_recycler.layoutManager = RecyclerViewLayoutManager1
        val categoryNameAdapter = SendFilesAdapter(activity!!, activity!!, document_id, list)
        product_recycler.adapter = categoryNameAdapter
    }

    fun getAlluser() {
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(Utils.BASE_URL + Utils.ALL_USER)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetUsersPojo::class.java, object : ParsedRequestListener<GetUsersPojo> {
                    override fun onResponse(user: GetUsersPojo) {
                          Utils.instance.dismissProgressDialog()

                        if (user.status.equals(true)) {
                            if (user.users.data.size > 0) {
                                list.addAll(user.users.data)
                                setProductRecyclerView()
                                product_recycler.visibility = View.VISIBLE
                                //   txt_nodata.visibility = View.GONE
                            } else {
                                product_recycler.visibility = View.GONE
                                //txt_nodata.visibility = View.VISIBLE
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

    fun getAlluserSearch(url: String?, search: String?) {
        AndroidNetworking.get(url + "?" + "search=" + search)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetUsersPojo::class.java, object : ParsedRequestListener<GetUsersPojo> {
                    override fun onResponse(user: GetUsersPojo) {
                        if (user.status.equals(true)) {
                            if (user.users.data.size > 0) {
                                list.clear()
                                list.addAll(user.users.data)
                                setProductRecyclerView()
                            } else {
                                product_recycler.visibility = View.GONE
                                Toast.makeText(activity, "No Data is available", Toast.LENGTH_LONG).show()

                            }

                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

}
