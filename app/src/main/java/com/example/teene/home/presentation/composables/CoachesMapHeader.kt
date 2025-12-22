package com.example.teene.home.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.teene.R

/**
 * Header for the coaches map screen. Shows Back and Filter actions.
 * Both icons are displayed inside a circular border (#F0F0F0),
 * matching the style used in CommonHeader.
 * Created by 3100lari on 2025/11/05
 */
@Composable
fun CoachesMapHeader(
    onBackClick: (() -> Unit)? = null,
    onFilterClick: (() -> Unit)? = null,
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button (left)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = Color.White, shape = CircleShape)
                .border(width = 1.dp, color = Color(0xFFF0F0F0), shape = CircleShape)
                .clickable(enabled = onBackClick != null) { onBackClick?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = Color.Black
            )
        }

        // Filter button (right)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = Color.White, shape = CircleShape)
                .border(width = 1.dp, color = Color(0xFFF0F0F0), shape = CircleShape)
                .clickable(enabled = onFilterClick != null) { onFilterClick?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_search_filter),
                contentDescription = "Open filters",
                tint = Color.Black
            )
        }
    }
}