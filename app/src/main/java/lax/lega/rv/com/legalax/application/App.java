package lax.lega.rv.com.legalax.application;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.activities.MainActivity;
import lax.lega.rv.com.legalax.activities.SenderCallActivityAnother;
import lax.lega.rv.com.legalax.common.Utils;


public class App extends Application implements LifecycleObserver {
    private static final String APP_KEY = "a461c54c-abc1-433b-b494-034b2841e4e8";
    private static final String APP_SECRET = "3OhLNK5s2U254J0g8NGw+w==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";
    public static SinchClient sinchClient;
    public static SharedPreferences sharedPreferences;
    public static Call call;
    public static Ringtone r;
    public static AudioManager am;
    public static ToneGenerator mToneGenerator;
    private static Context sContext;
    public Myinterface myinterface;
    private static App _instance;


    public static Context getAppContext() {
        return sContext;
    }

    public void SetInterface(Myinterface myinterface) {
        this.myinterface = myinterface;

    }

    public static SinchClient initiate(Context sContext) {
        Uri Emergency_sound_uri = Uri.parse("android.resource://" + sContext.getPackageName() + "/" + R.raw.ringing);
        Ringtone r = RingtoneManager.getRingtone(sContext, Emergency_sound_uri);

        sharedPreferences = sContext.getSharedPreferences("login_detail", Context.MODE_PRIVATE);
        sinchClient = Sinch.getSinchClientBuilder().context(sContext).userId(String.valueOf(sharedPreferences.getInt("id", 0))).applicationKey(APP_KEY).applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.setSupportManagedPush(true);
        sinchClient.setSupportMessaging(true);
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        return sinchClient;
    }

    public static SinchClient clean(Context sContext) {
        if (App.initiate(sContext) != null) {
            App.initiate(sContext).stopListeningOnActiveConnection();
            App.initiate(sContext).terminate();
            App.sinchClient = null;

        }
        return sinchClient;
    }


    @Override
    public void onCreate() {
        super.onCreate();


        new Utils(this);

        sContext = getApplicationContext();
        sharedPreferences = sContext.getSharedPreferences("login_detail", Context.MODE_PRIVATE);
        sinchClient = Sinch.getSinchClientBuilder().context(sContext).userId(String.valueOf(sharedPreferences.getInt("id", 0))).applicationKey(APP_KEY).applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.setSupportManagedPush(true);
        sinchClient.setSupportMessaging(true);
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        _instance = App.this;


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.e("MyApp", "App in background");
        if (myinterface != null) {
            myinterface.OnBackground();
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        Log.e("MyApp", "App in foreground");
        if (myinterface != null) {
            myinterface.OnForground();
        }

    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static boolean activityVisible;

    public static class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            if (endedCall != null) {
                endedCall.hangup();
                am = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
                int volume_level = am.getStreamVolume(AudioManager.STREAM_RING); // Highest Ring volume level is 7, lowest is 0
                mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume_level * 0); // Raising volume to 100% (For eg. 7 * 14 ~ 100)
                mToneGenerator.stopTone();
                call = null;

            }

//            Intent intent = new Intent(sContext, MainActivity.class);
//            intent.putExtra("f", "");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            sContext.startActivity(intent);
//            SenderCallActivityAnother.txt_type.setText("Hang Up");
//            mToneGenerator.stopTone();

//            if (SenderCallActivityAnother.txt_type != null)
//            {
//                SenderCallActivityAnother.img_Call.setVisibility(View.VISIBLE);
//                SenderCallActivityAnother.img_Callreject.setVisibility(View.GONE);
//                Intent intent = new Intent(sContext, MainActivity.class);
//                intent.putExtra("f", "");
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                sContext.startActivity(intent);
//            } else {
//
//            }
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            if (establishedCall != null) {
                establishedCall.answer();
//                am = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
//                int volume_level = am.getStreamVolume(AudioManager.STREAM_RING);
//                mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume_level * 0); // Raising volume to 100% (For eg. 7 * 14 ~ 100)
//                mToneGenerator.stopTone();

            }


            if (SenderCallActivityAnother.txt_type != null) {
                SenderCallActivityAnother.txt_type.setText("Hang Up");
                // img_Call.setImageResource(R.drawable.red_call);
                SenderCallActivityAnother.img_Call.setVisibility(View.GONE);
                SenderCallActivityAnother.img_Callreject.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            if (SenderCallActivityAnother.txt_type != null) {
                SenderCallActivityAnother.txt_type.setText("Connecting");

                SenderCallActivityAnother.img_Call.setVisibility(View.GONE);
                SenderCallActivityAnother.img_Callreject.setVisibility(View.VISIBLE);
                am = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
                int volume_level = am.getStreamVolume(AudioManager.STREAM_RING); // Highest Ring volume level is 7, lowest is 0
                mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume_level * 14); // Raising volume to 100% (For eg. 7 * 14 ~ 100)
                mToneGenerator.stopTone();
                mToneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 100);
            }
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    public static App getInstance() {
        return _instance;
    }


    public static class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            if (incomingCall != null) {
                call = incomingCall;
                call.addCallListener(new SinchCallListener());
            }
            return;
            //            txt_type.setText("Hang Up");
            //            img_Call.setImageResource(R.drawable.red_call);

        }
    }
}
