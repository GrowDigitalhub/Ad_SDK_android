package systems.hammer.adsdk

import android.content.Context
import systems.hammer.adsdk.data.NetworkAgent

object AdSDK {

    fun initialize(context : Context){
        NetworkAgent.getInstance(context).preload()
    }

    fun initialize(context : Context, uuid: String){
        NetworkAgent.getInstance(context).setGameUUID(uuid)
        NetworkAgent.getInstance(context).preload()
    }
}