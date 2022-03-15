package lax.lega.rv.com.legalax.utils

import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.android.volley.VolleyError
import com.androidnetworking.error.ANError
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.activities.LoginActivity
import lax.lega.rv.com.legalax.common.MySharedPreference
import retrofit2.HttpException
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun getRetrofitError(t: Throwable,activity:Activity) {

    val error = t as HttpException
    val errorBody = error.response().errorBody()?.string()
    //Log.e("error Body",errorBody)

}

fun getVollyError(activity:Activity,error:VolleyError){

    val networkResponse = error.networkResponse
    if (networkResponse != null) {
        Log.e("status code", networkResponse.statusCode.toString())
        if (networkResponse.statusCode==401){
           openLogoutPopup(activity)
        }
    }

}

fun getAnError(activity:Activity,error: ANError){

    if (error.errorCode==401){
        openLogoutPopup(activity)
    }

}

fun logoutUser(activity:Activity){

    GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
    val nMgr = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    nMgr.cancelAll()
//  LoginManager.getInstance().logOut()
    val mySharedPreference = MySharedPreference(activity)
    mySharedPreference.removeAll()
    val intent = Intent(activity, LoginActivity::class.java)
    activity.startActivity(intent)
    activity.finishAffinity()

}

fun openLogoutPopup(activity:Activity){

    val alertDialogBuilder = AlertDialog.Builder(activity)
    alertDialogBuilder.setMessage(activity.getString(R.string.msg_logout));
            alertDialogBuilder.setPositiveButton("ok"
            ) { _, _ ->
               logoutUser(activity)
            }

    val alertDialog = alertDialogBuilder.create()

    alertDialog.setOnShowListener { alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.resources.getColor(R.color.colorPrimary)); }

    alertDialog.show()

}


fun getDialog(context: Context, layout: Int, type: Int): Dialog {
    val dialog = Dialog(context)
    dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
    dialog.setContentView(layout)
    dialog .setCancelable(true)
    dialog.setCanceledOnTouchOutside(false)
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window?.attributes)
    when (type) {
        0 -> {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
        }
        1 -> {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            lp.gravity = Gravity.CENTER
        }
        2 -> {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.BOTTOM
        }
    }

    dialog.window?.attributes = lp
    dialog .show()
    return dialog
}


fun logRegistrationEvent (logger: AppEventsLogger, name: String, last_name: String
                          , email: String, age: String, location: String
                          , phone: String, role: String, countryCode: String, registrationType: String) {
    val params =  Bundle()
    params.putString("email", email)
    params.putString("firstName", name)
    params.putString("lastName", last_name)
    params.putString("age", age)
    params.putString("location", location)
//    params.putString("country_code", countryCode)
    params.putString("phone", phone)
    params.putString("selectedRole", role)
    params.putString("registrationType", registrationType)
    logger.logEvent("Registration", 1.0, params)
}

fun setVideoDuration(context: Context, videoToUpload: File?, view: View?): Long {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, Uri.fromFile(videoToUpload))
    val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)


    val timeInMillisec = java.lang.Long.parseLong(time)

    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillisec)
    // long seconds = (milliseconds / 1000);
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillisec)

   // textView.text = "$minutes:$seconds"

    return timeInMillisec

}

fun getDate(dateStr:String):String{

    return if (dateStr.isNotEmpty()){
        val simpleDateFormat= SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=simpleDateFormat.parse(dateStr)
        val dayOfTheWeek = DateFormat.format("EEE", date) as String // Thursday
        val day = DateFormat.format("dd", date) as String // 20
        val monthString = DateFormat.format("MMMM", date) as String // Jun
        var monthNumber = DateFormat.format("MM", date) as String // 06
        val year = DateFormat.format("yyyy", date) as String // 2013

        "$monthString $day,$year"
    }else{
        ""
    }
}


// apply comma format in number
fun doubleToStringNoDecimal(d: Double): String? {
    val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
    formatter.applyPattern("#,###")
    return formatter.format(d)
}

