package systems.hammer.adsdk.data

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import systems.hammer.adsdk.model.AdAction
import systems.hammer.adsdk.model.AdModel
import systems.hammer.adsdk.model.GameModel

internal interface AdAPI {

    @GET("api/advertisement/")
    suspend fun getAdvertisement(@Query("type") type : String, @Query("games") games : String) : AdModel

    @POST("api/advertisement/{id}/action/")
    suspend fun actionWithAdvertisement(
        @Path("id") id : String,
        @Query("game") gameUUID : String,
        @Query("action") action : AdAction,
        @Query("platform") platform : String = "android"
    )

    @GET("api/games/")
    suspend fun getGames() : ArrayList<GameModel>

}