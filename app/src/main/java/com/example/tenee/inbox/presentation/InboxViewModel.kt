package com.example.tenee.inbox.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tenee.inbox.data.InboxRepository
import com.example.tenee.inbox.data.QuickbloxManager
import com.quickblox.chat.model.QBChatDialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InboxViewModel(
    private val quickbloxManager: QuickbloxManager,
    private val repository: InboxRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val isConnected: Boolean = false,
        val error: String? = null,
        val selectedDialog: QBChatDialog? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        connectAndLoad()
    }

    fun connectAndLoad() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            quickbloxManager.init()
            val connectResult = quickbloxManager.connectChat()
            if (connectResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isConnected = false,
                    error = connectResult.exceptionOrNull()?.message
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isConnected = true
            )
        }
    }

    fun openDialog(dialog: QBChatDialog) {
        _uiState.value = _uiState.value.copy(selectedDialog = dialog)
    }

    fun openDialogById(dialogId: String, type: com.quickblox.chat.model.QBDialogType) {
        val dialog = QBChatDialog(dialogId)
        dialog.type = type
        _uiState.value = _uiState.value.copy(selectedDialog = dialog)
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(selectedDialog = null)
    }

    fun startDialogWith(trainerQuickbloxId: Int) {
        viewModelScope.launch {
            runCatching { quickbloxManager.runWithSessionRetry { repository.createPrivateDialog(trainerQuickbloxId) } }
                .onSuccess { dialog ->
                    openDialog(dialog)
                }
                .onFailure { t ->
                    _uiState.value = _uiState.value.copy(error = t.message)
                }
        }
    }
}
