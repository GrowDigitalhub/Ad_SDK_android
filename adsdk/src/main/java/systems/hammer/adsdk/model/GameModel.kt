package systems.hammer.adsdk.model

data class GameModel(
    val advertising: Boolean,
    val id: Int,
    val name: String,
    val slug: String,
    var uuid: String,
    val version: Double
) {
    companion object {
        val DEFAULT = GameModel(false, 0, "", "", "", 0.0)
    }
}