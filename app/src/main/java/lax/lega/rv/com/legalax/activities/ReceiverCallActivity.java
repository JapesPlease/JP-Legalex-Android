package lax.lega.rv.com.legalax.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.application.App;
import lax.lega.rv.com.legalax.common.CustomTextviewBold;
import lax.lega.rv.com.legalax.fcm.MyFirebaseMessagingService;

public class ReceiverCallActivity extends Activity
{
    ImageView img_Calling, img_Callreject;
    CustomTextviewBold txt_type, txt_name;
    String type = "", sender_id = "", badge = "", message, fullname = "", fcm_token = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        if (ContextCompat.checkSelfPermission(ReceiverCallActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ReceiverCallActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ReceiverCallActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE}, 1);
        }
        init();
        click();
        // txt_type.setText("Connected");
        if (App.sinchClient != null)
        {
            App.sinchClient.getCallClient().addCallClientListener(new App.SinchCallClientListener());
        }
    }

    private void click() {
        img_Calling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyFirebaseMessagingService.r != null) {
                    MyFirebaseMessagingService.r.stop();
                }
                try {
                    if (App.call != null) {
                        //    App.sinchClient.getCallClient().addCallClientListener(new App.SinchCallClientListener());

                        App.call.answer();
                        App.call.addCallListener(new SinchCallListener());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        img_Callreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyFirebaseMessagingService.r != null) {
                    MyFirebaseMessagingService.r.stop();
                }
                try {
                    if (App.call != null)
                    {
                        App.call.hangup();
                        App.call = null;
                        Intent intent = new Intent(ReceiverCallActivity.this, MainActivity.class);
                        intent.putExtra("f", "");

                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(ReceiverCallActivity.this, MainActivity.class);
                        intent.putExtra("f", "");

                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init()
    {
        img_Calling = (ImageView) findViewById(R.id.img_Calling);
        img_Callreject = (ImageView) findViewById(R.id.img_Callreject);
        txt_type = (CustomTextviewBold) findViewById(R.id.txt_type);
        txt_name = (CustomTextviewBold) findViewById(R.id.txt_name);
        type = getIntent().getStringExtra("type");
        sender_id = getIntent().getStringExtra("sender_id");
        badge = getIntent().getStringExtra("badge");
        message = getIntent().getStringExtra("message");
        fullname = getIntent().getStringExtra("fullname");
        fcm_token = getIntent().getStringExtra("fcm_token");
        txt_name.setText("" + fullname);
    }

    @Override
    public void onBackPressed()
    {
        if (MyFirebaseMessagingService.r != null)
        {
            MyFirebaseMessagingService.r.stop();
        }
        try {
            if (App.initiate(getApplicationContext()) != null)
            {
                App.call.hangup();

                App.clean(getApplicationContext());
                // App.initiate(getApplicationContext());
            }
            Intent intent = new Intent(ReceiverCallActivity.this, MainActivity.class);
            intent.putExtra("f", "");

            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            txt_type.setText("Call Ended");
            App.call = null;
            SinchError a = endedCall.getDetails().getError();
            Intent intent = new Intent(ReceiverCallActivity.this, MainActivity.class);
            intent.putExtra("f", "");

            startActivity(intent);
            finish();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall)
        {
            txt_type.setText("Call Connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            return;
        }

        @Override
        public void onCallProgressing(Call progressingCall)
        {
            txt_type.setText("Call Progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs)
        {

        }
    }

    public class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            App.call = incomingCall;
            Toast.makeText(ReceiverCallActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
            //            App.call.answer();
            //       App.call.addCallListener(new SinchCallListener());
            return;
        }
    }
}
