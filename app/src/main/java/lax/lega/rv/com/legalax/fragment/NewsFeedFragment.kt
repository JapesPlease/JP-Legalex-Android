package lax.lega.rv.com.legalax.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.newsfeed_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.NewsFeeddapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetNewsFeedPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError

class NewsFeedFragment : Fragment() {
    //    lateinit var product_recycler: RecyclerView
    private val REQUEST_CAMERA_PERMISSIONS = 931
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager
    lateinit var v: View
    lateinit var img_add: ImageView
    lateinit var img_profile: CircleImageView
    lateinit var sharedPreference: MySharedPreference
    val list = ArrayList<GetNewsFeedPojo.Response>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.newsfeed_activity, container, false)
        initt()
        click()
        if (Build.VERSION.SDK_INT > 15)
        {
            addPermission()
        } else {
            // click()
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun click()
    {
        img_add.setOnClickListener {
            //   val fragment = arrayOf<Fragment>(null)
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, AddNewsFeedFragment()).addToBackStack(null).commit()
        }

        img_profile.setOnClickListener( {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).addToBackStack(null).commit()

        })
    }

    private fun initt()
    {
        img_add = v.findViewById<View>(R.id.img_add) as ImageView
        img_profile = v.findViewById<View>(R.id.img_profile) as CircleImageView
        sharedPreference = MySharedPreference(activity)
        if (sharedPreference.getString("role").equals("1")) {
            img_add.visibility = View.VISIBLE
        } else {
            img_add.visibility = View.INVISIBLE
        }

        Picasso.with(activity).load(Utils.IMAGESURL + sharedPreference.getString("profile_image")).placeholder(R.drawable.icn_user_large).into(img_profile)

    }


    fun addPermission()
    {
        if (Build.VERSION.SDK_INT > 15)
        {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE)

            val permissionsToRequest = java.util.ArrayList<String>()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(activity!!, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission)
                }
            }
            if (!permissionsToRequest.isEmpty())
            {
                ActivityCompat.requestPermissions(activity!!, permissionsToRequest.toTypedArray(), REQUEST_CAMERA_PERMISSIONS)
            } else {
                // click()
            }
        } else {
            // click()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size != 0) {
            ///  click()
        }
    }

    fun setProductRecyclerView()
    {
        //  product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        recycler.layoutManager = RecyclerViewLayoutManager1
        val categoryNameAdapter = NewsFeeddapter(activity!!, list, activity!!)
        recycler.adapter = categoryNameAdapter
    }

    override fun onResume() {
        super.onResume()
        val connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else
        {
            getNewsFeed()
        }   /*if (!connectionDetector.isConnectingToInternet()) {
            val alert = AlertDialog.Builder(MainActivity.mainActivity)

            alert.setTitle("Internet connection unavailable.")
            alert.setMessage("You must be connected to an internet connection via WI-FI or Mobile Connection.")
            alert.setPositiveButton("OK") { dialog, whichButton -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }

            alert.show()
        } else {
            getNewsFeed()
        }*/
    }

    fun getNewsFeed()
    {
        list.clear()
          Utils.instance.showProgressBar(activity)
        AndroidNetworking.get(Utils.BASE_URL + Utils.GET_NEWSFEED)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetNewsFeedPojo::class.java, object : ParsedRequestListener<GetNewsFeedPojo> {
                    override fun onResponse(user: GetNewsFeedPojo) {
                          Utils.instance.dismissProgressDialog()

                        if (user.success.equals(true)) {
                            System.out.println("" + user.response)
                            if (user.response.size > 0) {
                                list.addAll(user.response)
                                setProductRecyclerView()
                                recycler.visibility = View.VISIBLE
                                txt_nodata.visibility = View.GONE
                            } else {
                                recycler.visibility = View.GONE
                                txt_nodata.visibility = View.VISIBLE
                            }

                            /*  for (user in user.response.withIndex()) {
                                  Log.e("aaa", "===[=" + user.value.title)
                                  list.add(user)
                              }*/
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


}
