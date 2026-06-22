package com.example.teene.authentication.register.trainer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.home.domain.usecases.GetSportsUseCase
import com.example.teene.home.domain.usecases.GetFeaturesUseCase
import com.example.teene.home.domain.usecases.CreateTrainerUseCase
import com.example.teene.data.UserDataStore
import com.example.teene.home.data.models.CareerHistoryRequest
import com.example.teene.home.data.models.TrainerCreateRequest
import com.example.teene.home.data.models.TrainerCreateResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

/**
 * ViewModel for RegisterTrainerScreen to fetch sports via the domain use case
 * and expose a suggestions stream suitable for a searchable dropdown.
 */
@OptIn(FlowPreview::class)
class RegisterTrainerViewModel(
    private val getSportsUseCase: GetSportsUseCase,
    private val getFeaturesUseCase: GetFeaturesUseCase,
    private val createTrainerUseCase: CreateTrainerUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    data class RegisterSportSuggestion(
        val id: Int,
        val name: String
    )

    data class RegisterLanguage(
        val code: String,
        val name: String
    )

    data class CareerHistoryItem(
        val companyName: String,
        val from: java.time.LocalDate,
        val to: java.time.LocalDate
    )

    // -------------------- Registration State --------------------
    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val aboutText = MutableStateFlow("")
    val priceText = MutableStateFlow("")
    val latitude = MutableStateFlow<Double?>(null)
    val longitude = MutableStateFlow<Double?>(null)
    val currency = MutableStateFlow("RSD")
    
    val selectedFeatureIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIntensities = MutableStateFlow<Set<String>>(emptySet())
    
    val eduDegree = MutableStateFlow(false)
    val eduCourse = MutableStateFlow(false)
    val eduNone = MutableStateFlow(false)

    val achievements = MutableStateFlow<List<String>>(emptyList())
    val careerHistory = MutableStateFlow<List<CareerHistoryItem>>(emptyList())

    private val _creationResult = MutableStateFlow<Result<TrainerCreateResponse>?>(null)
    val creationResult: StateFlow<Result<TrainerCreateResponse>?> = _creationResult

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

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
        val pool = list.filter { it.id !in selectedIds }
        if (trimmed.isEmpty()) {
            pool.take(10).toList()
        } else {
            pool.asSequence()
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

    val selectedLanguages: StateFlow<List<RegisterLanguage>> = selectedLanguageCodes.map { codes ->
        hardcodedLanguages.filter { it.code in codes }
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
        val pool = hardcodedLanguages.filter { it.code !in selected }
        if (trimmed.isEmpty()) {
            pool.take(10).toList()
        } else {
            pool.filter { it.name.contains(trimmed, ignoreCase = true) }
                .take(10)
                .toList()
        }
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

    fun createTrainer() {
        viewModelScope.launch {
            val userId = userDataStore.userIdFlow.first() ?: return@launch

            val education = mutableListOf<String>()
            if (eduDegree.value) education.add("Degree")
            if (eduCourse.value) education.add("Course")
            if (eduNone.value) education.add("None")

            val request = TrainerCreateRequest(
                userId = userId,
                firstName = firstName.value.takeIf { it.isNotBlank() },
                lastName = lastName.value.takeIf { it.isNotBlank() },
                sportId = selectedSportIds.value.firstOrNull(),
                about = aboutText.value,
                latitude = latitude.value,
                longitude = longitude.value,
                rate = priceText.value.toIntOrNull(),
                currency = currency.value,
                intensities = selectedIntensities.value.toList(),
                highestEducation = education,
                languages = selectedLanguageCodes.value.map { code ->
                    hardcodedLanguages.find { it.code == code }?.name ?: code
                },
                achievements = achievements.value,
                careerHistory = careerHistory.value.map {
                    CareerHistoryRequest(
                        companyName = it.companyName,
                        from = it.from.format(dateFormatter),
                        to = it.to.format(dateFormatter)
                    )
                },
                featureIds = selectedFeatureIds.value.toList()
            )

            createTrainerUseCase.execute(request).collect { result ->
                _creationResult.value = result
            }
        }
    }
}