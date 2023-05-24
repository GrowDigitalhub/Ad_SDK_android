package systems.hammer.adsdk.data

import android.content.Context
import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import systems.hammer.adsdk.AdSettings
import systems.hammer.adsdk.model.*
import java.util.concurrent.TimeUnit

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

    private val adAPI: AdAPI
    private val appPackageName: String
    private var currentGame: GameModel? = null


    init {
        adAPI = provideRetrofit(provideOKHTTP()).create(AdAPI::class.java)
        appPackageName = context.packageName ?: throw Exception("Null package name")
        findCurrentGame()
    }

    private var findGameJob: Job? = null

    private fun findCurrentGame() {
        findGameJob = GlobalScope.launch {
            try {
                val allGames = adAPI.getGames()
                currentGame = allGames.find { game ->
                    appPackageName.contains(game.slug)
                }
//                currentGame = allGames[0]
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


    private fun provideOKHTTP(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .connectTimeout(AdSettings.connectTimeOut, TimeUnit.SECONDS)
            .readTimeout(AdSettings.readTimeout, TimeUnit.SECONDS)
//            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(AdSettings.base_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .build()
    }

    override suspend fun getAd(type: AdType): AdFetchResult {
        findGameJob?.join()
        return try {
            AdFetchResult.Success(adAPI.getAdvertisement(type.toString(), currentGame!!.uuid))
        }catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Can't get ad.")
//            e.printStackTrace()
            AdFetchResult.Error(e)
        }
    }

    override suspend fun clickAd(id: String) {
        findGameJob?.join()
        try {
            adAPI.actionWithAdvertisement(id, currentGame!!.uuid, AdAction.click)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Can't send click event.}")
//            e.printStackTrace()
        }
    }

    override suspend fun viewAd(id: String) {
        findGameJob?.join()
        try {
            adAPI.actionWithAdvertisement(id, currentGame!!.uuid, AdAction.view)
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Can't send view event.")
//            e.printStackTrace()
        }
    }
}