package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.Gson
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.DocumentAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.interfaces.LegalConcernInterface
import lax.lega.rv.com.legalax.pojos.GetDocumentPojo
import lax.lega.rv.com.legalax.pojos.ProblemListResponse
import lax.lega.rv.com.legalax.utils.getAnError

class DocumentFragment(var isRequest:Boolean) : Fragment(), LegalConcernInterface {
    lateinit var rl_private_files: RelativeLayout
    lateinit var rl_business: RelativeLayout
    lateinit var rl_property: RelativeLayout
    lateinit var rl_personal: RelativeLayout
    lateinit var rl_finance: RelativeLayout
    lateinit var rl_upload: RelativeLayout
    lateinit var v: View
    lateinit var RecyclerViewLayoutManager1: LinearLayoutManager
    lateinit var recyclerView: RecyclerView
    lateinit var scrolViewMain: NestedScrollView
    val list = ArrayList<GetDocumentPojo.Data.Datum>()
    lateinit var sharedPreference: MySharedPreference
    lateinit var rll_main: RelativeLayout
    lateinit var ll_root_layout: LinearLayout
    lateinit var llBottom: LinearLayout
    lateinit var llCategory: LinearLayout
    lateinit var rl_view_list: RelativeLayout
    lateinit var imageViewUpload: ImageView
    lateinit var txt_nodata: TextView
    lateinit var searchView: SearchView
    lateinit var connectionDetector: ConnectionDetector
    lateinit var pdialog: ProgressDialog
    lateinit var handler: Handler
    lateinit var typeo: String
    var isType = true
    var flag = true
    var recyclerViewState: Parcelable? = null

    var isLoding = false
    var next_page_url: String? = null


    var categoryNameAdapter: DocumentAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.view_document_activity, container, false)
        Utils.hideKeyboard(activity)
        pdialog = ProgressDialog(activity)
        handler = Handler()

        sharedPreference = MySharedPreference(activity)

        init()
        if (sharedPreference.getString("role").equals("1")) {
            Log.e("Role", ">>>>>>>>>>>>>>>>" + sharedPreference.getString("role"))
            rl_private_files.visibility = View.VISIBLE
        //    imageViewUpload.visibility=View.VISIBLE

        } else {
            rl_upload.visibility = View.VISIBLE
        //    imageViewUpload.visibility=View.GONE
        }
        click()
        setProductRecyclerView()
        setupUI(rll_main)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    // For hide keyboard
    private fun hideKeyboardForSearchView() {
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
//        searchView.clearFocus()
        searchView.queryHint = "Search"
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }


    override fun onResume() {
        super.onResume()
//        searchView.setQuery("", false);
        categoryNameAdapter=null;
        ll_root_layout.requestFocus();
        hitApi(typeo, Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT)

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

    private fun hitApi(s: String, s1: String) {
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet == false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            if (flag) {

                list.clear()
                getDocument(s, s1)
//                flag = false
            }
        }
    }


    fun setProductRecyclerView()
    {
        if (categoryNameAdapter == null) {

            RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
            recyclerView.layoutManager = RecyclerViewLayoutManager1
            categoryNameAdapter = DocumentAdapter(activity!!, list, activity!!,isRequest,this)
            recyclerView.adapter = categoryNameAdapter

        } else {
            categoryNameAdapter!!.notifyDataSetChanged()
        }


    }

    private fun init() {
        typeo = "1"
        rl_private_files = v.findViewById<View>(R.id.rl_private_files) as RelativeLayout
        rl_upload = v.findViewById<View>(R.id.rl_upload) as RelativeLayout
        rl_property = v.findViewById<View>(R.id.rl_property) as RelativeLayout
        rl_business = v.findViewById<View>(R.id.rl_business) as RelativeLayout
        rl_personal = v.findViewById<View>(R.id.rl_personal) as RelativeLayout
        rl_finance = v.findViewById<View>(R.id.rl_finance) as RelativeLayout
        recyclerView = v.findViewById<View>(R.id.recycler) as RecyclerView
        txt_nodata = v.findViewById<View>(R.id.txt_nodata) as TextView
        imageViewUpload = v.findViewById<ImageView>(R.id.imageViewUpload)
        scrolViewMain = v.findViewById<NestedScrollView>(R.id.scrolViewMain) as NestedScrollView
        //scrollView = v.findViewById<ScrollView>(R.id.scrolViewMain) as ScrollView
        sharedPreference = MySharedPreference(activity)
        ll_root_layout = v.findViewById<View>(R.id.ll_root_layout) as LinearLayout
        llBottom = v.findViewById<View>(R.id.llBottom) as LinearLayout
        llCategory = v.findViewById<View>(R.id.llCategory) as LinearLayout
        rll_main = v.findViewById<View>(R.id.rll_main) as RelativeLayout
        searchView = v.findViewById<View>(R.id.searchView) as SearchView
        sharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        connectionDetector.isConnectingToInternet


        if (isRequest){
         //   llCategory.visibility=View.GONE
            llBottom.visibility=View.GONE

        }else{
         //   llCategory.visibility=View.VISIBLE
            llBottom.visibility=View.VISIBLE
        }

    }

    private fun click() {
        rl_private_files.setOnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, PrivateFilesFragment()).addToBackStack(null).commit()
        }
        rl_upload.setOnClickListener {
            //                Intent intent = new Intent(getActivity(), FileUploadFragment.class);
            //                startActivity(intent);
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, FileUploadFragment()).addToBackStack(null).commit()
        }
        rl_business.setOnClickListener({
            hitApi("2", Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT)
            typeo = "2"
            rl_personal.setBackgroundResource(R.drawable.white_outline)
            rl_business.setBackgroundResource(R.drawable.green_outline)
            rl_property.setBackgroundResource(R.drawable.white_outline)
            rl_finance.setBackgroundResource(R.drawable.white_outline)
        })
        rl_property.setOnClickListener({
            hitApi("3", Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT)
            typeo = "3"
            rl_personal.setBackgroundResource(R.drawable.white_outline)
            rl_business.setBackgroundResource(R.drawable.white_outline)
            rl_property.setBackgroundResource(R.drawable.green_outline)
            rl_finance.setBackgroundResource(R.drawable.white_outline)
        })
        rl_personal.setOnClickListener({
            hitApi("1", Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT)
            typeo = "1"
            rl_personal.setBackgroundResource(R.drawable.green_outline)
            rl_business.setBackgroundResource(R.drawable.white_outline)
            rl_property.setBackgroundResource(R.drawable.white_outline)
            rl_finance.setBackgroundResource(R.drawable.white_outline)
        })
        rl_finance.setOnClickListener({
            hitApi("4", Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT)
            typeo = "4"
            rl_personal.setBackgroundResource(R.drawable.white_outline)
            rl_business.setBackgroundResource(R.drawable.white_outline)
            rl_property.setBackgroundResource(R.drawable.white_outline)
            rl_finance.setBackgroundResource(R.drawable.green_outline)
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                val search_term = searchView.getQuery().toString();
                list.clear();
                searchView.setIconifiedByDefault(false);
                searchView.setIconified(false);
//                searchView.clearFocus();
//                Utils.hideKeyboard(activity)
//                hideKeyboardForSearchView()
                getSearchDocument(Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT, s)
                return false
            }


            override fun onQueryTextChange(s: String): Boolean {
                if (searchView.getQuery().length == 0) {
                    list.clear()
                    getSearchDocument(Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT, s)

                } else {
                    list.clear();
                    getSearchDocument(Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT, s)
                }
                return false
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                if (searchView.query.length == 0) {

                    list.clear();
                    getSearchDocument(Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT, "");
                }
                return false
            }
        })
        scrolViewMain.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                if (v!!.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {
                        if (!isLoding) {
                            getDocument(typeo, next_page_url!!)
                            recyclerViewState = recyclerView.layoutManager!!.onSaveInstanceState()
                            isLoding = true
                        }
                    }
                }
            }

        })
//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
//        {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if ((recyclerView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == list.size - 1 && next_page_url != null) {
//                    if (!isLoding) {
//                        getDocument(next_page_url!!)
//                        recyclerViewState = recyclerView.layoutManager!!.onSaveInstanceState()
//                        isLoding = true
//                    }
//                }
//            }
//        })
    }

    fun getDocument(type: String, s1: String) {

        Log.e("Typoe", ">>>>>>>>>>>>>" + type)
        //list.clear()


        if (pdialog != null) {
            if (pdialog.isShowing) {
                pdialog.dismiss()
            }
        }

        pdialog = ProgressDialog(activity)
        pdialog.setCancelable(false)
        pdialog.setTitle("Please Wait.......")
        pdialog.setMessage("Loading...........")
        pdialog.show()

        AndroidNetworking.cancel("h")
        AndroidNetworking.post(s1)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("type", type)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetDocumentPojo::class.java, object : ParsedRequestListener<GetDocumentPojo> {
                    override fun onResponse(user: GetDocumentPojo) {
                        pdialog.dismiss()
                        if (user.success.equals(true)) {
                            if (user.data.data.size > 0) {
                                list.addAll(user.data.data)
                                setProductRecyclerView()
                                recyclerView.visibility = View.VISIBLE
                                txt_nodata.visibility = View.GONE

                                if (user.data.nextPageUrl != null) {
                                    next_page_url = user.data.getNextPageUrl().toString()
                                } else {
                                    next_page_url = ""
                                }
                                isLoding = false
                                recyclerView.adapter!!.notifyDataSetChanged()
                            } else {
                                recyclerView.visibility = View.GONE
                                txt_nodata.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        pdialog.dismiss()
                        if (anError.errorBody == null) {


                        } else {
                            Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                        }
                        getAnError(context as Activity,anError)

                    }
                })
    }


    fun getSearchDocument(type: String, s: String) {
        list.clear()
//          Utils.instance.showProgressBar(activity)
        if (pdialog != null) {
            pdialog.dismiss()
        }
        pdialog = ProgressDialog(activity)
        pdialog.setCancelable(false)
        pdialog.setTitle("Please Wait.......")
        pdialog.setMessage("Loading...........")
        pdialog.show()
        AndroidNetworking.cancel("h")


        AndroidNetworking.post(Utils.BASE_URL + Utils.GET_ALL_TYPEDOCUMENT)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("type", typeo).addBodyParameter("search", s)
                .setPriority(Priority.HIGH).setTag("h")
                .build()
                .getAsObject(GetDocumentPojo::class.java, object : ParsedRequestListener<GetDocumentPojo> {
                    override fun onResponse(user: GetDocumentPojo) {
                        pdialog.dismiss()
                        if (user.success.equals(true)) {
                            if (user.data.data.size > 0) {
                                list.clear()

                                list.addAll(user.data.data)
                                if (user.data.nextPageUrl != null) {
                                    next_page_url = user.data.getNextPageUrl().toString()
                                } else {
                                    next_page_url = ""
                                }
                                isLoding = false
                                recyclerView.adapter!!.notifyDataSetChanged()
                                recyclerView.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            } else {
                                recyclerView.visibility = View.GONE
                                txt_nodata.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        pdialog.dismiss()
                        Log.e("Anerror", ">>>>>>>>>>>>>>>>>" + anError.errorBody)
                        if (anError.errorBody == null) {

                        } else {
                            Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                        }
                        getAnError(context as Activity,anError)

                    }
                })
    }

    override fun onClick(position: Int) {

        requestDocument(position)

    }

    override fun onDelete(position: Int) {

    }

    private fun requestDocument(id:Int){

        Utils.instance.showProgressBar(activity)
        val androidNetworking= AndroidNetworking.post(Utils.BASE_URL + Utils.REQUEST_DOCUMENT)

        //AndroidNetworking.upload(Utils.BASE_URL + Utils.SUBMIT_PROBLEM)
        androidNetworking.addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
        androidNetworking.addHeaders("Accept", "application/json")
        androidNetworking.addBodyParameter("id", id.toString())


        //.addMultipartFile("video", videoToUpload)
        androidNetworking.setPriority(Priority.HIGH)
        androidNetworking.build()
                .getAsObject(ProblemListResponse::class.java, object : ParsedRequestListener<ProblemListResponse> {
                    override fun onResponse(user: ProblemListResponse) {
                        Utils.instance.dismissProgressDialog()


                        if (user.success == true) {

                            fragmentManager?.beginTransaction()?.replace(R.id.flContent, GoToProposalFragment())?.addToBackStack(null)?.commit()


                        } else {
                            Toast.makeText(activity, "Unable to request document", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()

                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }



}
