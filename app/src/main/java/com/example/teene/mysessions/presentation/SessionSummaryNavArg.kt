package com.example.teene.mysessions.presentation

import java.io.Serializable

// Use Serializable for navigation args to avoid requiring parcelize plugin here
// If you prefer Parcelable, we can switch later.
data class SessionSummaryNavArg(
    val bookingId: Int,
    val sportName: String?,
    val trainerName: String?,
    val address: String?,
    val priceText: String?,
    val startTimeIso: String?,
    val trainerQuickbloxId: Int? = null
) : Serializable