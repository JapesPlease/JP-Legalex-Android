package lax.lega.rv.com.legalax.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
class SplashActivty : Activity() {


    internal var r: Runnable = Runnable {
        try {
            Thread.sleep(4000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (!sharedPreference.getString("access_token").equals("")) {

            val intent1 = Intent(this@SplashActivty, MainActivity::class.java)
            intent1.putExtras(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent1)
            finish()
        } else {
            val intent = Intent(this@SplashActivty, LoginActivity::class.java)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
            finish()
        }

    }

    lateinit var sharedPreference: MySharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Fabric.with(applicationContext, Crashlytics())
        setContentView(R.layout.splash_activity)
        sharedPreference = MySharedPreference(this@SplashActivty)
        getFBHashKey()
        val th = Thread(r)
        th.start()
    }

    private fun getFBHashKey() {
        try {
            val info = packageManager.getPackageInfo("lax.lega.rv.com.legalax", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key: ", something)

            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }

    }


}
