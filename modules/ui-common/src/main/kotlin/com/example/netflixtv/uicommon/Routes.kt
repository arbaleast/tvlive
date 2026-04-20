package com.example.netflixtv.uicommon

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{contentId}"
    const val PLAYER = "player/{contentId}"
    const val SEARCH = "search?query={query}"
    const val BROWSE = "browse"

    fun detailRoute(contentId: String) = "detail/$contentId"
    fun playerRoute(contentId: String) = "player/$contentId"
    fun searchRoute(query: String = "") = if (query.isNotEmpty()) "search?query=$query" else "search"
    fun browseRoute() = BROWSE
}
