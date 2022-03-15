package lax.lega.rv.com.legalax.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sinch.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.activities.MainActivity;
import lax.lega.rv.com.legalax.adapter.NewsFeeddapter;
import lax.lega.rv.com.legalax.application.App;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.pojos.GetNewsFeedPojo;
import lax.lega.rv.com.legalax.pojos.getProfile.GetProfileResponse;
import lax.lega.rv.com.legalax.retrofit.WebService;
import lax.lega.rv.com.legalax.utils.CommonFunctionsKt;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static lax.lega.rv.com.legalax.activities.MainActivity.activity;
import static lax.lega.rv.com.legalax.utils.CommonFunctionsKt.getVollyError;

public class HomeFragment extends Fragment {

    View v;
    ConstraintLayout clChat, rl_call, clDocuments, clSendLegalProblem, clProposal, clVideoCall;
    RelativeLayout rl_profile, rl_settings;
    CircleImageView circleImageView;
    private OnItemSelectedListener listener;
    MySharedPreference mySharedPreference;
    ProgressDialog progressDialog;
    MySharedPreference sharedPreference;
    SwitchCompat switchButton;
    LinearLayout linearLayoutOnlineStatus, llLegalAndDoc;
    TextView textViewOnLineOffline, tvDescriptionProposal, tvDescriptionChat, tvProposalTxt;
    ImageView icn_menu, img_add, switchImage;
    AppEventsLogger logger;
    LinearLayoutManager RecyclerViewLayoutManager1;
    RecyclerView recycler;
    NewsFeeddapter categoryNameAdapter;
    String updateUserVideoStatus = "0";
    ArrayList list = new ArrayList<GetNewsFeedPojo.Response>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_fragment_new, container, false);
//        RefreshFullActivity();

        sharedPreference = new MySharedPreference(activity);
        logger = AppEventsLogger.newLogger(getContext());
        init();
        click();
        getNewsFeed();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        App.activityPaused();
    }

    public void setProductRecyclerView() {
        //  product_recycler = v.findViewById<View>(R.id.recycler) as RecyclerView
        if (activity != null) {
            LinearLayoutManager RecyclerViewLayoutManager1 = new LinearLayoutManager(requireActivity());
            recycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
            categoryNameAdapter = new NewsFeeddapter(requireActivity(), list, requireActivity());
            recycler.setAdapter(categoryNameAdapter);
        }
    }


    private void click() {

        String videoCallStatus = sharedPreference.getString("videoCallStatus");

        Log.e("videoCallStatus", videoCallStatus);

        clChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPizzaItemSelected("chat");
                logHomeScreenEvent("chat", String.valueOf(mySharedPreference.getInt("id")), mySharedPreference.getString("role"), 1);
                //                Intent intent=new Intent(getActivity(), chatListFragment.class);
                //                startActivity(intent);
            }
        });


//        rl_call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onPizzaItemSelected("call");
//                logHomeScreenEvent("newsfeed",String.valueOf(mySharedPreference.getInt("id")),mySharedPreference.getString("role"),1);
//            }
//        });

        clSendLegalProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.flContent, new SubmitLegalProblemFragment(getString(R.string.submit_legal_problem)));
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        clProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mySharedPreference.getString("role").equals("2")) {
                    if (mySharedPreference.getInt("status") == 0) {
                        Utils.instance.dismissProgressDialog();
                        Utils.instance.showProgressBar(requireActivity());
                        Call<GetProfileResponse> add_credit = new WebService().getApiService().getUserProfileResponse(
                                "Bearer " + sharedPreference.getString("access_token")
                        );

                        add_credit.enqueue(new Callback<GetProfileResponse>() {
                            @Override
                            public void onResponse(Call<GetProfileResponse> call, retrofit2.Response<GetProfileResponse> response) {
                                Utils.instance.dismissProgressDialog();
                                Log.e("GETUSER", new com.google.gson.Gson().toJson(response.body()));
                                if (response.code() == 401) {
                                    CommonFunctionsKt.openLogoutPopup(requireActivity());
                                }
                                if (response.isSuccessful() && response.body() != null) {
                                    if (response.body().getUser().getStatus() == 0) {
                                        final Dialog dialog = new Dialog(requireActivity());
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
                                        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.flContent, new LawyerProposalFragment());
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<GetProfileResponse> call, Throwable t) {
                                Utils.instance.dismissProgressDialog();
                                Toast.makeText(requireActivity(), "", Toast.LENGTH_LONG).show();
                                Log.e("GETUSER", t.getMessage());
                            }
                        });


                    } else {
                        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.flContent, new LawyerProposalFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                } else {

                    LegalConcernFragment legalFragment = new LegalConcernFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", -1);
                    legalFragment.setArguments(bundle);

                    final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.flContent, legalFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }


            }
        });

        clVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPizzaItemSelected("news");
                logHomeScreenEvent("videoCall", String.valueOf(mySharedPreference.getInt("id")), mySharedPreference.getString("role"), 1);
            }
        });

        clDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPizzaItemSelected("document");
                logHomeScreenEvent("documents", String.valueOf(mySharedPreference.getInt("id")), mySharedPreference.getString("role"), 1);
            }
        });


        rl_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPizzaItemSelected("profile");
            }
        });


        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.flContent, new AddNewsFeedFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        switchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateUserVideoStatus.equals("0")) {
                    textViewOnLineOffline.setText("Online");
                    updateUserVideoStatus = "1";
                    updateUserVideoStatus("1");
                    switchImage.setImageResource(R.drawable.toggle_on);
                    sharedPreference.putString("videoCallStatus", "1");

                    final Dialog dialog = new Dialog(requireActivity());
                    dialog.setContentView(R.layout.contact_us_dialog);
                    TextView textView = dialog.findViewById(R.id.tv_ok);
                    TextView tvPopupMsg = dialog.findViewById(R.id.tvPopupMsg);
                    tvPopupMsg.setText(requireActivity().getResources().getText(R.string.online_click));
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    textViewOnLineOffline.setText("Offline");
                    updateUserVideoStatus = "0";
                    updateUserVideoStatus("0");
                    switchImage.setImageResource(R.drawable.toogle_off);
                    sharedPreference.putString("videoCallStatus", "0");
                }
            }
        });

        rl_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPizzaItemSelected("setting");
            }
        });

        if (new MySharedPreference(activity).getString("role").equals("2")) {
            icn_menu.setVisibility(View.GONE);
            //switchButton.setVisibility(View.VISIBLE);
            linearLayoutOnlineStatus.setVisibility(View.VISIBLE);
            changeStatus(videoCallStatus);
        } else {
            icn_menu.setVisibility(View.VISIBLE);
            linearLayoutOnlineStatus.setVisibility(View.GONE);
            //switchButton.setVisibility(View.GONE);
        }
    }

    private void init() {

        //  Log.e("token is",mySharedPreference.getString("access_token"));

//        try {
//            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
//                    "lax.lega.rv.com.legalax",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


        mySharedPreference = new MySharedPreference(activity);
        clChat = v.findViewById(R.id.clChat);
        clSendLegalProblem = v.findViewById(R.id.clSendLegalProblem);
        clProposal = v.findViewById(R.id.clProposal);
        rl_settings = v.findViewById(R.id.rl_settings);
        rl_profile = v.findViewById(R.id.rl_profile);
        switchImage = (ImageView) v.findViewById(R.id.switchButton);
        textViewOnLineOffline = (TextView) v.findViewById(R.id.textViewOnLineOffline);
        linearLayoutOnlineStatus = (LinearLayout) v.findViewById(R.id.linearLayoutOnlineStatus);
        icn_menu = (ImageView) v.findViewById(R.id.icn_menu);
        img_add = (ImageView) v.findViewById(R.id.img_add);
        recycler = (RecyclerView) v.findViewById(R.id.recycler);


        //  rl_call =v.findViewById(R.id.rl_call);
        clVideoCall = v.findViewById(R.id.clVideoCall);
        clDocuments = v.findViewById(R.id.clDocuments);
        circleImageView = v.findViewById(R.id.img_profile);
        tvDescriptionProposal = v.findViewById(R.id.tvDescriptionProposal);
        tvDescriptionChat = v.findViewById(R.id.tvDescriptionChat);
        tvProposalTxt = v.findViewById(R.id.tvProposalTxt);

        Picasso.with(activity).load(Utils.IMAGESURL + mySharedPreference.getString("profile_image")).placeholder(R.drawable.icn_user_large).into(circleImageView);


        if (sharedPreference.getString("role").equals("1")) {
            img_add.setVisibility(View.VISIBLE);
        } else if (sharedPreference.getString("role").equals("2")) {
            tvDescriptionProposal.setVisibility(View.GONE);
            tvDescriptionChat.setVisibility(View.INVISIBLE);
            img_add.setVisibility(View.INVISIBLE);
            v.findViewById(R.id.clSendLegalProblem).setVisibility(View.GONE);
            v.findViewById(R.id.clDocuments).setVisibility(View.GONE);
        } else {
            tvProposalTxt.setText("MY SUBMISSIONS");
            img_add.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        App.activityResumed();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            RefreshFullActivity();
        }
    }

    private void RefreshFullActivity() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading......");
        progressDialog.setTitle("Please Wait.....");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.BASE_URL + Utils.GET_USER_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Refresh Activity", ">>>>>>>>" + response);
                        progressDialog.dismiss();

                        if (response != null && response.length() > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean status = jsonObject.getBoolean("success");
                                if (status) {
                                    JSONObject mainobj = jsonObject.getJSONObject("user");
                                    String mainstatus = mainobj.getString("status");
                                    mySharedPreference.putInt("status", Integer.parseInt(mainstatus));
                                    int videoCallStatus = mainobj.getInt("video_call_status");
                                    sharedPreference.putString("videoCallStatus", Integer.toString(videoCallStatus));
                                    changeStatus(Integer.toString(videoCallStatus));
//                                    RecreateActivity();
                                }

                            } catch (Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "I Am In  Exception", Toast.LENGTH_LONG).show();
                                Log.e("Exception is", ">>>>>>>>>" + e.toString());


                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed To Refresh Please Try Again", Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", ">>>>>>>>>>>>>>" + error.toString());
                        getVollyError(getActivity(), error);
                        progressDialog.dismiss();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();
                parms.put("user_id", String.valueOf(mySharedPreference.getInt("id")));
                return parms;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + new MySharedPreference(getActivity()).getString("access_token"));
                headers.put("Accept", "application/json");
                return headers;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {      // context instanceof YourActivity
            this.listener = (OnItemSelectedListener) context; // = (YourActivity) context
        } else {
            throw new ClassCastException(context.toString() + " must implement PizzaMenuFragment.OnItemSelectedListener");
        }
    }


    /*@Override
    public void onPizzaItemSelected(String type) {
       *//* if (type.equals("user")){
            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.flContent, new UserAppointFragment());
            transaction.addToBackStack(null);
            transaction.commit();        }*//*
    }*/


    public interface OnItemSelectedListener {
        void onPizzaItemSelected(String type);
    }

    void changeStatus(String videoCallStatus) {
        Log.i("ChangeStatus","called....vlaue  "+videoCallStatus);
        if (videoCallStatus.equals("1")) {
            updateUserVideoStatus = "1";
            textViewOnLineOffline.setText("Online");
            switchImage.setImageResource(R.drawable.toggle_on);
        } else {
            updateUserVideoStatus = "0";
            textViewOnLineOffline.setText("Offline");
            switchImage.setImageResource(R.drawable.toogle_off);
        }
    }


    private void updateUserVideoStatus(String status) {
        Utils.instance.dismissProgressDialog();
        Utils.instance.showProgressBar(activity);
        Call<ResponseBody> request = new WebService().getApiService().updateVideoStatus(
                "Bearer " + sharedPreference.getString("access_token"), status
        );

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.e("response is", response.toString());
                Utils.instance.dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Utils.instance.dismissProgressDialog();
                Toast.makeText(getActivity(), "something", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void logHomeScreenEvent(String selectedTab, String userId, String userRole, double valToSum) {
        Bundle params = new Bundle();
        params.putString("selectedTab", selectedTab);
        params.putString("userId", userId);
        params.putString("userRole", userRole);
        logger.logEvent("HomeScreen", valToSum, params);
    }

    private void getNewsFeed() {
        // Utils.showLoader(IncomingCallScreenActivity.this);
        try {
            AndroidNetworking.get(Utils.BASE_URL + Utils.GET_NEWSFEED)
                    .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                    .addHeaders("Accept", "application/json")
                    .setPriority(Priority.HIGH).build().getAsObject(GetNewsFeedPojo.class, new ParsedRequestListener<GetNewsFeedPojo>() {
                @Override
                public void onResponse(GetNewsFeedPojo user) {
                    if (activity != null) {
                        Utils.instance.dismissProgressDialog();

                        if (user.getSuccess().equals(true)) {
                            Gson gson = new Gson();
//                        Log.e("news feed data", gson.toJson(user));
                            if (user.getResponse().size() > 0) {
                                list.clear();
                                list.addAll(user.getResponse());
                                setProductRecyclerView();
                                recycler.setVisibility(View.VISIBLE);
                                // txt_nodata.visibility = View.GONE;
                            } else {
                                recycler.setVisibility(View.GONE);
                                // txt_nodata.visibility = View.VISIBLE;
                            }

                            /*  for (user in user.response.withIndex()) {
                                  Log.e("aaa", "===[=" + user.value.title)
                                  list.add(user)
                              }*/
                        } else {
                            Toast.makeText(activity, "No Data is fetched", Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onError(ANError anError) {
                    /// Utils.hideLoader();
                    CommonFunctionsKt.getAnError(requireActivity(), anError);
                    Toast.makeText(requireContext(), "Unable to connect server", Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
