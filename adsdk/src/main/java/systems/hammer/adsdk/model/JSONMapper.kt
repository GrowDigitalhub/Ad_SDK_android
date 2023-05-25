package systems.hammer.adsdk.model

import org.json.JSONArray
import org.json.JSONObject

object JSONMapper {

    fun mapGames(jsonArray : JSONArray) : ArrayList<GameModel> {
        val result = arrayListOf<GameModel>()
        for (i in 0 until jsonArray.length()){
            val json = jsonArray.getJSONObject(i)
            result.add(
                GameModel(
                    json.getBoolean("advertising"),
                    json.getInt("id"),
                    json.getString("name"),
                    json.getString("slug"),
                    json.getString("uuid"),
                    json.getDouble("version")
                )
            )
        }
        return result
    }

    fun mapAd(json : JSONObject) : AdModel{
        return AdModel(
            json.getInt("id"),
            json.getString("content"),
            json.getString("description"),
            json.getString("link_game_uuid"),
            json.getString("link_type"),
            json.getString("link_url"),
            json.getString("name"),
            json.getString("title"),
            json.getInt("duration"),
        )
    }

}