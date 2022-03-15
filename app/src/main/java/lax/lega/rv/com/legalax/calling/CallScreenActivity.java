package lax.lega.rv.com.legalax.calling;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.activities.SinchService;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.pojos.SendCallStatusPojo;
import lax.lega.rv.com.legalax.retrofit.Constant;
import lax.lega.rv.com.legalax.utils.CommonFunctionsKt;

public class CallScreenActivity extends BaseActivity implements SensorEventListener {

    static final String TAG = CallScreenActivity.class.getSimpleName();
    MySharedPreference mySharedPreference;
    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;
    private String mCallId;
    ImageView ic_back_arrow;

    TextView tv_name, tv_caaling;
    ImageView backpress1;
    ImageView caller_image, img_Call_end;
    String user_img = "";
    SensorEventListener proximitySensorEventListener;
    SensorManager mySensorManager;
    private static final int SENSOR_SENSITIVITY = 4;
    private final String tag = "OnOffScreen";
    private PowerManager _powerManager;
    private PowerManager.WakeLock _screenOffWakeLock;
    Sensor myProximitySensor;
    private int field = 0x00000020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        _powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        _screenOffWakeLock = _powerManager.newWakeLock(field, getLocalClassName());
        setContentView(R.layout.call_sender);
        mySensorManager = (SensorManager) getSystemService(
                getApplicationContext().SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);
//        _powerManager = (PowerManager) getApplicationContext().getSystemService(getApplicationContext().POWER_SERVICE);
//
//        _screenOffWakeLock = _powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");

        tv_name = findViewById(R.id.tv_name);
        tv_caaling = findViewById(R.id.tv_calling);
        ic_back_arrow = findViewById(R.id.ic_back_arrow);
        ic_back_arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        backpress1 = (ImageView) findViewById(R.id.backpress1);
//        mCallButton = (Button) findViewById(R.id.callButton);
//        mCallButton.setEnabled(false);
//        mCallButton.setOnClickListener(buttonClickListener);
        caller_image = (ImageView) findViewById(R.id.caller_image);
        img_Call_end = (ImageView) findViewById(R.id.img_Call);
        mAudioPlayer = new AudioPlayer(this);

        mySharedPreference = new MySharedPreference(CallScreenActivity.this);
        img_Call_end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });


        String from = getIntent().getStringExtra("from");
        if (from != null) {
            if (from.equalsIgnoreCase("incoming")) {
                mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
                String id = getIntent().getStringExtra("id");
                String lastname = getIntent().getStringExtra("lastname");
                String image = getIntent().getStringExtra("image");
                if (lastname != null) {

                    tv_name.setText(lastname);
                }
                if (image != null) {
                    if (user_img != "") {
                        Picasso.with(CallScreenActivity.this).load(Utils.IMAGESURL + image).into(caller_image);
                    }
                }


                Call call = getSinchServiceInterface().callUser(id);
                if (call == null) {

                    // Service failed for some reason, show a Toast and abort
                    Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before " + "placing a call.", Toast.LENGTH_LONG).show();
                    return;
                }

                mCallId = call.getCallId();
            } else {
                String userName = getIntent().getStringExtra("id");

                Call call = getSinchServiceInterface().callUser(userName);
                if (call == null) {
                    // Service failed for some reason, show a Toast and abort
                    Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before " + "placing a call.", Toast.LENGTH_LONG).show();
                    return;
                }

                mCallId = call.getCallId();


//        Button stopButton = (Button) findViewById(R.id.stopButton);

//        stopButton.setOnClickListener(buttonClickListener);

                tv_name.setText(getIntent().getStringExtra("name") + " " + getIntent().getStringExtra("lastname"));
                user_img = getIntent().getStringExtra("image");
                if (user_img != "") {
                    Picasso.with(CallScreenActivity.this).load(Utils.IMAGESURL + user_img).into(caller_image);
                }

            }
        } else {

            String userName = getIntent().getStringExtra("id");


            Call call = getSinchServiceInterface().callUser(userName);
            if (call == null) {

                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before " + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }

            mCallId = call.getCallId();


//        Button stopButton = (Button) findViewById(R.id.stopButton);

//        stopButton.setOnClickListener(buttonClickListener);

            tv_name.setText(getIntent().getStringExtra("name") + " " + getIntent().getStringExtra("lastname"));
            user_img = getIntent().getStringExtra("image");
            if (user_img != "") {
                Picasso.with(CallScreenActivity.this).load(Utils.IMAGESURL + user_img).into(caller_image);
            }

        }


    }


    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            //mCallerName.setText(call.getRemoteUserId());
//            mCallerName.setText(getIntent().getStringExtra("name") + " " + getIntent().getStringExtra("lastname"));
            tv_caaling.setText(call.getState().toString());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        mDurationTask.cancel();
//        mTimer.cancel();
//        if (_screenOffWakeLock.isHeld()) {
//            _screenOffWakeLock.release();
//            _screenOffWakeLock.setReferenceCounted(false);
//        }
//
        mySensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

//        mySensorManager.registerListener(proximitySensorListener, proximitySensor,
//                2 * 1000 * 1000);
//        if (!_screenOffWakeLock.isHeld()) {
//            _screenOffWakeLock.acquire();
//        }
//
        mySensorManager.registerListener(this, myProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);


//        mDurationTask = new UpdateCallDurationTask();
//        mTimer.schedule(mDurationTask, 0, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }

        finish();
    }

    private String formatTimespan(int totalSeconds) {
//        long minutes = totalSeconds / 60;
//        long seconds = totalSeconds % 60;
//        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        return null;
    }

    private void updateCallDuration() {
//        Call call = getSinchServiceInterface().getCall(mCallId);
//        if (call != null) {
//            mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
//        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {


            if (event.values[0] == 0)
            {
                turnoffscreen();

            } else {
                turnOnScreen();
            }
        }
    }

    private void turnoffscreen() {
        try {
            if (!_screenOffWakeLock.isHeld()) {
                _screenOffWakeLock.acquire();
            }
//            _powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
//            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            lp.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
//            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
//           CallScreenActivity.this.getWindow().setAttributes(lp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void turnOnScreen() {
        try {
            if (_screenOffWakeLock.isHeld()) {
                _screenOffWakeLock.release();
            }
//            if (_screenOffWakeLock.isHeld()) {
//                _screenOffWakeLock.release();
//            }


        } catch (Exception e) {
            Log.e("Nulll", ">>>>>>>>>>>>>>>>>" + "Nulllllll");
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run()

                {
                    updateCallDuration();
                }
            });
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            // Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            tv_caaling.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            sendCallStatus(call.getRemoteUserId());

        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }

    public void sendCallStatus(String receiver_id) {
        // Utils.showLoader(SenderCallActivityAnother.this);

        AndroidNetworking.post(Utils.BASE_URL + Utils.SEND_CALL_STATUS)
                .addHeaders("Authorization", "Bearer " + new MySharedPreference(CallScreenActivity.this).getString("access_token")).addHeaders("Accept", "application/json").addBodyParameter("receiver_id", receiver_id).setTag(this).setPriority(Priority.HIGH).build().getAsObject(SendCallStatusPojo.class, new ParsedRequestListener<SendCallStatusPojo>() {
            @Override
            public void onResponse(SendCallStatusPojo user) {
                //   Utils.hideLoader();
                if (user.getSuccess() == true) {

                } else {
                }
            }

            @Override
            public void onError(ANError anError) {
                CommonFunctionsKt.getAnError(CallScreenActivity.this,anError);
                // Utils.hideLoader();

            }
        });
    }

}
