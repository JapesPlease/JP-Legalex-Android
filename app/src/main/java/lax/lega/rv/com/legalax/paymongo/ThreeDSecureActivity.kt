package lax.lega.rv.com.legalax.paymongo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_three_d_secure.*
import lax.lega.rv.com.legalax.R

class ThreeDSecureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three_d_secure)
        var url = intent.getStringExtra("url")
        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadUrl(url.toString())

        textViewCancel.setOnClickListener {
            finish()
        }
    }




}