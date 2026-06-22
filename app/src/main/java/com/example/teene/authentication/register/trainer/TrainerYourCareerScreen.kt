package com.example.teene.authentication.register.trainer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.authentication.AuthenticationNavGraph
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.ExploreScreenDestination
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private data class CareerItem(
    val company: String,
    val from: LocalDate,
    val to: LocalDate
)

private val careerDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

private fun LocalDate.toMillis(): Long = this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
private fun millisToLocalDate(millis: Long): LocalDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

/**
 * Trainer "Your career" screen.
 */
@Destination<AuthenticationNavGraph>(style = AuthorizationNavigationAnimations::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerYourCareerScreen(
    navigator: DestinationsNavigator? = null
) {
    val activity = androidx.compose.ui.platform.LocalContext.current as androidx.activity.ComponentActivity
    val registerVm = koinViewModel<RegisterTrainerViewModel>(viewModelStoreOwner = activity)

    // Local UI state for Add Career dialog and list
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var companyText by rememberSaveable { mutableStateOf("") }
    var fromDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var toDate by rememberSaveable { mutableStateOf(LocalDate.now()) }

    // Temporary in-memory list of saved career items
    val careerItems by registerVm.careerHistory.collectAsStateWithLifecycle()
    val creationResult by registerVm.creationResult.collectAsStateWithLifecycle()

    LaunchedEffect(creationResult) {
        if (creationResult?.isSuccess == true) {
            // Success: navigate to the main app (Explore) and clear auth stack
            navigator?.navigate(ExploreScreenDestination) {
                popUpTo(NavGraphs.authentication) { inclusive = true }
            }
        }
    }

    var achievementsText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = true)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            // Header with back button and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = { navigator?.navigateUp() },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFCFCFCF))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Your career",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp,
                        letterSpacing = (-0.02).sp,
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )
                )
            }

            Spacer(Modifier.width(0.dp))
            Spacer(Modifier.padding(top = 20.dp))
            Text(
                text = "To help the coach prepare more effectively for your session, it would be useful to know a few basic details about you",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF7A7A7A)
                )
            )

            Spacer(Modifier.padding(top = 24.dp))
            Text(
                text = "Achievements",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                    letterSpacing = (-0.02).sp,
                    color = Color.Black
                )
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = achievementsText,
                onValueChange = {
                    achievementsText = it
                    registerVm.achievements.value = it.split(",").map { s -> s.trim() }.filter { s -> s.isNotBlank() }
                },
                placeholder = { Text("e.g. National Champion, Awarded (comma separated)") },
                label = { Text("Achievements") },
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color(0xFF7A7A7A),
                    unfocusedBorderColor = Color(0xFFF0F0F0),
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

            Spacer(Modifier.padding(top = 24.dp))
            Text(
                text = "Career history",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                    letterSpacing = (-0.02).sp,
                    color = Color.Black
                )
            )
            Spacer(Modifier.height(8.dp))
            // Bordered button with plus icon on the left
            OutlinedButton(
                onClick = {
                    // Reset dialog fields to defaults and open dialog
                    companyText = ""
                    fromDate = LocalDate.now()
                    toDate = LocalDate.now()
                    showAddDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE8E7E5)) // Light Gray used elsewhere
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add career history",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal, // 400
                                lineHeight = 24.sp,
                                letterSpacing = (-0.02).sp,
                                color = Color.Black
                            )
                        )
                    }
                }
            }

            // Render temporary list of saved career items
            if (careerItems.isNotEmpty()) {
                Spacer(Modifier.padding(top = 16.dp))
                careerItems.forEachIndexed { index, item ->
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(BorderStroke(1.dp, Color(0xFFE8E7E5)), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.companyName,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 22.sp,
                                    letterSpacing = (-0.02).sp,
                                    color = Color.Black
                                )
                            )
                            Spacer(Modifier.size(4.dp))
                            Text(
                                text = "${item.from.format(careerDateFormatter)}  —  ${item.to.format(careerDateFormatter)}",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 20.sp,
                                    letterSpacing = (-0.02).sp,
                                    color = Color(0xFF7A7A7A)
                                )
                            )
                        }
                        androidx.compose.material3.IconButton(onClick = {
                            val newList = careerItems.toMutableList()
                            newList.removeAt(index)
                            registerVm.careerHistory.value = newList
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove career item",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Add Career History Dialog
        if (showAddDialog) {
            var showRangePicker by remember { mutableStateOf(false) }

            Dialog(onDismissRequest = { showAddDialog = false }) {
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Where did you work?",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 28.sp,
                                letterSpacing = (-0.02).sp,
                                color = Color.Black
                            )
                        )
                        Spacer(Modifier.size(12.dp))
                        OutlinedTextField(
                            value = companyText,
                            onValueChange = { companyText = it },
                            label = { Text("Company name") },
                            singleLine = true
                        )
                        Spacer(Modifier.size(12.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showRangePicker = true }
                            ) {
                                Text(
                                    text = "From",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF7A7A7A)
                                )
                                Spacer(Modifier.size(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFE8E7E5))
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 14.dp),
                                        text = fromDate.format(careerDateFormatter),
                                        style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = (-0.02).sp),
                                        color = Color.Black
                                    )
                                }
                            }

                            Spacer(Modifier.size(12.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showRangePicker = true }
                            ) {
                                Text(
                                    text = "To",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF7A7A7A)
                                )
                                Spacer(Modifier.size(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFE8E7E5))
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 14.dp),
                                        text = toDate.format(careerDateFormatter),
                                        style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = (-0.02).sp),
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        val isSaveEnabled = companyText.isNotBlank() && toDate.isAfter(fromDate)
                        Spacer(Modifier.size(16.dp))
                        com.example.teene.authentication.composables.ButtonWithIcon(
                            modifier = Modifier.fillMaxWidth(),
                            iconId = null,
                            text = "Save",
                            enabled = isSaveEnabled
                        ) {
                            // Save and close
                            val newItem = RegisterTrainerViewModel.CareerHistoryItem(
                                companyName = companyText.trim(),
                                from = fromDate,
                                to = toDate
                            )
                            registerVm.careerHistory.value = careerItems + newItem
                            showAddDialog = false
                        }

                        // Date range picker (use Material3 DatePickerDialog for proper styling)
                        if (showRangePicker) {
                            val rangeState = androidx.compose.material3.rememberDateRangePickerState(
                                initialSelectedStartDateMillis = fromDate.toMillis(),
                                initialSelectedEndDateMillis = toDate.toMillis()
                            )
                            androidx.compose.material3.DatePickerDialog(
                                onDismissRequest = { showRangePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val start = rangeState.selectedStartDateMillis
                                        val end = rangeState.selectedEndDateMillis
                                        if (start != null && end != null) {
                                            fromDate = millisToLocalDate(start)
                                            toDate = millisToLocalDate(end)
                                        }
                                        showRangePicker = false
                                    }) { Text("OK") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showRangePicker = false }) { Text("Cancel") }
                                }
                            ) {
                                androidx.compose.material3.DateRangePicker(
                                    state = rangeState,
                                    showModeToggle = false
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom full-width Next button (same as on RegisterTrainerScreen)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            if (creationResult?.isFailure == true) {
                Text(
                    text = creationResult?.exceptionOrNull()?.message ?: "Failed to create trainer",
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            com.example.teene.authentication.composables.ButtonWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                iconId = null,
                text = "Next",
                enabled = true,
            ) {
                registerVm.createTrainer()
            }
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun TrainerYourCareerScreenPreview() {
    com.example.teene.ui.theme.TeeneTheme {
        TrainerYourCareerScreen(navigator = null)
    }
}
