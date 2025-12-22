package com.example.teene.home.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.home.data.models.FeatureItem
import com.example.teene.home.domain.usecases.GetFeaturesUseCase
import com.example.teene.home.domain.usecases.GetSportsUseCase
import com.example.teene.home.presentation.models.SportSuggestion
import com.example.teene.home.presentation.models.SportWithTrainers
import com.example.teene.home.presentation.models.TrainerNumberCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf

/**
 * Created by 3100lari on 2025/02/16
 */
class ExploreViewModel(
    private val getSportsUseCase: GetSportsUseCase,
    private val getFeaturesUseCase: GetFeaturesUseCase
) : ViewModel()
{
    // Filters state (committed search)
    private val searchQuery = MutableStateFlow("")
    val committedQuery: StateFlow<String> = searchQuery // expose read-only reference for UI
    private val radius = MutableStateFlow<Int?>(null)
    private val featureIds = MutableStateFlow<List<Int>>(emptyList())
    private val intensities = MutableStateFlow<List<String>>(emptyList())

    // Expose selections to other screens (read-only)
    val selectedFeatureIds: StateFlow<List<Int>> = featureIds
    val selectedIntensities: StateFlow<List<String>> = intensities

    // Number of active filters for UI badge (features + intensities)
    val activeFiltersCount: StateFlow<Int> = combine(featureIds, intensities) { feats, ints ->
        (feats?.size ?: 0) + (ints?.size ?: 0)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )

    // Live typing state for suggestions (not yet committed)
    private val suggestionQuery = MutableStateFlow("")

    private data class FilterParams(
        val name: String,
        val radius: Int?,
        val featureIds: List<Int>,
        val intensities: List<String>
    )

    // Public API to update filters
    fun updateSearchQuery(query: String) { searchQuery.value = query }
    fun updateRadius(r: Int?) { radius.value = r }
    fun updateFeatureIds(ids: List<Int>) { featureIds.value = ids }
    fun updateIntensities(values: List<String>) { intensities.value = values }

    // Public API to update suggestions typing
    fun updateSuggestionQuery(q: String) { suggestionQuery.value = q }

    // Load features for filters UI
    val features: StateFlow<List<FeatureItem>> = getFeaturesUseCase.execute()
        .map { result -> result.getOrElse { emptyList() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Suggestions: debounced API-backed sport names (respect active filters)
    private data class SuggestionParams(
        val query: String,
        val featureIds: List<Int>,
        val intensities: List<String>
    )

    private val suggestionParamsFlow = combine(suggestionQuery, featureIds, intensities) { q, feats, ints ->
        SuggestionParams(q.trim(), feats, ints)
    }
        .debounce(350)
        .distinctUntilChanged { old, new ->
            old.query == new.query && old.featureIds == new.featureIds && old.intensities == new.intensities
        }

    val suggestions: StateFlow<List<String>> = suggestionParamsFlow
        .flatMapLatest { params ->
            val q = params.query
            if (q.length < 2) {
                flowOf(Result.success(emptyList()))
            } else {
                getSportsUseCase.execute(
                    featureIds = params.featureIds.takeIf { it.isNotEmpty() },
                    intensities = params.intensities.takeIf { it.isNotEmpty() },
                    name = q
                )
            }
        }
        .map { result ->
            result.fold(
                onSuccess = { list -> list.map { it.name }.distinct().take(10) },
                onFailure = { emptyList() }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Suggestions with counts for rendering subtitle under sport name (respect active filters)
    val suggestionsDetailed: StateFlow<List<SportSuggestion>> = suggestionParamsFlow
        .flatMapLatest { params ->
            val q = params.query
            if (q.length < 2) {
                flowOf(Result.success(emptyList()))
            } else {
                getSportsUseCase.execute(
                    featureIds = params.featureIds.takeIf { it.isNotEmpty() },
                    intensities = params.intensities.takeIf { it.isNotEmpty() },
                    name = q
                )
            }
        }
        .map { result ->
            result.fold(
                onSuccess = { list ->
                    list.map { sport ->
                        SportSuggestion(
                            id = sport.id,
                            name = sport.name,
                            trainerCount = sport.trainerCount
                        )
                    }.distinctBy { it.id }.take(10)
                },
                onFailure = { emptyList() }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val sportsList: StateFlow<List<SportWithTrainers>> =
        combine(searchQuery, radius, featureIds, intensities) { name: String, rad: Int?, feats: List<Int>, ints: List<String> ->
            FilterParams(name, rad, feats, ints)
        }.flatMapLatest { params ->
            val nameParam = params.name.takeIf { it.isNotBlank() }
            val featsParam = params.featureIds.takeIf { it.isNotEmpty() }
            val intsParam = params.intensities.takeIf { it.isNotEmpty() }
            getSportsUseCase.execute(
                radius = params.radius,
                featureIds = featsParam,
                intensities = intsParam,
                name = nameParam
            )
        }
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
                onFailure = {
                    emptyList()
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun onSportItemClick(id: Int)
    {
        Log.i("BOBAN","Clicked Sport item with ID: $id")
    }
}