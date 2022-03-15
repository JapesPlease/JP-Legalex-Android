package lax.lega.rv.com.legalax.common;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.sinch.android.rtc.SinchHelpers;

public class GcmIntentService extends IntentService implements ServiceConnection {

    private Intent mIntent;
public static String isFrom="";
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (SinchHelpers.isSinchPushIntent(intent)) {
            mIntent = intent;
            connectToService(mIntent);
        } else {
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void connectToService(Intent intent) {
        //        Utils.sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        //        Intent intent = new Intent(this, SenderCallActivity.class);
        //        intent.putExtra("from","receiving");
        //        startActivity(intent);
        if(mIntent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")){
            isFrom="receive";
            Intent intent1=new Intent(this,MessageService.class);
            intent1.putExtra("from","receive");
            getApplicationContext().bindService(intent1, this, Context.BIND_AUTO_CREATE);

        }else {
            isFrom="not receive";
            Intent intent1=new Intent(this,MessageService.class);
            intent1.putExtra("from","not receive");
            getApplicationContext().bindService(intent1, this, Context.BIND_AUTO_CREATE);

        }

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (mIntent == null) {
            return;
        }
        if (SinchHelpers.isSinchPushIntent(mIntent)) {
          /*  MessageService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) iBinder;
            if (sinchService != null) {
                NotificationResult result = sinchService.relayRemotePushNotificationPayload(mIntent);
                // handle result, e.g. show a notification or similar
            }*/
        }
        GcmBroadcastReceiver.completeWakefulIntent(mIntent);
        mIntent = null;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
