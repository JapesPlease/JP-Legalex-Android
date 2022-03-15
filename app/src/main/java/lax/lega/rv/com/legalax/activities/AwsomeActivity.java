package lax.lega.rv.com.legalax.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import lax.lega.rv.com.legalax.R;

public class AwsomeActivity extends Activity
{
    Button btn_submit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.awsome_screen_activity);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AwsomeActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
