package lax.lega.rv.com.legalax.calling;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.activities.SinchService;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.pojos.GetUserDetailPojo;
import lax.lega.rv.com.legalax.retrofit.Constant;
import lax.lega.rv.com.legalax.utils.CommonFunctionsKt;

public class IncomingCallScreenActivity extends BaseActivity implements SensorEventListener {
    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    MySharedPreference mySharedPreference;
    TextView remoteUser;
    RelativeLayout rl_cut, rl_answer;
    ImageView caller_image;
    private String mCallId, name, lastname, id;
    String image;
    private AudioPlayer mAudioPlayer;
    SensorManager mySensorManager;
    private static final int SENSOR_SENSITIVITY = 4;
    private final String tag = "OnOffScreen";
    private PowerManager _powerManager;
    private PowerManager.WakeLock _screenOffWakeLock;
    Sensor myProximitySensor;
    private int field = 0x00000020;


    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_answer:
                    answerClicked();
                    break;
                case R.id.img_end:
                    declineClicked();
                    break;
            }
        }
    };


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
        setContentView(R.layout.incoming);

//        Button answer = (Button) findViewById(R.id.answerButton);
//        answer.setOnClickListener(mClickListener);
//        Button decline = (Button) findViewById(R.id.declineButton);
        mySensorManager = (SensorManager) getSystemService(
                getApplicationContext().SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);

//        _powerManager = (PowerManager) getApplicationContext().getSystemService(getApplicationContext().POWER_SERVICE);
//
//        _screenOffWakeLock = _powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");


        mySharedPreference = new MySharedPreference(IncomingCallScreenActivity.this);


//        decline.setOnClickListener(mClickListener);
        rl_answer = findViewById(R.id.img_answer);
        rl_cut = findViewById(R.id.img_end);
        rl_cut.setOnClickListener(mClickListener);
        rl_answer.setOnClickListener(mClickListener);
        caller_image = findViewById(R.id.caller_image);
        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }


    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            remoteUser = (TextView) findViewById(R.id.tv_name);
            getIdDetail(Utils.BASE_URL + Utils.GET_USER_DATA, call.getRemoteUserId());
            //remoteUser.setText(call.getRemoteUserId());
        } else {

            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            try {
                call.answer();
                Intent intent = new Intent(this, CallScreenActivity.class);
                intent.putExtra("lastname", lastname);
                intent.putExtra("name", name);
                intent.putExtra("from", "incoming");
                intent.putExtra("id", id);
                intent.putExtra("image", image);
                intent.putExtra(SinchService.CALL_ID, mCallId);
                startActivity(intent);
//                finish();

            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        } else {
            finish();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now answer the call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast.LENGTH_SHORT).show();
        }
    }

    private void declineClicked() {
        try {
            mAudioPlayer.stopRingtone();
            Call call = getSinchServiceInterface().getCall(mCallId);
            if (call != null) {
//                if (_screenOffWakeLock.isHeld()) {
//                    _screenOffWakeLock.release();
//                    _screenOffWakeLock.setReferenceCounted(false);
//                }
                call.hangup();


            }

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//
//        if (!_screenOffWakeLock.isHeld()) {
//            _screenOffWakeLock.acquire();
//        }
        mySensorManager.registerListener(this, myProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

//        mDurationTask = new UpdateCallDurationTask();
//        mTimer.schedule(mDurationTask, 0, 500);
    }

    private void getIdDetail(String url, String user_id) {
        // Utils.showLoader(IncomingCallScreenActivity.this);
        try {
            AndroidNetworking.post(url)
                    .addHeaders("Authorization", "Bearer " + mySharedPreference.getString("access_token"))
                    .addHeaders("Accept", "application/json").addBodyParameter("user_id", user_id).setPriority(Priority.HIGH).build().getAsObject(GetUserDetailPojo.class, new ParsedRequestListener<GetUserDetailPojo>() {
                @Override
                public void onResponse(GetUserDetailPojo user) {
                    //Utils.hideLoader();
                    remoteUser.setText(user.getUser().getName() + " " + user.getUser().getLastName());
                    name = user.getUser().getName();
                    lastname = user.getUser().getName() + user.getUser().getLastName();
                    id = String.valueOf(user.getUser().getId());
                    image = user.getUser().getProfileImage();
                    if (image != null) {
                        Picasso.with(IncomingCallScreenActivity.this).load(Utils.IMAGESURL + image).into(caller_image);

                    }

                }

                @Override
                public void onError(ANError anError) {
                    /// Utils.hideLoader();
                    CommonFunctionsKt.getAnError(IncomingCallScreenActivity.this,anError);
                    Toast.makeText(IncomingCallScreenActivity.this, "Unable to connect server", Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
//            Toast.makeText(getApplicationContext(), "Proximity Detected", Toast.LENGTH_LONG).show();


            if (event.values[0] == 0) {
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
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onPause() {
        super.onPause();
//        mDurationTask.cancel();
////        mTimer.cancel();
//        if (_screenOffWakeLock.isHeld()) {
//            _screenOffWakeLock.release();
//            _screenOffWakeLock.setReferenceCounted(false);
//        }
//
//
        mySensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            Toast.makeText(IncomingCallScreenActivity.this, "Call Ended", Toast.LENGTH_SHORT).show();
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            rl_answer.setVisibility(View.GONE);


        }

        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(IncomingCallScreenActivity.this, "Call On process", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }

}


