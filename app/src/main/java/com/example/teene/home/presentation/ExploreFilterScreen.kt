package com.example.teene.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import com.example.teene.authentication.composables.ButtonWithIcon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.home.presentation.viewModels.ExploreViewModel
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.composables.ChipOption
import com.example.teene.ui.composables.MultiSelectChips
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Explore filters screen with top-left X close button and two sections:
 * - Intensity: multi-select chips (Low, Moderate, High)
 * - Features: loaded from API and shown as chips
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun ExploreFilterScreen(
    navigator: DestinationsNavigator
) {
    // Share ExploreViewModel instance with ExploreScreen by scoping to Activity owner
    val activity =
        androidx.compose.ui.platform.LocalContext.current as androidx.activity.ComponentActivity
    val vm = koinViewModel<ExploreViewModel>(viewModelStoreOwner = activity)

    // Observe data from VM
    val features = vm.features.collectAsStateWithLifecycle().value
    val preselectedFeatureIds = vm.selectedFeatureIds.collectAsStateWithLifecycle().value
    val preselectedIntensities = vm.selectedIntensities.collectAsStateWithLifecycle().value

    // Local UI state (independent until Apply)
    var selectedIntensities by rememberSaveable { mutableStateOf(preselectedIntensities.toSet()) }
    var selectedFeatureIds by rememberSaveable { mutableStateOf(preselectedFeatureIds.toSet()) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header with Close button on the top-left, matching BookScreen style
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = { navigator.navigateUp() },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFCFCFCF))
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Intensity section
            Text(text = "Intensity", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            MultiSelectChips(
                labels = listOf("Low", "Moderate", "High"),
                selectedLabels = selectedIntensities,
                onSelectionChange = { selectedIntensities = it },
                maxItemsInEachRow = 3
            )

            Spacer(Modifier.height(16.dp))

            // Features section
            Text(text = "Features", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (features.isEmpty()) {
                Text(text = "No features available", color = Color.DarkGray)
            } else {
                val items = features.map { ChipOption(key = it.id, label = it.name) }
                MultiSelectChips(
                    items = items,
                    selectedKeys = selectedFeatureIds,
                    onSelectionChange = { new -> selectedFeatureIds = new },
                    maxItemsInEachRow = 3
                )
            }
        }
        com.example.teene.ui.composables.ComonFooter(
            leftContent = {
                Text(
                    text = "Reset",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        letterSpacing = (-0.02).sp,
                        textDecoration = Underline
                    ),
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(vertical = 8.dp)
                        .clickable {
                            selectedIntensities = emptySet()
                            selectedFeatureIds = emptySet()
                        }
                )
            },
            buttonText = "Apply selection",
            buttonEnabled = true,
            buttonIconId = null,
            onButtonClick = {
                vm.updateIntensities(selectedIntensities.toList())
                vm.updateFeatureIds(selectedFeatureIds.toList())
                navigator.navigateUp()
            }
        )
    }
}
