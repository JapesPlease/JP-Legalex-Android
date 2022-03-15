package lax.lega.rv.com.legalax.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.application.App;
import lax.lega.rv.com.legalax.common.ConnectionDetector;
import lax.lega.rv.com.legalax.common.CustomTextviewBold;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.pojos.SendCallStatusPojo;
import lax.lega.rv.com.legalax.utils.CommonFunctionsKt;

public class SenderCallActivityAnother extends Activity {
    private static final String APP_KEY = "a461c54c-abc1-433b-b494-034b2841e4e8";
    private static final String APP_SECRET = "3OhLNK5s2U254J0g8NGw+w==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";
    public static CustomTextviewBold txt_type;
    public static ImageView img_Call, img_Callreject;
    ImageView backpress1;
    Intent intent;
    String receiver_id = "", senderid;
    ConnectionDetector connectionDetector;
    // private SinchClient sinchClient;
    CustomTextviewBold txt_name;
    //private Call call;
    private TextView callState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.call_sender);

    }


    @Override
    protected void onResume() {
        super.onResume();
        android.content.Context context = this.getApplicationContext();

        if (ContextCompat.checkSelfPermission(SenderCallActivityAnother.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(SenderCallActivityAnother.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SenderCallActivityAnother.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE}, 1);
        }
        connectionDetector = new ConnectionDetector(SenderCallActivityAnother.this);

        intent = getIntent();
        receiver_id = intent.getStringExtra("id");
        txt_type = (CustomTextviewBold) findViewById(R.id.txt_type);
        img_Call = (ImageView) findViewById(R.id.img_Call);
        backpress1 = (ImageView) findViewById(R.id.backpress1);
        img_Callreject = (ImageView) findViewById(R.id.img_Callreject);

        txt_name = (CustomTextviewBold) findViewById(R.id.txt_name);


        txt_name.setText(intent.getStringExtra("name"));

        if (App.sinchClient != null) {
            App.sinchClient.getCallClient().addCallClientListener(new App.SinchCallClientListener());
        }

        backpress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.clean(getApplicationContext());
                finish();
            }
        });

        if (getIntent().getStringExtra("from").equals("receiving")) {

        } else {
            img_Call.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {

                    ConnectionDetector connectionDetector = new ConnectionDetector(SenderCallActivityAnother.this);
                    connectionDetector.isConnectingToInternet();
                    if (connectionDetector.isConnectingToInternet() == false) {
                        Toast.makeText(SenderCallActivityAnother.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    } else {
                        if (App.sinchClient != null) {
                            if (App.sinchClient.isStarted()) {
                                if (!App.sinchClient.isStarted()) {
                                    App.sinchClient.start();
                                }
                                App.call = App.sinchClient.getCallClient().callUser(receiver_id);
                                App.call.addCallListener(new App.SinchCallListener());
                                //                                    txt_type.setText("Hang Up");
                                //                                    img_Call.setImageResource(R.drawable.red_call);
                                sendCallStatus(receiver_id);
                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to connnect Sinch", Toast.LENGTH_LONG).show();

                            }

                        } else {
                            if (App.call != null) {
                                App.call.hangup();

                            }
                            // img_Call.setImageResource(R.drawable.green_call);
                            img_Call.setVisibility(View.VISIBLE);
                            img_Callreject.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        img_Callreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.call != null) {
                    App.call.hangup();
                    App.call = null;
                }
                img_Call.setVisibility(View.VISIBLE);
                img_Callreject.setVisibility(View.GONE);
                Intent intent = new Intent(SenderCallActivityAnother.this, MainActivity.class);
                intent.putExtra("f", "");

                startActivity(intent);
                finish();
            }
        });

    }

    public void sendCallStatus(String receiver_id) {
        // Utils.showLoader(SenderCallActivityAnother.this);

        AndroidNetworking.post(Utils.BASE_URL + Utils.SEND_CALL_STATUS).addHeaders("Authorization", "Bearer " + new MySharedPreference(SenderCallActivityAnother.this).getString("access_token")).addHeaders("Accept", "application/json").addBodyParameter("receiver_id", receiver_id).setTag(this).setPriority(Priority.HIGH).build().getAsObject(SendCallStatusPojo.class, new ParsedRequestListener<SendCallStatusPojo>() {
            @Override
            public void onResponse(SendCallStatusPojo user) {
                //   Utils.hideLoader();
                if (user.getSuccess() == true) {
                } else {
                }
            }

            @Override
            public void onError(ANError anError) {
                CommonFunctionsKt.getAnError(SenderCallActivityAnother.this,anError);
                // Utils.hideLoader();

            }
        });
    }


    @Override
    public void onBackPressed() {
       /* try {
            if (App.initiate(getApplicationContext()) != null) {
                App.call.hangup();

                App.clean(getApplicationContext());
                // App.initiate(getApplicationContext());
            }
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

/*    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            App.call = null;
            SinchError a = endedCall.getDetails().getError();
            txt_type.setText("Call Ended");
           // img_Call.setImageResource(R.drawable.green_call);
            img_Call.setVisibility(View.VISIBLE);
            img_Callreject.setVisibility(View.GONE);
            finish();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            txt_type.setText("Call Connected");
            //img_Call.setImageResource(R.drawable.red_call);
            img_Call.setVisibility(View.GONE);
            img_Callreject.setVisibility(View.VISIBLE);
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            txt_type.setText("Call Ringing");
           // img_Call.setImageResource(R.drawable.red_call);
            img_Call.setVisibility(View.GONE);
            img_Callreject.setVisibility(View.VISIBLE);
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    public class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            App.call = incomingCall;
            Toast.makeText(SenderCallActivityAnother.this, "incoming call", Toast.LENGTH_SHORT).show();
            App.call.answer();
            App.call.addCallListener(new SinchCallListener());
            txt_type.setText("Hang Up");
           // img_Call.setImageResource(R.drawable.red_call);
            img_Call.setVisibility(View.GONE);
            img_Callreject.setVisibility(View.VISIBLE);
        }
    }*/
}
