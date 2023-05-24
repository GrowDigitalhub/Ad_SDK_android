package systems.hammer.adsdk.model

internal data class GameModel(
    val advertising: Boolean,
    val id: Int,
    val name: String,
    val slug: String,
    val uuid: String,
    val version: Double
) {
    companion object {
        val DEFAULT = GameModel(false, 0, "", "", "", 0.0)
    }
}