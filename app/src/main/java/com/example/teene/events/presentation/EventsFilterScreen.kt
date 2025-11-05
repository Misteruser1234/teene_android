package com.example.teene.events.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextField
import com.example.teene.R
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsFilterScreen(
    navigator: DestinationsNavigator
) {
    // Local filter states (UI-only for now)
    var latitudeText by rememberSaveable { mutableStateOf("") }
    var longitudeText by rememberSaveable { mutableStateOf("") }
    var radius by rememberSaveable { mutableStateOf(25f) }
    var maxPrice by rememberSaveable { mutableStateOf(80f) }
    var multiDay by rememberSaveable { mutableStateOf(false) }
    var selectedIntensities by rememberSaveable { mutableStateOf(setOf<String>()) }

    // Month dropdown state
    val months = remember { listOf("January","February","March","April","May","June","July","August","September","October","November","December") }
    var monthExpanded by remember { mutableStateOf(false) }
    var selectedMonth by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
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

        Text(text = "Month", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = monthExpanded,
            onExpandedChange = { monthExpanded = !monthExpanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedMonth ?: "Select month",
                onValueChange = {},
                label = { Text("Month") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            DropdownMenu(
                expanded = monthExpanded,
                onDismissRequest = { monthExpanded = false }
            ) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(month) },
                        onClick = {
                            selectedMonth = month
                            monthExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(text = "Location", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        com.example.teene.home.presentation.composables.SearchBarWithFilters(
            modifier = Modifier.fillMaxWidth(),
            onFilterClick = {},
            showFilterButton = false,
            showAskAiButton = false,
            cornerRadius = 4.dp,
            placeholder = "Type your location here"
        )

        Spacer(Modifier.height(20.dp))

        Text(text = "Radius: ${'$'}{radius.toInt()} km", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = radius,
            onValueChange = { radius = it },
            valueRange = 1f..100f,
            steps = 98
        )

        Spacer(Modifier.height(12.dp))

        Text(text = "Intensity", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Low", "Moderate", "High").forEach { level ->
                val selected = selectedIntensities.contains(level)
                FilterChip(
                    selected = selected,
                    onClick = {
                        selectedIntensities = if (selected) selectedIntensities - level else selectedIntensities + level
                    },
                    label = { Text(level) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.Black,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF5F5F5),
                        labelColor = Color.Black
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(text = "Max price: €${'$'}{maxPrice.toInt()}", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = maxPrice,
            onValueChange = { maxPrice = it },
            valueRange = 0f..200f,
            steps = 199
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Multi-day", style = MaterialTheme.typography.titleMedium)
            Switch(checked = multiDay, onCheckedChange = { multiDay = it })
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = {
                            latitudeText = ""
                            longitudeText = ""
                            radius = 25f
                            maxPrice = 80f
                            multiDay = false
                            selectedIntensities = emptySet()
                            selectedMonth = null
                            monthExpanded = false
                        }) {
                Text(text = "Reset")
            }

            FilledTonalButton(
                onClick = { navigator.popBackStack() },
                shape = CircleShape
            ) {
                Text(text = "Apply Selection", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
