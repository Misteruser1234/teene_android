package com.example.teene.authentication.register.trainer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.home.domain.usecases.GetSportsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for RegisterTrainerScreen to fetch sports via the domain use case
 * and expose a suggestions stream suitable for a searchable dropdown.
 */
class RegisterTrainerViewModel(
    private val getSportsUseCase: GetSportsUseCase,
    private val getFeaturesUseCase: com.example.teene.home.domain.usecases.GetFeaturesUseCase
) : ViewModel() {

    data class RegisterSportSuggestion(
        val id: Int,
        val name: String
    )

    data class RegisterLanguage(
        val code: String,
        val name: String
    )

    // Expose Features list to the screen (used for chips)
    val features: StateFlow<List<com.example.teene.home.data.models.FeatureItem>> =
        getFeaturesUseCase.execute()
            .map { result -> result.getOrElse { emptyList() } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    // Live query for sport search
    private val query = MutableStateFlow("")
    val sportQuery: StateFlow<String> = query
    fun updateSuggestionQuery(q: String) { query.value = q }

    // Selected sports (IDs)
    private val selectedSportIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedSportsIds: StateFlow<Set<Int>> = selectedSportIds

    // Fetch all sports once and map to UI suggestions; rely on client-side filtering for the dropdown
    private val allSports: StateFlow<List<RegisterSportSuggestion>> = getSportsUseCase.execute()
        .map { result ->
            result.getOrElse { emptyList() }
                .map { sport ->
                    RegisterSportSuggestion(
                        id = sport.id,
                        name = sport.name
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    // Selected sports full objects for chips rendering
    val selectedSports: StateFlow<List<RegisterSportSuggestion>> = combine(selectedSportIds, allSports) { ids, list ->
        if (ids.isEmpty()) emptyList() else list.filter { it.id in ids }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    // Suggestions exclude already-selected sports
    val suggestionsDetailed: StateFlow<List<RegisterSportSuggestion>> = combine(
        query.debounce(250).distinctUntilChanged(),
        allSports,
        selectedSportIds
    ) { q, list, selectedIds ->
        val trimmed = q.trim()
        if (trimmed.length < 2) {
            emptyList()
        } else {
            list.asSequence()
                .filter { it.id !in selectedIds }
                .filter { it.name.contains(trimmed, ignoreCase = true) }
                .distinctBy { it.id }
                .take(10)
                .toList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun addSport(sport: RegisterSportSuggestion) {
        val current = selectedSportIds.value
        if (sport.id !in current) {
            selectedSportIds.value = current + sport.id
        }
    }

    fun addSportById(id: Int) {
        val current = selectedSportIds.value
        if (id !in current) {
            selectedSportIds.value = current + id
        }
    }

    fun removeSport(id: Int) {
        val current = selectedSportIds.value
        if (id in current) {
            selectedSportIds.value = current - id
        }
    }

    fun setSelectedSports(newIds: Set<Int>) {
        selectedSportIds.value = newIds
    }

    // -------------------- Languages (hardcoded) --------------------
    private val hardcodedLanguages = listOf(
        RegisterLanguage(code = "en", name = "English"),
        RegisterLanguage(code = "es", name = "Spanish"),
        RegisterLanguage(code = "sr", name = "Serbian"),
        RegisterLanguage(code = "de", name = "German"),
        RegisterLanguage(code = "fr", name = "French"),
        RegisterLanguage(code = "it", name = "Italian")
    )

    private val languageQuery = MutableStateFlow("")
    val languagesQuery: StateFlow<String> = languageQuery
    fun updateLanguageQuery(q: String) { languageQuery.value = q }

    private val selectedLanguageCodes = MutableStateFlow<Set<String>>(emptySet())
    val selectedLanguagesCodes: StateFlow<Set<String>> = selectedLanguageCodes

    val selectedLanguages: StateFlow<List<RegisterLanguage>> = combine(selectedLanguageCodes) { codes ->
        val set = codes.firstOrNull() ?: emptySet()
        hardcodedLanguages.filter { it.code in set }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val languageSuggestions: StateFlow<List<RegisterLanguage>> = combine(
        languageQuery.debounce(250).distinctUntilChanged(),
        selectedLanguageCodes
    ) { q, selected ->
        val trimmed = q.trim()
        if (trimmed.length < 2) emptyList() else hardcodedLanguages.asSequence()
            .filter { it.code !in selected }
            .filter { it.name.contains(trimmed, ignoreCase = true) }
            .take(10)
            .toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun addLanguageByCode(code: String) {
        val current = selectedLanguageCodes.value
        if (code !in current) selectedLanguageCodes.value = current + code
    }

    fun removeLanguage(code: String) {
        val current = selectedLanguageCodes.value
        if (code in current) selectedLanguageCodes.value = current - code
    }

    fun setSelectedLanguages(codes: Set<String>) {
        selectedLanguageCodes.value = codes
    }
}