package lax.lega.rv.com.legalax.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sinch.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.activities.MainActivity;
import lax.lega.rv.com.legalax.activities.OpenChatActivity;
import lax.lega.rv.com.legalax.activities.VideoCallActivity;
import lax.lega.rv.com.legalax.common.GcmIntentService;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.pojos.GetChatDataPojo;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM_Service";
    public static Ringtone r;
    final int NotificationID = 1;
    JSONObject object, object2;
    Intent i;
    String CHANNEL_ID = "com.legalax";
    String CHANNEL_NAME = "Legalex";
    String CHANNEL_DESCRIPTION = "http://www.Legalex.com/";
    SharedPreferences sharedPreferences;
    String isChatActive = "";
    String type = "", sender_id = "", badge = "", message, fullname = "", fcm_token = "", groupChat = "1", chat_data;
    String points;
    GetChatDataPojo.Datum chatDatum;
    String mesa;
    String problem_id = "";
    private NotificationChannel mChannel;
    private NotificationManager notifManager;
    MySharedPreference mySharedPreference;


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedPreferences = getApplicationContext().getSharedPreferences("login_detail", Context.MODE_PRIVATE);

        mySharedPreference = new MySharedPreference(this);

        isChatActive = sharedPreferences.getString(MySharedPreference.IS_CHAT_ACTIVE, "");

        //  isChatActive = sharedPreferences.getString("CAll_Enable", "");
        Map<String, String> params = remoteMessage.getData();
        object = new JSONObject(params);
        Log.e("JSON_OBJECT", "" + object.toString());
        Log.e("JSON_keys", "" + object.keys());
        type = object.optString("type");
        Log.e("Type", ">>>>>>>>>>>>>>>>" + type);
        sender_id = object.optString("sender_id");
        if (type.equals("user_activate"))
            mySharedPreference.putInt("status", 1);
        if (type.equals("user_deactivate"))
            mySharedPreference.putInt("status", 0);

        if (type.equals("Chating") && object.has("message")) {
            chat_data = object.optString("message");
            //String message=chat_data.
            try {
                if (chat_data != null) {
                    // group Chat
                    if (new JSONObject(chat_data).has("firmid") && new JSONObject(chat_data).optInt("firmid") != 0)
                        sender_id = new JSONObject(chat_data).optInt("firmid") + "";

                    if (new JSONObject(chat_data).has("group_chat"))
                        groupChat = String.valueOf(new JSONObject(chat_data).optInt("group_chat") + 1);  // to match with OpenChatActivity Option variable value
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        badge = object.optString("badge");
        message = object.optString("message");
        fullname = object.optString("fullname");
        fcm_token = object.optString("fcm_token");
        problem_id = object.optString("problem_id");
        try {
            points = object.optString("points");
            mySharedPreference.putInt("points", Integer.parseInt(points));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (type != null) {
            sharedPreferences.getString("type", type);
        } else if (sender_id != null) {
            sharedPreferences.getString("sender_id", sender_id);

        } else if (sender_id != null) {
            sharedPreferences.getString("sender_id", sender_id);

        }
        if (type.equals("calling")) {
            try {
                //                Uri Emergency_sound_uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.ringing);
                //                r = RingtoneManager.getRingtone(getApplicationContext(), Emergency_sound_uri);
                //                r.play();
                //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                /*for (int i=0;i<100;i++) {
                    Uri Emergency_sound_uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.ringing);
                    r = R ingtoneManager.getRingtone(getApplicationContext(), Emergency_sound_uri);
                    r.play();
                }*/


            } catch (Exception e) {

                e.printStackTrace();
            }
            //            Intent intent = new Intent(getApplicationContext(), ReceiverCallActivity.class);
            //            intent.putExtra("from", "receiving");
            //            intent.putExtra("type", type);
            //            intent.putExtra("sender_id", sender_id);
            //            intent.putExtra("badge", badge);
            //            intent.putExtra("message", message);
            //            intent.putExtra("fullname", fullname);
            //            intent.putExtra("fcm_token", fcm_token);
            //            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //            startActivity(intent);
        } else if (type.equals("video_call")) {
            String session_id = "";
            String token = "";
            String reciver_name = "";
            String reciver_image = "";
            try {
                String jsonObject = object.getString("data");
                JSONObject object = new JSONObject(jsonObject);
                session_id = object.optString("session_id");
                token = object.optString("token");
                reciver_name = object.optString("reciver_name");
                reciver_image = object.optString("reciver_image");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, VideoCallActivity.class)
                    .putExtra("sessionId", session_id)
                    .putExtra("token", token)
                    .putExtra("callType", "Incoming")
                    .putExtra("image", reciver_image)
                    .putExtra("name", reciver_name)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (type.equals("Chating")) {
            try {
                if (object.has("message")) {
                    chat_data = object.optString("message");
                    //String message=chat_data.
                    object2 = new JSONObject(chat_data);
                    mesa = object2.optString("message");
                } else {

                }
                Gson gson = new Gson();
                if (chat_data != null) {
                    chatDatum = gson.fromJson(chat_data, GetChatDataPojo.Datum.class);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!isAppIsInBackground(getApplicationContext())) {
                if (isChatActive.equals("active") && sender_id.equals(OpenChatActivity.receiver_id)) {
                    Intent i = new Intent("android.intent.action.MAIN");
                    if (chat_data != null) {
                        i.putExtra("chat_data", chatDatum);
                        i.putExtra("name", fullname);
                    }
                    i.putExtra("id", sender_id);
                    i.putExtra("groupChat", groupChat);
                    i.putExtra("badge", badge);
                    this.sendBroadcast(i);
                } else {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.putExtra("badge", badge);
                    this.sendBroadcast(i);
                    sendNotification(mesa, "chat");
                }
                Intent i = new Intent("refreshList");
                this.sendBroadcast(i);

            } else {
                sendNotification(mesa, "chat");
            }
        } else if (type.equalsIgnoreCase("deactivate")) {

            if (object.has("message")) {

//                if (!isAppIsInBackground(getApplicationContext())) {
//                    sendNotification(message, "deactivate");
//                } else {
//                if (MainActivity.IsOpened) {
                Intent i = new Intent("hi");
                i.putExtra("refresh", "1");
                getApplicationContext().sendBroadcast(i);

//                } else {
                sendNotification(message, "deactivate");


                Log.e("TYesssssss", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + "yes");


            }
        } else if (type.equalsIgnoreCase("activate")) {
            if (object.has("message")) {

//                if (!isAppIsInBackground(getApplicationContext()))
//                {
//                    sendNotification(message, "activate");
//                } else {
//                if (MainActivity.IsOpened) {
                Intent i = new Intent("hi");
                i.putExtra("refresh", "1");
                getApplicationContext().sendBroadcast(i);
//                } else {
                sendNotification(message, "activate");
            }


//                }
            Log.e("TYesssssss", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + "yes");


        } else if (type.equals("appointment_notify")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendNotification(message, "appointment_notify");
            } else {
                sendNotification(message, "appointment_notify");

            }
            Uri Emergency_sound_uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.ringing);
            r = RingtoneManager.getRingtone(getApplicationContext(), Emergency_sound_uri);
            r.play();


        } else if (type.equals("booking_status")) {
            if (!isAppIsInBackground(getApplicationContext())) {

                Intent i = new Intent("bookingStatus");
                i.putExtra("badge", badge);
                this.sendBroadcast(i);


                sendNotification(message, "booking_status");
            } else {
                sendNotification(message, "booking_status");

            }
        } else if (type.equals("accept_reject_appointment")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendNotification(message, "accept_reject_appointment");
            } else {
                sendNotification(message, "accept_reject_appointment");
            }
        } else if (type.equals("newsfeed")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendNotification(message, "newsfeed");
                //sharedPreferences.
            } else {
                sendNotification(message, "newsfeed");
            }
        } else if (type.equals("Credit Added")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendNotification(message, "");
            } else {
                sendNotification(message, "");
            }
        } else if (type.equals("SendDocument")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendNotification(message, "");
            } else {
                sendNotification(message, "");
            }
        } else if (type.equals("bid_accept")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendBroadcast(new Intent("bid_accept"));
                sendNotification(message, "bid_accept");
            } else {
                sendBroadcast(new Intent("bid_accept"));
                sendNotification(message, "bid_accept");
            }
        } else if (type.equals("submitted_offer")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendBroadcast(new Intent("submitted_offer"));
                sendNotification(message, "submitted_offer");
            } else {
                sendBroadcast(new Intent("submitted_offer"));
                sendNotification(message, "submitted_offer");
            }
        } else if (type.equals("new_legeal_concern_added")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendBroadcast(new Intent("new_legeal_concern_added"));
                sendNotification(message, "new_legeal_concern_added");
            } else {
                sendBroadcast(new Intent("new_legeal_concern_added"));
                sendNotification(message, "new_legeal_concern_added");
            }
        } else if (type.equals("submit_doc_for_review")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendBroadcast(new Intent("submit_doc_for_review"));
                sendNotification(message, "submit_doc_for_review");
            } else {
                sendBroadcast(new Intent("submit_doc_for_review"));
                sendNotification(message, "submit_doc_for_review");
            }
        }else if (type.equals("lawyer_online_offline")) {
            if (!isAppIsInBackground(getApplicationContext())) {
                sendBroadcast(new Intent("lawyer_online_offline"));
            } else {
                sendBroadcast(new Intent("lawyer_online_offline"));
            }
        } else if (GcmIntentService.isFrom.equals("receive")) {
            return;
        } else {
            //            Intent intent = new Intent(getApplicationContext(), SenderCallActivityAnother.class);
            //            intent.putExtra("from", "receiving");
            //            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //            startActivity(intent);
        }
        // sendNotification(remoteMessage.getNotification().getBody());
    }

    void sendNotification(String message, String to_intent) {
        //////////////////////////////////////////////////
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.MAGENTA);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(false);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            // Create a notification and set the notification channel.
            Intent i = null;
            /*if (to_intent.equals("chat")) {
                i = new Intent(getApplicationContext(), OpenChatActivity.class);
                i.putExtra("id", sender_id);
                i.putExtra("groupChat", groupChat);
                i.putExtra("name", fullname);
            } else */
            if (to_intent.equals("chat")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("open", "inboxFragment");
            } else if (to_intent.equalsIgnoreCase("booking_status")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("open", "fragment");
//                startActivity(i);
            } else if (to_intent.equalsIgnoreCase("deactivate")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("refresh", "d");
//                startActivity(i);
            } else if (to_intent.equalsIgnoreCase("activate")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("refresh", "a");
//                startActivity(i);
            } else if (to_intent.equalsIgnoreCase("accept_reject_appointment")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("open", "fragment");
            } else if (to_intent.equals("bid_accept")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("bid", "bid_accept");
            } else if (to_intent.equals("submitted_offer")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("submitted_offer", "submitted_offer");
                i.putExtra("problem_id", problem_id);
            } else if (to_intent.equals("new_legeal_concern_added")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("legal_concern", "new_legeal_concern_added");
            } else if (to_intent.equals("submit_doc_for_review")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("legal_concern", "new_legeal_concern_added");
            } else {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("f", "");
            }

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.
                    getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notificationnn = new Notification.Builder(getApplicationContext()).setContentTitle("Legalex").setContentText(message).setSmallIcon(R.mipmap.app_logo)
                    // .setBadgeIconType(R.drawable.notification_icn)
                    .setChannelId(CHANNEL_ID).setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL).setColor(Color.parseColor("#000000")).setPriority(Notification.PRIORITY_HIGH).setAutoCancel(true).build();

            if (mNotificationManager != null) {
                Log.e("aaa", "-=-=-= no createing notification -=-==");
                mNotificationManager.createNotificationChannel(mChannel);
            } else {
                Log.e("aaa", "-=-=-= created notification -=-==");
            }
            mNotificationManager.notify(NotificationID, notificationnn);
        } else {
            Intent i;
            if (to_intent.equals("chat")) {
                i = new Intent(getApplicationContext(), OpenChatActivity.class);
                i.putExtra("id", sender_id);
                i.putExtra("groupChat", groupChat);
                i.putExtra("name", fullname);
            } else if (to_intent.equalsIgnoreCase("booking_status")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("open", "fragment");
//                startActivity(i);
            } else if (to_intent.equalsIgnoreCase("deactivate")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("refresh", "d");
//                startActivity(i);
            } else if (to_intent.equalsIgnoreCase("activate")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("refresh", "a");
//                startActivity(i);
            } else if (to_intent.equalsIgnoreCase("accept_reject_appointment")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("open", "fragment");
            } else if (to_intent.equals("bid_accept")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("bid", "bid_accept");
            } else if (to_intent.equals("submitted_offer")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("submitted_offer", "submitted_offer");
            } else if (to_intent.equals("new_legeal_concern_added")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("legal_concern", "new_legeal_concern_added");
            } else if (to_intent.equals("submit_doc_for_review")) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("legal_concern", "new_legeal_concern_added");
            } else {
                i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("f", "");
            }
            //        i.putExtra("data", "hell");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.
                    getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.app_logo)
                    //.setBadgeIconType(R.drawable.notification_icn)
                    .setContentTitle("Legalex")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL).setColor(Color.parseColor("#000000")).setPriority(Notification.PRIORITY_HIGH);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NotificationID, notificationBuilder.build());
        }
        //////////////////////////////////////////////////
    }

}