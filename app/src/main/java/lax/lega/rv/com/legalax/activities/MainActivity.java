package lax.lega.rv.com.legalax.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.application.App;
import lax.lega.rv.com.legalax.application.Myinterface;
import lax.lega.rv.com.legalax.calling.BaseActivity;
import lax.lega.rv.com.legalax.common.ConnectionDetector;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.fragment.AppoinmentFragment;
import lax.lega.rv.com.legalax.fragment.CallListFragment;
import lax.lega.rv.com.legalax.fragment.ChatListFragment;
import lax.lega.rv.com.legalax.fragment.DocumentFragment;
import lax.lega.rv.com.legalax.fragment.GetAllSendReceivedFragment;
import lax.lega.rv.com.legalax.fragment.HomeFragment;
import lax.lega.rv.com.legalax.fragment.InboxlistFragment;
import lax.lega.rv.com.legalax.fragment.LawFragment;
import lax.lega.rv.com.legalax.fragment.LawyerProposalFragment;
import lax.lega.rv.com.legalax.fragment.NewsFeedFragment;
import lax.lega.rv.com.legalax.fragment.ProfileFragment;
import lax.lega.rv.com.legalax.fragment.ProposalFragment;
import lax.lega.rv.com.legalax.fragment.SettingsFragment;
import lax.lega.rv.com.legalax.fragment.ShowDocumentFragment;
import lax.lega.rv.com.legalax.fragment.UserListFragment;
import lax.lega.rv.com.legalax.fragment.UserVideoListFragment;
import lax.lega.rv.com.legalax.pojos.FCMTokenPojo;
import lax.lega.rv.com.legalax.pojos.WriteSomethingPojo;
import lax.lega.rv.com.legalax.pojos.getProfile.GetProfileResponse;
import lax.lega.rv.com.legalax.retrofit.WebService;
import lax.lega.rv.com.legalax.utils.CommonFunctionsKt;
import retrofit2.Call;
import retrofit2.Callback;

import static lax.lega.rv.com.legalax.utils.CommonFunctionsKt.getVollyError;

public class MainActivity extends BaseActivity implements HomeFragment.OnItemSelectedListener, SinchService.StartFailedListener {

    private static final String APP_KEY = "a461c54c-abc1-433b-b494-034b2841e4e8";
    private static final String APP_SECRET = "3OhLNK5s2U254J0g8NGw+w==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";
    public static Context mainActivity;
    public static Activity activity;
    RelativeLayout rl_home, rl_chat, rl_appoint, rl_law, rl_profile;
    ImageView img_chat, img_home, img_law, img_appoint;
    TextView textViewMessageCount, textViewCountCalender;
    String firebase_access_token, sp_firebase_access_token;
    private SinchClient sinchClient;
    public static boolean IsOpened = false;
    MySharedPreference mysharedprefrence;
    Myinterface myinterface;
    private PowerManager.WakeLock _screenOffWakeLock;
    Sensor myProximitySensor;
    SensorManager mySensorManager;
    private PowerManager _powerManager;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    BroadcastReceiver bookingStatusReceiver;
    IntentFilter intentFilter2;
    AppEventsLogger logger;
    MySharedPreference sharedPreference;

    public interface FragmentRefreshListener {
        void onRefresh();
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   Fabric.with(this, new Crashlytics());
        setContentView(R.layout.dashboard_activity);
        sharedPreference = new MySharedPreference(this);
        logger = AppEventsLogger.newLogger(this);
        intentFilter = new IntentFilter("android.intent.action.MAIN");
        intentFilter2 = new IntentFilter("bookingStatus");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String badge = intent.getStringExtra("badge");
                final FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.findFragmentById(R.id.flContent) instanceof InboxlistFragment) {

                } else {
                    // textViewMessageCount.setVisibility(View.VISIBLE);
                    //textViewMessageCount.setText(badge);
                }

            }
        };
        this.registerReceiver(receiver, intentFilter);

        bookingStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String badge = intent.getStringExtra("badge");
                final FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.findFragmentById(R.id.flContent) instanceof InboxlistFragment) {

                } else {
                    // textViewCountCalender.setVisibility(View.VISIBLE);
                    // textViewCountCalender.setText(badge);
                }


            }
        };
        this.registerReceiver(bookingStatusReceiver, intentFilter2);

        IsOpened = true;

        mysharedprefrence = new MySharedPreference(MainActivity.this);

        this.mainActivity = getApplicationContext();
        activity = MainActivity.this;
        firebase_access_token = FirebaseInstanceId.getInstance().getToken();
        Log.e("FirebaseToken", ">>>>>>>>>>>>>>>>>>>>>>>>>" + firebase_access_token);
        if (firebase_access_token != null) {
            if (!firebase_access_token.equals("" + new MySharedPreference(MainActivity.this).getString("sp_fcm"))) {
                hitApiFCMTokenUpdate();
            }
        }
        CheckBackgroundFireground();


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        init();
        click();
        MakeUserOnline("1");
        DeletuserfromFirebase();

//      RefreshFullActivity();

        img_home.setImageResource(R.drawable.icn_green_home);
        img_chat.setImageResource(R.drawable.icn_message);
        img_law.setImageResource(R.drawable.icn_law);
        img_appoint.setImageResource(R.drawable.icn_calender);
        replaceFrgamnet(new HomeFragment());

        String refresh = getIntent().getStringExtra("refresh");
        if (refresh != null) {
            if (refresh.equalsIgnoreCase("a")) {
                replaceFrgamnet(new HomeFragment());
                RefreshFullActivity();
            } else if (refresh.equalsIgnoreCase("d")) {
                replaceFrgamnet(new HomeFragment());
                RefreshFullActivity();
            }
        } else {


        }
//        RefreshFullActivity("2");
        String open = getIntent().getStringExtra("open");
        if (open != null) {

            if (open.equalsIgnoreCase("fragment")) {
                replaceFrgamnet(new GetAllSendReceivedFragment());
            }

            ////////////////////06_july_2020///////////////////////////

            else if (open.equalsIgnoreCase("inboxFragment")) {
                img_chat.setImageResource(R.drawable.icn_green_chat);
                img_home.setImageResource(R.drawable.ic_home_outline_grey);
                img_law.setImageResource(R.drawable.icn_law);
                img_appoint.setImageResource(R.drawable.icn_calender);
                replaceFrgamnet(new InboxlistFragment());
            }
            //////////////////////////////////////////////////////////
        }


        String f = getIntent().getStringExtra("f");
        if (f != null) {
            if (f.equals("")) {
                img_home.setImageResource(R.drawable.icn_green_home);
                img_chat.setImageResource(R.drawable.icn_message);
                img_law.setImageResource(R.drawable.icn_law);
                img_appoint.setImageResource(R.drawable.icn_calender);
                replaceFrgamnet(new HomeFragment());
            } else {
                replaceFrgamnet(new ChatListFragment());
                img_home.setImageResource(R.drawable.icn_home);
                img_chat.setImageResource(R.drawable.icn_message);
                img_law.setImageResource(R.drawable.icn_law);
                img_appoint.setImageResource(R.drawable.icn_calender);
            }
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String userName = String.valueOf(new MySharedPreference(MainActivity.this).getInt("id"));

                if (!userName.equals(getSinchServiceInterface().getUserName())) {
                    getSinchServiceInterface().stopClient();
                }

                if (!getSinchServiceInterface().isStarted()) {
                    getSinchServiceInterface().startClient(userName);
                }
            }
        }, 5000);

        if (getIntent().getStringExtra("submitted_offer") != null && getIntent().getStringExtra("submitted_offer").equals("submitted_offer")) {
            ProposalFragment fragmentProposal = new ProposalFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", getIntent().getStringExtra("problem_id"));
            fragmentProposal.setArguments(bundle);
            replaceFrgamnet(fragmentProposal);
        }
    }

    private void DeletuserfromFirebase() {

//        FirebaseAuth auth=FirebaseAuth.getInstance();
//        String currentuser = auth.getCurrentUser().getUid();
//        Log.e("Currentuser",">>>>>>>>>>>"+currentuser);
//        FirebaseAuth.getInstance().revokeRefreshTokens(uid);
//        UserRecord user = FirebaseAuth.getInstance().getUser(uid);
//// Convert to seconds as the auth_time in the token claims is in seconds too.
//        long revocationSecond = user.getTokensValidAfterTimestamp() / 1000;
//        System.out.println("Tokens revoked at: " + revocationSecond);


    }

    private void CheckBackgroundFireground() {
        ConnectionDetector connectionDetector = new ConnectionDetector(MainActivity.this);
        if (connectionDetector.isConnectingToInternet()) {
            App.getInstance().SetInterface(new Myinterface() {
                @Override
                public void OnBackground() {
                    MakeUserOffline("0");
                    Log.e("MyBackground", ">>>>>>>>>>>>>>" + "Activity");
                }

                @Override
                public void OnForground() {
                    MakeUserOnline("1");
                    Log.e("Foregropund", ">>>>>>>>>>>>>>" + "Activity");
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Sorry no internet Connection Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void MakeUserOnline(String s) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.BASE_URL + Utils.ISONLINE_OFFLINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response make online", ">>>>>>>>>>>>>" + response);
                        try {
                            if (response != null && response.length() > 0) {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {


//                                      fragmentRefreshListener.onRefresh();
//                                    Intent i = new Intent("Refresh");
//                                    i.putExtra("refresh", "data");
//                                    sendBroadcast(i);
                                } else {


                                }
                            }

                        } catch (Exception e) {

                            Log.e("Exception is", ">>>>>>>>>>>>>>" + e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getVollyError((MainActivity) activity, error);
                        Log.e("Error", ">>>>>>>>>>>>>>" + error);


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();
                parms.put("is_online", s);
                return parms;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + new MySharedPreference(MainActivity.this).getString("access_token"));
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    private void MakeUserOffline(String s) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.BASE_URL + Utils.ISONLINE_OFFLINE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response != null && response.length() > 0) {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
//                                    Intent i = new Intent("Refresh");
//                                    i.putExtra("refresh", "data");
//                                    sendBroadcast(i);
//                                    fragmentRefreshListener.onRefresh();

                                } else {

                                }
                            }
                        } catch (Exception e) {
                            Log.e("Exception is", ">>>>>>>>>>>>>>" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getVollyError((MainActivity) activity, error);
                        Log.e("Error", ">>>>>>>>>>>>>>" + error);


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();
                parms.put("is_online", s);
                return parms;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + new MySharedPreference(MainActivity.this).getString("access_token"));
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(reciever, new IntentFilter("hi"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(reciever);
    }

    private void RefreshFullActivity() {
        Utils.instance.showProgressBar(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.BASE_URL + Utils.GET_USER_DATA,
                response -> {
                    Utils.instance.dismissProgressDialog();
                    if (response != null && response.length() > 0) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("success");
                            if (status) {
                                JSONObject mainobj = jsonObject.getJSONObject("user");
                                String mainstatus = mainobj.getString("status");
                                mysharedprefrence.putInt("status", Integer.parseInt(mainstatus));
//                                    RecreateActivity();
                            }


                        } catch (Exception e) {
//                              Utils.instance.dismissProgressDialog();
                            Log.e("Exception is", ">>>>>>>>>" + e.toString());
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed To Refresh Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Utils.instance.dismissProgressDialog();
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("VolleyError", ">>>>>>>>>>>>>>" + error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();
                parms.put("user_id", String.valueOf(mysharedprefrence.getInt("id")));
                return parms;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + new MySharedPreference(MainActivity.this).getString("access_token"));
                headers.put("Accept", "application/json");
                return headers;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);


    }

    private void RecreateActivity() throws IOException {

        //        finish();
        //        startActivity(getIntent());
        //        recreate();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.e("On Intent Caleddd", ">>>>>>>>>>>>>>>>>>>>>>>>>>" + "On Intent Calleedddd.....");
        setIntent(intent);
        if (intent != null) {
            if (intent.getStringExtra("refresh") != null) {
                if (intent != null && intent.getStringExtra("refresh").equalsIgnoreCase("a")) {
                    RefreshFullActivity();

                } else if (intent != null && intent.getStringExtra("refresh").equalsIgnoreCase("d")) {
                    RefreshFullActivity();
                }
            } else if (intent.getStringExtra("open") != null) {
                if (intent.getStringExtra("open").equalsIgnoreCase("fragment")) {
                    replaceFrgamnet(new GetAllSendReceivedFragment());
                } else if (intent.getStringExtra("open").equalsIgnoreCase("inboxFragment")) {
                    replaceFrgamnet(new InboxlistFragment());
                    img_home.setImageResource(R.drawable.icn_home);
                    img_chat.setImageResource(R.drawable.icn_green_chat);
                    img_law.setImageResource(R.drawable.icn_law);
                    img_appoint.setImageResource(R.drawable.icn_calender);
                }
            } else if (intent.getStringExtra("bid") != null && intent.getStringExtra("bid").equals("bid_accept")) {
                if (sharedPreference.getString("role").equals("2")) {
                    if (sharedPreference.getInt("status") == 0) {
                        Utils.instance.dismissProgressDialog();
                        Utils.instance.showProgressBar(this);
                        Call<GetProfileResponse> add_credit = new WebService().getApiService().getUserProfileResponse(
                                "Bearer " + sharedPreference.getString("access_token")
                        );

                        add_credit.enqueue(new Callback<GetProfileResponse>() {
                            @Override
                            public void onResponse(Call<GetProfileResponse> call, retrofit2.Response<GetProfileResponse> response) {
                                Utils.instance.dismissProgressDialog();
                                Log.e("GETUSER", new com.google.gson.Gson().toJson(response.body()));
                                if (response.code() == 401) {
                                    CommonFunctionsKt.openLogoutPopup(activity);
                                }
                                if (response.isSuccessful() && response.body() != null) {
                                    if (response.body().getUser().getStatus() == 0) {
                                        final Dialog dialog = new Dialog(activity);
                                        dialog.setContentView(R.layout.contact_us_dialog);
                                        dialog.setTitle("Custom Dialog");
                                        TextView textView = dialog.findViewById(R.id.tv_ok);
                                        textView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    } else {
                                        sharedPreference.putInt("status", 1);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("bid", "bid_accept");
                                        LawyerProposalFragment fragment = new LawyerProposalFragment();
                                        fragment.setArguments(bundle);
                                        replaceFrgamnet(fragment);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                                Utils.instance.dismissProgressDialog();

                                Log.e("GETUSER", t.getMessage());
                            }
                        });


                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("bid", "bid_accept");
                        LawyerProposalFragment fragment = new LawyerProposalFragment();
                        fragment.setArguments(bundle);
                        replaceFrgamnet(fragment);
                    }
                }
            } else if (intent.getStringExtra("legal_concern") != null && intent.getStringExtra("legal_concern").equals("new_legeal_concern_added")) {
                if (sharedPreference.getString("role").equals("2")) {
                    if (sharedPreference.getInt("status") == 0) {
                        Utils.instance.dismissProgressDialog();
                        Utils.instance.showProgressBar(this);
                        Call<GetProfileResponse> add_credit = new WebService().getApiService().getUserProfileResponse(
                                "Bearer " + sharedPreference.getString("access_token")
                        );

                        add_credit.enqueue(new Callback<GetProfileResponse>() {
                            @Override
                            public void onResponse(Call<GetProfileResponse> call, retrofit2.Response<GetProfileResponse> response) {
                                Utils.instance.dismissProgressDialog();
                                Log.e("GETUSER", new com.google.gson.Gson().toJson(response.body()));
                                if (response.code() == 401) {
                                    CommonFunctionsKt.openLogoutPopup(activity);
                                }
                                if (response.isSuccessful() && response.body() != null) {
                                    if (response.body().getUser().getStatus() == 0) {
                                        final Dialog dialog = new Dialog(activity);
                                        dialog.setContentView(R.layout.contact_us_dialog);
                                        dialog.setTitle("Custom Dialog");
                                        TextView textView = dialog.findViewById(R.id.tv_ok);
                                        textView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    } else {
                                        sharedPreference.putInt("status", 1);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("legal_concern", "new_legeal_concern_added");
                                        LawyerProposalFragment fragment = new LawyerProposalFragment();
                                        fragment.setArguments(bundle);
                                        replaceFrgamnet(fragment);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                                Utils.instance.dismissProgressDialog();

                                Log.e("GETUSER", t.getMessage());
                            }
                        });


                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("legal_concern", "new_legeal_concern_added");
                        LawyerProposalFragment fragment = new LawyerProposalFragment();
                        fragment.setArguments(bundle);
                        replaceFrgamnet(fragment);
                    }
                }
            } else if (intent.getStringExtra("submitted_offer") != null && intent.getStringExtra("submitted_offer").equals("submitted_offer")) {
                ProposalFragment fragmentProposal = new ProposalFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", intent.getStringExtra("problem_id"));
                fragmentProposal.setArguments(bundle);
                replaceFrgamnet(fragmentProposal);
            }
        }
    }

    private void click() {
        rl_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_home.setImageResource(R.drawable.icn_green_home);
                img_chat.setImageResource(R.drawable.icn_message);
                img_law.setImageResource(R.drawable.icn_law);
                img_appoint.setImageResource(R.drawable.icn_calender);
                replaceFrgamnet(new HomeFragment());
            }
        });

        rl_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewMessageCount.setVisibility(View.GONE);
                if (mysharedprefrence.getString("role").equalsIgnoreCase("1")) {
                    if (mysharedprefrence.getInt("status") == 0) {
                        showPaymentDialog();
                    } else {
                        img_chat.setImageResource(R.drawable.icn_green_chat);
                        img_home.setImageResource(R.drawable.ic_home_outline_grey);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_calender);
                        replaceFrgamnet(new InboxlistFragment());
                    }
                } else if (mysharedprefrence.getString("role").equalsIgnoreCase("2")) {
                    if (mysharedprefrence.getInt("status") == 0) {
                        ShowContactUsDialog();
                    } else {
                        img_chat.setImageResource(R.drawable.icn_green_chat);
                        img_home.setImageResource(R.drawable.ic_home_outline_grey);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_calender);
                        replaceFrgamnet(new InboxlistFragment());
                    }
                } else if (mysharedprefrence.getString("role").equalsIgnoreCase("4")) {
                    if (mysharedprefrence.getInt("status") == 0) {
                        showPaymentDialog();
                    } else {
                        img_chat.setImageResource(R.drawable.icn_green_chat);
                        img_home.setImageResource(R.drawable.ic_home_outline_grey);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_calender);
                        replaceFrgamnet(new InboxlistFragment());
                    }
                }


            }
        });
        rl_law.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_chat.setImageResource(R.drawable.icn_message);
                img_home.setImageResource(R.drawable.ic_home_outline_grey);
                img_law.setImageResource(R.drawable.ic_law_outline);
                img_appoint.setImageResource(R.drawable.icn_calender);
                replaceFrgamnet(new LawFragment());

            }
        });
        rl_appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewCountCalender.setVisibility(View.GONE);
                if (mysharedprefrence.getString("role").equalsIgnoreCase("1")) {
                    if (mysharedprefrence.getInt("status") == 0) {
                        showPaymentDialog();
                    } else {
                        replaceFrgamnet(new AppoinmentFragment());
                        img_chat.setImageResource(R.drawable.icn_message);
                        img_home.setImageResource(R.drawable.ic_home_outline_grey);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_green_calender);
                    }
                } else if (mysharedprefrence.getString("role").equalsIgnoreCase("2")) {
                    if (mysharedprefrence.getInt("status") == 0) {
                        ShowContactUsDialog();
                    } else {
                        replaceFrgamnet(new AppoinmentFragment());
                        img_chat.setImageResource(R.drawable.icn_message);
                        img_home.setImageResource(R.drawable.ic_home_outline_grey);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_green_calender);
                    }
                } else if (mysharedprefrence.getString("role").equalsIgnoreCase("4")) {

                    replaceFrgamnet(new AppoinmentFragment());
                    img_chat.setImageResource(R.drawable.icn_message);
                    img_home.setImageResource(R.drawable.ic_home_outline_grey);
                    img_law.setImageResource(R.drawable.icn_law);
                    img_appoint.setImageResource(R.drawable.icn_green_calender);
                    /*if (mysharedprefrence.getInt("status") == 0) {
                        showPaymentDialog();
                    } else {
                        replaceFrgamnet(new AppoinmentFragment());
                        img_chat.setImageResource(R.drawable.icn_message);
                        img_home.setImageResource(R.drawable.ic_home_outline_grey);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_green_calender);
                    }*/
                }

            }

        });
        rl_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFrgamnet(new ProfileFragment());
                //                Intent intent = new Intent(MainActivity.this, ProfileFragment.class);
                //                startActivity(intent);
            }
        });
    }



    BroadcastReceiver lawyerOnlineOffline = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
             if (currentFragment instanceof  AppoinmentFragment)
             {
                 rl_appoint.performClick();
             }
        }
    };



    private void init() {

        rl_profile = (RelativeLayout) findViewById(R.id.rl_profile);
        rl_home = (RelativeLayout) findViewById(R.id.rl_home);
        rl_chat = (RelativeLayout) findViewById(R.id.rl_chat);
        rl_appoint = (RelativeLayout) findViewById(R.id.rl_appoint);
        rl_law = (RelativeLayout) findViewById(R.id.rl_law);
        img_chat = (ImageView) findViewById(R.id.img_chat);
        img_home = (ImageView) findViewById(R.id.img_home);
        img_law = (ImageView) findViewById(R.id.img_law);
        img_appoint = (ImageView) findViewById(R.id.img_appoint);
        textViewMessageCount = (TextView) findViewById(R.id.textViewMessageCount);
        textViewCountCalender = (TextView) findViewById(R.id.textViewCountCalender);

        firebase_access_token = FirebaseInstanceId.getInstance().getToken();
        if (new MySharedPreference(MainActivity.this).getString("role").equals("2")) {
            rl_law.setVisibility(View.GONE);
        } else {
            rl_law.setVisibility(View.VISIBLE);

        }
        registerReceiver(
                lawyerOnlineOffline,
                new IntentFilter("lawyer_online_offline")
        );
    }

    public void SendStatus(String status) {
        // Utils.showLoader(SenderCallActivityAnother.this);

        AndroidNetworking.post(Utils.BASE_URL + Utils.SEND_CALL_STATUS).addHeaders("Authorization", "Bearer " + new MySharedPreference(MainActivity.this).getString("access_token")).addHeaders("Accept", "application/json").addBodyParameter("is_online", status).setTag(this).setPriority(Priority.HIGH).build().getAsObject(WriteSomethingPojo.class, new ParsedRequestListener<WriteSomethingPojo>() {
            @Override
            public void onResponse(WriteSomethingPojo user) {
                //   Utils.hideLoader();
                if (user.getSuccess() == true) {
                } else {
                }
            }

            @Override
            public void onError(ANError anError) {

                // Utils.hideLoader();

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        IsOpened = true;
        SendStatus("1");
        App.activityResumed();
//        RefreshFullActivity();


    }

    BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getStringExtra("refresh").equalsIgnoreCase("1")) {
                RefreshFullActivity();
            }
        }
    };


    public void replaceFrgamnet(Fragment fragmentType) {
        final Fragment[] fragment = {null};
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragment[0] = fragmentType;
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment[0]).addToBackStack(null).commit();
    }


    public void showPaymentDialog() {

        final Dialog priceListDialog = new Dialog(MainActivity.this);
        final LayoutInflater inflater = getLayoutInflater();
        View vv = inflater.inflate(R.layout.dialog_payment, (ViewGroup) findViewById(R.id.ll_main), false);
        Button btn_payment = vv.findViewById(R.id.btn_payment);
        TextView btn_cancel = vv.findViewById(R.id.tv_cancel);
        TextView btn_alreadypaid = vv.findViewById(R.id.tvc_already_paid);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logSubscription_FinishEvent(1);
                new MySharedPreference(MainActivity.this).putInt("status", 0);
                priceListDialog.dismiss();

            }

        });

        btn_alreadypaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceListDialog.dismiss();
            }
        });

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MySharedPreference(MainActivity.this).putInt("status", 1);
                logSubscription_InitiateEvent(1);
                priceListDialog.dismiss();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.legalex.ph/payment/"));
                startActivity(browserIntent);

            }
        });

        priceListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (vv.getParent() != null) {
            ((ViewGroup) vv.getParent()).removeView(vv);
        }// <- fix

        priceListDialog.setContentView(vv);

        priceListDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        priceListDialog.setCancelable(false);
        priceListDialog.show();

    }

    @Override
    public void onPizzaItemSelected(String type) {
        switch (type) {
            case "chat":
                //1-> admin, 2-> lawyer, 3-> SME User, 4-> regular user

                switch (new MySharedPreference(MainActivity.this).getString("role")) {
                    case "2":
                        if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                            Utils.instance.dismissProgressDialog();
                            Utils.instance.showProgressBar(MainActivity.this);
                            Call<GetProfileResponse> add_credit = new WebService().getApiService().getUserProfileResponse(
                                    "Bearer " + sharedPreference.getString("access_token")
                            );

                            add_credit.enqueue(new Callback<GetProfileResponse>() {
                                @Override
                                public void onResponse(Call<GetProfileResponse> call, retrofit2.Response<GetProfileResponse> response) {
                                    Utils.instance.dismissProgressDialog();
                                    Log.e("GETUSER", new Gson().toJson(response.body()));
                                    if (response.code() == 401) {
                                        CommonFunctionsKt.openLogoutPopup(MainActivity.this);
                                    }
                                    if (response.isSuccessful() && response.body() != null) {
                                        if (response.body().getUser().getStatus() == 0) {
                                            ShowContactUsDialog();
                                        } else {
                                            new MySharedPreference(MainActivity.this).putInt("status", 1);
                                            replaceFrgamnet(new ChatListFragment());
                                            img_home.setImageResource(R.drawable.icn_home);
                                            img_chat.setImageResource(R.drawable.icn_message);
                                            img_law.setImageResource(R.drawable.icn_law);
                                            img_appoint.setImageResource(R.drawable.icn_calender);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                                    Utils.instance.dismissProgressDialog();
                                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
                                    Log.e("GETUSER", t.getMessage());
                                }
                            });
                        } else {
                            replaceFrgamnet(new ChatListFragment());
                            img_home.setImageResource(R.drawable.icn_home);
                            img_chat.setImageResource(R.drawable.icn_message);
                            img_law.setImageResource(R.drawable.icn_law);
                            img_appoint.setImageResource(R.drawable.icn_calender);
                        }
                        break;
                    case "3":
                        if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                            showPaymentDialog();
                        } else {
                            replaceFrgamnet(new ChatListFragment());
                            img_home.setImageResource(R.drawable.icn_home);
                            img_chat.setImageResource(R.drawable.icn_message);
                            img_law.setImageResource(R.drawable.icn_law);
                            img_appoint.setImageResource(R.drawable.icn_calender);
                        }
                        break;
                    case "1":
                        if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                            showPaymentDialog();
                        } else {
                            replaceFrgamnet(new ChatListFragment());
                            img_home.setImageResource(R.drawable.icn_home);
                            img_chat.setImageResource(R.drawable.icn_message);
                            img_law.setImageResource(R.drawable.icn_law);
                            img_appoint.setImageResource(R.drawable.icn_calender);
                        }
                        break;
                    case "4":
                        if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                            showPaymentDialog();
                        } else {
                            Bundle args = new Bundle();
                            args.putString("from", "chat");
                            UserListFragment fragobj = new UserListFragment();
                            fragobj.setArguments(args);

                            replaceFrgamnet(fragobj);
                            img_home.setImageResource(R.drawable.icn_home);
                            img_chat.setImageResource(R.drawable.icn_message);
                            img_law.setImageResource(R.drawable.icn_law);
                            img_appoint.setImageResource(R.drawable.icn_calender);
                        }
                        break;
                    default:

                        Bundle args = new Bundle();
                        args.putString("from", "chat");
                        UserListFragment fragobj = new UserListFragment();
                        fragobj.setArguments(args);

                        replaceFrgamnet(fragobj);
                        //replaceFrgamnet(new ChatListFragment());
                        img_home.setImageResource(R.drawable.icn_home);
                        img_chat.setImageResource(R.drawable.icn_message);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_calender);
                        break;
                }

                break;
            case "call":
                loginClicked();
            /*if (new MySharedPreference(MainActivity.this).getString("role").equals("2")) {
                //loginClicked();
                if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                    ShowContactUsDialog();
                } else {
                    loginClicked();
                }
            } else if (new MySharedPreference(MainActivity.this).getString("role").equals("3")) {
                if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                    showPaymentDialog();
                } else {
                    loginClicked();
                }
            } else if (new MySharedPreference(MainActivity.this).getString("role").equals("1")) {
                if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                    showPaymentDialog();
                } else {
                    loginClicked();
                }
            } else if (new MySharedPreference(MainActivity.this).getString("role").equals("4")) {
                if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                    showPaymentDialog();
                } else {
                    loginClicked();
                }
            } else {
                loginClicked();
            }*/

                break;
            case "news":
                if (new MySharedPreference(MainActivity.this).getString("role").equals("2")) {
                    //videoCallClick();
                    if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                        //ShowContactUsDialog();
                        Utils.instance.dismissProgressDialog();
                        Utils.instance.showProgressBar(MainActivity.this);
                        Call<GetProfileResponse> add_credit = new WebService().getApiService().getUserProfileResponse(
                                "Bearer " + sharedPreference.getString("access_token")
                        );

                        add_credit.enqueue(new Callback<GetProfileResponse>() {
                            @Override
                            public void onResponse(Call<GetProfileResponse> call, retrofit2.Response<GetProfileResponse> response) {
                                Utils.instance.dismissProgressDialog();
                                Log.e("GETUSER", new Gson().toJson(response.body()));
                                if (response.code() == 401) {
                                    CommonFunctionsKt.openLogoutPopup(MainActivity.this);
                                }
                                if (response.isSuccessful() && response.body() != null) {
                                    if (response.body().getUser().getStatus() == 0) {
                                        ShowContactUsDialog();
                                    } else {
                                        new MySharedPreference(MainActivity.this).putInt("status", 1);
                                        videoCallClick();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                                Utils.instance.dismissProgressDialog();
                                Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
                                Log.e("GETUSER", t.getMessage());
                            }
                        });
                    } else {
                        videoCallClick();
                    }
                } else if (new MySharedPreference(MainActivity.this).getString("role").equals("3")) {
                    if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                        showPaymentDialog();
                    } else {
                        videoCallClick();
                    }
                } else if (new MySharedPreference(MainActivity.this).getString("role").equals("1")) {
                    if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                        showPaymentDialog();
                    } else {
                        videoCallClick();
                    }
                } else if (new MySharedPreference(MainActivity.this).getString("role").equals("4")) {

                    Intent intent = new Intent(MainActivity.this, PurchaseCreditsActivity.class);
                    startActivityForResult(intent, 100);
                    //videoCallClick();
                /*if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                    showPaymentDialog();
                } else {
                    videoCallClick();
                }*/
                } else {
                    videoCallClick();
                }
           /* replaceFrgamnet(new CallListFragment());
            img_home.setImageResource(R.drawable.icn_home);
            img_chat.setImageResource(R.drawable.icn_message);
            img_law.setImageResource(R.drawable.icn_law);
            img_appoint.setImageResource(R.drawable.icn_calender);*/
           /* replaceFrgamnet(new NewsFeedFragment());
            img_home.setImageResource(R.drawable.icn_home);
            img_chat.setImageResource(R.drawable.icn_message);
            img_law.setImageResource(R.drawable.icn_law);
            img_appoint.setImageResource(R.drawable.icn_calender);*/
                break;
            case "document":

                //     replaceFrgamnet(new ShowDocumentFragment());

                if (new MySharedPreference(MainActivity.this).getString("role").equals("2")) {
                    if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                        ShowContactUsDialog();
                    } else {
                        replaceFrgamnet(new DocumentFragment(false));
                        img_home.setImageResource(R.drawable.icn_home);
                        img_chat.setImageResource(R.drawable.icn_message);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_calender);
                    }
                } else if (new MySharedPreference(MainActivity.this).getString("role").equals("3")) {
                    if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
                        showPaymentDialog();
                    } else {
                        replaceFrgamnet(new DocumentFragment(false));
                        img_home.setImageResource(R.drawable.icn_home);
                        img_chat.setImageResource(R.drawable.icn_message);
                        img_law.setImageResource(R.drawable.icn_law);
                        img_appoint.setImageResource(R.drawable.icn_calender);
                    }
                } else if (new MySharedPreference(MainActivity.this).getString("role").equals("1")) {
//                    if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
//                        showPaymentDialog();
//                    } else {
                    replaceFrgamnet(new DocumentFragment(false));
                    img_home.setImageResource(R.drawable.icn_home);
                    img_chat.setImageResource(R.drawable.icn_message);
                    img_law.setImageResource(R.drawable.icn_law);
                    img_appoint.setImageResource(R.drawable.icn_calender);
                    //    }
                } else if (new MySharedPreference(MainActivity.this).getString("role").equals("4")) {
//                    if (new MySharedPreference(MainActivity.this).getInt("status") == 0) {
//                        showPaymentDialog();
//                    } else {
                    replaceFrgamnet(new ShowDocumentFragment());
                    img_home.setImageResource(R.drawable.icn_home);
                    img_chat.setImageResource(R.drawable.icn_message);
                    img_law.setImageResource(R.drawable.icn_law);
                    img_appoint.setImageResource(R.drawable.icn_calender);
                    //  }
                } else {
                    replaceFrgamnet(new DocumentFragment(false));
                    img_home.setImageResource(R.drawable.icn_home);
                    img_chat.setImageResource(R.drawable.icn_message);
                    img_law.setImageResource(R.drawable.icn_law);
                    img_appoint.setImageResource(R.drawable.icn_calender);
                }

                break;
            case "setting":
                replaceFrgamnet(new SettingsFragment());
                img_home.setImageResource(R.drawable.icn_home);
                img_chat.setImageResource(R.drawable.icn_message);
                img_law.setImageResource(R.drawable.icn_law);
                img_appoint.setImageResource(R.drawable.icn_calender);
                break;
            case "profile":
                replaceFrgamnet(new ProfileFragment());
                img_home.setImageResource(R.drawable.icn_home);
                img_chat.setImageResource(R.drawable.icn_message);
                img_law.setImageResource(R.drawable.icn_law);
                img_appoint.setImageResource(R.drawable.icn_calender);
                break;
        }

    }

    private void ShowContactUsDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.contact_us_dialog);
        // Set dialog title
        dialog.setTitle("Custom Dialog");
        TextView textView = dialog.findViewById(R.id.tv_ok);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // set values for custom dialog components - text, image and button

        dialog.show();

        // if decline button is clicked, close the custom dialog


    }


    @Override
    public void onBackPressed() {

        FragmentManager manager = getSupportFragmentManager();
       /* if (manager.getBackStackEntryCount() >=1) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(1);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            finish();
        }*/
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }

    }


    void hitApiFCMTokenUpdate() {
        ConnectionDetector connectionDetector = new ConnectionDetector(MainActivity.this);
        connectionDetector.isConnectingToInternet();
        if (!connectionDetector.isConnectingToInternet()) {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            Utils.instance.showProgressBar(this);

            AndroidNetworking.post(Utils.BASE_URL + Utils.UPDATE_FCM_TOKEN).
                    addHeaders("Authorization", "Bearer " + new MySharedPreference(MainActivity.this).getString("access_token")).addHeaders("Accept", "application/json").
                    addBodyParameter("fcm_token", firebase_access_token).
                    addBodyParameter("device_type", "1").setTag(this).
                    setPriority(Priority.HIGH).build().
                    getAsObject(FCMTokenPojo.class, new ParsedRequestListener<FCMTokenPojo>() {
                        @Override
                        public void onResponse(FCMTokenPojo user) {
                            Utils.instance.dismissProgressDialog();
                            if (user.getSuccess()) {
                                new MySharedPreference(MainActivity.this).putString("sp_fcm", firebase_access_token);
                            } else {
                                //                        Toast.makeText(getApplicationContext(), "Unable", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            // handle error
                            Utils.instance.dismissProgressDialog();
                            CommonFunctionsKt.getAnError(MainActivity.this, anError);
                            Toast.makeText(getApplicationContext(), "Unable to Connect server", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 100 && resultCode == 1) {

            Bundle args = new Bundle();
            args.putString("from", "call");
            UserVideoListFragment fragobj = new UserVideoListFragment();
            fragobj.setArguments(args);
            replaceFrgamnet(fragobj);
            img_home.setImageResource(R.drawable.icn_home);
            img_chat.setImageResource(R.drawable.icn_message);
            img_law.setImageResource(R.drawable.icn_law);
            img_appoint.setImageResource(R.drawable.icn_calender);


          /*  Bundle args = new Bundle();
            args.putString("from", "call");
            UserVideoListFragment fragobj = new UserVideoListFragment();
            fragobj.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragobj).addToBackStack(null).commit();*/


           /* replaceFrgamnet(new VideoCallListFragment());
            img_home.setImageResource(R.drawable.icn_home);
            img_chat.setImageResource(R.drawable.icn_message);
            img_law.setImageResource(R.drawable.icn_law);
            img_appoint.setImageResource(R.drawable.icn_calender);*/
        }
    }


    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SendStatus("0");
        App.activityPaused();
        IsOpened = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SendStatus("0");
        unregisterReceiver(lawyerOnlineOffline);
        unregisterReceiver(receiver);
        unregisterReceiver(bookingStatusReceiver);

    }

    @Override
    public void onStartFailed(SinchError error) {


    }

    @Override
    public void onStarted() {
        //openPlaceCallActivity();
    }

    private void loginClicked() {
       /* String userName = String.valueOf(new MySharedPreference(MainActivity.this).getInt("id"));


        if (!userName.equals(getSinchServiceInterface().getUserName())) {
            getSinchServiceInterface().stopClient();
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
        } else {
            openPlaceCallActivity();
        }*/
        replaceFrgamnet(new NewsFeedFragment());
        /*val fragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.flContent, NewsFeedFragment()).commit()*/
    }

    private void openPlaceCallActivity() {
        replaceFrgamnet(new CallListFragment());
        img_home.setImageResource(R.drawable.icn_home);
        img_chat.setImageResource(R.drawable.icn_message);
        img_law.setImageResource(R.drawable.icn_law);
        img_appoint.setImageResource(R.drawable.icn_calender);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        HideKeyboard(ev);

        return super.dispatchTouchEvent(ev);
    }

    public boolean HideKeyboard(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return true;
    }

    void videoCallClick() {
        /*Intent intent = new Intent(MainActivity.this, PurchaseCreditsActivity.class);
        startActivityForResult(intent, 100);*/


        Bundle args = new Bundle();
        args.putString("from", "call");
        UserVideoListFragment fragobj = new UserVideoListFragment();
        fragobj.setArguments(args);
        replaceFrgamnet(fragobj);
        img_home.setImageResource(R.drawable.icn_home);
        img_chat.setImageResource(R.drawable.icn_message);
        img_law.setImageResource(R.drawable.icn_law);
        img_appoint.setImageResource(R.drawable.icn_calender);

        /*replaceFrgamnet(new VideoCallListFragment());
        img_home.setImageResource(R.drawable.icn_home);
        img_chat.setImageResource(R.drawable.icn_message);
        img_law.setImageResource(R.drawable.icn_law);
        img_appoint.setImageResource(R.drawable.icn_calender);*/
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been * created using AppEventsLogger.newLogger() call.
     */
    public void logSubscription_InitiateEvent(double valToSum) {
        Bundle params = new Bundle();
        params.putString("currency", "");
        params.putString("plan", "");
        logger.logEvent("subscription_Initiate", valToSum, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been * created using AppEventsLogger.newLogger() call.
     */
    public void logSubscription_FinishEvent(double valToSum) {
        Bundle params = new Bundle();
        params.putString("currency", "");
        params.putString("plan", "");
        params.putBoolean("status", false);
        params.putString("error", "Cancelled");
        logger.logEvent("subscription_Finish", valToSum, params);
    }

    private void getUserProfile() {

    }

}
