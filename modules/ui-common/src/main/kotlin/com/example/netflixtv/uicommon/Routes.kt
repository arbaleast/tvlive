package com.example.netflixtv.uicommon

object Routes {
    const val HOME = "home"
    const val PLAYER = "player/{contentId}"

    fun playerRoute(contentId: String) = "player/$contentId"
}
