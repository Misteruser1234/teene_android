package com.example.teene.mysessions.presentation

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.teene.home.presentation.composables.CommonHeader
import com.example.teene.home.presentation.composables.SearchBarWithFilters
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.mysessions.presentation.SessionSummaryNavArg
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.generated.destinations.SessionDetailsScreenDestination
import org.koin.androidx.compose.koinViewModel
import java.time.Instant

/**
 * Created by 3100lari on 2025/02/12
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun MySessionsScreen(
    navigator: DestinationsNavigator
) {
    val viewModel = koinViewModel<MySessionsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchBookings()
    }

    MySessionsContent(
        state = uiState,
        onCardClick = { args ->
            navigator.navigate(
                com.ramcosta.composedestinations.generated.destinations.SessionDetailsScreenDestination(
                    args
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySessionsContent(
    state: MySessionsUiState,
    onCardClick: (SessionSummaryNavArg) -> Unit = {}
) {
    // Tabs: 0 = Upcoming, 1 = Past
    val (selectedTab, setSelectedTab) = rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Standard header without the map icon
        CommonHeader(headerText = "My Sessions")
        // Search bar with filter below the header
        SearchBarWithFilters(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            showAskAiButton = false
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = Color(0x3F3F3F99))

        // SecondaryTabRow must be directly below the divider
        Surface(
            modifier = Modifier
                .padding(vertical = 24.dp),
            color = Color(0xFF009DC3),
            shape = RoundedCornerShape(4.dp),
            shadowElevation = 0.dp
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                indicator = {},
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { setSelectedTab(0) },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.White,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (selectedTab == 0) Color.White else Color.Transparent)
                ) {
                    Text(
                        text = "Upcoming",
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }

                Tab(
                    selected = selectedTab == 1,
                    onClick = { setSelectedTab(1) },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.White,
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (selectedTab == 1) Color.White else Color.Transparent)
                ) {
                    Text(
                        text = "Past",
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }
            }
        }

        // Screen content
        when (state) {
            is MySessionsUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is MySessionsUiState.Error -> Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(text = state.message, color = MaterialTheme.colorScheme.error) }

            is MySessionsUiState.Success -> {
                val now = Instant.now()
                val (upcoming, past) = state.bookings.partition { booking ->
                    val start = runCatching { Instant.parse(booking.startTime) }.getOrNull()
                    // Treat unparsable dates as past to avoid showing invalid future
                    start?.isAfter(now) == true
                }
                val list = if (selectedTab == 0) upcoming else past

                if (list.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { MySessionsEmptyState() }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                            12.dp
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
                    ) {
                        items(list) { booking ->
                            MySessionCard(
                                booking = booking,
                                onClick = { onCardClick(booking.toNavArg()) }
                            )
                        }
                    }
                }
            }

            MySessionsUiState.Idle -> Unit
        }
    }
}

@Composable
fun MySessionsEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No sessions to show... yet!",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif, // TODO: replace with Contrail One when font is added
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp,
                    lineHeight = 46.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "You haven’t book any session or sports event yet.",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif, // TODO: replace with Inter when font is added
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    letterSpacing = (-0.02).sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, name = "My Sessions – Empty State Only")
@Composable
fun MySessionsEmptyPreview() {
    MySessionsEmptyState()
}

@Preview(showBackground = true, name = "My Sessions Screen – Empty")
@Composable
fun MySessionsScreenEmptyPreview() {
    MySessionsContent(MySessionsUiState.Success(emptyList()))
}

@Composable
fun MySessionCard(
    booking: com.example.teene.home.data.models.TrainingBookingDto,
    onClick: () -> Unit = {}
) {
    // Extract fields safely
    val sportName = booking.trainer?.sport?.name?.takeIf { it.isNotBlank() } ?: "Unknown sport"
    val trainerName = listOfNotNull(booking.trainer?.firstName, booking.trainer?.lastName)
        .joinToString(" ").trim().ifBlank { "Unknown trainer" }

    val location = booking.trainer?.address?.let { addr ->
        // Expecting comma separated components; show Street, City if available
        val parts = addr.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        when {
            parts.size >= 2 -> listOf(parts[0], parts[1]).joinToString(", ")
            parts.size == 1 -> parts[0]
            else -> null
        }
    }

    val priceText = formatPrice(booking.trainer?.currency, booking.trainer?.rate)

    val locale = java.util.Locale.getDefault()
    val zoned = parseZonedDateTime(booking.startTime)
    val dateText = zoned?.toLocalDate()?.let {
        java.time.format.DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM)
            .withLocale(locale)
            .format(it)
    } ?: "—"
    val timeText = zoned?.toLocalTime()?.let {
        java.time.format.DateTimeFormatter.ofLocalizedTime(java.time.format.FormatStyle.SHORT)
            .withLocale(locale)
            .format(it)
    } ?: "—"

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0)),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Top image: sport image if available; fallback to trainer image; otherwise a local sport icon
            val sportImageUrl = booking.trainer?.sport?.image?.url
            val trainerImageUrl = booking.trainer?.images?.firstOrNull()?.image?.url

            if (!sportImageUrl.isNullOrBlank() || !trainerImageUrl.isNullOrBlank()) {
                // Remote image available
                AsyncImage(
                    model = sportImageUrl ?: trainerImageUrl,
                    contentDescription = "Sport image",
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                    contentScale = ContentScale.FillBounds
                )
            } else {
                // Fallback to a local vector based on sport name
                Image(
                    painter = com.example.teene.home.presentation.icons.sportIconPainter(sportName),
                    contentDescription = "Sport icon",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                // Sport name
                Text(
                    text = sportName,
                    color = Color(0xFF171725),
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif, // TODO: replace with Inter when font is added
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        letterSpacing = (-0.02).sp
                    )
                )
                // with <trainer>
                Text(
                    text = "with $trainerName",
                    color = Color(0xFF9CA4AB),
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif, // TODO: replace with Inter when font is added
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        letterSpacing = (-0.02).sp
                    )
                )
                // Location, only if available
                if (!location.isNullOrBlank()) {
                    Text(text = location, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                // Price, only if available
                if (!priceText.isNullOrBlank()) {
                    val perToken = "/per session"
                    val idx = priceText.indexOf(perToken)
                    val amountPart = if (idx > 0) priceText.substring(0, idx).trim() else priceText
                    val perSessionPart = if (idx > 0) perToken else ""

                    val annotated = androidx.compose.ui.text.buildAnnotatedString {
                        // Currency + amount in blue (same as tab background)
                        withStyle(androidx.compose.ui.text.SpanStyle(color = Color(0xFF009DC3))) {
                            append(amountPart)
                        }
                        if (perSessionPart.isNotEmpty()) {
                            append(" ")
                            // "per session" uses same typography as the sport name; only color differs
                            withStyle(androidx.compose.ui.text.SpanStyle(color = Color(0xFF171725))) {
                                append(perSessionPart)
                            }
                        }
                    }

                    Text(
                        text = annotated,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif, // TODO: replace with Inter when font is added
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            letterSpacing = (-0.02).sp
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0x3F3F3F99))
                Spacer(Modifier.height(12.dp))

                // Date row
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.teene.R.drawable.calendar),
                            contentDescription = "Date",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Date")
                    }
                    Text(text = dateText)
                }

                Spacer(Modifier.height(8.dp))
                // Time row
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.teene.R.drawable.clock),
                            contentDescription = "Time",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Time")
                    }
                    Text(text = timeText)
                }
            }
        }
    }
}

private fun parseZonedDateTime(isoInstantString: String?): java.time.ZonedDateTime? {
    if (isoInstantString.isNullOrBlank()) return null
    return try {
        val instant = java.time.Instant.parse(isoInstantString)
        instant.atZone(java.time.ZoneId.systemDefault())
    } catch (_: Exception) {
        null
    }
}

private fun formatPrice(currency: String?, rate: Double?): String? {
    if (currency.isNullOrBlank() || rate == null) return null
    val nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.getDefault())
    nf.maximumFractionDigits = 2
    nf.minimumFractionDigits = if (rate % 1.0 == 0.0) 0 else 2
    return "$currency ${nf.format(rate)} /per session"
}

private fun com.example.teene.home.data.models.TrainingBookingDto.toNavArg(): SessionSummaryNavArg {
    val sportName = this.trainer?.sport?.name
    val trainerName = listOfNotNull(this.trainer?.firstName, this.trainer?.lastName)
        .joinToString(" ").trim().ifBlank { null }
    val address = this.trainer?.address?.let { addr ->
        val parts = addr.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        when {
            parts.size >= 2 -> listOf(parts[0], parts[1]).joinToString(", ")
            parts.size == 1 -> parts[0]
            else -> null
        }
    }
    val priceText = formatPrice(this.trainer?.currency, this.trainer?.rate)
    return SessionSummaryNavArg(
        bookingId = this.id,
        sportName = sportName,
        trainerName = trainerName,
        address = address,
        priceText = priceText,
        startTimeIso = this.startTime
    )
}

@Preview(showBackground = true)
@Composable
fun MySessionCardPreview() {
    val booking = com.example.teene.home.data.models.TrainingBookingDto(
        id = 1,
        userId = 1012,
        startTime = "2025-12-14T10:00:00.000Z",
        endTime = "2025-12-14T10:45:00.000Z",
        approved = false,
        cancelledAt = null,
        createdAt = null,
        trainer = com.example.teene.home.data.models.TrainerSummaryDto(
            id = 832,
            firstName = "Chauncey",
            lastName = "Barton",
            latitude = null,
            longitude = null,
            rate = 2323.0,
            currency = "RSD",
            address = "21 Jump Street, Belgrade, Serbia",
            rating = null,
            reviewCount = 0,
            about = null,
            images = emptyList(),
            sport = com.example.teene.home.data.models.SportDto(
                id = 1,
                name = "Taekwondo",
                image = null
            )
        )
    )
    MySessionCard(booking = booking)
}
