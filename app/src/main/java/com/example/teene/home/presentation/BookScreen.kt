package com.example.teene.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.R
import com.example.teene.authentication.composables.SelectableButton
import com.example.teene.home.presentation.models.BookingUiState
import com.example.teene.home.presentation.models.TrainerAvailabilityUiState
import com.example.teene.home.presentation.viewModels.BookingTrainerViewModel
import com.example.teene.home.presentation.viewModels.SportViewModel
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.composables.ComonFooter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.generated.destinations.MySessionsScreenDestination
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.util.Locale

/**
 * Created by 3100lari on 2025/07/22
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun BookScreen(
    trainerId: Int,
    navigator: DestinationsNavigator,
    trainerName: String,
    featureNames: String,
    rate: Double,
    currency: String
)
{
    val bookingTrainerViewModel = koinViewModel<BookingTrainerViewModel>()

    var selectedDateTime by remember { mutableStateOf<java.time.LocalDateTime?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var peopleCount by remember { mutableStateOf(1) }

    // Observe booking state
    val bookingState = bookingTrainerViewModel.bookingState.collectAsStateWithLifecycle().value
    val isBookingLoading = bookingState is BookingUiState.Loading

    // On first composition, default the selected date to today and fetch availability for today
    LaunchedEffect(Unit) {
        bookingTrainerViewModel.fetchTrainerAvailability(
            trainerId = trainerId,
            startDate = selectedDate,
            endDate = selectedDate
        )
    }

    
    // Navigate to My Sessions on successful booking
    LaunchedEffect(bookingState) {
        when (bookingState) {
            is BookingUiState.Success -> {
                navigator.navigate(MySessionsScreenDestination) {
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }


    val availabilityUiState = bookingTrainerViewModel.availabilityState
        .collectAsStateWithLifecycle().value

    var timeSlots by remember { mutableStateOf<List<Timeslot>>(emptyList()) }

    when (availabilityUiState)
    {
        is TrainerAvailabilityUiState.Loading ->
        {
            // Show loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading availability...")
            }
        }

        is TrainerAvailabilityUiState.Error ->
        {
            // Show error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error loading availability")
            }
        }

        is TrainerAvailabilityUiState.Success.Available ->
        {
            timeSlots = availabilityUiState.availableSlots.map { timeSlot ->
                Timeslot(timeSlot, true)
            }

            // Show available time slots
            // Here you can use the fetched availability data to display available time slots
        }

        is TrainerAvailabilityUiState.Success.Empty ->
        {

        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedIconButton(
                onClick = { navigator.navigateUp() },
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFFCFCFCF))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            Text(
                text = "Book Session",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp)) // Placeholder for symmetrical spacing
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Coach Field
        Text(
            "Coach",
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black,
            )
        )
        OutlinedTextField(
            enabled = false,
            value = "$trainerName · $featureNames",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Date Picker
        Text(
            "When",
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black,
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        DatePickerFieldToModal(onDateSelected = { date ->
            if (date != null) {
                // Clear previously selected time when date changes
                if (selectedDate != date) selectedDateTime = null
                selectedDate = date
                bookingTrainerViewModel.fetchTrainerAvailability(
                    trainerId = trainerId,
                    startDate = date,
                    endDate = date
                )
            }
        })

        Spacer(modifier = Modifier.height(24.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            maxItemsInEachRow = 3
        ) {
            timeSlots.forEach { it ->

                SelectableButton(
                    modifier = Modifier
                        .fillMaxWidth(0.33f)
                        .padding(4.dp),
                    text = it.time,
                    isSelected = run {
                        val sel = selectedDateTime
                        val slotTime = parseLocalTime(it.time)
                        sel != null && slotTime != null && sel.toLocalDate() == selectedDate && sel.toLocalTime() == slotTime
                    },
                    onClick = {
                        if (it.isAvailable) {
                            val slotTime = parseLocalTime(it.time)
                            if (slotTime != null) {
                                selectedDateTime = java.time.LocalDateTime.of(selectedDate, slotTime)
                            }
                        }
                    },
                    isEnabled = it.isAvailable,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // People Selector
        Text("Add People", style = MaterialTheme.typography.labelMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedIconButton(
                onClick = { if (peopleCount > 1) peopleCount-- },
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFFCFCFCF))
            ) {
                Icon(
                    painter = painterResource(R.drawable.remove),
                    contentDescription = null
                )
            }

            Text(
                text = peopleCount.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )

            OutlinedIconButton(
                onClick = { peopleCount++ },
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFFCFCFCF))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add person")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

       ComonFooter(
            leftContent = {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$rate ")
                            }
                            append(currency)
                            append(" / trening")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )

                    // Date and time, bold when selected
                    val dateText = formatDayMonth(selectedDate)
                    val timeText = selectedDateTime?.toLocalTime()?.let { formatTime(it) }
                    if (timeText != null) {
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(dateText)
                                }
                                append(" at ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(timeText)
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    } else {
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }

                    // People count
                    Text("$peopleCount people", style = MaterialTheme.typography.bodySmall, color = Color.Black)
                }
            },
            buttonText = "Book now",
            onButtonClick = {
                val sel = selectedDateTime
                if (!isBookingLoading && sel != null) {
                    bookingTrainerViewModel.bookSession(trainerId, sel)
                }
            },
            buttonEnabled = !isBookingLoading && selectedDateTime != null, 
            buttonIconId = R.drawable.rocket_1
        )
        // Overlay loading spinner while booking
        if (isBookingLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
}


@Composable
fun DatePickerFieldToModal(modifier: Modifier = Modifier, onDateSelected: (LocalDate?) -> Unit)
{
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var showModal by remember { mutableStateOf(false) }

    val displayFormatter = java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())

    OutlinedTextField(
        value = selectedDate?.format(displayFormatter) ?: "",
        onValueChange = { },
        label = { Text("Pick a date") },
        placeholder = { Text(LocalDate.now().format(displayFormatter)) },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null)
                    {
                        showModal = true
                    }
                }
            }
    )

    if (showModal)
    {
        DatePickerModal(
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
                onDateSelected(it)
            },
            onDismiss = { showModal = false }
        )
    }
}


fun formatDayMonth(date: LocalDate): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("d. MMM", Locale.getDefault())
    return date.format(formatter)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDate: LocalDate? = null,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
)
{
    val initialMillis = selectedDate?.atStartOfDay(java.time.ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val chosenDate = datePickerState.selectedDateMillis?.let { millis ->
                    java.time.Instant.ofEpochMilli(millis)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                }
                onDateSelected(chosenDate)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

data class Timeslot(val time: String, val isAvailable: Boolean = true)


private fun parseLocalTime(text: String): java.time.LocalTime? {
    return try {
        // Try strict 24h format with leading zero e.g., 09:00
        java.time.LocalTime.parse(text, java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e1: Exception) {
        try {
            // Try single-hour digit e.g., 9:00
            java.time.LocalTime.parse(text, java.time.format.DateTimeFormatter.ofPattern("H:mm"))
        } catch (e2: Exception) {
            null
        }
    }
}

private fun formatTime(time: java.time.LocalTime): String {
    return time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
}
