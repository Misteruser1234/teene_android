package com.example.teene.home.data.repositories

import com.example.teene.home.data.models.FeatureItem
import com.example.teene.home.data.models.ImageItem
import com.example.teene.home.data.models.TrainerResponseItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Mock implementation returning hardcoded trainers for testing/debug builds.
 * Provides two realistic padel coaches when sportId == 2.
 * Created by 3100lari on 2025/11/05
 */
class MockTrainersRepositoryImpl : TrainersRepository {

    private fun padelCoaches(): List<TrainerResponseItem> = listOf(
        TrainerResponseItem(
            id = 101,
            firstName = "Lucía",
            lastName = "Navarro",
            latitude = 40.4168,  // Madrid center
            longitude = -3.7038,
            rate = 45.0,
            currency = "EUR",
            address = "Madrid, Spain",
            distance = 3.2,
            images = listOf(com.example.teene.home.data.models.TrainerImageItem(image = ImageItem(url = "https://images.unsplash.com/photo-1587502536955-6df563e350b5?q=80&w=1080&auto=format"))),
            rating = 4.8,
            about = "Former WPT player with 8+ years of coaching experience. Focus on tactics and positioning.",
            features = listOf(
                FeatureItem(id = 1, name = "Beginner friendly"),
                FeatureItem(id = 2, name = "Video analysis"),
                FeatureItem(id = 3, name = "English & Spanish")
            )
        ),
        TrainerResponseItem(
            id = 102,
            firstName = "Marco",
            lastName = "Alonso",
            latitude = 40.4314,  // North Madrid
            longitude = -3.6788,
            rate = 38.0,
            currency = "EUR",
            address = "Chamartín, Madrid",
            distance = 5.7,
            images = listOf(com.example.teene.home.data.models.TrainerImageItem(image = ImageItem(url = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?q=80&w=1080&auto=format"))),
            rating = 4.6,
            about = "Certified padel coach specialized in technique and footwork for intermediate players.",
            features = listOf(
                FeatureItem(id = 4, name = "Intermediate/Advanced"),
                FeatureItem(id = 5, name = "Equipment provided"),
                FeatureItem(id = 6, name = "Flexible hours")
            )
        )
    )

    override fun getTrainersForSport(sportId: Int): Flow<Result<List<TrainerResponseItem>>> = flow {
        if (sportId == 2) {
            emit(Result.success(padelCoaches()))
        } else {
            emit(Result.success(emptyList()))
        }
    }

    override fun getAllTrainers(): Flow<Result<List<TrainerResponseItem>>> = flow {
        // For the mock, we only have padel coaches.
        emit(Result.success(padelCoaches()))
    }
}
