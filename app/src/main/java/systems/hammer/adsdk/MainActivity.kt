package systems.hammer.adsdk

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        AdSDK.initialize(this,"40760e05-cd42-49e7-a5c8-b0f1e851ad5f","en")
        AdSDK.initialize(this,"69158e64-d083-11ed-afa1-0242ac120002","en")
//        AdSettings.manualDisableAd = true
//        AdSettings.showSkipButton = false
        findViewById<Button>(R.id.btnClick2).setOnClickListener {
            DialogAd(this).apply{
                setOnCloseListener {
                    Log.d(this@MainActivity.javaClass.simpleName,"On cloose dialog ad")
                }
            }.show(supportFragmentManager,"")
        }
            findViewById<BannerAdView>(R.id.baAd).initBanner()
    }


}
