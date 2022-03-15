package lax.lega.rv.com.legalax.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.sinch.android.rtc.Sinch;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LooperThread extends Thread {
    private static final String APP_KEY = "a461c54c-abc1-433b-b494-034b2841e4e8";
    private static final String APP_SECRET = "3OhLNK5s2U254J0g8NGw+w==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";
      public Handler mHandler;
    SharedPreferences sharedPreferences;
      @Override
      public void run() {
          Looper.prepare();

          mHandler = new Handler() {
              @Override
              public void handleMessage(Message msg) {
                  // process incoming messages here
                  sharedPreferences = getApplicationContext().getSharedPreferences("login_detail", Context.MODE_PRIVATE);

                  Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(String.valueOf(sharedPreferences.getInt("id", 0))).applicationKey(APP_KEY).applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();

              }
          };

          Looper.loop();
      }
  }