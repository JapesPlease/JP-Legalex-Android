package lax.lega.rv.com.legalax.adapter

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import kotlinx.android.synthetic.main.item_view_document.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.fragment.SendFileFragment
import lax.lega.rv.com.legalax.interfaces.LegalConcernInterface
import lax.lega.rv.com.legalax.pojos.GetDocumentPojo
import lax.lega.rv.com.legalax.pojos.SendDocumentPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.getAnError
import java.io.File
import java.util.*


class DocumentAdapter(private val context: Context, private val list: ArrayList<GetDocumentPojo.Data.Datum>?, private val activity: Activity,var isRequest: Boolean,var listener: LegalConcernInterface) : RecyclerView.Adapter<DocumentAdapter.MyView>() {

    lateinit var itemView: View
    internal var mySharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector

    init {
        mySharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(context)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentAdapter.MyView {

        itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view_document, parent, false)

        return MyView(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: DocumentAdapter.MyView, position: Int) {
        if (list!!.get(position).title == null) {
            holder.txt_title.text = "No title available"
        } else {
            holder.txt_title.text = list[position].title
        }
        if (list!!.get(position).document == null) {
            holder.txt_filename.text = "No file available"
        } else {
            holder.txt_filename.text = list[position].document
        }

        if (mySharedPreference.getString("role").equals("3")||mySharedPreference.getString("role").equals("4")) {
            holder.img_send.visibility = View.GONE
        } else {
            holder.img_send.visibility = View.VISIBLE
        }


        if (isRequest){
            itemView.tvRequest.visibility=View.VISIBLE
            holder.img_download.visibility=View.GONE
            holder.img_send.visibility=View.GONE
        }else{
            itemView.tvRequest.visibility=View.GONE
            holder.img_download.visibility=View.VISIBLE
            holder.img_send.visibility=View.VISIBLE
        }

        holder.tvRequest.setOnClickListener {

            listener.onClick(list[position].id)
//            val fragment=GoToProposalFragment()
//            val bundle=Bundle()
//            bundle.putInt("id",list[position].id)
//            fragment.arguments=bundle

        }

        holder.img_send.setOnClickListener(View.OnClickListener {

            val fragment = SendFileFragment()
            val bundle = Bundle()
            bundle.putString("document_id", list[position].id.toString())
            fragment.arguments = bundle
            (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit()


            /*val connectionDetector = ConnectionDetector(activity!!.applicationContext)
            connectionDetector.isConnectingToInternet
            if (connectionDetector.isConnectingToInternet === false) run {
                Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {
                SendDocument(list!!.get(position).userId.toString(), list!!.get(position).id.toString(), position)
            }*/

        })
        Log.e("PDFFFFS", ">>>>>>>>>>>>" + list[position].document)
        holder.img_download.setOnClickListener {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
            } else {
                val url = Constant.DOCUMENTURL + list[position].document
                val request = DownloadManager.Request(Uri.parse(url))
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                request.setTitle("Downloading")
                request.setDescription("Downloading" + list[position].title)
                request.setVisibleInDownloadsUi(false)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Legalex Doc/" + "/" + list.get(position).document)
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                var downloadID = downloadManager.enqueue(request)
                Toast.makeText(activity, "Downloading Started", Toast.LENGTH_SHORT).show()
//                Download_Uri = Uri.parse(list.get(position).document)
//                val request = DownloadManager.Request(Download_Uri)
//                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//                request.setAllowedOverRoaming(false)
//                request.setTitle("GadgetSaint Downloading " + "Sample" + ".png")
//                request.setDescription("Downloading " + "Sample" + ".png")
//                request.setVisibleInDownloadsUi(true)
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/GadgetSaint/" + "/" + "Sample" + ".png")
//                refid = downloadManager.enqueue(request)

//                Downloader.DownloadFile(Constant.DOCUMENTURL + list!!.get(position).document, file)

//                showPdf()
//            }
//            else {
//                downloadImage(Constant.DOCUMENTURL + list!!.get(position).document)
//            }
            }

        }
    }


    fun showPdf() {
        val file = File(Environment.getExternalStorageDirectory().toString() + "/pdf/Read.pdf")
        val packageManager = context.packageManager
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.type = "application/pdf"
        val list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "application/pdf")
        context.startActivity(intent)
    }


    override fun getItemCount(): Int {
        return list!!.size
    }


    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        internal var txt_title: CustomTextviewBold
        internal var tvRequest: CustomTextviewBold
        internal var txt_filename: CustomTextviewHelvicNormal
        internal var img_send: ImageView
        internal var img_download: ImageView

        init {
            txt_title = view.findViewById<View>(R.id.txt_title) as CustomTextviewBold
            tvRequest = view.findViewById<View>(R.id.tvRequest) as CustomTextviewBold
            txt_filename = view.findViewById<View>(R.id.txt_filename) as CustomTextviewHelvicNormal
            img_download = view.findViewById<View>(R.id.img_download) as ImageView
            img_send = view.findViewById<View>(R.id.img_send) as ImageView

        }
    }

    fun SendDocument(receiver_id: String, document_id: String, pos: Int) {
         Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(Utils.BASE_URL + Utils.SHARE_DOCUMENT)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("receiver_id", receiver_id)
                .addBodyParameter("document_id", document_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(SendDocumentPojo::class.java, object : ParsedRequestListener<SendDocumentPojo> {
                    override fun onResponse(user: SendDocumentPojo) {
                          Utils.instance.dismissProgressDialog()
                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "Document has been send successfully", Toast.LENGTH_LONG).show()

                        } else {
                            Toast.makeText(activity, "Document has not been send successfully", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    fun downloadImage(uRl: String) {
        val direct = File(Environment.getExternalStorageDirectory().toString() + "/AnhsirkDasarp")

        if (!direct.exists()) {
            direct.mkdirs()
        }

        val mgr = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(uRl)
        val request = DownloadManager.Request(
                downloadUri)

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/AnhsirkDasarpFiles", "fileName.jpg")

        mgr.enqueue(request)

        // Open Download Manager to view File progress
        Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG).show()
        context.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }
}

