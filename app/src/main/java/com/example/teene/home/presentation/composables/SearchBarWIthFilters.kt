package com.example.teene.home.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Divider
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.R
import com.example.teene.home.presentation.models.SportSuggestion

/**
 * Created by 3100lari on 2025/02/14
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithFilters(
    modifier: Modifier = Modifier,
    onFilterClick: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    searchResults: List<String> = emptyList(),
    onQueryTyping: (String) -> Unit = {},
    showFilterButton: Boolean = true,
    showAskAiButton: Boolean = true,
    cornerRadius: androidx.compose.ui.unit.Dp = 30.dp,
    placeholder: String = "What are you up to today?",
    suggestionItems: List<SportSuggestion> = emptyList(),
    activeFiltersCount: Int = 0
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var isFullScreen by rememberSaveable { mutableStateOf(false) }
        var searchQuery by rememberSaveable { mutableStateOf("") }

        SearchBar(
            windowInsets = WindowInsets.ime,
            colors = SearchBarColors(containerColor = Color.White, Color.DarkGray),
            modifier = Modifier
                .weight(1f)
                .then(
                    Modifier.border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(cornerRadius)
                    )
                )
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    colors = TextFieldDefaults.colors(),
                    onSearch = {
                        isFullScreen = true
                    },
                    modifier = Modifier,
                    expanded = false,
                    onExpandedChange = { expanded ->
                        if (expanded) isFullScreen = true
                    },
                    placeholder = {
                        Text(
                            placeholder,
                            color = Color.DarkGray,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search, contentDescription = null, tint = Color.DarkGray
                        )
                    },
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                )
            },
            expanded = false,
            onExpandedChange = { expanded ->
                if (expanded) isFullScreen = true
            }
        ) { }

        if (showFilterButton) {
            Spacer(Modifier.width(10.dp))
            Box {
                OutlinedIconButton(
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.LightGray),
                    onClick = onFilterClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_filter),
                        contentDescription = if (activeFiltersCount > 0) "Filters (${activeFiltersCount} active)" else "Filters",
                        tint = Color.DarkGray
                    )
                }
                if (activeFiltersCount > 0) {
                    // Small badge on the top-right of the filter icon
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .background(Color.Black, shape = CircleShape)
                            .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val display = if (activeFiltersCount > 99) "99+" else activeFiltersCount.toString()
                        Text(
                            text = display,
                            color = Color.White,
                            fontSize = 10.sp,
                            maxLines = 1,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = androidx.compose.ui.text.TextStyle(
                                lineHeight = 10.sp,
                                platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false)
                            )
                        )
                    }
                }
            }
        }
        if (showAskAiButton) {
            Spacer(Modifier.width(10.dp))
            FilledIconButton(
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Black),
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_ask_ai),
                    contentDescription = "Localized description",
                    tint = Color.White
                )
            }
        }

        if (isFullScreen) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { isFullScreen = false },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { isFullScreen = false }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                    onQueryTyping(it)
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                placeholder = { Text(placeholder, color = Color.DarkGray) },
                                colors = TextFieldDefaults.colors()
                            )
                        }
                        // Suggestions list (from API) in full-screen mode
                        Column(modifier = Modifier.fillMaxWidth()) {
                            val items: List<Pair<String, Int?>> = if (suggestionItems.isNotEmpty()) {
                                suggestionItems.map { it.name to it.trainerCount }
                            } else {
                                searchResults.map { it to null }
                            }
                            items.forEachIndexed { index, (name, count) ->
                                val trimmedQuery = searchQuery.trim()
                                val annotated = if (trimmedQuery.isBlank()) null else buildAnnotatedString {
                                    val q = trimmedQuery.lowercase()
                                    val lower = name.lowercase()
                                    var start = 0
                                    var idx = if (q.isEmpty()) -1 else lower.indexOf(q, start)
                                    while (idx >= 0) {
                                        append(name.substring(start, idx))
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(name.substring(idx, idx + q.length))
                                        }
                                        start = idx + q.length
                                        idx = lower.indexOf(q, start)
                                    }
                                    append(name.substring(start))
                                }

                                androidx.compose.material3.ListItem(
                                    headlineContent = {
                                        if (annotated != null) Text(annotated) else Text(name)
                                    },
                                    supportingContent = {
                                        if (count != null) {
                                            val label = when (count) {
                                                0 -> "No trainers yet"
                                                1 -> "1 trainer"
                                                else -> "$count trainers"
                                            }
                                            Text(label, color = Color.DarkGray)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchQuery = name
                                            onQueryChange(name)
                                            isFullScreen = false
                                        }
                                )

                                if (index < items.lastIndex) {
                                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
//}

@Preview
@Composable
fun SearchBarWithFiltersPreview() {
    SearchBarWithFilters()
}