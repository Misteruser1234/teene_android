import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.authentication.composables.SelectableButton

@Composable
fun ToggleablePhysicalLevel(onSelectedLevel: (Int) -> Unit = {})
{
    val levels = (1..5).toList()
    var selectedLevelButton by remember { mutableIntStateOf(3) }
    // Defaults to
    Column {
        Text(
            text = "Physical Preparation",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight(500)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            levels.forEachIndexed { index, level ->
                SelectableButton(
                    modifier = Modifier
                        .weight(1f)
                        .width(56.dp)
                        .height(64.dp),
                    text = level.toString(),
                    isSelected = level == selectedLevelButton,
                    onClick = {
                        val newSelection =
                            if (selectedLevelButton == level) 0 else level
                        selectedLevelButton = newSelection
                        onSelectedLevel(selectedLevelButton)

                    }
                )

                if (index < levels.size - 1)
                {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ToggleableLevelPreview()
{
    var selectedLevel by remember { mutableIntStateOf(0) }

    ToggleablePhysicalLevel(onSelectedLevel = {
        Log.i("BOBAN", "selected: $it")
    })
}