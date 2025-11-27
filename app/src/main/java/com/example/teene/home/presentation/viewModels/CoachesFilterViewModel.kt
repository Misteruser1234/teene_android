package com.example.teene.home.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.home.data.models.TrainerResponseItem
import com.example.teene.home.domain.usecases.GetAllTrainersUseCase
import com.example.teene.home.domain.usecases.GetSportsUseCase
import com.example.teene.home.domain.usecases.GetTrainersForSportUseCase
import com.example.teene.home.presentation.models.CoachFilters
import com.example.teene.home.presentation.models.SportWithTrainers
import com.example.teene.home.presentation.models.TrainerNumberCategory
import com.example.teene.home.presentation.models.TrainerWithCoordinates
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first

/**
 * ViewModel responsible for managing Sports list and Coaches list according to the provided filters.
 * Intended to be used by the Map screen and a future Filter screen.
 * Created by 3100lari on 2025/11/05
 */
class CoachesFilterViewModel(
    getSportsUseCase: GetSportsUseCase,
    private val getTrainersForSportUseCase: GetTrainersForSportUseCase,
    private val getAllTrainersUseCase: GetAllTrainersUseCase,
) : ViewModel() {

    // Public filters state
    private val _filters = MutableStateFlow(CoachFilters())
    val filters: StateFlow<CoachFilters> = _filters

    // Sports list (unfiltered)
    val sports: StateFlow<List<SportWithTrainers>> = getSportsUseCase.execute()
        .map { result ->
            result.fold(
                onSuccess = { sports ->
                    sports.map { sport ->
                        SportWithTrainers(
                            id = sport.id,
                            sportName = sport.name,
                            trainerNumberCategory = TrainerNumberCategory.fromNumber(sport.trainerCount),
                            imageUrl = sport.image.url
                        )
                    }
                },
                onFailure = { emptyList() }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Upstream trainers paired with their sport name (before client-side filters)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val upstreamTrainers: StateFlow<List<Pair<TrainerResponseItem, String?>>> = combine(_filters.map { it.sportId }, sports) { sportId, sportsList ->
        sportId to sportsList
    }.flatMapLatest { (sportId, sportsList) ->
        if (sportId != null) {
            val sportName = sportsList.firstOrNull { it.id == sportId }?.sportName
            getTrainersForSportUseCase.execute(sportId)
                .map { result ->
                    result.getOrElse { emptyList() }.map { it to sportName }
                }
        } else {
            getAllTrainersUseCase.execute()
                .map { result ->
                    result.getOrElse { emptyList() }.map { it to null }
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    // Final trainers after applying client-side filters
    val trainers: StateFlow<List<TrainerWithCoordinates>> = combine(upstreamTrainers, _filters, sports) { trainersWithSport, f, sportsList ->
        val selectedSportName: String? = f.sportId?.let { id -> sportsList.firstOrNull { it.id == id }?.sportName }
        trainersWithSport.filter { (t, _) ->
            val priceOk = (f.priceMin?.let { t.rate >= it } ?: true) && (f.priceMax?.let { t.rate <= it } ?: true)
            val ratingOk = f.minRating?.let { (t.rating ?: 0.0) >= it } ?: true
            val distanceOk = f.maxDistanceKm?.let { t.distance <= it } ?: true
            val featuresOk = if (f.featureIds.isEmpty()) true else t.features.any { it.id in f.featureIds }
            priceOk && ratingOk && distanceOk && featuresOk
        }.map { (trainer, sport) ->
            TrainerWithCoordinates(
                id = trainer.id,
                firstName = trainer.firstName,
                lastName = trainer.lastName,
                latitude = trainer.latitude,
                longitude = trainer.longitude,
                rate = trainer.rate,
                currency = trainer.currency,
                address = trainer.address,
                distance = trainer.distance,
                imageUrl = trainer.image.url,
                rating = trainer.rating ?: 0.0,
                about = trainer.about,
                features = trainer.features,
                sportName = sport ?: selectedSportName
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    // API: update the whole filters object
    fun setFilters(newFilters: CoachFilters) {
        _filters.value = newFilters
    }

    // API: set/clear sport id independently (commonly set by map/explore context)
    fun setSportId(sportId: Int?) {
        _filters.value = _filters.value.copy(sportId = sportId)
    }

    // Convenience: update only certain fields
    fun updatePriceRange(min: Double?, max: Double?) {
        _filters.value = _filters.value.copy(priceMin = min, priceMax = max)
    }

    fun updateMinRating(minRating: Double?) {
        _filters.value = _filters.value.copy(minRating = minRating)
    }

    fun updateMaxDistance(km: Double?) {
        _filters.value = _filters.value.copy(maxDistanceKm = km)
    }

    fun updateFeatures(featureIds: Set<Int>) {
        _filters.value = _filters.value.copy(featureIds = featureIds)
    }
}
