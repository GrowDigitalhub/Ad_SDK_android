package systems.hammer.adsdk

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import systems.hammer.adsdk.data.NetworkAPI
import systems.hammer.adsdk.data.NetworkAgent
import systems.hammer.adsdk.model.AdFetchResult
import systems.hammer.adsdk.model.AdModel
import systems.hammer.adsdk.model.AdType
import java.net.HttpURLConnection
import java.net.URL
import kotlin.properties.Delegates


class DialogAd(context: Context, var onSkip: () -> Unit = {}) : DialogFragment() {

    init {
        setNetworkSource(NetworkAgent.getInstance(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_fullscreen_ad, container, false)
    }

    private var btnSkip: AppCompatButton? = null
    private var tvTimer: TextView? = null
    private var btnClose: ImageButton? = null
    private var ivAd: ImageView? = null
    private lateinit var networkAgent: NetworkAPI
    private var currentAd: AdModel? = null
    private var flTimerClose : FrameLayout? = null

    fun setNetworkSource(networkAPI: NetworkAPI) {
        networkAgent = networkAPI
    }

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressure()

        ivAd = requireView().findViewById(R.id.ivAd)
        btnSkip = view.findViewById(R.id.btnSkip)
        btnClose = view.findViewById(R.id.ibClose)
        tvTimer = view.findViewById(R.id.tvTimer)
        flTimerClose = view.findViewById(R.id.flTimerClose)

        loadAd()
    }

    private fun showButtons(){
        flTimerClose?.isVisible = true
        btnSkip?.isVisible = AdSettings.showSkipButton
    }

    private fun setUI(){
        btnClose?.setOnClickListener {
            close()
        }
        btnSkip?.setOnClickListener {
            onSkip()
        }
        showButtons()
        startTimer()
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val windowParams: WindowManager.LayoutParams =
                dialog?.window?.getAttributes()!!
            windowParams.dimAmount = 0.0f

            dialog?.window?.setAttributes(windowParams)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (AdSettings.showAd)
            super.show(manager, tag)
    }

    private fun loadAd() {
        coroutineScope.launch(Dispatchers.IO) {
            when (val fetchResult = networkAgent.getAd(AdType.fullscreen_img)) {
                is AdFetchResult.Success -> {
                    currentAd = fetchResult.ad
                    currentAd?.duration?.let { setTimer(it) }
                    withContext(Dispatchers.Main) {
                        setUI()
                        setImageByUrl(ivAd!!, currentAd!!.content)
                        ivAd?.setOnClickListener {
                            clickAd()
                            requireContext().startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(currentAd!!.link_url)
                                )
                            )
                            close()
                        }
                    }
                    networkAgent.viewAd(currentAd!!.id.toString())
                }
                is AdFetchResult.Error -> {
                    dismiss()

                }
            }
        }
    }

    private fun clickAd() {
        coroutineScope.launch(Dispatchers.Default) {
            networkAgent.clickAd(currentAd!!.id.toString())
        }
    }

    private fun setImageByUrl(imageView: ImageView, url: String) {
        Picasso.get().load(url).placeholder(R.drawable.adsdk_placeholder).into(imageView)
    }

    private fun close() {
        dismiss()
        onClose()
    }

    private var onClose = {}

    private var timeSeconds by Delegates.notNull<Int>()
    private var timerJob: Job? = null

    private fun startTimer() {
        var s = timeSeconds
        tvTimer?.text = s.toString()
        btnClose?.isVisible = false
        tvTimer?.isVisible = true
        timerJob = coroutineScope.launch {
            while (s > 0) {
                s--
                delay(1000)
                withContext(Dispatchers.Main) {
                    tvTimer?.text = s.toString()
                }
            }
            withContext(Dispatchers.Main) {
                stopTimer()
            }
        }
    }

    private fun handleBackPressure() {
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                return@OnKeyListener true
            }
            false
        })
    }


    private fun stopTimer() {
        tvTimer?.isVisible = false
        btnClose?.isVisible = true
        flTimerClose?.background = ColorDrawable(Color.TRANSPARENT)
    }

    fun setTimer(seconds: Int) {
        timeSeconds = seconds
    }

    fun setOnCloseListener(onClose: () -> Unit) {
        this.onClose = onClose
    }

    fun setSkipText(text: String) {
        btnSkip?.setText(text)
    }

    override fun onDestroyView() {
        timerJob?.cancel()
        ivAd = null
        btnSkip = null
        tvTimer = null
        btnClose = null
        super.onDestroyView()
    }

}