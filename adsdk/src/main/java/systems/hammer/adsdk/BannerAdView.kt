package systems.hammer.adsdk

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.isVisible
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import systems.hammer.adsdk.data.NetworkAPI
import systems.hammer.adsdk.data.NetworkAgent
import systems.hammer.adsdk.model.AdFetchResult
import systems.hammer.adsdk.model.AdModel
import systems.hammer.adsdk.model.AdType
import java.net.URL


class BannerAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var ivBanner: ImageView? = null
    private var btnClose: ImageButton? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_banner_ad, this, true)

        initCloseButton()
//        initBanner()
    }

    private fun initCloseButton() {
        btnClose = findViewById(R.id.ibClose)
        btnClose?.isVisible = AdSettings.showSkipButton
        btnClose?.setOnClickListener {
            onSkip()
        }
    }

    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    private var currentAd : AdModel? =  null

    private lateinit var networkAgent: NetworkAPI

    fun setNetworkSource(networkAPI: NetworkAPI){
        networkAgent = networkAPI
    }

    fun initBanner() {
        if (!AdSettings.showAd) {
            visibility = View.GONE
            return
        }
        if (isInEditMode)
            return

        setNetworkSource(NetworkAgent.getInstance(context))
        ivBanner = findViewById(R.id.ivBanner)
        GlobalScope.launch(Dispatchers.IO) {
            val fetchResult = networkAgent.getAd(AdType.banner)
            when (fetchResult){
                is AdFetchResult.Success -> {
                    currentAd = fetchResult.ad
                    networkAgent.viewAd(currentAd!!.id.toString())
                    withContext(Dispatchers.Main) {
                        ivBanner?.let { setImageByUrl(it, currentAd!!.content) }
                        btnClose?.isVisible = true
                        ivBanner?.setOnClickListener {
                            clickAd()
                            followLink(currentAd!!.link_url)
                        }
                    }
                }
                is AdFetchResult.Error -> {
                    withContext(Dispatchers.Main) {visibility = View.GONE }
                }
            }
        }
    }

    private var onSkip = {}

    fun setOnSkip(onSkip : () -> Unit){
        this.onSkip = onSkip
    }
  
    private fun followLink(url: String){
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }

    private fun clickAd(){
        coroutineScope.launch{
            networkAgent.clickAd(currentAd!!.id.toString())
        }
    }

    private fun setImageByUrl(imageView : ImageView,url: String) {
        Picasso.get().load(url).placeholder(R.drawable.adsdk_placeholder).into(imageView)
    }
}