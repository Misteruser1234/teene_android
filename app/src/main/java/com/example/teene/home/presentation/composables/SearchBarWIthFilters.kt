package com.example.teene.home.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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

/**
 * Created by 3100lari on 2025/02/14
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithFilters(modifier: Modifier = Modifier)
{
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        var searchQuery by rememberSaveable { mutableStateOf("") }

        SearchBar(
            windowInsets = WindowInsets.ime,
            colors = SearchBarColors(containerColor = Color.White, Color.DarkGray),
            modifier = Modifier
                .then(
                    if (!expanded) Modifier.border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(30.dp)
                    )
                    else Modifier
                )

                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    colors = TextFieldDefaults.colors(),
                    onSearch = { expanded = false },
                    modifier = Modifier.width(250.dp),
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = {
                        Text(
                            "What are you up to today?",
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
            expanded = expanded,
            onExpandedChange = { expanded = it }) {

        }
        Spacer(Modifier.width(10.dp))

        OutlinedIconButton(
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.LightGray),
            onClick = {}
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search_filter),
                contentDescription = "Localized " +
                    "description",
                tint = Color.DarkGray
            )
        }
        Spacer(Modifier.width(10.dp))
        FilledIconButton(
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Black),
            onClick = {}
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_ask_ai),
                contentDescription = "Localized " +
                    "description",
                tint = Color.White
            )
        }

    }
}
//}

@Preview
@Composable
fun SearchBarWithFiltersPreview()
{
    SearchBarWithFilters()
}