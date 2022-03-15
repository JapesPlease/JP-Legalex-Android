package lax.lega.rv.com.legalax.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        //        storeRegIdInPref(refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Log.e(TAG, "sendRegistrationToServer: " + token);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        //        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        //        registrationComplete.putExtra("token", refreshedToken);
        //        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}