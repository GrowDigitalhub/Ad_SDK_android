package systems.hammer.adsdk.model

data class AdModel(
    val id : Int,
    val content: String,
    val description: String,
    val link_game_uuid: String,
    val link_type: String,
    val link_url: String,
    val name: String,
    val title: String,
    val duration : Int
)