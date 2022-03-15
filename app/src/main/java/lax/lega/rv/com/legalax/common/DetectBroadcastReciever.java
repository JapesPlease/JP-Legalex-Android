package lax.lega.rv.com.legalax.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import lax.lega.rv.com.legalax.application.App;

public class DetectBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {

        if (App.activityVisible)
        {
            Toast.makeText(context, "Application is running", Toast.LENGTH_SHORT).show();
            // application is in foreground and running
            // do stuff here
        }
        else {
            Toast.makeText(context, "Application in background", Toast.LENGTH_SHORT).show();
            // application is in background
            // do stuff here

        }

    }
}
