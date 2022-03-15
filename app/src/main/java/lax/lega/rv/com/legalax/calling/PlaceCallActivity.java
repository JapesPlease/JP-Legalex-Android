package lax.lega.rv.com.legalax.calling;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;

import java.util.Timer;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.activities.SinchService;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.retrofit.Constant;

public class PlaceCallActivity extends BaseActivity {
    TextView tv_name;
    ImageView backpress1;
    ImageView caller_image, img_Call_end;
    String user_img = "";
    static final String TAG = CallScreenActivity.class.getSimpleName();
    MySharedPreference mySharedPreference;
    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private String mCallId;

    private Button mCallButton;
//    private OnClickListener buttonClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.callButton:
//                    callButtonClicked();
//                    break;
//
//                case R.id.stopButton:
//                    stopButtonClicked();
//                    break;
//
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_sender);
        tv_name = findViewById(R.id.tv_name);
//        backpress1 = (ImageView) findViewById(R.id.backpress1);
//        mCallButton = (Button) findViewById(R.id.callButton);
//        mCallButton.setEnabled(false);
//        mCallButton.setOnClickListener(buttonClickListener);
        caller_image = (ImageView) findViewById(R.id.caller_image);
        img_Call_end = (ImageView) findViewById(R.id.img_Call);


//        Button stopButton = (Button) findViewById(R.id.stopButton);
        MakeCall();
//        stopButton.setOnClickListener(buttonClickListener);

        tv_name.setText(getIntent().getStringExtra("name") + " " + getIntent().getStringExtra("lastname"));
        user_img = getIntent().getStringExtra("image");
        if (user_img != "")
        {
            Picasso.with(PlaceCallActivity.this).load(Utils.IMAGESURL + user_img).into(caller_image);
        }
        else {
            Picasso.with(PlaceCallActivity.this).load(R.drawable.lock).into(caller_image);
        }
//        backpress1.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
    }

    private void MakeCall() {



}

    @Override
    protected void onServiceConnected()
    {
//        mCallButton.setEnabled(true);
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private void callButtonClicked() {

        String userName = getIntent().getStringExtra("id");
        try {
            Call call = getSinchServiceInterface().callUser(userName);
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before " + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra("lastname", getIntent().getStringExtra("lastname"));
            callScreen.putExtra("name", getIntent().getStringExtra("name"));
            callScreen.putExtra(SinchService.CALL_ID, callId);
            startActivity(callScreen);
            finish();
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast.LENGTH_SHORT).show();
        }
    }
}
