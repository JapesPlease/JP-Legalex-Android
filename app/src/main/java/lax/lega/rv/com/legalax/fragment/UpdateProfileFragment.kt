package lax.lega.rv.com.legalax.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.hbb20.CountryCodePicker
import com.squareup.picasso.Picasso
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.MainActivity
import lax.lega.rv.com.legalax.adapter.SelectedCategoryAdapter
import lax.lega.rv.com.legalax.common.*
import lax.lega.rv.com.legalax.pojos.GetUserDetailPojo
import lax.lega.rv.com.legalax.pojos.UpdateProfilePojo
import lax.lega.rv.com.legalax.pojos.lawyerCategories.GetLawyerCategories
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.getAnError
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class UpdateProfileFragment : Fragment(), Imageutils.ImageAttachmentListener, SelectedCategoryAdapter.ClickHandler {
    lateinit var edt_email_sign: CustomEdittextHelvic
    lateinit var edt_firstname_sign: CustomEdittextHelvic
    lateinit var edt_lastname_sign: CustomEdittextHelvic
    lateinit var edt_age_sign: TextView
    lateinit var edt_location_sign: CustomEdittextHelvic
    lateinit var edt_number_sign: CustomEdittextHelvic
    lateinit var textViewAdd: CustomTextviewHelvicNormal
    lateinit var tvAboutUsTitle: CustomTextviewHelvicNormal
    lateinit var edt_aboutus: CustomEdittextHelvic
    lateinit var edt_information: CustomEdittextHelvic
    lateinit var img_feed_gllery: ImageView
    lateinit var btn_continue: Button
    lateinit var backpress1: ImageView
    lateinit var v: View
    lateinit var mySharedPreference: MySharedPreference
    var file1: File? = null
    var image: String? = null
    lateinit var connectionDetector: ConnectionDetector
    lateinit var imageutils: Imageutils
    lateinit var bitmap: Bitmap
    lateinit var localFile: File
    lateinit var ccp: CountryCodePicker
    var textLocation: String? = null
    private var selectedAge = ""
    lateinit var linearLayoutAge: LinearLayout
    lateinit var linearLayoutSpecification: LinearLayout
    lateinit var bottomView: View
    lateinit var recyclerView: RecyclerView


    lateinit var dialog: BottomSheetDialog
    var array = arrayOfNulls<String>(0)

    var allCategoriesList: MutableList<lax.lega.rv.com.legalax.pojos.lawyerCategories.Response> = ArrayList()
    var selectedCategories: MutableList<lax.lega.rv.com.legalax.pojos.lawyerCategories.Response> = ArrayList()

    var adapter: SelectedCategoryAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.update_profile_fragment, container, false)
        initt()
        click()
        if (connectionDetector.isConnectingToInternet === false) run {
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
        } else {
            getIdDetail(Utils.BASE_URL + Utils.GET_USER_DATA, mySharedPreference.getInt("id").toString())
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onResume() {
        if (mySharedPreference.getString("role") == "2") {
            getAllLawyerCatgory()
            getSelectedCategory()
        }
        super.onResume()
    }

    private fun getIdDetail(url: String, user_id: String?) {
        Utils.instance.showProgressBar(activity)
        AndroidNetworking.post(url)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("user_id", user_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(GetUserDetailPojo::class.java, object : ParsedRequestListener<GetUserDetailPojo> {
                    override fun onResponse(user: GetUserDetailPojo) {
                        Utils.instance.dismissProgressDialog()
                        if (user.user.role == 2)
                            tvAboutUsTitle.text = "About me"
                        if (user.user.email != null) {
                            edt_email_sign.setText(user.user.email)
                        } else {
                            edt_email_sign.setText("No data available")
                        }

                        if (user.user.name != null) {
                            edt_firstname_sign.setText(user.user.name)
                        } else {
                            edt_firstname_sign.setText("No data available")
                        }

                        if (user.user.lastName != null) {
                            edt_lastname_sign.setText(user.user.lastName)
                        } else {
                            edt_lastname_sign.setText("No data available")
                        }

                        if (user.user.age != null) {
                            edt_age_sign.setText(user.user.age.toString())
                        } else {
                            edt_age_sign.setText("No data available")
                        }

                        if (user.user.location != null) {
                            edt_location_sign.setText(user.user.location)
                        } else {
                            edt_location_sign.setText("No data available")
                        }

                        if (user.user.phone != null) {
                            var phone = user.user.phone
                            if (phone.contains("-", ignoreCase = true)) {
//                            phone = user.user.phone.substring(user.user.phone.lastIndexOf("-") + 1)
//                            edt_number_sign.setText(phone)
//                            var countrycode = user.user.phone.substring(1, user.user.phone.lastIndexOf("-"))
//                            ccp.setCountryForPhoneCode(countrycode.toInt())
                                var tmpPhone = user.user.phone.split("-")
                                if (tmpPhone.size == 1) {
                                    var countrycode = tmpPhone[0]
                                    ccp.setCountryForPhoneCode(countrycode.toInt())
                                }
                                if (tmpPhone.size >= 2) {
                                    var countrycode = tmpPhone[0]
                                    ccp.setCountryForPhoneCode(countrycode.toInt())
                                    var phone = tmpPhone[1]
                                    edt_number_sign.setText(phone)
                                }
                            }
                        } else {
                            edt_number_sign.setText("No data available")
                        }

                        if (user.user.aboutUs != null) {
                            edt_aboutus.setText(user.user.aboutUs)
                        } else {
                            edt_aboutus.setText("No data available")
                        }

                        if (user.user.information != null) {
                            edt_information.setText(user.user.information)
                        } else {
                            edt_information.setText("No data available")
                        }

                        if (user.user.profileImage != null) {
                            Picasso.with(activity).load(Utils.IMAGESURL + user.user.profileImage).fit().placeholder(R.drawable.icn_user_large).into(img_feed_gllery)
                        } else {
                            //     Picasso.with(activity).load("http://13.251.150.219/spree/public/profileimages/" + filelist.get(position).sellerinfo_user_image).placeholder(R.drawable.icn_profile).into(img_profile)
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun getAllLawyerCatgory() {
        Utils.instance.dismissProgressDialog()
        Utils.instance.showProgressBar(activity)
        val api = WebService().apiService.getAllLawyerCategory(
                "Bearer " + mySharedPreference.getString("access_token")
        )

        api.enqueue(object : retrofit2.Callback<GetLawyerCategories> {

            override fun onFailure(call: Call<GetLawyerCategories>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(activity!!, "Some thing went wrong", Toast.LENGTH_LONG).show()
               // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<GetLawyerCategories>, response: Response<GetLawyerCategories>) {
                Utils.instance.dismissProgressDialog()
                allCategoriesList = response.body()!!.response!!
                array = arrayOfNulls<String>(response.body()!!.response!!.size)
                for (i in response.body()!!.response!!.indices) {
                    array[i] = response.body()!!.response!![i].name
                }
            }
        })
    }

    private fun getSelectedCategory() {
        Utils.instance.dismissProgressDialog()
        Utils.instance.showProgressBar(activity)
        val api = WebService().apiService.getSelectedCategory(
                "Bearer " + mySharedPreference.getString("access_token")
        )

        api.enqueue(object : retrofit2.Callback<GetLawyerCategories> {

            override fun onFailure(call: Call<GetLawyerCategories>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(activity!!, "Some thing went wrong", Toast.LENGTH_LONG).show()
              //  Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<GetLawyerCategories>, response: Response<GetLawyerCategories>) {
                Log.e("response is", response.toString())
                Utils.instance.dismissProgressDialog()
                selectedCategories = response.body()!!.response!!
                setUpAdapter(selectedCategories)


            }
        })
    }


    @SuppressLint("SetTextI18n")
    private fun click() {
        if (mySharedPreference.getString("role") == "2") {
            textViewAdd.setOnClickListener {
                val dialogView: View = layoutInflater.inflate(R.layout.bottom_sheet, null)
                var textViewSearch = dialogView.findViewById<TextView>(R.id.textViewSearch)
                var textViewCancel = dialogView.findViewById<TextView>(R.id.textViewCancel)

                var picker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)

                textViewCancel.setText("Done")

                textViewCancel.setOnClickListener {
                    dialog.dismiss()
                }

                textViewSearch.setText("Add")

                textViewSearch.setOnClickListener {
                    dialog.dismiss()
                    for (i in selectedCategories.indices) {
                        if (allCategoriesList.get(picker.value).id == selectedCategories.get(i).id) {
                            return@setOnClickListener
                        }
                    }
                    var response = lax.lega.rv.com.legalax.pojos.lawyerCategories.Response()
                    response.name = allCategoriesList.get(picker.value).name
                    response.id = allCategoriesList.get(picker.value).id
                    selectedCategories.add(response)
                    updateCategory()
                }
                picker.setMinValue(0)
                picker.setMaxValue(array.size - 1)
                picker.setDisplayedValues(array!!)

                dialog = BottomSheetDialog(activity!!)
                dialog.setContentView(dialogView)
                dialog.show()
            }
        } else {
            bottomView.visibility = View.GONE
            linearLayoutSpecification.visibility = View.GONE
        }

        backpress1.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("f", "")
            startActivity(intent)
            activity!!.finish()
        }

        img_feed_gllery.setOnClickListener(View.OnClickListener {
            // imagedialog()
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PackageManager.PERMISSION_GRANTED)
            } else {
                //imageutils.imagepicker(2)
                selectOptionPopup()
            }
        })


        edt_age_sign.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            var cyear = c.get(Calendar.YEAR)
            var cmonth = c.get(Calendar.MONTH)
            var cday = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                    requireActivity(),
                    AlertDialog.THEME_HOLO_DARK,
                    object : DatePickerDialog.OnDateSetListener {
                        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
                            when (month) {
                                0 -> {
                                    selectedAge = "January ${year}"
                                }
                                1 -> {
                                    selectedAge = "February ${year}"
                                }
                                2 -> {
                                    selectedAge = "March ${year}"
                                }
                                3 -> {
                                    selectedAge = "April ${year}"
                                }
                                4 -> {
                                    selectedAge = "May ${year}"
                                }
                                5 -> {
                                    selectedAge = "June ${year}"
                                }
                                6 -> {
                                    selectedAge = "July ${year}"
                                }
                                7 -> {
                                    selectedAge = "August ${year}"
                                }
                                8 -> {
                                    selectedAge = "September ${year}"
                                }
                                9 -> {
                                    selectedAge = "October ${year}"
                                }
                                10 -> {
                                    selectedAge = "November ${year}"
                                }
                                11 -> {
                                    selectedAge = "December ${year}"
                                }
                                else -> selectedAge = ""
                            }
                            edt_age_sign.setText(selectedAge)
                        }
                    },
                    cyear, cmonth, cday)
            datePickerDialog.getDatePicker().findViewById<NumberPicker>(resources.getIdentifier("day", "id", "android")).setVisibility(View.GONE)
            datePickerDialog.show()
        }

        btn_continue.setOnClickListener {
            //            ccp.setDefaultCxountryUsingNameCode("+91")
            var countrycode = "+" + ccp.selectedCountryCode
            textLocation = edt_location_sign.text.toString()
            if (!isValidEmail(edt_email_sign.text.toString().trim())) {
                edt_email_sign.error = "Please enter the valid email address"
                edt_email_sign.requestFocus()
            } else if (edt_firstname_sign.text.isEmpty()) {
                edt_firstname_sign.error = "Please enter the First name"
                edt_firstname_sign.requestFocus()

            } else if (Utils.CheckStringLength(30, edt_firstname_sign.text.toString().trim())) {
                edt_firstname_sign.error = "you can add up to 30 characters"
                edt_firstname_sign.requestFocus()

            } else if (edt_lastname_sign.text.isEmpty()) {
                edt_lastname_sign.error = "Please enter the Last name"
                edt_lastname_sign.requestFocus()
            } else if (Utils.CheckStringLength(30, edt_lastname_sign.text.toString().trim())) {
                edt_lastname_sign.error = "you can add up to 30 characters"
                edt_lastname_sign.requestFocus()
            } else if (edt_age_sign.text.isEmpty()) {
                edt_age_sign.error = "Please enter the Age"
                edt_age_sign.requestFocus()
            } else if (edt_location_sign.text.isEmpty()) {
                edt_location_sign.error = "Please enter the Location"
                edt_location_sign.requestFocus()
            } else if (Utils.CheckStringLength(30, edt_location_sign.text.toString().trim())) {
                edt_location_sign.error = "you can add up to 30 characters"
                edt_location_sign.requestFocus()
            } else if (countrycode == "00") {
                Toast.makeText(activity, "Please select any country code", Toast.LENGTH_LONG).show()
            } else if (edt_number_sign.text.toString().isEmpty()) {
                edt_number_sign.error = "Please enter the valid mobile number"
                edt_number_sign.requestFocus()
            } else if (edt_aboutus.text.toString().isEmpty()) {
                edt_aboutus.error = "Write About us"
                edt_aboutus.requestFocus()
            } else if (Utils.CheckStringLength(200, edt_aboutus.text.toString().trim())) {
                edt_aboutus.error = "you can add up to 200 characters"
                edt_aboutus.requestFocus()
            } else if (edt_information.text.toString().isEmpty()) {
                edt_information.error = "Write About us"
                edt_information.requestFocus()
            } else if (Utils.CheckStringLength(1000, edt_information.text.toString().trim())) {
                edt_information.error = "you can add up to 1000 characters"
                edt_information.requestFocus()
            } else {
                if (connectionDetector.isConnectingToInternet === false) run {
                    Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
                } else {
                    if (edt_location_sign.text.isNotEmpty()) {
                        val p = Pattern.compile("([0-9])")
                        val m = p.matcher(textLocation)

                        if (m.find()) {
                            Toast.makeText(activity, "Please enter correct location", Toast.LENGTH_LONG).show()
                            edt_location_sign.error = "Please enter correct location"
                            edt_location_sign.requestFocus()
                        } else {
                            if (edt_firstname_sign.text.toString() == edt_lastname_sign.text.toString()) {
                                edt_firstname_sign.error = "First name and last name cannot be same"
                                edt_firstname_sign.requestFocus()
                            } else {
                                if (file1 == null) {
                                    Update_withoutprofile(edt_email_sign.text.toString(),
                                            edt_firstname_sign.text.toString(),
                                            edt_lastname_sign.text.toString(),
                                            edt_age_sign.text.toString(),
                                            edt_location_sign.text.toString(),
                                            countrycode + "-" + edt_number_sign.text.toString(),
                                            edt_aboutus.text.toString(),
                                            edt_information.text.toString())
                                } else {
                                    Update_withprofile(edt_email_sign.text.toString(), edt_firstname_sign.text.toString(),
                                            edt_lastname_sign.text.toString(),
                                            edt_age_sign.text.toString(),
                                            edt_location_sign.text.toString(),
                                            countrycode + "-" + edt_number_sign.text.toString(),
                                            edt_aboutus.text.toString(),
                                            edt_information.text.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun initt() {
        edt_email_sign = v.findViewById<CustomEdittextHelvic>(R.id.edt_email_sign)
        edt_firstname_sign = v.findViewById<CustomEdittextHelvic>(R.id.edt_firstname_sign)
        edt_lastname_sign = v.findViewById<CustomEdittextHelvic>(R.id.edt_lastname_sign)
        edt_age_sign = v.findViewById<TextView>(R.id.edt_age_sign)
        edt_location_sign = v.findViewById<CustomEdittextHelvic>(R.id.edt_location_sign)
        edt_number_sign = v.findViewById<CustomEdittextHelvic>(R.id.edt_number_sign)
        edt_number_sign.movementMethod = ScrollingMovementMethod()

        textViewAdd = v.findViewById<CustomTextviewHelvicNormal>(R.id.textViewAdd)
        tvAboutUsTitle = v.findViewById<CustomTextviewHelvicNormal>(R.id.tvAboutUsTitle)

        edt_aboutus = v.findViewById<CustomEdittextHelvic>(R.id.edt_aboutus)
        edt_information = v.findViewById<CustomEdittextHelvic>(R.id.edt_information)
        btn_continue = v.findViewById<Button>(R.id.btn_continue)
        ccp = v.findViewById<CountryCodePicker>(R.id.ccp)
        linearLayoutAge = v.findViewById<LinearLayout>(R.id.linearLayoutAge)
        linearLayoutSpecification = v.findViewById<LinearLayout>(R.id.linearLayoutSpecification)
        bottomView = v.findViewById<LinearLayout>(R.id.bottomView)
        recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)
        img_feed_gllery = v.findViewById<ImageView>(R.id.img_feed_gllery)
        backpress1 = v.findViewById<View>(R.id.backpress1) as ImageView
        mySharedPreference = MySharedPreference(activity)
        connectionDetector = ConnectionDetector(activity)
        imageutils = Imageutils(activity)
        imageutils.setListener(this@UpdateProfileFragment)
        //  imageutils.setVideoListener(this@UpdateProfileFragment)
        connectionDetector = ConnectionDetector(activity!!.applicationContext)
        connectionDetector.isConnectingToInternet


        // ccp.setCcpClickable(false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size != 0) {
            click()
        }
    }


    fun Update_withoutprofile(email: String, name: String,
                              last_name: String, age: String, location: String, phone: String, about_us: String, information: String) {

        Utils.instance.showProgressBar(activity)
        AndroidNetworking.upload(Utils.BASE_URL + Utils.UPDATE_PROFILE)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addMultipartParameter("email", email)
                .addMultipartParameter("name", name)
                .addMultipartParameter("last_name", last_name)
                .addMultipartParameter("age", age)
                .addMultipartParameter("location", location)
                .addMultipartParameter("phone", phone)
                .addMultipartParameter("about_us", about_us)
                .addMultipartParameter("information", information)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(UpdateProfilePojo::class.java, object : ParsedRequestListener<UpdateProfilePojo> {
                    override fun onResponse(user: UpdateProfilePojo) {
                        Utils.instance.dismissProgressDialog()
                        Log.e("update", ">>>>>>>>>>>" + user.message);

                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "Record has been updated successfully", Toast.LENGTH_LONG).show()
                            var updateProfileFragment = UpdateProfileFragment()
                            val fragmentManager = activity!!.supportFragmentManager

                            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).commit()


                        } else {
                            Toast.makeText(activity, "Unable to update record", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()

                    }
                })
    }

    fun Update_withprofile(email: String, name: String,
                           last_name: String, age: String, location: String, phone: String, about_us: String, information: String) {

        Utils.instance.showProgressBar(activity)
        AndroidNetworking.upload(Utils.BASE_URL + Utils.UPDATE_PROFILE)
                .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addMultipartParameter("email", email)
                .addMultipartParameter("name", name)
                .addMultipartParameter("last_name", last_name)
                .addMultipartParameter("age", age)
                .addMultipartParameter("location", location)
                .addMultipartParameter("phone", phone)
                .addMultipartParameter("about_us", about_us)
                .addMultipartParameter("information", information)
                .addMultipartFile("profile_image", file1)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(UpdateProfilePojo::class.java, object : ParsedRequestListener<UpdateProfilePojo> {
                    override fun onResponse(user: UpdateProfilePojo) {
                        Utils.instance.dismissProgressDialog()

                        if (user.success.equals(true)) {
                            Toast.makeText(activity, "Record has been updated successfully", Toast.LENGTH_LONG).show()
                            val fragmentManager = activity!!.supportFragmentManager
                            fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).commit()
                        } else {
                            Toast.makeText(activity, "Unable to update record", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        getAnError(context as Activity, anError)
                        Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        try {
//            super.onActivityResult(requestCode, resultCode, data)
//            imageutils.onActivityResult(requestCode, resultCode, data)
//            val baos = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//            val imageBytes = baos.toByteArray()
//
//        } catch (e: Exception) {
//        }
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ImageUtilsNew.REQ_CODE_CAMERA_PICTURE -> {
                    file1 = ImageUtilsNew.getFile(requireContext())
                    img_feed_gllery.setImageURI(Uri.fromFile(file1))
                }

                ImageUtilsNew.REQ_CODE_GALLERY_PICTURE -> {
                    file1 = ImageUtilsNew.getImagePathFromGallery(requireContext(), data?.data
                            ?: Uri.EMPTY)
                    img_feed_gllery.setImageURI(Uri.fromFile(file1))

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
                                var len = 0
                                while (inputStream?.read(buffer).also { len = it ?: 0 } ?: 0 >= 0) {
                                    out.write(buffer, 0, len)
                                }
                                out.flush()
                            } finally {
                                fos.fd.sync()
                                out.close()
                                inputStream?.close()
                            }
                            file1 = pdfFile

                        } catch (e: Exception) {
                        }
                    } else if (uriString?.startsWith("file://") == true) {
                        file1 = myFile

                    }


                }
            }
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


    fun setUpAdapter(arraySelectedCategories: MutableList<lax.lega.rv.com.legalax.pojos.lawyerCategories.Response>) {
        if (adapter == null) {
            adapter = SelectedCategoryAdapter(arraySelectedCategories!!)
            adapter!!.clickListener(this)
            recyclerView.layoutManager = LinearLayoutManager(activity!!)
            recyclerView.adapter = adapter
        } else {
            adapter!!.customNotifiy(arraySelectedCategories)
        }

    }

    override fun onCrossClick(category: lax.lega.rv.com.legalax.pojos.lawyerCategories.Response) {
        selectedCategories.remove(category)
        updateCategory()
    }

    private fun updateCategory() {
        Utils.instance.dismissProgressDialog()
        Utils.instance.showProgressBar(activity)
        var list = selectedCategories.map { it.id.toString() }
        val api = WebService().apiService.updateSelectedCategories(
                "Bearer " + mySharedPreference.getString("access_token"),
                list
        )
        api.enqueue(object : retrofit2.Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Utils.instance.dismissProgressDialog()
                Toast.makeText(activity!!, "Some thing went wrong", Toast.LENGTH_LONG).show()
                //Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e("response is", response.toString())
                Utils.instance.dismissProgressDialog()
                setUpAdapter(selectedCategories)
            }
        })
    }

    private fun selectOptionPopup() {

        val items: Array<CharSequence?> = arrayOfNulls(2)
        items[0] = "Camera"
        items[1] = "Gallery"

        val alertDialog = android.app.AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Upload Images")
        alertDialog.setItems(items) { dialog, item ->
            when {
                items[item] == "Camera" -> {
                    ImageUtilsNew.openCamera(requireContext())
                }
                items[item] == "Gallery" -> {

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
        else if (code == 2) {
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Pdf"),
                    ImageUtilsNew.REQ_CODE_GALLERY_DOC)
        }
        //  ImageUtilsNew.openGalleryDoc(requireContext())
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        android.support.v7.app.AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

}

