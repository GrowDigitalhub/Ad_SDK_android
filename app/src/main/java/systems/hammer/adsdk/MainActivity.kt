package systems.hammer.adsdk

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
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
        AdSDK.initialize(this)
//        AdSettings.manualDisableAd = true
//        AdSettings.showSkipButton = false
        findViewById<Button>(R.id.btnClick2).setOnClickListener {
            DialogAd(this).show(supportFragmentManager,"")
        }
        findViewById<BannerAdView>(R.id.baAd).initBanner()
    }


}
