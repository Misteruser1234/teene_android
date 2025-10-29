package com.example.teene.home.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Created by 3100lari on 2025/02/13
 */
@Composable
fun TopicsSelectableList(modifier: Modifier = Modifier)
{
    val items = listOf("For you", "Trending", "New", "Nearby")
    var selectedItem by rememberSaveable { mutableStateOf<String?>("For you") }

    LazyRow(modifier = modifier) {
        itemsIndexed(items) { index, item ->
            val isSelected = selectedItem == item
            ListItem(
                text = item,
                isSelected = isSelected,
                onClick = { selectedItem = if (isSelected) null else item }
            )
            if (index < items.size - 1)
            {
                Spacer(modifier = Modifier.width(56.dp))
            }
        }
    }
}

@Composable
fun ListItem(text: String, isSelected: Boolean, onClick: () -> Unit)
{
    val backgroundColor = if (isSelected) Color.Black else Color.LightGray

    Box(
        modifier = Modifier
            .wrapContentSize()
            .clickable { onClick() },
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(text = text, fontSize = 14.sp, color = backgroundColor, lineHeight = 20.sp)
    }
}

@Preview
@Composable
fun PreviewLazySelectableList()
{
    TopicsSelectableList(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
    )
}