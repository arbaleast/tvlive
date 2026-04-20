package com.example.netflixtv.data

/**
 * Application-wide constants.
 */
object AppConstants {

    // JSON
    const val CONTENT_DATA_FILE = "content_data.json"

    // Cache
    const val CACHE_TTL_MS = 30 * 60 * 1000L // 30 minutes

    // Player
    const val SEEK_BACK_MS = 10_000L
    const val SEEK_FORWARD_MS = 10_000L

    // UI — Cards
    const val CARD_WIDTH_DP = 150
    const val CARD_HEIGHT_DP = 280
    const val CARD_CORNER_RP = 8
    const val CARD_FOCUS_SCALE = 1.08f
    const val CARD_FOCUS_ELEVATION_DP = 12

    // UI — Hero
    const val HERO_HEIGHT_DP = 400
    const val KEN_BURNS_SCALE_END = 1.12f
    const val KEN_BURNS_DURATION_MS = 12_000L

    // UI — Colors
    const val FOCUS_BORDER_COLOR_HEX = "#FF0000" // Color.Red

    // UI — Typography
    const val HERO_TITLE_FONT_SIZE_SP = 48
    const val SECTION_TITLE_FONT_SIZE_SP = 20

    // UI — Spacing
    const val SECTION_HORIZONTAL_PADDING_DP = 16
    const val CARD_SPACING_DP = 12
}
