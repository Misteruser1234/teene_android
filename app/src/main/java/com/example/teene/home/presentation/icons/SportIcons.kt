package com.example.teene.home.presentation.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.teene.R

/**
 * Resolves a sport name to a drawable painter following the naming convention:
 *   sport_icon_<key>
 * where <key> is lowercase with spaces/hyphens replaced by underscores. A synonym
 * map is applied first to align with the iOS SF Symbols mapping you provided.
 *
 * Examples:
 *   - "football" -> sport_icon_soccer
 *   - "taekwondo" -> sport_icon_martial
 *   - unknown -> sport_icon_default
 */
@Composable
fun sportIconPainter(name: String): Painter {
    val context = LocalContext.current
    val resources = context.resources
    val pkg = context.packageName

    fun normalize(input: String): String = input.trim()
        .lowercase()
        .replace("-", "_")
        .replace(Regex("\\s+"), "_")

    val normalized = normalize(name)

    val synonyms = mapOf(
        "taekwondo" to "martial",
        "powerlifting" to "strengthtraining_traditional",
        "cycling" to "outdoor_cycle",
        "football" to "soccer",
        "football" to "soccer",
        "rowing" to "rower",
        "skiing" to "skiing_downhill",
        "swimming" to "pool_swim",
        "skateboarding" to "skating",
        "tennis" to "tennis"
    )

    val mapped = synonyms[normalized]

    val candidates = listOfNotNull(mapped, normalized).distinct()

    for (key in candidates) {
        val drawableName = "sport_icon_$key"
        val id = resources.getIdentifier(drawableName, "drawable", pkg)
        if (id != 0) {
            return painterResource(id)
        }
    }

    return painterResource(R.drawable.sport_icon_default)
}

@Deprecated(
    message = "Use sportIconPainter(name) which resolves drawable painters by name.",
    replaceWith = ReplaceWith("sportIconPainter(name)")
)
@Composable
fun sportIconFor(name: String): Painter = sportIconPainter(name)