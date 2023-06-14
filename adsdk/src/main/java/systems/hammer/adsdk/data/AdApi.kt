package systems.hammer.adsdk.data

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import systems.hammer.adsdk.AdSettings
import systems.hammer.adsdk.model.*

  object AdApi {

    private val adRequest = "api/advertisement/"
    private val gameRequest = "api/games/"

    fun getAdvertisement(type: AdType, games: String, language : String?): AdFetchResult {
        var request = AdSettings.base_URL + adRequest + "?type=$type&games=$games"
        if (language != null)
            request += "&language=$language"
        val result = RequestHandler.requestGET(request)
        Log.d(this.javaClass.simpleName,"Ad request = $request")
        Log.d(this.javaClass.simpleName,"Ad result = $result")
        return if (result == null) {
            AdFetchResult.Error(Exception("AdClient. Can't fetch ad"))
        } else {
            AdFetchResult.Success(JSONMapper.mapAd(JSONObject(result)))
        }
    }

    fun getGames(): ArrayList<GameModel> {
        val request = AdSettings.base_URL + gameRequest
        val result = RequestHandler.requestGET(request)
        Log.d(this.javaClass.simpleName,"Game result = $result")
        return if (result == null) {
            arrayListOf()
        } else {
            JSONMapper.mapGames(JSONArray(result))
        }
    }


    fun clickAd(id: String, game: String, platform: String = "android") {
        val request = AdSettings.base_URL + adRequest +
                "$id/action/?game=$game&action=${AdAction.click}&platform=$platform"
        RequestHandler.requestPOST(request, null)
    }

    fun viewAd(id: String, game: String, platform: String = "android") {
        val request = AdSettings.base_URL + adRequest +
                "$id/action/?game=$game&action=${AdAction.view}&platform=$platform"
        RequestHandler.requestPOST(request, null)
    }
}