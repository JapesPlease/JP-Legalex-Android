package lax.lega.rv.com.legalax.common;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class DownloadBroadcastReciever extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = manager.query(query);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0) {

                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    Long download_id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

                    // status contain Download Status
                    // download_id contain current download reference id

                    if (status == DownloadManager.STATUS_SUCCESSFUL)
                    {
                        String downloadFilePath = (cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).replace("file://", "");
                        String downloadTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                        //file contains downloaded file name
                        Log.e("DownloadFilePath", ">>>>>>>>>>>>>>>>>>" + downloadFilePath);
                        Log.e("DownloadTitle", ">>>>>>>>>>>>>>>>>>" + downloadTitle);
//                        openFile(downloadFilePath, context);
                        Toast.makeText(context, "Download Successfully tap on notification to open", Toast.LENGTH_SHORT).show();

                        // do your stuff here on download success

                    }
                }
            }
            cursor.close();
        }
        if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED))
        {


            Toast.makeText(context, "Notification clicked ", Toast.LENGTH_SHORT).show();

        }
    }

    protected void openFile(String fileName, Context context)
    {
        Uri uri = Uri.fromFile(context.getFileStreamPath(fileName));

        try {
            Intent intentUrl = new Intent(Intent.ACTION_VIEW);
            intentUrl.setDataAndType(uri, "application/pdf");
            intentUrl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentUrl);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
        }
    }
}

