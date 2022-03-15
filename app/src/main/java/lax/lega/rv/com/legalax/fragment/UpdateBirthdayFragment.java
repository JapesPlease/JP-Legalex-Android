package lax.lega.rv.com.legalax.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.pojos.UpdateProfilePojo;
import lax.lega.rv.com.legalax.utils.CommonFunctionsKt;

import static lax.lega.rv.com.legalax.utils.CommonFunctionsKt.getVollyError;

public class UpdateBirthdayFragment extends Fragment {
    EditText et_birthday;
    Button btn_submit;
    private Calendar myCalendar;
    MySharedPreference sharedPreference;
    String email, name, lastname, age, location, phone, aboutus, information, role;
    String birthday;
    ImageView backpress1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.updatebirthdayfragment, container, false);
        sharedPreference = new MySharedPreference(getActivity());
        GetData();
        init(view);

        return view;
    }

    private void GetData() {
        Utils.instance.showProgressBar(getActivity());


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.BASE_URL + Utils.GET_USER_DATA,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Utils.instance.dismissProgressDialog();
                        Log.e("GetResponse", ">>>>>>>>" + response);

                        if (response != null && response.length() > 0) {
                            try {
                                Gson gson = new Gson();
                                JSONObject jsonObject = new JSONObject(response);
                                boolean status = jsonObject.getBoolean("success");
                                if (status) {
                                    JSONObject mainobject = jsonObject.getJSONObject("user");
                                    phone = mainobject.getString("phone");
                                    lastname = mainobject.getString("last_name");
                                    location = mainobject.getString("location");
                                    email = mainobject.getString("email");
                                    age = mainobject.getString("age");
                                    name = mainobject.getString("name");
                                    aboutus = mainobject.getString("about_us");
                                    information = mainobject.getString("information");
                                    birthday = mainobject.getString("birthday");
                                    if (!birthday.equalsIgnoreCase("null")) {
                                        et_birthday.setText(birthday);
                                    } else {
                                        et_birthday.setText("Add Date of birth");
                                    }


                                }

//                                JSONObject jsonObject = new JSONObject(response);
//                                boolean status = jsonObject.getBoolean("success");
//                                if (status) {
//                                    JSONObject mainobj = jsonObject.getJSONObject("user");
//                                    String mainstatus = mainobj.getString("status");
//                                    sharedPreference.putInt("status", Integer.parseInt(mainstatus));
//
////                                    RecreateActivity();
//
//
//                                }

                            } catch (Exception e) {
//                                  Utils.instance.dismissProgressDialog();
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
                        Utils.instance.dismissProgressDialog();
                        getVollyError(getActivity(), error);

                        Log.e("VolleyError", ">>>>>>>>>>>>>>" + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parms = new HashMap<>();
                parms.put("user_id", String.valueOf(sharedPreference.getInt("id")));
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


    private void init(View view) {
        myCalendar = Calendar.getInstance();
        et_birthday = view.findViewById(R.id.et_date_of_birth);
        backpress1 = view.findViewById(R.id.backpress1);

        btn_submit = view.findViewById(R.id.btn_submit);
        et_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDatePickerDialog();


            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_birthday.getText().toString().length() > 0) {
                    UpdateBirthday(et_birthday.getText().toString().trim());
                } else {
                    et_birthday.setError("Required");
                    et_birthday.requestFocus();
                }

            }
        });
        backpress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, new SettingsFragment()).commit();
            }
        });
    }

    private void UpdateBirthday(String trim) {
        Utils.instance.showProgressBar(getActivity());
        if (information.equalsIgnoreCase("null")) {
            information = "No data avialable";
        }
        if (aboutus.equalsIgnoreCase("null")) {
            aboutus = "No data avialable";
        }
        AndroidNetworking.upload(Utils.BASE_URL + Utils.UPDATE_PROFILE)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addMultipartParameter("email", email)
                .addMultipartParameter("name", name)
                .addMultipartParameter("last_name", lastname)
                .addMultipartParameter("age", age)
                .addMultipartParameter("location", location)
                .addMultipartParameter("phone", phone)
                .addMultipartParameter("about_us", aboutus)
                .addMultipartParameter("information", information)
                .addMultipartParameter("birthday", trim)
                .setPriority(Priority.HIGH)
                .build().
                getAsObject(UpdateProfilePojo.class, new ParsedRequestListener<UpdateProfilePojo>() {

                    @Override
                    public void onResponse(UpdateProfilePojo response) {
                        Utils.instance.dismissProgressDialog();
                        if (response.getSuccess().equals(true)) {

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.flContent, new SettingsFragment()).commit();
                            Toast.makeText(getActivity(), "Birthday Updated Successfully", Toast.LENGTH_SHORT).show();
                            if (response.getUser().getBirthday() != null) {
//                                et_birthday.setText(response.getUser().getBirthday());
                            }
                        } else {
                            Toast.makeText(getActivity(), "Unable TO Update Birthday", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Utils.instance.dismissProgressDialog();
                        CommonFunctionsKt.getAnError(getActivity(), anError);
                        Log.e("ANR", ">>>>>>>>>>" + anError.getErrorBody());
                    }
                });


//                .getAsObject(UpdateProfilePojo::class.java, object : ParsedRequestListener<UpdateProfilePojo> {
//            override fun onResponse(user:UpdateProfilePojo) {
//                  Utils.instance.dismissProgressDialog()
//                Log.e("update", ">>>>>>>>>>>" + user.message);
//
//                if (user.success.equals(true)) {
//                    Toast.makeText(activity, "Record has been updated successfully", Toast.LENGTH_LONG).show()
//                    var updateProfileFragment = UpdateProfileFragment()
//                    val fragmentManager = activity!!.supportFragmentManager
//
//                    fragmentManager.beginTransaction().replace(R.id.flContent, ProfileFragment()).commit()
//
//
//                } else {
//                    Toast.makeText(activity, "Unable to update record", Toast.LENGTH_LONG).show()
//
//                }
//            }
//
//            override fun onError(anError: ANError)
//            {
//                  Utils.instance.dismissProgressDialog()
//                Toast.makeText(activity, "Unable to connect server", Toast.LENGTH_LONG).show()
//
//            }
//        })


    }

    private void openDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d,yyyy");
                String date = simpleDateFormat.format(newDate.getTime());
                et_birthday.setText(date);
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

}
