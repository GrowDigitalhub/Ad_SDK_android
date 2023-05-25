package systems.hammer.adsdk.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import systems.hammer.adsdk.AdSettings
import systems.hammer.adsdk.model.AdFetchResult
import systems.hammer.adsdk.model.AdType
import systems.hammer.adsdk.model.GameModel

internal class NetworkAgent private constructor(context: Context) : NetworkAPI {
    companion object {
        private var networkAgent: NetworkAgent? = null

        fun getInstance(context: Context): NetworkAgent {
            if (networkAgent == null) {
                networkAgent = NetworkAgent(context)
            }
            return networkAgent!!
        }

        fun destroyInstance() {
            networkAgent = null
        }
    }

    private val appPackageName: String
    private var currentGame: GameModel? = null
    private var bannerAd : AdFetchResult? = null
    private var fullScreenAd : AdFetchResult? = null


    init {
        appPackageName = context.packageName ?: throw Exception("Null package name")
        findCurrentGame()
    }

    private var findGameJob: Job? = null

    private fun findCurrentGame() {
        findGameJob = GlobalScope.launch(Dispatchers.IO) {
            try {
                val allGames = AdApi.getGames()      // adAPI.getGames()
                currentGame = allGames.find { game ->
                    appPackageName.contains(game.slug)
                }
                currentGame = allGames[0]
                if (currentGame == null) {
                    AdSettings.showAd = false
                    Log.e(this.javaClass.simpleName, "Can't find current game")
                } else
                    AdSettings.showAd = currentGame!!.advertising
            } catch (e : Exception){
                Log.e(this.javaClass.simpleName,"Can't find available games. \n ${e.message}")
            }
        }
    }

    private var preloadJob: Job? = null

    fun preload(){
        preloadJob = GlobalScope.launch(Dispatchers.IO) {
            bannerAd = loadAd(AdType.banner)
            fullScreenAd = loadAd(AdType.fullscreen_img)
        }
    }

    private suspend fun loadAd(type : AdType): AdFetchResult{
        findGameJob?.join()
        return try {
            AdApi.getAdvertisement(type, currentGame!!.uuid)
        }catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Can't get ad.\n ${e.message}")
//            e.printStackTrace()
            AdFetchResult.Error(e)
        }
    }

    override suspend fun getAd(type: AdType): AdFetchResult {
        preloadJob?.join()
        return when(type) {
            AdType.banner -> bannerAd ?: AdFetchResult.Error(Exception("Fetched ad is null"))
            AdType.fullscreen_img -> fullScreenAd ?: AdFetchResult.Error(Exception("Fetched ad is null"))
        }
    }

    override suspend fun clickAd(id: String) {
        findGameJob?.join()
        try {
            AdApi.clickAd(id, currentGame!!.uuid)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Can't send click event.\n ${e.message}}")
//            e.printStackTrace()
        }
    }

    override suspend fun viewAd(id: String) {
        findGameJob?.join()
        try {
            AdApi.viewAd(id, currentGame!!.uuid)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Can't send view event.\n ${e.message}}")
//            e.printStackTrace()
        }
    }
}