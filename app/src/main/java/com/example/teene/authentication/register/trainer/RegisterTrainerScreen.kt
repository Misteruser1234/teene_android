package com.example.teene.authentication.register.trainer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LastBaseline
import com.example.teene.authentication.AuthenticationNavGraph
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.example.teene.ui.composables.ChipOption
import com.example.teene.ui.composables.MultiSelectChips
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.home.presentation.icons.sportIconPainter
import java.util.Currency
import java.util.Locale
import com.ramcosta.composedestinations.generated.destinations.TrainerYourCareerScreenDestination

// UI model for sport suggestions (id and display name only; no image URL)
data class SportSuggestionUi(val id: Int, val name: String)

// UI model for language suggestions
private data class LanguageSuggestionUi(val code: String, val name: String)

/**
 * Trainer registration screen following the spec:
 * - Scrollable content
 * - Header with back button and title
 * - Description
 * - Image upload placeholders (UI only, max 4)
 * - Sport picker (search-like field)
 * - About (multi-line)
 * - Price per training (numeric)
 * - Features chips (from API)
 * - Highest education (3 checkboxes)
 * - Language picker (popular languages)
 * - Bottom Next button (no icon)
 */
@Destination<AuthenticationNavGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun RegisterTrainerScreen(
    navigator: DestinationsNavigator? = null
) {
    // Images & actions used by the grid (runtime wiring using Koin VMs)
    val imagesVm = org.koin.androidx.compose.koinViewModel<RegisterTrainerImagesViewModel>()
    val imagesState = imagesVm.images.collectAsStateWithLifecycle().value

    val pickMedia = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia(
            4
        ),
        onResult = { uris ->
            if (!uris.isNullOrEmpty()) {
                imagesVm.pick(uris)
            }
        }
    )
    val onAddClick = {
        pickMedia.launch(
            androidx.activity.result.PickVisualMediaRequest(
                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }
    val onRemoveClick: (Int) -> Unit = { index -> imagesVm.removeAt(index) }
    val onRetryClick: (Int) -> Unit = { index -> imagesVm.retryUpload(index) }

    val activity = androidx.compose.ui.platform.LocalContext.current as androidx.activity.ComponentActivity
    val registerVm = org.koin.androidx.compose.koinViewModel<RegisterTrainerViewModel>(viewModelStoreOwner = activity)

    // Bind state from ViewModel
    val aboutText by registerVm.aboutText.collectAsStateWithLifecycle()
    val priceText by registerVm.priceText.collectAsStateWithLifecycle()
    val selectedFeatureIds by registerVm.selectedFeatureIds.collectAsStateWithLifecycle()
    val eduDegree by registerVm.eduDegree.collectAsStateWithLifecycle()
    val eduCourse by registerVm.eduCourse.collectAsStateWithLifecycle()
    val eduNone by registerVm.eduNone.collectAsStateWithLifecycle()
    val selectedIntensities by registerVm.selectedIntensities.collectAsStateWithLifecycle()

    val features = registerVm.features.collectAsStateWithLifecycle().value
    val featureItems = features.map { ChipOption(key = it.id, label = it.name) }

    // Sport search state + suggestions from VM
    val sportQuery = registerVm.sportQuery.collectAsStateWithLifecycle().value
    val selectedSports = registerVm.selectedSports.collectAsStateWithLifecycle().value
    val suggestionsDetailed = registerVm.suggestionsDetailed.collectAsStateWithLifecycle().value
    val sportSuggestions: List<SportSuggestionUi> =
        suggestionsDetailed.map { SportSuggestionUi(id = it.id, name = it.name) }

    // Next click behavior
    var isUploading by rememberSaveable { mutableStateOf(false) }
    var hadUploadErrors by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val onNextClick: () -> Unit = {
        if (!isUploading) {
            isUploading = true
            hadUploadErrors = false
            scope.launch {
                val success = imagesVm.uploadAllAndReturnResult()
                isUploading = false
                hadUploadErrors = !success
                if (success) {
                    navigator?.navigate(TrainerYourCareerScreenDestination)
                }
            }
        }
    }

    // Languages search + selections (via VM; hardcoded list inside VM)
    val languageQuery = registerVm.languagesQuery.collectAsStateWithLifecycle().value
    val languageSuggestionsVm = registerVm.languageSuggestions.collectAsStateWithLifecycle().value
    val languageSuggestions: List<LanguageSuggestionUi> =
        languageSuggestionsVm.map { LanguageSuggestionUi(code = it.code, name = it.name) }
    val selectedLanguageCodes =
        registerVm.selectedLanguagesCodes.collectAsStateWithLifecycle().value
    val selectedLanguagesVm = registerVm.selectedLanguages.collectAsStateWithLifecycle().value


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
                    text = "Tell customers about yourself",
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

            Spacer(Modifier.height(20.dp))
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

            Spacer(Modifier.height(24.dp))
            // Image upload section
            RequiredLabel(text = "Upload pictures (max 4)", showAsterisk = imagesState.isEmpty())
            Spacer(Modifier.height(8.dp))
            ImageUploaderGrid(
                maxImages = 4,
                items = imagesState,
                onAddClick = onAddClick,
                onRemoveClick = onRemoveClick,
                onRetryClick = onRetryClick
            )

            Spacer(Modifier.height(24.dp))
            // Sports selection (multi-select via search dropdown + chips)
            RequiredLabel(
                text = "Sports",
                showAsterisk = registerVm.selectedSportsIds.collectAsStateWithLifecycle().value.isEmpty()
            )
            Spacer(Modifier.height(8.dp))
            SportDropdownSearch(
                query = sportQuery,
                suggestions = sportSuggestions,
                onQueryChange = { q ->
                    registerVm.updateSuggestionQuery(q)
                },
                onSelect = { item ->
                    registerVm.addSportById(item.id)
                    // Clear query after adding one selection to allow typing next
                    registerVm.updateSuggestionQuery("")
                }
            )
            Spacer(Modifier.height(8.dp))
            // Selected sports chips (removable)
            run {
                val selectedIds = registerVm.selectedSportsIds.collectAsStateWithLifecycle().value
                val selectedItems = selectedSports
                if (selectedItems.isEmpty()) {
                    Text(text = "No sports selected", color = Color(0xFF7A7A7A))
                } else {
                    val chipItems = selectedItems.map { ChipOption(key = it.id, label = it.name) }
                    MultiSelectChips(
                        items = chipItems,
                        selectedKeys = selectedIds,
                        onSelectionChange = { newSet ->
                            // Remove any ID that was toggled off
                            val removed = selectedIds - newSet
                            removed.forEach { id -> registerVm.removeSport(id) }
                        },
                        maxItemsInEachRow = 3
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            // About (multi-line)
            RequiredLabel(text = "About", showAsterisk = aboutText.isBlank())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                value = aboutText,
                onValueChange = { registerVm.aboutText.value = it },
                placeholder = { Text("Tell customers about your experience, approach, and what to expect") },
                label = { Text("About you") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color(0xFF7A7A7A),
                    unfocusedBorderColor = Color(0xFFF0F0F0),
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black
                )
            )

            Spacer(Modifier.height(24.dp))
            // Price per training
            RequiredLabel(text = "Price per training", showAsterisk = priceText.isBlank())
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min), // row matches tallest child (the TextField)
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f), // optional: let it take available width
                    value = priceText,
                    onValueChange = {
                        registerVm.priceText.value = it.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }
                    },
                    placeholder = { Text("Type your price here") },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color(0xFF7A7A7A),
                        unfocusedBorderColor = Color(0xFFF0F0F0),
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black
                    )
                )

                val currency: Currency = Currency.getInstance(Locale.getDefault())
                val unitText = "${currency.symbol} / Training" // symbol is usually what you want

                // Make the right side match the TextField height
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 8.dp)
                         // take the Row’s height (same as TextField now)
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(modifier = Modifier.padding(horizontal = 16.dp),
                        text = unitText,
                        color = Color.Black,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            // Features chips
            RequiredLabel(text = "Features", showAsterisk = selectedFeatureIds.isEmpty())
            Spacer(Modifier.height(8.dp))
            if (featureItems.isEmpty()) {
                Text(text = "No features available", color = Color.DarkGray)
            } else {
                MultiSelectChips(
                    items = featureItems,
                    selectedKeys = selectedFeatureIds,
                    onSelectionChange = { new -> registerVm.selectedFeatureIds.value = new },
                    maxItemsInEachRow = 3
                )
            }

            Spacer(Modifier.height(24.dp))
            // Training Intensity
            RequiredLabel(text = "Training Intensity", showAsterisk = selectedIntensities.isEmpty())
            Spacer(Modifier.height(8.dp))
            MultiSelectChips(
                items = listOf(
                    ChipOption(key = "Low", label = "Low"),
                    ChipOption(key = "Moderate", label = "Moderate"),
                    ChipOption(key = "High", label = "High")
                ),
                selectedKeys = selectedIntensities,
                onSelectionChange = { new -> registerVm.selectedIntensities.value = new },
                maxItemsInEachRow = 3
            )

            Spacer(Modifier.height(24.dp))
            // Highest education
            RequiredLabel(
                text = "Highest education",
                showAsterisk = !(eduDegree || eduCourse || eduNone)
            )
            Spacer(Modifier.height(8.dp))
            EducationCheckboxRow(
                checked = eduDegree,
                onCheckedChange = {
                    registerVm.eduDegree.value = it
                    if (it) {
                        registerVm.eduCourse.value = false; registerVm.eduNone.value = false
                    }
                },
                label = "Completed a degree in sport or higher"
            )
            EducationCheckboxRow(
                checked = eduCourse,
                onCheckedChange = {
                    registerVm.eduCourse.value = it
                    if (it) {
                        registerVm.eduDegree.value = false; registerVm.eduNone.value = false
                    }
                },
                label = "Completed a specific course in a sport"
            )
            EducationCheckboxRow(
                checked = eduNone,
                onCheckedChange = {
                    registerVm.eduNone.value = it
                    if (it) {
                        registerVm.eduDegree.value = false; registerVm.eduCourse.value = false
                    }
                },
                label = "No sporting education"
            )

            Spacer(Modifier.height(24.dp))
            // Languages (search + chips)
            RequiredLabel(text = "Languages", showAsterisk = selectedLanguageCodes.isEmpty())
            Spacer(Modifier.height(8.dp))
            LanguageDropdownSearch(
                query = languageQuery,
                suggestions = languageSuggestions,
                onQueryChange = { q -> registerVm.updateLanguageQuery(q) },
                onSelect = { item ->
                    registerVm.addLanguageByCode(item.code)
                    registerVm.updateLanguageQuery("")
                }
            )
            Spacer(Modifier.height(8.dp))
            run {
                val chipItems =
                    selectedLanguagesVm.map { LanguageSuggestionUi(code = it.code, name = it.name) }
                if (chipItems.isEmpty()) {
                    Text(text = "No languages selected", color = Color(0xFF7A7A7A))
                } else {
                    MultiSelectChips(
                        items = chipItems.map { ChipOption(key = it.code, label = it.name) },
                        selectedKeys = selectedLanguageCodes,
                        onSelectionChange = { newSet ->
                            val removed = selectedLanguageCodes - newSet
                            removed.forEach { code -> registerVm.removeLanguage(code) }
                        },
                        maxItemsInEachRow = 3
                    )
                }
            }
        }

        // Bottom full-width button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            if (hadUploadErrors) {
                Text(
                    text = "Some images failed to upload. Please retry failed items or try again.",
                    color = Color(0xFFB00020), // error red
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(4.dp))
            }
            com.example.teene.authentication.composables.ButtonWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                iconId = null,
                text = if (isUploading) "Uploading…" else "Next",
                enabled = !isUploading
            ) {
                onNextClick()
            }
        }
    }
}

@Composable
private fun ImageUploaderGrid(
    maxImages: Int,
    items: List<RegisterTrainerImagesViewModel.ImageUi>,
    onAddClick: () -> Unit,
    onRemoveClick: (Int) -> Unit,
    onRetryClick: (Int) -> Unit,
) {
    val totalSlots = maxImages

    Column(modifier = Modifier.fillMaxWidth()) {
        // Helper to draw a single slot with required layout
        @Composable
        fun Slot(index: Int) {
            val hasItem = index < items.size
            val baseModifier = Modifier
                .height(120.dp)
                .weight(1f)
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))

            Box(
                modifier = if (index < totalSlots) {
                    if (hasItem) {
                        baseModifier.border(
                            BorderStroke(1.dp, Color(0xFF009DC3)),
                            RoundedCornerShape(8.dp)
                        )
                    } else {
                        baseModifier
                            .clickable { onAddClick() }
                            .border(BorderStroke(1.dp, Color(0xFFE8E7E5)), RoundedCornerShape(8.dp))
                    }
                } else {
                    // safety fallback (shouldn't happen for even maxImages)
                    baseModifier
                },
                contentAlignment = Alignment.Center
            ) {
                if (hasItem) {
                    val item = items[index]
                    val model: Any? = item.remoteUrl ?: item.localUri
                    if (model != null) {
                        coil3.compose.AsyncImage(
                            model = model,
                            contentDescription = "Selected image",
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }

                    when (item.status) {
                        RegisterTrainerImagesViewModel.ImageUi.Status.Uploading -> {
                            Text(
                                text = "Uploading…",
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color(0x80000000), RoundedCornerShape(8.dp))
                                    .padding(4.dp)
                            )
                        }

                        RegisterTrainerImagesViewModel.ImageUi.Status.Error -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Failed",
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color(0x80FF0000), RoundedCornerShape(8.dp))
                                        .padding(4.dp)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Retry",
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color(0x80009DC3), RoundedCornerShape(4.dp))
                                        .clickable { onRetryClick(index) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        else -> Unit
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .size(18.dp)
                            .background(Color.White, RoundedCornerShape(9.dp))
                            .border(BorderStroke(1.dp, Color(0xFFE8E7E5)), RoundedCornerShape(9.dp))
                            .clickable { onRemoveClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Close,
                            contentDescription = "Remove image",
                            tint = Color.Black,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                } else if (index < totalSlots) {
                    Text(text = "+", color = Color.Black)
                }
            }
        }

        val rows = (totalSlots + 1) / 2
        var currentIndex = 0
        repeat(rows) { rowIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Slot(currentIndex)
                Spacer(Modifier.width(12.dp))
                currentIndex++
                if (currentIndex < totalSlots) {
                    Slot(currentIndex)
                } else {
                    // Filler to keep two columns layout when odd count
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                    )
                }
                currentIndex++
            }
            if (rowIndex < rows - 1) Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SportDropdownSearch(
    query: String,
    suggestions: List<SportSuggestionUi>,
    onQueryChange: (String) -> Unit,
    onSelect: (SportSuggestionUi) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.material3.ExposedDropdownMenuBox(
        expanded = expanded && suggestions.isNotEmpty(),
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryEditable),
            value = query,
            onValueChange = { q ->
                onQueryChange(q)
                expanded =
                    true // open while typing; menu content decides visibility via suggestions
            },
            placeholder = { Text("Search sport...") },
            trailingIcon = {
                androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && suggestions.isNotEmpty())
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color(0xFF7A7A7A),
                unfocusedBorderColor = Color(0xFFF0F0F0),
                focusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black
            )
        )

        androidx.compose.material3.DropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .exposedDropdownSize(true)
        ) {
            suggestions.forEach { item ->
                androidx.compose.material3.DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = sportIconPainter(item.name),
                                contentDescription = item.name,
                                tint = Color(0xFF555555),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(text = item.name, color = Color.Black)
                        }
                    },
                    onClick = {
                        onSelect(item)
                        // Let caller decide what to do with the query (e.g., clear it for multi-select)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageDropdownSearch(
    query: String,
    suggestions: List<LanguageSuggestionUi>,
    onQueryChange: (String) -> Unit,
    onSelect: (LanguageSuggestionUi) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && suggestions.isNotEmpty(),
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryEditable),
            value = query,
            onValueChange = { q ->
                onQueryChange(q)
                expanded = true
            },
            placeholder = { Text("Search language...") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && suggestions.isNotEmpty())
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color(0xFF7A7A7A),
                unfocusedBorderColor = Color(0xFFF0F0F0),
                focusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black
            )
        )

        androidx.compose.material3.DropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            suggestions.forEach { item ->
                androidx.compose.material3.DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Languages: show plain text (no icon), keep spacing consistent
                            Text(text = item.name, color = Color.Black)
                        }
                    },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EducationCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(Modifier.size(8.dp))
        Text(text = label)
    }
}

@Composable
private fun RequiredLabel(
    text: String,
    showAsterisk: Boolean,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = text, style = style, color = Color.Black)
        if (showAsterisk) {
            Text(text = " *", style = style, color = Color(0xFFB00020))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun RegisterTrainerScreenPreview() {
    RegisterTrainerScreen(navigator = null)
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
private fun RegisterTrainerScreenSmallPreview() {
    RegisterTrainerScreen(navigator = null)
}


@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    name = "SportDropdownSearch - Empty"
)
@Composable
private fun SportDropdownSearchPreview_Empty() {
    var query by remember { mutableStateOf("") }
    val allSuggestions = listOf(
        SportSuggestionUi(id = 1, name = "Padel"),
        SportSuggestionUi(id = 2, name = "Basketball"),
        SportSuggestionUi(id = 3, name = "Boxing"),
        SportSuggestionUi(id = 4, name = "Pilates"),
        SportSuggestionUi(id = 5, name = "Badminton"),
        SportSuggestionUi(id = 6, name = "Parkour")
    )
    val filtered = allSuggestions.filter { it.name.contains(query, ignoreCase = true) }

    Column(modifier = Modifier.padding(16.dp)) {
        SportDropdownSearch(
            query = query,
            suggestions = filtered,
            onQueryChange = { query = it },
            onSelect = { query = it.name },
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    name = "SportDropdownSearch - With Query"
)
@Composable
private fun SportDropdownSearchPreview_WithQuery() {
    var query by remember { mutableStateOf("Ba") }
    val allSuggestions = listOf(
        SportSuggestionUi(id = 1, name = "Padel"),
        SportSuggestionUi(id = 2, name = "Basketball"),
        SportSuggestionUi(id = 3, name = "Boxing"),
        SportSuggestionUi(id = 4, name = "Pilates"),
        SportSuggestionUi(id = 5, name = "Badminton"),
        SportSuggestionUi(id = 6, name = "Parkour")
    )
    val filtered = allSuggestions.filter { it.name.contains(query, ignoreCase = true) }

    Column(modifier = Modifier.padding(16.dp)) {
        SportDropdownSearch(
            query = query,
            suggestions = filtered,
            onQueryChange = { query = it },
            onSelect = { query = it.name },
        )
    }
}
