package systems.hammer.adsdk


object AdSettings {

    internal var showAd = true
    get() {
       return if (!manualDisableAd)
           field
        else false
    }

    var manualDisableAd = false
    var showSkipButton = true

    var base_URL = "https:/games.hammer.systems/"
    get() {
        if (field.isBlank())
            throw Exception("Base URL is blank. Change it in AdSettings.")
        return field
    }

    var connectTimeOut: Long = 30L
    var readTimeout: Long = 30L
}