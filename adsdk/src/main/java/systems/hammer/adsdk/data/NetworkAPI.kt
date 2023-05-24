package systems.hammer.adsdk.data

import systems.hammer.adsdk.model.AdFetchResult
import systems.hammer.adsdk.model.AdModel
import systems.hammer.adsdk.model.AdType

interface NetworkAPI {

    suspend fun getAd(type: AdType): AdFetchResult

    suspend fun clickAd(id: String)

    suspend fun viewAd(id: String)
}