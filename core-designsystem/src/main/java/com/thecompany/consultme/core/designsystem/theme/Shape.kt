// Copyright 2026 MyCompany
package com.thecompany.consultme.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Shape tokens consumed by ConsultMeTheme. Values mirror the Material 3
// defaults so behavior is unchanged out of the box; this file exists so
// adopters customizing component corners have a single place to edit
// (and so MaterialTheme.shapes is wired explicitly rather than falling
// back silently). See Google's `jetpack-compose/theming/styles` Claude
// Code skill when extending these tokens.
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
