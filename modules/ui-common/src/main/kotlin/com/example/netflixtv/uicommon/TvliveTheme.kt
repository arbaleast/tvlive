package com.example.netflixtv.uicommon

import androidx.compose.ui.graphics.Color

// Brand Colors - Modern TV App Palette
object TvliveColors {
    // Primary brand color - vibrant red gradient
    val Primary = Color(0xFFE50914)           // Netflix red
    val PrimaryDark = Color(0xFFB2070F)       // Darker red
    val PrimaryVariant = Color(0xFFFF1F1F)      // Lighter red for highlights

    // Gradient presets
    val BrandGradient = listOf(
        Color(0xFFFF9900),  // Orange accent
        Color(0xFFFF0000)    // Red
    )

    // Background - deep dark theme
    val BackgroundPrimary = Color(0xFF0D0D0D)       // Near black
    val BackgroundSecondary = Color(0xFF1A1A1A)      // Dark gray
    val BackgroundElevated = Color(0xFF242424)        // Lifted surfaces

    // Text colors
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFB3B3B3)
    val TextTertiary = Color(0xFF808080)

    // Accent colors
    val AccentLive = Color(0xFFFF4444)
    val AccentNew = Color(0xFF46D369)          // Green for "NEW"
    val AccentPopular = Color(0xFFFFD700)     // Gold for "POPULAR"

    // Focus/glow colors
    val FocusGlow = Primary.copy(alpha = 0.6f)
    val FocusBorder = Primary.copy(alpha = 0.8f)

    // Surface overlay
    val CardOverlay = Color.Black.copy(alpha = 0.75f)
    val GradientScrim = listOf(
        Color.Transparent,
        Color.Black.copy(alpha = 0.8f)
    )
}

// Typography presets
object TvliveTypography {
    // Title sizes
    const val HeroTitleSize = 52
    const val SectionTitleSize = 24
    const val CardTitleSize = 14

    // Body sizes
    const val BodyLargeSize = 18
    const val BodyMediumSize = 16
    const val BodySmallSize = 14

    // Label sizes
    const val LabelSize = 12
    const val BadgeSize = 10

    // Letter spacing ( Compose uses em)
    const val TitleLetterSpacing = -0.5f
    const val BodyLetterSpacing = 0f
}