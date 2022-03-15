package lax.lega.rv.com.legalax.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.activities.Image_user;
import lax.lega.rv.com.legalax.common.CustomTextviewHelvicNormal;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.pojos.GetChatDataPojo;
import lax.lega.rv.com.legalax.retrofit.Constant;
import okhttp3.internal.Util;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Activity context;
    String user_profile_pic;
    String option;
    private ArrayList<GetChatDataPojo.Datum> mMessageList;

    public MessageListAdapter(Activity context, ArrayList<GetChatDataPojo.Datum> messageList, String userProfilePic, String option) {
        this.context = context;
        mMessageList = messageList;
        this.user_profile_pic = user_profile_pic;
        this.option = option;

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject obj = null;
        GetChatDataPojo.Datum message = (GetChatDataPojo.Datum) mMessageList.get(position);
        try {
            if (message.getSenderId().toString().equals(String.valueOf(new MySharedPreference(context).getInt("id")))) {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            } else {
                return VIEW_TYPE_MESSAGE_SENT;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_receiver, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_sender, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GetChatDataPojo.Datum message = (GetChatDataPojo.Datum) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        CustomTextviewHelvicNormal messageText, messagedate;
        ImageView image, imageDocument;
        CardView cv_image_boder;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (CustomTextviewHelvicNormal) itemView.findViewById(R.id.txtInfo);
            messagedate = (CustomTextviewHelvicNormal) itemView.findViewById(R.id.txtdate);
            image = (ImageView) itemView.findViewById(R.id.image);
            cv_image_boder = itemView.findViewById(R.id.cv_image_boder);
            imageDocument = (ImageView) itemView.findViewById(R.id.imageDocument);
            //     cv_profile_image = itemView.findViewById(R.id.cv_profile_image);
        }

        void bind(final GetChatDataPojo.Datum message) {
            try {
                if (message.getFile() == null || message.getFile().equals("")) {
                    messageText.setVisibility(View.VISIBLE);
                    image.setVisibility(View.GONE);
                    imageDocument.setVisibility(View.GONE);
                    messageText.setText(message.getMessage());
                    messagedate.setText(getDate(message.getCreatedAt()));
//                    Log.e("CreatedAt", ">>>>>>>>>>>>" + message.getCreatedAt());
                } else if (message.getFile().contains(".png") | message.getFile().contains(".jpg")) {
                    image.setVisibility(View.VISIBLE);

                    //Picasso.with(context).load(Constant.CHATIMAGEURL + message.getFile()).placeholder(R.drawable.icn_user_large).into(image);
                    imageDocument.setVisibility(View.GONE);
                    messageText.setVisibility(View.GONE);
                    cv_image_boder.setVisibility(View.VISIBLE);
                    messagedate.setText(getDate(message.getCreatedAt()));
                    if(message.getFile().contains("http://admin.legalex.ph/chatfile/")){
                        Picasso.with(context).load( message.getFile()).into(image);
                    }else{
                        Picasso.with(context).load(Utils.CHAT_IMAGES_URL + message.getFile()).into(image);
                    }
//                    Picasso.with(context).load(Utils.CHAT_IMAGES_URL + message.getFile()).into(image);
                } else if (message.getFile().contains(".")) {
                    image.setVisibility(View.GONE);
                    imageDocument.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.GONE);
                    messagedate.setText(getDate(message.getCreatedAt()));
                    cv_image_boder.setVisibility(View.VISIBLE);
                    //Picasso.with(context).load(Utils.CHAT_IMAGES_URL + message.getFile()).into(image);
                } else {
                    messageText.setVisibility(View.VISIBLE);
                    image.setVisibility(View.GONE);
                    imageDocument.setVisibility(View.GONE);
                    messagedate.setText(message.getCreatedAt());
                    messagedate.setText(getDate(message.getCreatedAt()));
                }
//               Picasso.with(context).load(Utils.CHAT_PROFILE_PICS + user_profile_pic).placeholder(R.drawable.noimage).into(cv_profile_image);

            } catch (Exception e) {
                messageText.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                messageText.setText(message.getMessage());
                messagedate.setText(getDate(message.getCreatedAt()));
                //if ()
                // Picasso.with(context).load(UrlConstants.CHAT_PROFILE_PICS + user_profile_pic).placeholder(R.drawable.noimage).into(cv_profile_image);
            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(context, Image_user.class);
                    if( message.getFile().contains("http://admin.legalex.ph/chatfile/"))
                        it.putExtra("image_user",  message.getFile());
                    else
                    it.putExtra("image_user", Utils.CHAT_IMAGES_URL + message.getFile());
                    context.startActivity(it);

                }
            });

            imageDocument.setOnClickListener((view) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.CHAT_IMAGES_URL + message.getFile()));
                context.startActivity(browserIntent);
            });

        }
    }

    private String getDate(String ourDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(ourDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            ourDate = dateFormatter.format(value);

            //Log.d("ourDate", ourDate);
        } catch (Exception e) {
            ourDate = "00-00-0000 00:00";
        }
        return ourDate;
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        CustomTextviewHelvicNormal messageText, messagedate, textSenderName;
        ImageView image, imageDocument;
        CardView cv_image_boder;
        // CircleImageView cv_profile_image;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (CustomTextviewHelvicNormal) itemView.findViewById(R.id.txtInfo);
            messagedate = (CustomTextviewHelvicNormal) itemView.findViewById(R.id.txt_date);
            textSenderName = (CustomTextviewHelvicNormal) itemView.findViewById(R.id.textSenderName);
            image = (ImageView) itemView.findViewById(R.id.image);
            imageDocument = (ImageView) itemView.findViewById(R.id.imageDocument);
            cv_image_boder = itemView.findViewById(R.id.cv_image_boder);
            ///cv_profile_image = itemView.findViewById(R.id.cv_profile_image);
        }

        void bind(final GetChatDataPojo.Datum message) {


            if (option.equals("2")) {
                textSenderName.setVisibility(View.VISIBLE);
                textSenderName.setText(message.getSenderName());
            } else {
                textSenderName.setVisibility(View.GONE);
            }


            try {
                if (message.getFile() == null || message.getFile().equals("")) {
                    Log.e("xxxx", "null pointer");
                    messageText.setVisibility(View.VISIBLE);
                    image.setVisibility(View.GONE);
                    imageDocument.setVisibility(View.GONE);
                    messageText.setText(message.getMessage());
                    cv_image_boder.setVisibility(View.GONE);
                    messagedate.setText(getDate(message.getCreatedAt()));

                } else if (message.getFile().contains(".png") | message.getFile().contains(".jpg")) {
                    image.setVisibility(View.VISIBLE);
                    imageDocument.setVisibility(View.GONE);
                    messageText.setVisibility(View.GONE);
                    cv_image_boder.setVisibility(View.VISIBLE);
                    messagedate.setText(getDate(message.getCreatedAt()));
                    if(message.getFile().contains("http://admin.legalex.ph/chatfile/")){
                        Picasso.with(context).load( message.getFile()).into(image);
                    }else{
                        Picasso.with(context).load(Utils.CHAT_IMAGES_URL + message.getFile()).into(image);
                    }

                } else if (message.getFile().contains(".")) {
                    image.setVisibility(View.GONE);
                    imageDocument.setVisibility(View.VISIBLE);
                    messagedate.setText(getDate(message.getCreatedAt()));
                    cv_image_boder.setVisibility(View.VISIBLE);
                } else {
                    messageText.setVisibility(View.VISIBLE);
                    image.setVisibility(View.GONE);
                    cv_image_boder.setVisibility(View.GONE);
                    imageDocument.setVisibility(View.GONE);
                    messageText.setText(message.getMessage());
                    messagedate.setText(getDate(message.getCreatedAt()));
                }
//               Picasso.with(context).load(Utils.CHAT_PROFILE_PICS + my_profile_pic).placeholder(R.drawable.noimage).into(cv_profile_image);
            } catch (Exception e) {
                messageText.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                messageText.setText(message.getMessage());
                messagedate.setText(getDate(message.getCreatedAt()));
                //Picasso.with(context).load(UrlConstants.CHAT_PROFILE_PICS + my_profile_pic).placeholder(R.drawable.noimage).into(cv_profile_image);

            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(context, Image_user.class);
                    it.putExtra("image_user", Utils.CHAT_IMAGES_URL + message.getFile());
                    context.startActivity(it);

                }
            });

            imageDocument.setOnClickListener((view) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.CHAT_IMAGES_URL + message.getFile()));
                context.startActivity(browserIntent);
            });
        }
    }


    public void customNotifiy() {

    }

}