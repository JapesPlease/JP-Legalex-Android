package lax.lega.rv.com.legalax.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_web_view.*
import lax.lega.rv.com.legalax.R

class WebViewActivity : AppCompatActivity() {

    var url = "";

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        url = intent.getStringExtra("url").toString()
        webv.webViewClient = WebViewClient()
        webv.settings.javaScriptEnabled = true
        webv.loadUrl(url)
    }
}
