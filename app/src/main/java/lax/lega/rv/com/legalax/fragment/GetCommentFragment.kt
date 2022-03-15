package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import kotlinx.android.synthetic.main.get_comment_activity.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.GetAllCommentAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.AddNewsFeedPojo
import lax.lega.rv.com.legalax.pojos.GetAllCommentPojo
import lax.lega.rv.com.legalax.pojos.GetNewsFeedPojo
import lax.lega.rv.com.legalax.pojos.PostCommentPojo
import lax.lega.rv.com.legalax.utils.getAnError
import java.util.*

class GetCommentFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var ll_bottom: LinearLayout
    // lateinit var getAllCommentAdapter: GetAllCommentAdapter
    //ArrayList<GetCommentModel> getCommentModels = new ArrayList<>();
    var dialog: ProgressDialog? = null
    var dialog1: ProgressDialog? = null
  //  lateinit var edt_comment: EditText
   // lateinit var txt_post: TextView
    lateinit var i: Intent
    lateinit var ll_back: LinearLayout
    var FromData = ""
    lateinit var v: View
    lateinit var sharedPreference: MySharedPreference
    var pos: Int? = null
    var id: Int? = null
    val list = ArrayList<GetAllCommentPojo.Comments.Datum>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.get_comment_activity, container, false)
        init()
        clcik()
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            list.clear()
            getAllComment(id.toString())
        }
    }

    private fun clcik() {
        v.rl_send.setOnClickListener {
            if (v.edt_comment.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter comment", Toast.LENGTH_LONG).show()
            } else {
                AddNewsFeedCOmment(id.toString(),v.edt_comment.text.toString())
                //CreateCOmment(MySharedPreference(activity).getString("id"), i.getStringExtra("post_id"), edt_comment.text.toString(), MySharedPreference(this@GetCommentActivity).getString("token"))
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.e("RecyclerView", "onScrollStateChanged")
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun setIntentData() {

    }

    private fun init() {
        recyclerView = v.findViewById(R.id.rv_comment) as RecyclerView
        ll_bottom = v.findViewById(R.id.ll_bottom) as LinearLayout
        sharedPreference = MySharedPreference(activity)
        val transactionList = arguments!!.getSerializable("list") as ArrayList<GetNewsFeedPojo.Response>
        pos = arguments!!.getInt("size")
        id = transactionList.get(pos!!).id
        arguments!!.remove("list");

        if(sharedPreference.getString("role").equals("1")){
            ll_bottom.visibility=View.VISIBLE
        }else{
            ll_bottom.visibility=View.GONE

        }
    }

    fun getAllComment(post_id: String) {
        list.clear()
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.GET_COMMENT)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("newsfeed_id", post_id)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d("TAG", " timeTakenInMillis : $timeTakenInMillis")
                    Log.d("TAG", " bytesSent : $bytesSent")
                    Log.d("TAG", " bytesReceived : $bytesReceived")
                    Log.d("TAG", " isFromCache : $isFromCache")
                }
                .getAsObject(GetAllCommentPojo::class.java, object : ParsedRequestListener<GetAllCommentPojo> {
                    override fun onResponse(user: GetAllCommentPojo) {
                          Utils.instance.dismissProgressDialog()

                        if (user.success.equals(true)) {
                            if (user.comments.data.size > 0) {
                                list.addAll(user.comments.data)
                            } else {

                            }
                            setrecyclerview()
                        } else {
                            Toast.makeText(activity, "Unable to add NewsFeed", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    private fun setrecyclerview() {
        val mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        val categoryNameAdapter = GetAllCommentAdapter(activity!!, list, activity!!)
        recyclerView.adapter = categoryNameAdapter
    }

    fun AddNewsFeedCOmment(newsfeed_id: String, comment: String) {

          Utils.instance.showProgressBar(activity)
        AndroidNetworking.upload(Utils.BASE_URL + Utils.ADD_COMMENT)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addMultipartParameter("newsfeed_id", newsfeed_id)
                .addMultipartParameter("comment", comment)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(PostCommentPojo::class.java, object : ParsedRequestListener<PostCommentPojo> {
                    override fun onResponse(user: PostCommentPojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "Comments has been added successfully.", Toast.LENGTH_LONG).show()
                            val fragmentManager = activity!!.supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.flContent, NewsFeedFragment()).addToBackStack(null).commit()
                        } else {
                            Toast.makeText(activity, "Comments has not been added successfully.", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


}
