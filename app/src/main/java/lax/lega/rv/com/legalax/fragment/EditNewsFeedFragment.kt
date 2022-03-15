package lax.lega.rv.com.legalax.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
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
import kotlinx.android.synthetic.main.editnewsfeed.*
import kotlinx.android.synthetic.main.editnewsfeed.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.pojos.AddNewsFeedPojo
import lax.lega.rv.com.legalax.pojos.GetNewsFeedPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import lax.lega.rv.com.legalax.utils.MediaUtils
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.setVideoDuration
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class EditNewsFeedFragment : Fragment() {
    lateinit var v: View
    var file1: File? = null
    var image: String? = null
    var pos: Int? = null
    var id: Int? = null
    var imageUri: Uri? = null
    private val REQUEST_CAMERA_PERMISSIONS = 931
    lateinit var img_feed_gllery1: ImageView
    lateinit var txt_day: CustomTextviewHelvic
    lateinit var rl_submit: RelativeLayout
    lateinit var rl_profile: RelativeLayout
    lateinit var sharedPreference: MySharedPreference
    lateinit var connectionDetector: ConnectionDetector
    val list = ArrayList<GetNewsFeedPojo.Response>()
    //var organizationList = ArrayList<GetNewsFeedPojo.Response>()
    lateinit var img_profile:CircleImageView
    lateinit var imageutils: Imageutils
    private var isVideo=false
    private var videoToUpload: File? = null
    private var thumbNailFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.editnewsfeed, container, false)
        initt()
        setData()
        if (Build.VERSION.SDK_INT > 15) {
            addPermission()
        } else {
            click()
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun setData() {
        val transactionList = arguments!!.getSerializable("list") as ArrayList<GetNewsFeedPojo.Response>
        pos = arguments!!.getInt("size")
        id = transactionList.get(pos!!).id
        v.edt_title1.setText(transactionList.get(pos!!).title.toString())
        v.edt_description1.setText(transactionList.get(pos!!).description.toString())
        v.edt_author_name1.setText(transactionList.get(pos!!).authorName.toString())
        v.txt_day1.setText(transactionList.get(pos!!).date.toString())
        v.edt_story1.setText(transactionList.get(pos!!).story.toString())
        if (transactionList.get(pos!!).image !== "") {
            Picasso.with(context).load(Constant.NEWSFEEDURL + transactionList.get(pos!!).image).into(v.img_feed_gllery1)
//            image=Constant.NEWSFEEDURL + transactionList.get(pos!!).image
//            file1=File(image)
        }

        arguments!!.remove("list");
    }

    private fun initt() {
        img_feed_gllery1 = v.findViewById<ImageView>(R.id.img_feed_gllery1)
        txt_day = v.findViewById<CustomTextviewHelvic>(R.id.txt_day1)
        rl_submit = v.findViewById<RelativeLayout>(R.id.rl_submit1)
        rl_profile = v.findViewById<RelativeLayout>(R.id.rl_profile)
        img_profile=v.findViewById(R.id.img_profile)

        sharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)

        Picasso.with(activity).load(Utils.IMAGESURL + sharedPreference.getString("profile_image")).fit().placeholder(R.drawable.icn_user_large).into(img_profile)

    }

    fun addPermission() {
        if (Build.VERSION.SDK_INT > 15) {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE)

            val permissionsToRequest = ArrayList<String>()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(activity!!, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission)
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(activity!!, permissionsToRequest.toTypedArray(), REQUEST_CAMERA_PERMISSIONS)
            } else {
                click()
            }
        } else {
            click()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size != 0) {
            click()
        }
    }

    private fun click() {
        v.ll_calender1.setOnClickListener(View.OnClickListener {
            showCalender()
        })

        rl_profile.setOnClickListener(View.OnClickListener {
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).addToBackStack(null).commit()

        })

        rl_submit.setOnClickListener(View.OnClickListener {
            if (v.edt_title1.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter title", Toast.LENGTH_LONG).show()
            } else if (v.edt_description1.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter description", Toast.LENGTH_LONG).show()

            } else if (v.edt_author_name1.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter author name", Toast.LENGTH_LONG).show()

            } else if (v.txt_day1.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter date", Toast.LENGTH_LONG).show()

            } else if (v.edt_story1.text.toString().length == 0) {
                Toast.makeText(activity, "Please enter story", Toast.LENGTH_LONG).show()

            } else {

                val connectionDetector = ConnectionDetector(activity!!.applicationContext)
                connectionDetector.isConnectingToInternet
                if (connectionDetector.isConnectingToInternet === false) run {
                    Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
                    if (file1 == null) {
                        EditWithoutNewsFeed(v.edt_title1.text.toString(),v. edt_description1.text.toString(),
                                v.edt_author_name1.text.toString(), v.txt_day1.text.toString(),v. edt_story1.text.toString(), id!!)

                    } else {
                        AddNewsFeed(v.edt_title1.text.toString(), v.edt_description1.text.toString(),
                                v.edt_author_name1.text.toString(),v. txt_day1.text.toString(),v. edt_story1.text.toString(), id!!)
                    }
                }
                /* if (!connectionDetector.isConnectingToInternet()) {
                     val alert = android.support.v7.app.AlertDialog.Builder(MainActivity.mainActivity)

                     alert.setTitle("Internet connection unavailable.")
                     alert.setMessage("You must be connected to an internet connection via WI-FI or Mobile Connection.")
                     alert.setPositiveButton("OK") { dialog, whichButton -> startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }

                     alert.show()
                 } else {

                 }*/
            }
        })
        v.img_feed_gllery1.setOnClickListener(View.OnClickListener {
            selectOptionPopup()
        })
    }

    fun AddNewsFeed(title: String, description: String,
                    author_name: String, date: String, story: String, id: Int) {

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
                .addMultipartParameter("id", id.toString())
              //  .addMultipartFile("image", file1)
                  .addMultipartFile(key, value)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(AddNewsFeedPojo::class.java, object : ParsedRequestListener<AddNewsFeedPojo> {
                    override fun onResponse(user: AddNewsFeedPojo) {
                          Utils.instance.dismissProgressDialog()

                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "NewsFeed updated successfully", Toast.LENGTH_LONG).show()
                            val fragmentManager = activity!!.supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.flContent, NewsFeedFragment()).addToBackStack(null).commit()


                        } else {
                            Toast.makeText(activity, "Unable to update NewsFeed", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    fun EditWithoutNewsFeed(title: String, description: String,
                            author_name: String, date: String, story: String, id: Int) {

          Utils.instance.showProgressBar(activity)
        AndroidNetworking.upload(Utils.BASE_URL + Utils.ADD_NEWSFEED)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addMultipartParameter("title", title)
                .addMultipartParameter("description", description)
                .addMultipartParameter("author_name", author_name)
                .addMultipartParameter("date", date)
                .addMultipartParameter("story", story)
                .addMultipartParameter("id", id.toString())
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(AddNewsFeedPojo::class.java, object : ParsedRequestListener<AddNewsFeedPojo> {
                    override fun onResponse(user: AddNewsFeedPojo) {
                          Utils.instance.dismissProgressDialog()

                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "NewsFeed updated successfully", Toast.LENGTH_LONG).show()
                            val fragmentManager = activity!!.supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.flContent, NewsFeedFragment()).addToBackStack(null).commit()


                        } else {
                            Toast.makeText(activity, "Unable to update NewsFeed", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                          Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity,anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // TODO Auto-generated method stub
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
                    Glide.with(this).load(curThumb).into(img_feed_gllery1)
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
                    Glide.with(this).load(curThumb).into(img_feed_gllery1)

                }
            }
            200->{
                Utils.selected_image = getPickImageResultUri(data)
                try {
                    img_feed_gllery1.setImageBitmap(Utils.handleSamplingAndRotationBitmap(activity, Utils.selected_image))
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                image = getRealPathFromURI(activity!!, Utils.selected_image)

                file1 = File(image)
            }

            111->{
                val photo = data.extras?.get("data") as Bitmap
                img_feed_gllery1.setImageBitmap(photo)
                imageUri = getImageUri(activity!!, photo)
                image = getRealPathFromURI(activity!!, imageUri!!)
                file1 = File(image)
            }
            else-> {
                Utils.pimageUri = null
            }

        }

    }

    fun showCalender() {

        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(activity!!, R.style.Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> txt_day.text = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString() }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }


    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun imagedialog() {

        val items = arrayOf<CharSequence>("Pick from gallery", "Open camera", "Cancel")
        AlertDialog.Builder(activity, R.style.AppTheme_Dialog).setItems(items) { dialog, which ->
            dialog.dismiss()
            if (which == 0) {
                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, 200)
            } else if (which == 1) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 111)
            } else {
                dialog.dismiss()
            }
        }.show()
    }

    fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri() else data!!.data
    }


    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage = activity!!.getExternalCacheDir()
        if (getImage != null) {
            // outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            outputFileUri = FileProvider.getUriForFile(activity!!, activity!!.getPackageName() + ".provider", File(getImage!!.getPath(), "pickImageResult.jpeg"))
        }
        return outputFileUri
    }


    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
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
                imagedialog()
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
        android.support.v7.app.AlertDialog.Builder(requireActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

}
