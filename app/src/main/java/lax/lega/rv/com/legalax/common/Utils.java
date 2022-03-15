package lax.lega.rv.com.legalax.common;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String PAYPAL_CLIENT_ID = "AdynwM7FXS-Yz8W6I_-j13g_gUr9gJAu2uBIaaN3WMGRsd8youOyDlcsdK8aXQ-rOSUV9QUBacLCrfMk";
    public static final String PAYPAL_CLIENT_ID_LIVE = "Aeh5HrE87Y3C8seWT0OcqbEgOI9Xr9INSNXLxgL6C6-iXknOnV8WC5KX5m208E2L59bqby3x1sUob7Zl";

    /*Local server*/
//    public static String SERVER_URL = "http://192.168.1.67/JP-Legalex-IOS_Backend/public";

    /*Dev*/
   public static String SERVER_URL = "http://server4.rvtechnologies.in/JP-Legalex-IOS_Backend/public";

    /*Live Server*/
//        public static String SERVER_URL = "http://admin.legalex.ph";

    public static String BASE_URL = SERVER_URL + "/api/";
    public static String BASE_URL_VIDEO = SERVER_URL + "/newsfeed/";

    public static String PAYMONGO_BASEURL = "https://api.paymongo.com/v1/";
    //   public static String PAYMONGO_PUBLIC_KEY = "pk_test_KWkM6YxpH1Vpfp9CNNWjhPev" ;  // sandbox
    public static String PAYMONGO_PUBLIC_KEY = "pk_live_ALeuD9viq5afxvL1Y8TvuxzQ";  // live

    public static String PAYMONGO_SUCCESS_URL = "http://admin.legalex.ph/success-url?device_type=android";//"http://111.93.38.132/server1/lagalax/public/success-url" ;
    public static String PAYMONGO_FAIL_URL = "http://admin.legalex.ph/failure-url?device_type=android";//"http://111.93.38.132/server1/lagalax/public/failure-url" ;

    public static String logout = BASE_URL + "logout";
    public static final String IMAGESURL = SERVER_URL + "/profile/";
    public static String CHAT_IMAGES_URL = SERVER_URL + "/chatfile/";

    public static String CHECK_REGISTERED = "check-user-registered";
    public static String SIGNUP = "register";
    public static String LOGIN = "login";
    public static String LOGIN_HERE = "login-here";
    public static String FORGOT_PWD = "forgot";
    public static String RESET_PWD = "reset-password";
    public static String FB_LOGIN = "facebook-login";
    public static String FB_ISREGESTERD = "check-fb";
    public static String GET_NEWSFEED = "newsfeeds";
    public static String ADD_NEWSFEED = "add-edit-newsfeed";
    public static String DELETE_NEWSFEED = "delete-newsfeed";
    public static String ADD_COMMENT = "add-comments";
    public static String GET_COMMENT = "get-comments";
    public static String DELETE_COMMENT = "delete-comment";
    public static String GET_LAWYER_LIST = "lawyer-list";
    public static String CHAT_LIST_DATA = "chats";
    public static String SEND_MESSAGE = "send-message";
    public static String BOOK_LAWYER = "book-lawyer";
    public static String BOOK_USER = "book-user";
    public static String ALL_USER = "get-users";
    public static String GET_CHAT_HISTORY = "chat-users-list";
    public static String SEND_CALL_STATUS = "save-call-history";
    public static String GET_CALL_HISTORY = "get-call-history";
    public static String GET_VIDEO_CALL_HISTORY = "getVideoCallHistroy";
    public static String SEND_RECIEVE_BOOKING = "send-received-booking";
    public static String SEND_RECIEVE_BOOKING_USER = "send-received-booking-user";
    public static String ACCEPT_REJECT = "accept-reject-booking";
    public static String ACCEPT_REJECT_USER = "accept-reject-booking-user";
    public static String ACCEPT_BOOKING = "allbooking-lawyer-user";
    public static String ACCEPT_BOOKING_SENT = "allbooking-user-lawyer";
    public static String UPDATE_FCM_TOKEN = "update-fcm-token";
    public static String GET_USER_DATA = "get-user-data";
    public static String GET_ALL_TYPEDOCUMENT = "get-documents";
    public static String SHARE_DOCUMENT = "send-document";
    public static String RECEIVED_DOCUMENT = "get-send-documents";
    public static String CHANGE_PWD = "change-password";
    public static String UPDATE_PROFILE = "update-profile";
    public static String DELETE_APPOINTMENT = "delete-appointments";
    public static String WRITE_SOMETHING = "update-write-something";
    public static String ISONLINE_OFFLINE = "online-offline";
    public static String CREATE_SESSION_TOKEN = "createSession";
    public static String UPDATEBADGE = "update-badge";
    public static String UPDATEVIDEOSTATUS = "update/user/video/status";
    public static String SUBMIT_PROBLEM = "submit-problem";
    public static String SUBMIT_DOCUMENT_PROBLEM = "submit-document-problem";
    public static String GET_PROBLEM_CATEGORY = "get-problem-category";
    public static String GET_PROBLEM_LISTING = "get-problem-listing";
    public static String GET_DOCUMENT_PROBLEM_LISTING = "get-problem-document";
    public static String REQUEST_DOCUMENT = "change/document/to/problem";
    public static String GET_BID_LIST = "get/bid/list?type=";
    public static String CREATE_BID = "create/bid";
    public static String GET_BID_LIST_USER_SIDE = "get/bid/list/by/user?type=";
    public static String ACCEPT_BID = "accept/bid";
    public static String DELETE_PROBLEM = "delete/problem/";
    public static String GET_FEE = "get/fee";

    ObjectAnimator anim;
    public static Uri selected_image;
    public static Uri pimageUri = null;
    public static Bitmap croppedImage;
    public static boolean change_image = true;
    private Application activity;
    public ProgressDialog progressDialog;

    public Utils(Application activity) {
        this.activity = activity;
        if (instance == null) {
            instance = this;
        }
    }

    public static Utils instance;

    /*public static Utils getInstance() {
        if (instance == null) {
            synchronized (Utils.class) {
                if (instance == null) {
                    instance = new Utils();
                }
            }
        }
        return instance;
    }*/

    public static String getRealPathFromDocumentUri(Context context, Uri uri) {
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());

        String imgId = m.group();

        String[] column = {MediaStore.Images.Media.DATA};
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{imgId}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }


    static String getDestinationFilePath() {
        File image = null;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile("legalex", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image.getAbsolutePath();
    }

    public static File getFileFromDrive(Context mContext, Uri uri) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getDestinationFilePath());
            try (BufferedOutputStream out = new BufferedOutputStream(fos);
                 InputStream in = mContext.getContentResolver().openInputStream(uri)) {
                byte[] buffer = new byte[8192];
                int len = 0;

                while ((len = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, len);
                }

                out.flush();
            } finally {
                fos.getFD().sync();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(getDestinationFilePath());
        if (Integer.parseInt(String.valueOf(file.length() / 1024)) > 1024) {
            InputStream imageStream = null;
            try {
                imageStream = mContext.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }


    public static boolean CheckStringLength(int length, String dummystring) {
        boolean check = false;
        if (dummystring != null && dummystring.length() > 0) {
            try {
                if (dummystring.length() > length) {
                    check = true;

                } else {
                    check = false;
                }

            } catch (NullPointerException e) {

            }
        }
        return check;

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*//----------------------------------show loader-----------------
    public static void showLoader(Activity activity) {
        progressDialog = new ACProgressFlower.Builder(activity).text("Loading..").build();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    //-----------------------------hide loader------------
    public static void hideLoader() {
        progressDialog.dismiss();
    }*/

    public static int getScreenWidth(Context context) {
        return getPointSize(context).x;
    }

    public static int getScreenHeight(Context context) {
        return getPointSize(context).y;
    }

    private static Point getPointSize(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }


    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static File getFileFromImage(Activity activity, String filename, Bitmap bitmap) {
        //create a file to write bitmap data
        File f = new File(activity.getCacheDir(), filename);
        try {
            f.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            Log.e("aaa", "-=-=IOException at onClick_signup -=" + e);
        }

        return f;
//        : jp26@legalex.com
//        Pw: $uStagen26
    }

    public void showProgressBar(Activity context) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        try {

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please Wait......");
            progressDialog.setMessage("Loading......");
            progressDialog.setCancelable(false);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            } else if (progressDialog != null) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
       /* try {
            if (progressDialog != null) {
                do {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                } while (progressDialog.isShowing());
            }
        } catch (Exception e) {
            Log.e("vvv", "-=-=Exception at ProgressBar hide -=" + e);
        }*/

    }
}
