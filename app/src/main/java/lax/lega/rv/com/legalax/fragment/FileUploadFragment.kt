package lax.lega.rv.com.legalax.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.JsonElement
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.models.sort.SortingTypes
import droidninja.filepicker.utils.Orientation
import kotlinx.android.synthetic.main.file_upload_activity.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.adapter.ImageAdapter
import lax.lega.rv.com.legalax.common.ConnectionDetector
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.ProblemCategoryData
import lax.lega.rv.com.legalax.pojos.ProblemCategoryResponse
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class FileUploadFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    val RC_PHOTO_PICKER_PERM = 123
    val RC_FILE_PICKER_PERM = 321
    val CUSTOM_REQUEST_CODE = 532
    val MAX_ATTACHMENT_COUNT = 5
    var hashSet = HashSet<File>()
    var hashSet1 = HashSet<String>()
    var photoPaths = ArrayList<String>()
    var docPaths = ArrayList<String>()
    lateinit var backpress1: ImageView
    lateinit var btn_upload: Button
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var rl_sendFile: RelativeLayout
    lateinit var v: View
    private var listCategoryNames=ArrayList<String>()
    private var listCategoryIds=ArrayList<Int>()
    internal var break_list = arrayOf("Select type", "Personal", "Business", "Property", "Finance")
    lateinit var spn_break: Spinner
    private var selectedCategoryId:Int?=null
    var role: Int? = null
    var type: String? = null
    var type_value: String? = null
    lateinit var listView: ListView
    val filePathsName = ArrayList<String>()
    val filePaths = ArrayList<String>()
    val fileArray = ArrayList<File>()
    lateinit var sharedPreference: MySharedPreference
    val progress_bar_type = 0
    private var listCategoryModel =ArrayList<ProblemCategoryData>()
    lateinit var RecyclerViewLayoutManager1: RecyclerView.LayoutManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.file_upload_activity, container, false)
        init()
        click()
        getCategory()

        setAdapterDocument()
        return v
    }

    fun onPickPhoto() {
        val maxCount = MAX_ATTACHMENT_COUNT - docPaths.size
        if (docPaths.size + photoPaths.size == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(activity, "Cannot select more than $MAX_ATTACHMENT_COUNT items",
                    Toast.LENGTH_SHORT).show()
        } else {
            FilePickerBuilder.instance
                    .setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select media")
                    .enableVideoPicker(true)
                    .enableCameraSupport(true)
                    .showGifs(true)
                    .showFolderView(true)
                    .enableSelectAll(false)
                    .enableImagePicker(true)
                    .setCameraPlaceholder(R.drawable.custom_camera)
                    .withOrientation(Orientation.UNSPECIFIED)
                    .pickPhoto(this, CUSTOM_REQUEST_CODE)
        }
    }


    fun onPickAllDoc() {
        val maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size
        if (docPaths.size + photoPaths.size == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(activity, "Cannot select more than $MAX_ATTACHMENT_COUNT items",
                    Toast.LENGTH_SHORT).show()
        } else {
            FilePickerBuilder.instance
                    .setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .enableDocSupport(true)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .pickFile(this)
        }
    }


    fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.choose_dialog);
        dialog.setTitle("Title...");
        var radioDoc = dialog.findViewById(R.id.radioDoc) as RadioGroup
        var btn_continue = dialog.findViewById(R.id.btn_continue) as Button

        btn_continue.setOnClickListener {
            val selectedId = radioDoc.getCheckedRadioButtonId()
            val radioSexButton = dialog.findViewById<View>(selectedId) as RadioButton
            type = radioSexButton.text.toString()
            if (type.equals("Gallery and photos")) {
                if (EasyPermissions.hasPermissions(activity!!.applicationContext, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
                    dialog.dismiss()
                    onPickPhoto()
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.rationale_photo_picker),
                            RC_PHOTO_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER)
                }
            } else {
                if (EasyPermissions.hasPermissions(activity!!.applicationContext, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
                    dialog.dismiss()
                    onPickDoc()
                    //onPickAllDoc()
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.rationale_doc_picker),
                            RC_FILE_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER)
                }
            }
        }
        dialog.show()
    }

    private fun setAdapter() {
        val break_adapter = ArrayAdapter(requireActivity(), R.layout.spinner_text, break_list)
        break_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_break.adapter = break_adapter
    }

    private fun click() {
        backpress1.setOnClickListener {
            //            val intent = Intent(activity, MainActivity::class.java)
//            intent.putExtra("f", "")
//            startActivity(intent)
//            activity!!.finish()
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, DocumentFragment(false)).addToBackStack(null).commit()

        }
        rl_sendFile.setOnClickListener {


            when {
                role==0 -> {
                    Toast.makeText(activity, "Please select document type", Toast.LENGTH_LONG).show()
                }
                edt_document.text.toString().isEmpty() -> {
                    Toast.makeText(activity, "Please enter document name", Toast.LENGTH_LONG).show()
                }
                edt_disc.text.toString().isEmpty() -> {
                    Toast.makeText(activity, "Please enter document description", Toast.LENGTH_LONG).show()
                }
                edt_filename.text.toString().isEmpty() -> {
                    Toast.makeText(activity, "Please enter file name", Toast.LENGTH_LONG).show()
                }
                else -> {
                    setRequestBodyData()
                }
            }



//            val mutableList = mutableListOf(filePathsName)
//
//            val filePathsName1 = mutableList.iterator()
//            while (filePathsName1.hasNext()) {
//                if (filePathsName1.next().contains("")) {
//                    filePathsName.remove("")
//                }
//            }
//
//
//
////            System.out.println("tttt:: "+ listOf(filePathsName1).iterator().asSequence().toList())
//            /*if (filePathsName.size == 0) {
//                Toast.makeText(activity, "Please select file", Toast.LENGTH_LONG).show()
//            } else if (role == 0) {
//                Toast.makeText(activity, "Please select document", Toast.LENGTH_LONG).show()
//
//            } else {
//                setRequestBodyData()
//
//            }*/
//
//
//
//            if (fileArray.size != filePathsName.size) {
////                Toast.makeText(activity,"Some data is missing",Toast.LENGTH_LONG).show()
//            } else if (role == 0) {
//                Toast.makeText(activity, "Please Select Type of Document", Toast.LENGTH_SHORT).show()
//
//
//            }
//            else{
//                setRequestBodyData()
//
//            }
        }

        spn_break.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

              //  selectedCategoryId=listCategoryIds[position]

                if (position == 0) {
                    role = 0
                } else {
                    if (position == 1) {
                        role = 1

                    } else if (position == 2) {
                        role = 2

                    } else if (position == 3) {
                        role = 3

                    } else if (position == 4) {
                        role = 4
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btn_upload.setOnClickListener {
            //DialogShow()
            showDialog()
        }
    }

    private fun setRequestBodyData() {

        var requestBody: RequestBody
        val imageFileBody: ArrayList<MultipartBody.Part> = ArrayList()
        val listTitle=ArrayList<RequestBody>()
        val listDescription=ArrayList<RequestBody>()

        val rb_type = RequestBody.create(MediaType.parse("text/plain"), role.toString())
        val fileName = RequestBody.create(MediaType.parse("text/plain"), edt_filename.text.toString())


        listTitle.add(RequestBody.create(MediaType.parse("text/plain"),edt_document.text.toString()))
        listDescription.add(RequestBody.create(MediaType.parse("text/plain"),edt_disc.text.toString()))

//        for (i in fileArray.indices) {
//            val file = fileArray[i]
//            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//            imageFileBody.add(MultipartBody.Part.createFormData("docs[]", file.name, requestBody))
//        }

        /*var imageFileBody = ArrayList<RequestBody>()
        for (i in filePaths.indices) {
            val requestBody = RequestBody.create(okhttp3.MultipartBody.FORM, filePaths.get(i))
            imageFileBody.add(requestBody)
        }

*/
//        val TitleFileBody = ArrayList<RequestBody>()
//        for (i in fileArray.indices) {
//            val requestBody = RequestBody.create(okhttp3.MultipartBody.FORM, filePathsName[i])
//            TitleFileBody.add(requestBody)
//        }

        val connectionDetector = ConnectionDetector(activity)
        connectionDetector.isConnectingToInternet
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
              Utils.instance.showProgressBar(activity)
            val addPostcall = WebService().apiService
                    .add_event("Bearer " + sharedPreference.getString("access_token")
                            , "application/json"
                            , rb_type
                            , listTitle,fileName,listDescription)
            addPostcall.enqueue(object : retrofit2.Callback<JsonElement> {
                override fun onFailure(call: retrofit2.Call<JsonElement>?, t: Throwable?) {
                      Utils.instance.dismissProgressDialog()
                    Log.e("throwable", ">>>>>>>>>>>>>>>>>>>>>>>>" + t.toString())
                    Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                }

                override fun onResponse(call: retrofit2.Call<JsonElement>?, response: retrofit2.Response<JsonElement>?) {
                      Utils.instance.dismissProgressDialog()

                    if (response?.code()==401){

                        openLogoutPopup(context as Activity)

                    }else{
                        if (response?.body()?.asJsonObject?.get("success")?.asBoolean == true) {
                            Toast.makeText(activity, "Documents has been uploaded successfully.", Toast.LENGTH_LONG).show()
                            val fragmentManager = activity!!.supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.flContent, DocumentFragment(false)).addToBackStack(null).commit()

                        } else {
                            Toast.makeText(activity, "Unable to upload documents", Toast.LENGTH_LONG).show()

                        }
                    }

                }


            })


            /*          progressBar = ProgressBar(activity);
                      progressBar.setIndeterminate(false);
                      progressBar.setMax(100);
                      val totalProgressTime = 100
                      val t = object : Thread() {
                          override fun run() {
                              var jumpTime = 0
                             // if (jumpTime < totalProgressTime) {
                                  while (jumpTime < totalProgressTime) {
                                      try {
                                          Thread.sleep(200)
                                          jumpTime += 5
                                          progressBar.setProgress(jumpTime)
                                      } catch (e: InterruptedException) {
                                          // TODO Auto-generated catch block
                                          e.printStackTrace()
                                      }

                                  }
                            //  } else {

                             // }

                          }
                      }
                      t.start()*/


        }

    }


    fun onPickDoc() {
        val zips = arrayOf(".zip", ".rar")
        val pdfs = arrayOf(".pdf")
        val doc = arrayOf(".doc", ".docs")
        val xls = arrayOf(".xls")
        val maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size
        if (docPaths.size + photoPaths.size == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(activity, "Cannot select more than $MAX_ATTACHMENT_COUNT items",
                    Toast.LENGTH_SHORT).show()
        } else {
            FilePickerBuilder.instance
                    .setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select doc")
                    .addFileSupport("ZIP", zips)
                    .addFileSupport("PDF", pdfs, R.drawable.pdf_blue)
                    .addFileSupport("DOC", doc)
                    .addFileSupport("XLS", xls)
                    .enableDocSupport(true)
                    .enableSelectAll(true)
                    .sortDocumentsBy(SortingTypes.name)
                    .withOrientation(Orientation.UNSPECIFIED)
                    .pickFile(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CUSTOM_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK && data != null) {
                photoPaths = ArrayList()
                if (data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA) != null)
                photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)!!)
            }

            FilePickerConst.REQUEST_CODE_DOC -> if (resultCode == Activity.RESULT_OK && data != null) {
                docPaths = ArrayList()
                if (data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS) != null)
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)!!)
            }
        }

        addThemToView(photoPaths, docPaths)
    }


    private fun addThemToView(imagePaths: ArrayList<String>?, docPaths: ArrayList<String>?) {

        if (imagePaths != null) filePaths.addAll(imagePaths)

        if (docPaths != null) filePaths.addAll(docPaths)

        hashSet1.addAll(filePaths);
        filePaths.clear();
        filePaths.addAll(hashSet1);

        for (i in filePaths.indices) {
            val file1 = File(filePaths.get(i))
            fileArray.add(file1)
        }

        hashSet.addAll(fileArray);
        fileArray.clear();
        fileArray.addAll(hashSet);

        filePathsName.add("")

        if (recyclerView != null) {
            RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
            recyclerView.layoutManager = RecyclerViewLayoutManager1
            val imageAdapter = ImageAdapter(activity!!.applicationContext, filePaths, filePathsName, activity!!)
            recyclerView.adapter = imageAdapter
            recyclerView.itemAnimator = DefaultItemAnimator()
        }

    }


    private fun setAdapterDocument(){
        RecyclerViewLayoutManager1 = LinearLayoutManager(activity)
        recyclerView.layoutManager = RecyclerViewLayoutManager1
        val imageAdapter = ImageAdapter(activity!!.applicationContext, filePaths, filePathsName, activity!!)
        recyclerView.adapter = imageAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun init() {
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        rl_sendFile = v.findViewById<View>(R.id.rl_sendFile) as RelativeLayout
        spn_break = v.findViewById<View>(R.id.spn_break) as Spinner
        btn_upload = v.findViewById<View>(R.id.btn_upload) as Button
        recyclerView = v.findViewById<View>(R.id.recyclerView) as RecyclerView
        progressBar = v.findViewById<View>(R.id.progressBar) as ProgressBar
        sharedPreference = MySharedPreference(activity)

//        customAdapter = CustomAdapter(activity, f);
//        listView.adapter = customAdapter

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

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

                            listCategoryIds.clear()
                            listCategoryNames.clear()
                            listCategoryModel.clear()

                            listCategoryNames.add("Select Category")
                            listCategoryIds.add(-1)

                            if (user.data?.isNotEmpty() == true) {
                                listCategoryModel.clear()
                                listCategoryModel.addAll(user.data ?: emptyList())
                            }

                            for (item in listCategoryModel){
                                listCategoryNames.add(item.name?:"")
                                listCategoryIds.add(item.id?:-1)
                            }

                            setAdapter()

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

}
