package systems.hammer.adsdk.model

sealed class AdFetchResult {

    class Success(val ad : AdModel) : AdFetchResult()

    class Error(val e : Exception) : AdFetchResult()

}