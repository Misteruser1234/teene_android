package com.example.teene.home.presentation.models

/**
 * Created by 3100lari on 2025/02/15
 */
enum class TrainerNumberCategory(val displayText: String, val range: IntRange?)
{
    ONE_TO_TEN("1-10 trainers", 1..10),
    TEN_PLUS("10+ trainers", 11..50),
    FIFTY_PLUS("50+ trainers", 50..99),
    HUNDRED_PLUS("100+ trainers", 100..999),
    THOUSAND_PLUS("1000+ trainers", null);

    companion object
    {
        fun fromNumber(number: Int): TrainerNumberCategory
        {
            return entries.find { it.range?.contains(number) ?: (number >= 1000) } ?: THOUSAND_PLUS
        }
    }
}