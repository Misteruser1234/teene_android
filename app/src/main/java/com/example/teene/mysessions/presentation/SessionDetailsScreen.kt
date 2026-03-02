package com.example.teene.mysessions.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun SessionDetailsScreen(
    navigator: DestinationsNavigator,
    args: SessionSummaryNavArg
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = args.sportName ?: "Session",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(8.dp))
        args.trainerName?.let {
            Text(text = "with $it", style = MaterialTheme.typography.bodyMedium)
        }
        args.address?.let {
            Spacer(Modifier.height(4.dp))
            Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        args.priceText?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, style = MaterialTheme.typography.titleMedium)
        }
        args.startTimeIso?.let {
            Spacer(Modifier.height(12.dp))
            Text(text = "Starts at: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}