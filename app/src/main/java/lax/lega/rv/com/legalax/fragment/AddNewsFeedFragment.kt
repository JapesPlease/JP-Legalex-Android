package lax.lega.rv.com.legalax.fragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_add_news_feed.*
import kotlinx.android.synthetic.main.fragment_add_news_feed.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.pojos.AddNewsFeedPojo
import lax.lega.rv.com.legalax.utils.MediaUtils
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.setVideoDuration
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AddNewsFeedFragment : Fragment(), Imageutils.ImageAttachmentListener {
    lateinit var v: View
    var file1: File? = null
    var image: String? = null
    var imageUri: Uri? = null
    private val REQUEST_CAMERA_PERMISSIONS = 931
    lateinit var img_feed_gllery: ImageView
    lateinit var rl_profile: RelativeLayout
    lateinit var txt_day: CustomTextviewHelvic
    lateinit var rl_submit: RelativeLayout
    lateinit var sharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector
    lateinit var imageutils: Imageutils
    lateinit var bitmap: Bitmap
    lateinit var localFile: File
    lateinit var localVideoFile: File
    lateinit var img_profile:CircleImageView
    lateinit var mainActivity:MainActivity


    private var videoToUpload: File? = null
    private var thumbNailFile: File? = null
    private var isVideo=false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_add_news_feed, container, false)


        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initt()
        click()
        Hidekeyboardd(v.main_layout)

    }

    private fun Hidekeyboardd(main_layout: LinearLayout?): Boolean
    {
        main_layout!!.performClick()
        main_layout!!.setOnTouchListener{ view: View, motionEvent: MotionEvent ->
            mainActivity.HideKeyboard(motionEvent)

        }
        return true

    }

    private fun initt() {
        mainActivity = activity as MainActivity
        img_feed_gllery = v.findViewById<ImageView>(R.id.img_feed_gllery)
        txt_day = v.findViewById<CustomTextviewHelvic>(R.id.txt_day)
        rl_submit = v.findViewById<RelativeLayout>(R.id.rl_submit)
        rl_profile = v.findViewById<RelativeLayout>(R.id.rl_profile)
        img_profile=v.findViewById(R.id.img_profile)


        sharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        imageutils = Imageutils(activity)
        imageutils.setListener(this@AddNewsFeedFragment)

        Picasso.with(activity).load(Utils.IMAGESURL + sharedPreference.getString("profile_image")).fit().placeholder(R.drawable.icn_user_large).into(img_profile)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size != 0) {
            click()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun click() {


        v.ll_calender.setOnClickListener(View.OnClickListener {
            //            val location = IntArray(2)
//            v.getLocationOnScreen(location)
//            val point = Point()
//            point.x = location[0]
//            point.y = location[1]
//            showcommentPopup(activity!!, point)
            showCalender()
        })


        rl_submit.setOnClickListener(View.OnClickListener {
            if (v.edt_title.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter title", Toast.LENGTH_LONG).show()
            } else if (v.edt_description.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter description", Toast.LENGTH_LONG).show()

            } else if (v.edt_author_name.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter author name", Toast.LENGTH_LONG).show()

            } else if (txt_day.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter date", Toast.LENGTH_LONG).show()

            } else if (v.edt_story.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter story", Toast.LENGTH_LONG).show()

            } else if (file1 == null && videoToUpload==null) {
                Toast.makeText(activity, "Please select image or video", Toast.LENGTH_LONG).show()

            } else {
                val connectionDetector = ConnectionDetector(activity!!.applicationContext)
                connectionDetector.isConnectingToInternet
                if (connectionDetector.isConnectingToInternet === false) run {
                    Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
                    AddNewsFeed(v.edt_title.text.toString(),v. edt_description.text.toString(),
                            v.edt_author_name.text.toString(), txt_day.text.toString(), v.edt_story.text.toString())
                }        /*  if (!connectionDetector.isConnectingToInternet()) {
                    val alert = android.support.v7.app.AlertDialog.Builder(activity!!.applicationContext)

                    alert.setTitle("Internet connection unavailable.")
                    alert.setMessage("You must be connected to an internet connection via WI-FI or Mobile Connection.")
                    alert.setPositiveButton("OK") { dialog, whichButton -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }

                    alert.show()
                } else {

                }*/
            }
        })
        img_feed_gllery.setOnClickListener(View.OnClickListener {
            // imagedialog()
          //  imageutils.imagepicker(2)
          //   permission_check(2)

            selectOptionPopup()
        })


        rl_profile.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).addToBackStack(null).commit()

        })


    }

    fun selectOptionPopup() {
        val items: Array<CharSequence?>

            items = arrayOfNulls(2)
            items[0] = "Photo"
            items[1] = "Video"

        val alertdialog = android.app.AlertDialog.Builder(requireContext())
        alertdialog.setTitle("Upload Images")
        alertdialog.setItems(items) { dialog, item ->
            if (items[item] == "Photo") {
                isVideo=false
                imageutils.imagepicker(2)
            } else if (items[item] == "Video") {
                isVideo=true
                permission_check(2)
            }
        }
        alertdialog.show()
    }

    fun permission_check(code: Int) {
        val hasWriteContactsPermission = ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showMessageOKCancel("For adding images , You need to provide permission to access your files",
                        DialogInterface.OnClickListener { dialog, which ->
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    code)
                        })
                return
            }
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    code)
            return
        }

        ImageUtilsNew.displayVideoPicker(requireContext())

    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)

            when(requestCode) {

                ImageUtilsNew.REQ_CODE_CAMERA_VIDEO -> {
                    videoToUpload = ImageUtilsNew.getVideoFile(context as Context)
                    val curThumb = ThumbnailUtils.createVideoThumbnail(
                            videoToUpload?.absolutePath.toString(),
                            MediaStore.Video.Thumbnails.MINI_KIND)

                    thumbNailFile = MediaUtils.getThumbnailFromVideo(videoToUpload?.path
                            ?: "", requireActivity().cacheDir.absolutePath,
                            MediaStore.Video.Thumbnails.MINI_KIND)

                    if (setVideoDuration(requireContext(), videoToUpload, view) > 30010) {

                        Toast.makeText(requireContext(), getString(R.string.file_size_video), Toast.LENGTH_SHORT).show()
                        //  view?.showSnackBar(getString(R.string.file_size_video))

                    } else {
                        Glide.with(this).load(curThumb).into(img_feed_gllery)
                    }
                }
                ImageUtilsNew.REQ_CODE_GALLERY_VIDEO -> {
                    videoToUpload = ImageUtilsNew.getVideoPathFromGallery(
                            context as Context, data?.data
                            ?: Uri.EMPTY)
                    val curThumb = ThumbnailUtils.createVideoThumbnail(
                            videoToUpload?.absolutePath.toString(),
                            MediaStore.Video.Thumbnails.MINI_KIND)

                    thumbNailFile = MediaUtils.getThumbnailFromVideo(videoToUpload?.path
                            ?: "", requireActivity().cacheDir.absolutePath,
                            MediaStore.Video.Thumbnails.MINI_KIND)

                    if (setVideoDuration(requireContext(), videoToUpload, view) > 30010) {
                        Toast.makeText(requireContext(), getString(R.string.file_size_video), Toast.LENGTH_SHORT).show()
                    } else {
                        Glide.with(this).load(curThumb).into(img_feed_gllery)

                    }
                }


                else->{

                    imageutils.onActivityResult(requestCode, resultCode, data)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val imageBytes = baos.toByteArray()

                 //   Glide.with(this).load(imageBytes).into(img_feed_gllery)
                }

            }

        } catch (e: Exception) {
        }


    }

    override fun image_attachment(from: Int, filename: String, file: Bitmap, uri: Uri) {
        bitmap = file
        val path = Environment.getExternalStorageDirectory().toString() + File.separator + "ImageAttach" + File.separator
        imageutils.createImage(file, filename, path, false)
        localFile = getFileFromImage(activity!!, filename, bitmap)
        img_feed_gllery.setImageBitmap(bitmap)
        file1 = localFile
        Log.d("filename", filename)

    }

    fun getFileFromImage(activity: Activity, filename: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val f = File(activity.cacheDir, filename)
        try {
            f.createNewFile()
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

        } catch (e: IOException) {
            Log.e("aaa", "-=-=IOException at onClick_signup -=$e")
        }

        return f
    }


    fun AddNewsFeed(title: String, description: String,
                    author_name: String, date: String, story: String) {

        var key=""
        var value:File?
        if (isVideo){
            key="video"
            value=videoToUpload
        }else{
            key="image"
            value=file1
        }

         Utils.instance.showProgressBar(activity)
        AndroidNetworking.upload(Utils.BASE_URL + Utils.ADD_NEWSFEED)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addMultipartParameter("title", title)
                .addMultipartParameter("description", description)
                .addMultipartParameter("author_name", author_name)
                .addMultipartParameter("date", date)
                .addMultipartParameter("story", story)
                .addMultipartFile(key, value)
            //    .addMultipartFile("video", videoToUpload)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(AddNewsFeedPojo::class.java, object : ParsedRequestListener<AddNewsFeedPojo> {
                    override fun onResponse(user: AddNewsFeedPojo) {
                        Utils.instance.dismissProgressDialog()

                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "NewsFeed added successfully", Toast.LENGTH_LONG).show()
                            val fragmentManager = activity!!.supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.flContent, NewsFeedFragment()).addToBackStack(null).commit()


                        } else {
                            Toast.makeText(activity, "Unable to add NewsFeed", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


    fun showCalender() {
        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(activity!!, R.style.Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> txt_day.text = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString() }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

}
