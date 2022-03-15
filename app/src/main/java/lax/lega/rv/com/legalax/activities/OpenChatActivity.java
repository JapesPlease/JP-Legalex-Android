package lax.lega.rv.com.legalax.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.deepak.besaat.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.adapter.MessageListAdapter;
import lax.lega.rv.com.legalax.common.ConnectionDetector;
import lax.lega.rv.com.legalax.common.CustomTextviewBold;
import lax.lega.rv.com.legalax.common.Imageutils;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.pojos.GetChatDataPojo;
import lax.lega.rv.com.legalax.pojos.SendMessagePojo;
import lax.lega.rv.com.legalax.pojos.updateBadgeResponse.UpdateBadgeResponse;
import lax.lega.rv.com.legalax.retrofit.WebService;
import lax.lega.rv.com.legalax.utils.CommonFunctionsKt;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenChatActivity extends Activity implements Imageutils.ImageAttachmentListener {
    public static String receiver_id = "";
    Button button_camera, button_gallary, button_cancle, button_document;
    ImageView backpress1;
    RecyclerView product_recycler;
    RecyclerView.LayoutManager RecyclerViewLayoutManager1;
    boolean isLoding = false;
    String next_page_url;
    MessageListAdapter categoryNameAdapter;
    Parcelable recyclerViewState;
    EditText ed_message;
    ImageView img_send, iv_choose1;
    CircleImageView img_profile;
    ArrayList<GetChatDataPojo.Datum> data = new ArrayList<>();
    CustomTextviewBold txt_name;
    ConnectionDetector connectionDetector;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    private Imageutils imgutils;
    private Bitmap bitmap;
    private File localFile;
    public static boolean Check = false;
    private MySharedPreference sharedPreference;

    private String option;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_chat_activity);
        sharedPreference = new MySharedPreference(this);


        option = getIntent().getStringExtra("groupChat");

        init();
        click();
        setPermission();
        ConnectionDetector connectionDetector = new ConnectionDetector(OpenChatActivity.this);
        connectionDetector.isConnectingToInternet();
        if (connectionDetector.isConnectingToInternet() == false) {
            Toast.makeText(OpenChatActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            data.clear();
            if (option != null && option.equals("2")) {
                GetMessages(Utils.BASE_URL + Utils.CHAT_LIST_DATA, getIntent().getStringExtra("id"), "1");
            } else {
                GetMessages(Utils.BASE_URL + Utils.CHAT_LIST_DATA, getIntent().getStringExtra("id"), "0");
            }

        }

        updateBadge(Utils.BASE_URL + Utils.UPDATEBADGE, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        intentFilter = new IntentFilter("android.intent.action.MAIN");
        new MySharedPreference(OpenChatActivity.this).putString(MySharedPreference.IS_CHAT_ACTIVE, "active");
        setUpId();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                GetChatDataPojo.Datum datum_msg;
                datum_msg = (GetChatDataPojo.Datum) intent.getSerializableExtra("chat_data");
//                Log.e("Data", ">>>>>>>>>>>>>>>>>" + datum_msg.getCreatedAt());

                if (getIntent() != null && datum_msg != null) {
                    data.add(0, datum_msg);
                    categoryNameAdapter.notifyDataSetChanged();
                }
            }
        };
        this.registerReceiver(receiver, intentFilter);
    }


    void updateBadge(String url, Boolean isLoding) {
        if (isLoding) {
              Utils.instance.showProgressBar(this);

        }
        AndroidNetworking.get(url + "?" + "for=" + "message" + "&" + "sender_id=" + getIntent().getStringExtra("id"))
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(UpdateBadgeResponse.class, new ParsedRequestListener<UpdateBadgeResponse>() {

                    @Override
                    public void onResponse(UpdateBadgeResponse response) {
                        if (isLoding) {
                              Utils.instance.dismissProgressDialog();
                            Check = true;
                            finish();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (isLoding) {
                              Utils.instance.dismissProgressDialog();
                        }

                        CommonFunctionsKt.getAnError(OpenChatActivity.this, anError);
                        Toast.makeText(OpenChatActivity.this, "Api not hit succesfully", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
        Check = true;

        new MySharedPreference(OpenChatActivity.this).putString(MySharedPreference.IS_CHAT_ACTIVE, "close");
        receiver_id = "";
        try {
            this.unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.e("exp", "" + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Check = true;
    }

    public void setProductRecyclerView() {
        product_recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerViewLayoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, true);

        product_recycler.setLayoutManager(RecyclerViewLayoutManager1);
        categoryNameAdapter = new MessageListAdapter(OpenChatActivity.this, data, "", option);
        product_recycler.setAdapter(categoryNameAdapter);
        categoryNameAdapter.notifyDataSetChanged();
        product_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == data.size() - 1 && !(next_page_url == null)) {
                    if (!isLoding) {

                        if (option != null && option.equals("2")) {
                            GetMessages(next_page_url, getIntent().getStringExtra("id"), "1");
                        } else {
                            GetMessages(next_page_url, getIntent().getStringExtra("id"), "0");
                        }
                        recyclerViewState = product_recycler.getLayoutManager().onSaveInstanceState();
                        isLoding = true;
                    }
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        }
    }

    private void click() {
        backpress1 = (ImageView) findViewById(R.id.backpress1);
        backpress1.setOnClickListener(view -> onBackPressed()
        );
        img_send.setOnClickListener(view -> {
                    if (ed_message.getText().toString().trim().equals("")) {
                        Toast.makeText(OpenChatActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                    } else {

                        if (option != null && option.equals("2")) {
                            SendMessage(Utils.BASE_URL + Utils.SEND_MESSAGE, getIntent().getStringExtra("id"), ed_message.getText().toString(), "1");
                        } else {
                            SendMessage(Utils.BASE_URL + Utils.SEND_MESSAGE, getIntent().getStringExtra("id"), ed_message.getText().toString(), "0");
                        }
                    }
                }
        );

        iv_choose1.setOnClickListener(new View.OnClickListener() {
                                          @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                          @Override
                                          public void onClick(View view) {
                                              chooseImage();
                                          }
                                      }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void chooseImage() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);

        dialog.getWindow().setBackgroundDrawableResource(R.color.colorBlackTrans);
        LayoutInflater inflater = (LayoutInflater) this.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.choosepicturelayout, null);

        dialog.setContentView(v);
        dialog.create();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

        button_camera = (Button) v.findViewById(R.id.button_camera);
        button_gallary = (Button) v.findViewById(R.id.button_gallary);
        button_cancle = (Button) v.findViewById(R.id.button_cancle);
        button_document = (Button) v.findViewById(R.id.button_document);


        button_document.setOnClickListener((view) -> {
            dialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, 3);
        });


        button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        button_camera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(OpenChatActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
                } else {
                    imgutils.launchCamera(1);
                }
            }
        });

        button_gallary.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  dialog.dismiss();
                                                  imgutils.launchGallery(1);
                                              }
                                          }
        );
    }


    private void init() {
        txt_name = (CustomTextviewBold) findViewById(R.id.txt_name);
        imgutils = new Imageutils(OpenChatActivity.this);
        imgutils.setListener(this);
      //  imgutils.setVideoListener(this);
        ed_message = (EditText) findViewById(R.id.ed_message);
        connectionDetector = new ConnectionDetector(OpenChatActivity.this);
        iv_choose1 = (ImageView) findViewById(R.id.iv_choose);
        img_send = (ImageView) findViewById(R.id.img_send);
        img_profile = (CircleImageView) findViewById(R.id.img_profile);


        Intent intent = getIntent();
        setUpId();
        String image = intent.getStringExtra("image");
        Log.e("Image", ">>>>>>>>>>>>>>>>>" + image);
        if (image != null) {

            Picasso.with(OpenChatActivity.this).load(Utils.IMAGESURL + image).into(img_profile);
        }
        txt_name.setText("" + intent.getStringExtra("name"));
    }


    public void SendMessage(String url, String receiver_id, String message, String option) {
         Utils.instance.showProgressBar(this);

        AndroidNetworking.post(url).addHeaders("Authorization", "Bearer " + new MySharedPreference(OpenChatActivity.this).getString("access_token")).addHeaders("Accept", "application/json").addBodyParameter("receiver_id", receiver_id).addBodyParameter("message", message).addBodyParameter("group_chat", option).setTag(this).setPriority(Priority.HIGH).build().getAsObject(SendMessagePojo.class, new ParsedRequestListener<SendMessagePojo>() {
            @Override
            public void onResponse(SendMessagePojo user) {
                  Utils.instance.dismissProgressDialog();
                if (user.getSuccess()) {
                    //                    Intent intent = getIntent();
                    //                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    //                    finish();
                    //                    overridePendingTransition(0, 0);
                    //                    startActivity(intent);
                    //                    overridePendingTransition(0, 0);
                    data.add(0, user.getLastMessage());
                    categoryNameAdapter.notifyDataSetChanged();
                    ed_message.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to send message", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                // handle error
                  Utils.instance.dismissProgressDialog();
                CommonFunctionsKt.getAnError(OpenChatActivity.this, anError);
                Toast.makeText(getApplicationContext(), "Unable to Connect server", Toast.LENGTH_LONG).show();

            }
        });
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


    public void SendImage(String url, String receiver_id, File file) {
        Log.e("file", file.toString());

         Utils.instance.showProgressBar(this);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),
                file);
        RequestBody receiver_id_part = RequestBody.create(MediaType.parse("text/plain"), receiver_id);
        RequestBody group_1 = RequestBody.create(MediaType.parse("text/plain"), "1");
        RequestBody group_0 = RequestBody.create(MediaType.parse("text/plain"), "0");
        Call<SendMessagePojo> call=null;
        if (option != null && option.equals("2")) {
           call = new WebService().getApiService().uploadImageOnChat("Bearer " + sharedPreference.getString("access_token")
                    , "application/json", receiver_id_part,group_1, MultipartBody.Part.createFormData("file", file.getName(), requestBody));

//            SendMessage(Utils.BASE_URL + Utils.SEND_MESSAGE, getIntent().getStringExtra("id"), ed_message.getText().toString(), "1");
        } else {
            call = new WebService().getApiService().uploadImageOnChat("Bearer " + sharedPreference.getString("access_token")
                    , "application/json", receiver_id_part,group_0, MultipartBody.Part.createFormData("file", file.getName(), requestBody));

//            SendMessage(Utils.BASE_URL + Utils.SEND_MESSAGE, getIntent().getStringExtra("id"), ed_message.getText().toString(), "0");
        }

        call.enqueue(new Callback<SendMessagePojo>() {
            @Override
            public void onResponse(Call<SendMessagePojo> call, Response<SendMessagePojo> response) {
              /*  try {
                    String responseString = response.body().string().toString();
                    Log.e("response String is",responseString);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                  Utils.instance.dismissProgressDialog();
                if (response.body().getSuccess()) {
//                    SendMessagePojo pojo = new Gson().fromJson(response.toString(), SendMessagePojo.class);


                    Log.e("file msg", response.body().getMessage());


                        data.add(0, response.body().getLastMessage());


                    categoryNameAdapter.notifyDataSetChanged();
                    ed_message.setText("");
//                    Toast.makeText(getApplicationContext(), "upload image in chat..", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to send message", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<SendMessagePojo> call, Throwable t) {
                  Utils.instance.dismissProgressDialog();
                Log.e("throwable", ">>>>>>>>>>>>>>>>>>>>>>>>" + t.toString());
                Toast.makeText(getApplicationContext(), "Unable to Connect server", Toast.LENGTH_LONG).show();
            }
        });


//        AndroidNetworking.upload(url).addHeaders("Authorization", "Bearer " + new MySharedPreference(OpenChatActivity.this).getString("access_token"))
//                .addHeaders("Accept", "application/json")
//                .addMultipartParameter("receiver_id", receiver_id)
//                .addMultipartFile("file", fbody)
//                .setTag(this).setPriority(Priority.HIGH).build().getAsObject(SendMessagePojo.class, new ParsedRequestListener<SendMessagePojo>() {
//            @Override
//            public void onResponse(SendMessagePojo user) {
//                  Utils.instance.dismissProgressDialog();
//                if (user.getSuccess()) {
//                    Log.e("file msg", user.getMessage());
//
//                    data.add(0, user.getLastMessage());
//                    categoryNameAdapter.notifyDataSetChanged();
//                    ed_message.setText("");
//                    Toast.makeText(getApplicationContext(), "upload image in chat..", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Unable to send message", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onError(ANError anError) {
//                // handle error
//                  Utils.instance.dismissProgressDialog();
//                CommonFunctionsKt.getAnError(OpenChatActivity.this, anError);
//                Toast.makeText(getApplicationContext(), "Unable to Connect server", Toast.LENGTH_LONG).show();
//
//            }
//        });
    }


    public void GetMessages(String url, String receiver_id, String groupChat) {
         Utils.instance.showProgressBar(this);


        Call<GetChatDataPojo> call = new WebService().getApiService().getChats("Bearer " + sharedPreference.getString("access_token")
                , "application/json", receiver_id, groupChat);

        call.enqueue(new Callback<GetChatDataPojo>() {
            @Override
            public void onResponse(Call<GetChatDataPojo> call, Response<GetChatDataPojo> response) {
                  Utils.instance.dismissProgressDialog();
                if (response.body().getSuccess()) {
                    setProductRecyclerView();
                    data.addAll(response.body().getChat().getData());
                    if (response.body().getChat().getNextPageUrl() != null) {
                        next_page_url = response.body().getChat().getNextPageUrl().toString();
                    } else {
                        next_page_url = null;
                    }
                    isLoding = false;
                    categoryNameAdapter.notifyDataSetChanged();

                    Log.e("chat", data + "");

                } else {
                    Toast.makeText(OpenChatActivity.this, "This is for testing purpose", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetChatDataPojo> call, Throwable t) {
                  Utils.instance.dismissProgressDialog();
                Toast.makeText(OpenChatActivity.this, "Some thing went wrong", Toast.LENGTH_LONG).show();
                Log.e("AddCreditPointsOnError", t.getMessage());
            }
        });


//        AndroidNetworking
//                .post(url)
//                .addHeaders("Authorization", "Bearer " + new MySharedPreference(OpenChatActivity.this).getString("access_token"))
//                .addHeaders("Accept", "application/json")
//                .addBodyParameter("receiver_id", receiver_id)
//                .addBodyParameter("group_chat", groupChat)
//                .setTag(this)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsObject(GetChatDataPojo.class, new ParsedRequestListener<GetChatDataPojo>() {
//                    @Override
//                    public void onResponse(GetChatDataPojo user) {
//                          Utils.instance.dismissProgressDialog();
//                        if (user.getSuccess()) {
//                            setProductRecyclerView();
//                            data.addAll(user.getChat().getData());
//                            if (user.getChat().getNextPageUrl() != null) {
//                                next_page_url = user.getChat().getNextPageUrl().toString();
//                            } else {
//                                next_page_url = null;
//                            }
//                            isLoding = false;
//                            categoryNameAdapter.notifyDataSetChanged();
//
//                            Log.e("chat", data + "");
//
//                        } else {
//                            Toast.makeText(OpenChatActivity.this, "This is for testing purpose", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        // handle error
//                          Utils.instance.dismissProgressDialog();
//                        CommonFunctionsKt.getAnError(OpenChatActivity.this, anError);
//                    }
//                });
    }

    @Override
    public void onBackPressed() {
        updateBadge(Utils.BASE_URL + Utils.UPDATEBADGE, true);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        bitmap = file;
        String file_name = filename;
        String path = Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imgutils.createImage(file, filename, path, false);
        localFile = Utils.getFileFromImage(this, filename, bitmap);
        setUpId();

        SendImage(Utils.BASE_URL + Utils.SEND_MESSAGE, getIntent().getStringExtra("id"), localFile);
        Log.d("filename", file_name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            String filePath = "";
            if (resultCode == Activity.RESULT_OK) {
                if (data.getData().toString().contains("com.google.android.apps.docs.storage")) {
                    Toast.makeText(this, "First please download this file", Toast.LENGTH_LONG).show();
                    return;
                   /* Uri uri = Uri.parse(data.getData().toString());
                    filePath=Utils.getDriveFilePath(uri,this);
                    Log.e("File path is", filePath);*/
                } else {
                    Uri uri = Uri.parse(data.getData().toString());
                    filePath = FileUtil.Companion.getPath(uri, this);
                }
                if (filePath == null && filePath.equals("")) {
                    Toast.makeText(this, "File is not correct", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Log.e("File path is", filePath);
                    File file = new File(filePath);
                    setUpId();
                    SendImage(Utils.BASE_URL + Utils.SEND_MESSAGE, getIntent().getStringExtra("id"), file);
                }
            }
        } else {
            try {
                imgutils.onActivityResult(requestCode, resultCode, data);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
            } catch (Exception e) {
            }
        }
    }


    void setUpId() {
        if (option != null && option.equals("2")) {
            receiver_id = getIntent().getStringExtra("groupId");
            Log.e("receieverId is",receiver_id);
        } else {
            receiver_id = getIntent().getStringExtra("id");
        }
    }
}
