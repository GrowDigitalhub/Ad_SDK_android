package systems.hammer.adsdk

import android.content.Context
import systems.hammer.adsdk.data.NetworkAgent

object AdSDK {

    fun initialize(context : Context,language : String?){
        NetworkAgent.getInstance(context).setLanguage(language)
        NetworkAgent.getInstance(context).preload()
    }

    fun initialize(context : Context, uuid: String,language : String?){
        NetworkAgent.getInstance(context).setLanguage(language)
        NetworkAgent.getInstance(context).setGameUUID(uuid)
        NetworkAgent.getInstance(context).preload()
    }
}