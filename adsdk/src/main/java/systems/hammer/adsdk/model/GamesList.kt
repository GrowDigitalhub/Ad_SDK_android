package systems.hammer.adsdk.model

enum class GamesList(val gameName : String,val packageName: String) {
    CROCODILE("CROCODILE","crocodile"),
    SPY("spy","hs_spy_android"),
    NEVER("I NEVER","hs"), // TODO change
    ALIAS("ALIAS","adsdk"),
    TOD("Truth or dare","hs_pid"),
}