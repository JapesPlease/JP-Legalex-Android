package lax.lega.rv.com.legalax.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.common.Utils;
import lax.lega.rv.com.legalax.custom_views.TouchImageView;

public class Image_user extends Activity {

    ImageView imgUser;
    TextView txtClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_image);
        imgUser = findViewById(R.id.imgUser);
        txtClose = findViewById(R.id.txtClose);

        Intent it = getIntent();

        String image = it.getStringExtra("image_user");
        if (image != null) {
            Picasso.with(Image_user.this).load(image).into(imgUser);

        }

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public class GetImageBitmap extends AsyncTask<String, String, Bitmap> {

        String url;

        public GetImageBitmap(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap bitmap =  Utils.instance.getBitmapFromURL(url);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);

            if (s != null) {

            } else {

            }

        }
    }

}
