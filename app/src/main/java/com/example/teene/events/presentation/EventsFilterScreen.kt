package com.example.teene.events.presentation

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.home.presentation.composables.SearchBarWithFilters
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.composables.MultiSelectChips
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
    var locationQuery by rememberSaveable { mutableStateOf("") }
    var selectedIntensities by rememberSaveable { mutableStateOf(setOf<String>()) }
    var maxPriceText by rememberSaveable { mutableStateOf("") }
    var selectedDuration by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedExtras by rememberSaveable { mutableStateOf(setOf<String>()) }

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

        // Month
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

        // Location
        Text(text = "Location", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        SearchBarWithFilters(
            modifier = Modifier.fillMaxWidth(),
            onFilterClick = {},
            showFilterButton = false,
            showAskAiButton = false,
            cornerRadius = 4.dp,
            placeholder = "Search location",
            onQueryChange = { q -> locationQuery = q }
        )

        Spacer(Modifier.height(20.dp))

        // Intensity
        Text(text = "Intensity", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        MultiSelectChips(
            labels = listOf("Low", "Moderate", "High"),
            selectedLabels = selectedIntensities,
            onSelectionChange = { selectedIntensities = it },
            maxItemsInEachRow = 3
        )

        Spacer(Modifier.height(20.dp))

        // Maximum price (numeric input)
        Text(text = "Maximum price", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = maxPriceText,
            onValueChange = { new ->
                // accept only digits
                if (new.all { it.isDigit() }) {
                    maxPriceText = new
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Enter amount") },
            trailingIcon = { Text(text = "€") }
        )

        Spacer(Modifier.height(20.dp))

        // Duration (single-select chips)
        Text(text = "Duration", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        MultiSelectChips(
            labels = listOf("One-day", "Multi-day"),
            selectedLabels = selectedDuration,
            onSelectionChange = { selectedDuration = it },
            maxItemsInEachRow = 2
        )

        Spacer(Modifier.height(20.dp))

        // Extras
        Text(text = "Extras", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        MultiSelectChips(
            labels = listOf("With coach", "Without coach"),
            selectedLabels = selectedExtras,
            onSelectionChange = { selectedExtras = it },
            maxItemsInEachRow = 2
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reset",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.02).sp,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                ),
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        // Reset all fields
                        locationQuery = ""
                        selectedIntensities = emptySet()
                        maxPriceText = ""
                        selectedDuration = emptySet()
                        selectedExtras = emptySet()
                        selectedMonth = null
                        monthExpanded = false
                    }
            )

            FilledTonalButton(
                onClick = { navigator.navigateUp() },
                shape = CircleShape,
                modifier = Modifier.weight(2f)
            ) {
                Text(text = "Apply Selection", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
