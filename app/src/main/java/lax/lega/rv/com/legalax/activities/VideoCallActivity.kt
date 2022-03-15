package lax.lega.rv.com.legalax.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.opentok.android.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_video_call.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.calling.AudioPlayer
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.AddCreditPointsPojo
import lax.lega.rv.com.legalax.retrofit.WebService
import lax.lega.rv.com.legalax.utils.openLogoutPopup
import lax.lega.rv.com.legalax.videoCall.OpenTokConfig
import lax.lega.rv.com.legalax.videoCall.WebServiceCoordinator
import okhttp3.ResponseBody
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Response

class VideoCallActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, WebServiceCoordinator.Listener, Session.SessionListener, PublisherKit.PublisherListener, SubscriberKit.SubscriberListener {

    private val LOG_TAG = MainActivity::class.java.simpleName
    private val RC_SETTINGS_SCREEN_PERM = 123
    private val RC_VIDEO_APP_PERM = 124
    var timer: CountDownTimer? = null
    var timer2: CountDownTimer? = null

    private var mWebServiceCoordinator: WebServiceCoordinator? = null

    private var mSession: Session? = null
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null
    var sessionId = ""
    var token = ""
    var callType = ""
    var image = ""
    var name = ""
    var reciverId = ""
    var id = ""
    var isProposal = false
    lateinit var recievedStream: Stream
    private var mAudioPlayer: AudioPlayer? = null
    lateinit var sharedPreference: MySharedPreference
    var handler: Handler? = null
    var runnable: Runnable? = null

    lateinit var mWakeLock: PowerManager.WakeLock
    private var field = 0x00000020
    private var _powerManager: PowerManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        sharedPreference = MySharedPreference(this)

        setData()
        mAudioPlayer = AudioPlayer(this)
        countDownTimer()
        if (callType.equals("outGoing")) {
            OpenTokConfig.SESSION_ID = sessionId
            OpenTokConfig.TOKEN = token
            requestPermissions()
            mAudioPlayer!!.playProgressTone()
            relativeLayoutGoingCall.visibility = View.VISIBLE
            relativeLayoutIncomingCall.visibility = View.GONE
        } else {
            mAudioPlayer!!.playRingtone()
            relativeLayoutGoingCall.visibility = View.GONE
            relativeLayoutIncomingCall.visibility = View.VISIBLE
        }

        clickListener()

        if (Build.VERSION.SDK_INT < 27) {
            val win = window
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        } else {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }


        if (callType.equals("outGoing")) {
            reciverId = intent.getStringExtra("id").toString()
            saveVideoCall()
        }
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // turnOnScreen()
    }

    private fun setData() {
        sessionId = intent.getStringExtra("sessionId").toString()
        token = intent.getStringExtra("token").toString()
        callType = intent.getStringExtra("callType").toString()
        image = intent.getStringExtra("image").toString()
        name = intent.getStringExtra("name").toString()
        isProposal = intent.getBooleanExtra("isProposal", false)
        textViewPersonName.text = name
        if (image.startsWith("http") || image.startsWith("https"))
            Picasso.with(this).load(image).fit().placeholder(R.drawable.icn_user_large).into(circularImageView)
        else
            Picasso.with(this).load(Utils.IMAGESURL + image).fit().placeholder(R.drawable.icn_user_large).into(circularImageView)

    }

    private fun clickListener() {
        img_end_onGoingCall.setOnClickListener {
            mSession!!.disconnect()
            mAudioPlayer!!.stopProgressTone()
            finish()
        }

        imageViewCamera.setOnClickListener {
            if (mPublisher != null) {
                mPublisher!!.cycleCamera()
            }
        }


        img_end_Call.setOnClickListener {
            //  mSession!!.disconnect()
            mAudioPlayer!!.stopRingtone()
            finish()
        }

        img_answer.setOnClickListener {
            mAudioPlayer!!.stopRingtone()
            timer!!.cancel()
            OpenTokConfig.SESSION_ID = sessionId
            OpenTokConfig.TOKEN = token
            requestPermissions()
            relativeLayoutGoingCall.visibility = View.VISIBLE
            relativeLayoutIncomingCall.visibility = View.GONE
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "onPause")

        super.onPause()
        if (mSession != null) {
            mSession!!.onPause()
        }
    }

    override fun onBackPressed() {

    }

    override fun onResume() {

        Log.d(LOG_TAG, "onResume")

        super.onResume()

        if (mSession != null) {
            mSession!!.onResume()
        }
    }

    @AfterPermissionGranted(124)
    private fun requestPermissions() {

        val perms = arrayOf(Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            if (OpenTokConfig.CHAT_SERVER_URL == null) {
                if (OpenTokConfig.areHardCodedConfigsValid()) {
                    initializeSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN)
                } else {
                    showConfigError("Configuration Error", OpenTokConfig.hardCodedConfigErrorMessage)
                }
            } else {
                if (OpenTokConfig.isWebServerConfigUrlValid()) {
                    mWebServiceCoordinator = WebServiceCoordinator(this, this)
                    mWebServiceCoordinator!!.fetchSessionConnectionData(OpenTokConfig.SESSION_INFO_ENDPOINT)
                } else {
                    showConfigError("Configuration Error", OpenTokConfig.webServerConfigErrorMessage)
                }
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, *perms)
        }
    }


    private fun initializeSession(apiKey: String, sessionId: String, token: String) {
        mSession = Session.Builder(this, apiKey, sessionId).build()
        mSession!!.setSessionListener(this)
        mSession!!.connect(token)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onSessionConnectionDataReady(apiKey: String?, sessionId: String?, token: String?) {
        Log.d(LOG_TAG, "ApiKey: $apiKey SessionId: $sessionId Token: $token")
        initializeSession(apiKey!!, sessionId!!, token!!)
    }

    override fun onWebServiceCoordinatorError(error: Exception?) {
        Log.e(LOG_TAG, "Web Service error: " + error!!.message)
        Toast.makeText(this, "Web Service error: " + error.message, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {
        Log.d(LOG_TAG, "onStreamDropped: Stream Dropped: " + stream!!.getStreamId() + " in session: " + session!!.getSessionId())
        if (mSubscriber != null) {
            mSubscriber = null
            subscriber_container.removeAllViews()
            finish()
        }

    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {
        timeHandler();

        countDownTimer2();
        Log.d(LOG_TAG, "onStreamReceived: New Stream Received " + stream!!.getStreamId() + " in session: " + session!!.getSessionId())
        recievedStream = stream
        if (mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this, recievedStream).build()
            mSubscriber!!.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
            mSubscriber!!.setSubscriberListener(this)
            mSession!!.subscribe(mSubscriber)
            subscriber_container.addView(mSubscriber!!.getView())
            if (callType.equals("outGoing")) {
                mAudioPlayer!!.stopProgressTone()
            }
            timer!!.cancel()
        }
    }

    override fun onConnected(session: Session?) {
        Log.d(LOG_TAG, "onConnected: Connected to session: " + session!!.getSessionId())

        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = Publisher.Builder(this).build()
        mPublisher!!.setPublisherListener(this)

        // set publisher video style to fill view
        mPublisher!!.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL)
        publisher_container.addView(mPublisher!!.getView())
        if (mPublisher!!.getView() is GLSurfaceView) {
            (mPublisher!!.getView() as GLSurfaceView).setZOrderOnTop(true)
        }
        if (mSession != null) {
            mSession!!.publish(mPublisher)
        }
    }


    override fun onError(session: Session?, opentokError: OpentokError?) {
        Log.e(LOG_TAG, "onError: " + opentokError!!.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - " + opentokError.getMessage() + " in session: " + session!!.getSessionId())

        showOpenTokError(opentokError)
    }

    override fun onConnected(subscriberKit: SubscriberKit?) {

    }

    override fun onDisconnected(session: Session?) {

    }

    override fun onDisconnected(subscriberKit: SubscriberKit?) {

    }

    override fun onError(p0: PublisherKit?, opentokError: OpentokError?) {
        Log.e(LOG_TAG, "onError: " + opentokError!!.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - " + opentokError.getMessage())

        showOpenTokError(opentokError)
    }

    override fun onError(p0: SubscriberKit?, opentokError: OpentokError?) {
        Log.e(LOG_TAG, "onError: " + opentokError!!.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - " + opentokError.getMessage())

        showOpenTokError(opentokError)
    }

    override fun onStreamCreated(p0: PublisherKit?, stream: com.opentok.android.Stream?) {

    }

    override fun onStreamDestroyed(p0: PublisherKit?, stream: com.opentok.android.Stream?) {
        finish()
    }


    private fun showOpenTokError(opentokError: OpentokError) {
        finish()
    }


    private fun showConfigError(alertTitle: String, errorMessage: String) {
        Log.e(LOG_TAG, "Error $alertTitle: $errorMessage")
        AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(errorMessage)
                .setPositiveButton("ok") { dialog, which -> this@VideoCallActivity.finish() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    fun countDownTimer() {
        timer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (callType.equals("outGoing")) {
                    mSession!!.disconnect()
                    mSession = null
                    mAudioPlayer!!.stopProgressTone()
                    finish()
                } else {
                    mAudioPlayer!!.stopRingtone()
                    finish()
                }
            }
        }.start()
    }

    fun countDownTimer2() {
        timer2 = object : CountDownTimer(1800000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var remainingSeconds = millisUntilFinished / 1000;
                Log.e("remaingSeconds", remainingSeconds.toString());
                if (remainingSeconds.toInt() == 1796) {
                    if (callType.equals("outGoing")) {
                        if (sharedPreference.getString("role").equals("2")) {
                            if (!isProposal)
                                deductCreditsApi(reciverId)
                        } else {
                            if (!isProposal)
                                deductCreditsApi(sharedPreference.getInt("id").toString())
                        }
                    }
                }
                //300000
                if (remainingSeconds.toInt() == 300) {
                    textViewlast5minutes.visibility = View.VISIBLE
                    //chronometer2.setTextColor(ContextCompat.getColor(this@VideoCallActivity, R.color.red_900))
                    countDownTimer3()
                }

                /* if(remainingSeconds.toInt()==300000){

                 }*/

            }

            override fun onFinish() {
                if (callType.equals("outGoing")) {
                    mSession!!.disconnect()
                    mSession = null
                    mAudioPlayer!!.stopProgressTone()
                    startActivity(Intent(this@VideoCallActivity, MainActivity::class.java))
                    finishAffinity()
                } else {
                    mAudioPlayer!!.stopRingtone()
                    startActivity(Intent(this@VideoCallActivity, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }.start()
    }

    /*val connectionDetector = ConnectionDetector(activity!!.applicationContext)
     connectionDetector.isConnectingToInternet
     if (connectionDetector.isConnectingToInternet === false) run {
         Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show()
     } else {
         SendDocument(list!!.get(position).userId.toString(), list!!.get(position).id.toString(), position)
     }*/

    fun countDownTimer3() {
        timer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val v = java.lang.String.format("%02d", millisUntilFinished / 60000)
                val va = (millisUntilFinished % 60000 / 1000).toInt()
                textViewlast5minutes.setText(v + ":" + String.format("%02d", va))
            }

            override fun onFinish() {

            }
        }.start()
    }


    fun timeHandler() {
        chronometer2.setBase(SystemClock.elapsedRealtime());
        chronometer2.start();
        /*handler = Handler()
        runnable = object : Runnable {
           override fun run() {


               handler!!.postDelayed(this, 1000)

           }
       }
       handler!!.postDelayed(runnable, 10)*/
    }


    private fun deductCreditsApi(id: String) {
        val add_credit = WebService().apiService.add_credit(
                "Bearer " + sharedPreference.getString("access_token"),
                "application/json",
                "1",
                id.toLong(),
                "0"
        )

        add_credit.enqueue(object : retrofit2.Callback<AddCreditPointsPojo> {

            override fun onFailure(call: Call<AddCreditPointsPojo>, t: Throwable) {
                Toast.makeText(this@VideoCallActivity, "Some thing went wrong", Toast.LENGTH_LONG).show()
               // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<AddCreditPointsPojo>, response: Response<AddCreditPointsPojo>) {

                if (response.code() == 401) {
                    openLogoutPopup(this@VideoCallActivity)
                } else {

                    if (response.isSuccessful) {

                        sharedPreference.putInt("points", response.body()!!.points)
                        Toast.makeText(this@VideoCallActivity, "Api hit successfully", Toast.LENGTH_LONG).show()
                        Log.e("deductCredApiHitSuccess", response.body().toString())
                    }
                }
            }
        })
    }


    private fun saveVideoCall() {
        val add_credit = WebService().apiService.saveVideoCall(
                "Bearer " + sharedPreference.getString("access_token"),
                reciverId
        )

        add_credit.enqueue(object : retrofit2.Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@VideoCallActivity, "Some thing went wrong", Toast.LENGTH_LONG).show()
               // Log.e("AddCreditPointsOnError", t.message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e("response is", response.body()!!.string().toString())
                if (response.code() == 401) {
                    openLogoutPopup(this@VideoCallActivity)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        chronometer2.stop()
        if (timer2 != null) {
            timer2!!.cancel();
        }
    }
}
