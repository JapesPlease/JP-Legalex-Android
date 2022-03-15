package lax.lega.rv.com.legalax.fragment

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_submit_legal_problem.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.ImageUtilsNew
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.ProblemCategoryData
import lax.lega.rv.com.legalax.pojos.ProblemCategoryResponse
import lax.lega.rv.com.legalax.pojos.SubmitFormResponse
import lax.lega.rv.com.legalax.utils.getAnError
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SubmitLegalProblemFragment(var header: String) : Fragment(), View.OnClickListener {

    private var listCategory =ArrayList<String>()
    private var listCategoryModel =ArrayList<ProblemCategoryData>()
    private var fileToUpload: File? = null
    lateinit var sharedPreference: MySharedPreference
    private var selectedcategory=""
    private var selectedcategoryId:Int?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submit_legal_problem, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
        getCategory()

        /*to scroll inside edit text*/
        etDescription.setOnTouchListener(View.OnTouchListener { v, event ->
            if (etDescription.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
    }

    private fun init(){
        sharedPreference = MySharedPreference(activity)
        tvHeader.text= header
    }

    private fun setCategoryAdapter() {
        val breakAdapter = ArrayAdapter(requireContext(), R.layout.spinner_text, listCategory)
        breakAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCategory.adapter = breakAdapter
    }

    private fun listeners(){

        ivBack.setOnClickListener(this)
        clAttach.setOnClickListener(this)
        tvSubmit.setOnClickListener(this)

        spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //"Select Role", "Admin", "Regular User", "SME User", "Lawyer"

               if (position == 0) {
                   selectedcategory= ""
                   selectedcategoryId=null
                } else {
                   selectedcategory= listCategory[position]
                   selectedcategoryId=listCategoryModel[position - 1].id

                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


    }

    private fun checkvalidations(){
        when{
            selectedcategory=="" -> {
               Toast.makeText(requireContext(), "Please select category", Toast.LENGTH_SHORT).show()
            }
            etTitle.text.toString().isEmpty()->{
                Toast.makeText(requireContext(), "Please enter title", Toast.LENGTH_SHORT).show()
            }
            etDescription.text.toString().isEmpty()->{
                Toast.makeText(requireContext(), "Please enter description", Toast.LENGTH_SHORT).show()
            }

            else->{
                callSubmitProblemApi()
            }
        }
    }

    private fun callSubmitProblemApi(){
        if (header==getString(R.string.review_document_)){
            submitDocumentProblem()
        }else{
            submitProblem()
        }
    }

    private fun selectOptionPopup() {

        val items: Array<CharSequence?> = arrayOfNulls(4)
        items[0] = "Camera"
        items[1] = "Photo Library"
        items[2] = "Document"
        items[3] = "Cancel"

        val alertDialog = android.app.AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Upload Images")
        alertDialog.setItems(items) { dialog, item ->
            when {
                items[item] == "Camera" -> {
                    ImageUtilsNew.openCamera(requireContext())
                }
                items[item] == "Photo Library" -> {

                    if (Build.VERSION.SDK_INT >= 23) {
                        permission_check(1)
                    } else {
                        ImageUtilsNew.openGallery(requireContext())
                    }

                }
                items[item] == "Document" -> {
                    dialog.dismiss()
                  //  permission_check(2)

                    if (Build.VERSION.SDK_INT >= 23) {
                        permission_check(2)
                    } else {
                        ImageUtilsNew.openGalleryDoc(requireContext())
                    }


//                    val intent = Intent(Intent.ACTION_GET_CONTENT)
//                    //  intent.type = "application/pdf"
//                    intent.type = "application/pdf"
//                    startActivityForResult(intent, ImageUtilsNew.REQ_CODE_GALLERY_DOC)




//                    val intent=getDocIntent(requireContext(),"application/pdf","Documents")
//                    startActivityForResult(intent, ImageUtilsNew.REQ_CODE_GALLERY_DOC)

                }
                items[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        alertDialog.show()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.ivBack -> {
                activity?.onBackPressed()
            }
            R.id.tvSubmit -> {
                checkvalidations()
            }
            R.id.clAttach -> {
                selectOptionPopup()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ImageUtilsNew.REQ_CODE_CAMERA_PICTURE -> {
                    fileToUpload = ImageUtilsNew.getFile(requireContext())

                }

                ImageUtilsNew.REQ_CODE_GALLERY_PICTURE -> {
                    fileToUpload = ImageUtilsNew.getImagePathFromGallery(requireContext(), data?.data
                            ?: Uri.EMPTY)

                }

                ImageUtilsNew.REQ_CODE_GALLERY_DOC -> {


//                    val path = ImageUtilsNew.getPathFromUri(requireActivity(), data?.data
//                            ?: Uri.EMPTY)
//                    //  var file = File(URI(path))
//
//                    //  val path=data?.data?.path
//                    fileToUpload = File(path ?: "")

                    val dir = File(Environment.getExternalStorageDirectory(), "/app")
                    if (!dir.exists()) dir.mkdirs()
                    val uri = data?.data
                    val uriString = uri?.toString()
                    val pdfFile: File?
                    val myFile = File(uriString ?: "")
                    if (uriString?.startsWith("content://") == true) {
                        try {
                            val v = myFile.name.replace("[^a-zA-Z]+".toRegex(), "_") + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".pdf"
                            pdfFile = File(dir, v)
                            val fos = FileOutputStream(pdfFile)
                            val out = BufferedOutputStream(fos)
                            val inputStream = context?.contentResolver?.openInputStream(uri)
                            try {
                                val buffer = ByteArray(8192)
                                var len=0
                                while (inputStream?.read(buffer).also { len = it ?: 0 } ?: 0 >= 0) {
                                    out.write(buffer, 0, len)
                                }
                                out.flush()
                            } finally {
                                fos.fd.sync()
                                out.close()
                                inputStream?.close()
                            }
                            fileToUpload = pdfFile

                        } catch (e: Exception) {
                        }
                    } else if (uriString?.startsWith("file://") == true) {
                        fileToUpload = myFile

                    }


                }
            }

        }

        tvFileName.text=fileToUpload?.name
    }


    private fun submitProblem(){
        Utils.instance.showProgressBar(activity)


        val androidNetworking=AndroidNetworking.upload(Utils.BASE_URL + Utils.SUBMIT_PROBLEM)

        //AndroidNetworking.upload(Utils.BASE_URL + Utils.SUBMIT_PROBLEM)
        androidNetworking.addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
        androidNetworking.addHeaders("Accept", "application/json")
        androidNetworking.addMultipartParameter("cat", selectedcategoryId.toString())
        androidNetworking.addMultipartParameter("desc", etDescription.text.toString())
        androidNetworking.addMultipartParameter("title", etTitle.text.toString())
        if (fileToUpload!=null){
            androidNetworking.addMultipartFile("media", fileToUpload)
        }

                //    .addMultipartFile("video", videoToUpload)
        androidNetworking.setPriority(Priority.HIGH)
        androidNetworking.build()
                .getAsObject(SubmitFormResponse::class.java, object : ParsedRequestListener<SubmitFormResponse> {
                    override fun onResponse(user: SubmitFormResponse) {
                        Utils.instance.dismissProgressDialog()


                        if (user.success == true) {
                            //  Toast.makeText(activity, "Submit problem successfully", Toast.LENGTH_LONG).show()
//                            val fragment=GoToProposalFragment()
//                            val bundle=Bundle()
//                            bundle.putInt("id",-1)
//                            fragment.arguments=bundle
                            requireFragmentManager().beginTransaction().replace(R.id.flContent, GoToProposalFragment()).commit()


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


    private fun submitDocumentProblem(){
        Utils.instance.showProgressBar(activity)


        val androidNetworking=AndroidNetworking.upload(Utils.BASE_URL + Utils.SUBMIT_DOCUMENT_PROBLEM)

        //AndroidNetworking.upload(Utils.BASE_URL + Utils.SUBMIT_PROBLEM)
        androidNetworking.addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
        androidNetworking.addHeaders("Accept", "application/json")
        androidNetworking.addMultipartParameter("cat", selectedcategoryId.toString())
        androidNetworking.addMultipartParameter("desc", etDescription.text.toString())
        androidNetworking.addMultipartParameter("title", etTitle.text.toString())
        if (fileToUpload!=null){
            androidNetworking.addMultipartFile("media", fileToUpload)
        }

        //    .addMultipartFile("video", videoToUpload)
        androidNetworking.setPriority(Priority.HIGH)
        androidNetworking.build()
                .getAsObject(SubmitFormResponse::class.java, object : ParsedRequestListener<SubmitFormResponse> {
                    override fun onResponse(user: SubmitFormResponse) {
                        Utils.instance.dismissProgressDialog()


                        if (user.success == true) {
                            //  Toast.makeText(activity, "Submit problem successfully", Toast.LENGTH_LONG).show()
                            requireFragmentManager().beginTransaction().replace(R.id.flContent, GoToProposalFragment()).addToBackStack(null).commit()


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


    private fun getCategory() {
        AndroidNetworking.get(Utils.BASE_URL + Utils.GET_PROBLEM_CATEGORY)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(ProblemCategoryResponse::class.java, object : ParsedRequestListener<ProblemCategoryResponse> {
                    override fun onResponse(user: ProblemCategoryResponse) {

                        if (user.success == true) {

                            if (user.data?.isNotEmpty() == true) {
                                listCategoryModel.clear()
                                listCategoryModel.addAll(user.data ?: emptyList())
                                addCategoryData()
                            }


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

    private fun addCategoryData(){

        listCategory.clear()
        listCategory.add("Select Category")

        for (item in listCategoryModel){
            listCategory.add(item.name ?: "")
        }

        setCategoryAdapter()
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
        if (code == 1)
            ImageUtilsNew.openGallery(requireContext())
        else if (code == 2){
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Pdf"),
                    ImageUtilsNew.REQ_CODE_GALLERY_DOC)
        }
          //  ImageUtilsNew.openGalleryDoc(requireContext())
    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

}