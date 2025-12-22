package com.example.teene.home.presentation.composables
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.home.presentation.icons.sportIconPainter
import com.example.teene.home.presentation.models.SportUi

@Composable
fun SportsHorizontalSelector(
    sports: List<SportUi>,
    selectedSportId: Int,
    onSportClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (sports.isEmpty()) return

    // Colors aligned with BottomBar items
    val selectedColor = Color.Black
    val unselectedColor = Color(0xFF9E9E9E)

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(sports) { sport ->
            val isSelected = sport.id == selectedSportId
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { onSportClick(sport.id) }
            ) {
                Card(
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = if (isSelected) BorderStroke(1.dp, selectedColor) else BorderStroke(1.dp, Color(0xFFF0F0F0))
                ) {
                    Icon(
                        painter = sportIconPainter(sport.name),
                        contentDescription = sport.name,
                        tint = if (isSelected) selectedColor else unselectedColor,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = sport.name,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) selectedColor else unselectedColor
                )
            }
        }
    }
}