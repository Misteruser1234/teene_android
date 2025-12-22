package com.example.teene.ui.composables

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.teene.authentication.composables.SelectableButton

/**
 * Simple option model used by [MultiSelectChips].
 */
data class ChipOption<K>(
    val key: K,
    val label: String,
    val enabled: Boolean = true
)

/**
 * Reusable multi-select chip grid, wrapping items across rows using [FlowRow].
 *
 * - Tapping a chip toggles its selection state.
 * - Disabled chips are not interactive and render with `isEnabled = false`.
 * - You can limit the number of selections via [selectionLimit] (null = unlimited).
 * - Uses the project-wide `SelectableButton` for visuals to keep consistency.
 *
 * Typical usage:
 * ```kotlin
 * val (selected, setSelected) = rememberMultiSelectionState<String>()
 * MultiSelectChips(
 *     items = timeSlots.map { ChipOption(key = it.time, label = it.time, enabled = it.isAvailable) },
 *     selectedKeys = selected,
 *     onSelectionChange = setSelected,
 *     maxItemsInEachRow = 3
 * )
 * ```
 */
@Composable
fun <K> MultiSelectChips(
    items: List<ChipOption<K>>,
    selectedKeys: Set<K>,
    onSelectionChange: (Set<K>) -> Unit,
    modifier: Modifier = Modifier,
    chipModifier: Modifier = Modifier,
    maxItemsInEachRow: Int = 3,
    selectionLimit: Int? = null,
    chipHorizontalPadding: Dp = 4.dp
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = false) {},
        maxItemsInEachRow = maxItemsInEachRow,
        // Align chips in the center like current BookScreen
        // Note: FlowRow from foundation has limited alignment params; using default spacing.
    ) {
        items.forEach { option ->
            val isSelected = selectedKeys.contains(option.key)

            val canSelectMore = selectionLimit == null || selectedKeys.size < selectionLimit || isSelected

            SelectableButton(
                modifier = chipModifier
                    .fillMaxWidth(1f / maxItemsInEachRow)
                    .padding(chipHorizontalPadding)
                    .semantics(mergeDescendants = true) {
                        selected = isSelected
                        this.role = Role.Checkbox
                    },
                text = option.label,
                isSelected = isSelected,
                isEnabled = option.enabled && canSelectMore,
                onClick = {
                    if (!option.enabled) return@SelectableButton

                    if (isSelected) {
                        onSelectionChange(selectedKeys - option.key)
                    } else if (canSelectMore) {
                        onSelectionChange(selectedKeys + option.key)
                    }
                }
            )
        }
    }
}

/**
 * Convenience overload for a simple list of Strings where the key is the label.
 */
@Composable
fun MultiSelectChips(
    labels: List<String>,
    selectedLabels: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    chipModifier: Modifier = Modifier,
    enabledPredicate: (String) -> Boolean = { true },
    maxItemsInEachRow: Int = 3,
    selectionLimit: Int? = null,
    chipHorizontalPadding: Dp = 4.dp
) {
    val items = remember(labels) {
        labels.map { label -> ChipOption(key = label, label = label, enabled = enabledPredicate(label)) }
    }
    MultiSelectChips(
        items = items,
        selectedKeys = selectedLabels,
        onSelectionChange = onSelectionChange,
        modifier = modifier,
        chipModifier = chipModifier,
        maxItemsInEachRow = maxItemsInEachRow,
        selectionLimit = selectionLimit,
        chipHorizontalPadding = chipHorizontalPadding
    )
}

/**
 * Lightweight state holder for multi-selection.
 */
@Composable
fun <K> rememberMultiSelectionState(initial: Set<K> = emptySet()): Pair<Set<K>, (Set<K>) -> Unit> {
    val state: MutableState<Set<K>> = remember { mutableStateOf(initial) }
    return state.value to { new -> state.value = new }
}


@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun MultiSelectChipsPreview_Multiple() {
    val all = listOf("08:00", "08:30", "09:00", "09:30", "10:00", "10:30")
    val (selected, setSelected) = rememberMultiSelectionState<String>()
    MultiSelectChips(
        labels = all,
        selectedLabels = selected,
        onSelectionChange = setSelected,
        maxItemsInEachRow = 3,
        enabledPredicate = { it != "09:30" } // example disabled chip
    )
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun MultiSelectChipsPreview_SingleSelect() {
    val options = listOf(
        ChipOption(key = 1, label = "Beginner"),
        ChipOption(key = 2, label = "Intermediate"),
        ChipOption(key = 3, label = "Advanced", enabled = false)
    )
    val (selected, setSelected) = rememberMultiSelectionState<Int>()
    MultiSelectChips(
        items = options,
        selectedKeys = selected,
        onSelectionChange = setSelected,
        maxItemsInEachRow = 3,
        selectionLimit = 1
    )
}
