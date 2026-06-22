package com.example.teene.inbox.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teene.inbox.data.InboxRepository
import com.example.teene.inbox.data.QuickbloxManager
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
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
        val dialogs: List<QBChatDialog> = emptyList(),
        val error: String? = null,
        val selectedDialog: QBChatDialog? = null,
        val messages: List<QBChatMessage> = emptyList(),
        val isSending: Boolean = false
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
            try {
                val dialogs = quickbloxManager.runWithSessionRetry { repository.getDialogs() }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isConnected = true,
                    dialogs = dialogs
                )
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isConnected = true,
                    error = t.message
                )
            }
        }
    }

    fun openDialog(dialog: QBChatDialog) {
        _uiState.value = _uiState.value.copy(selectedDialog = dialog, messages = emptyList())
        viewModelScope.launch {
            runCatching { quickbloxManager.runWithSessionRetry { repository.getDialogMessages(dialog) } }
                .onSuccess { msgs ->
                    _uiState.value = _uiState.value.copy(messages = msgs)
                }
                .onFailure { t ->
                    _uiState.value = _uiState.value.copy(error = t.message)
                }
        }
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(selectedDialog = null, messages = emptyList())
    }

    fun sendMessage(text: String) {
        val dialog = _uiState.value.selectedDialog ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            runCatching { quickbloxManager.runWithSessionRetry { repository.sendMessage(dialog, text) } }
                .onSuccess {
                    val refreshed = runCatching { quickbloxManager.runWithSessionRetry { repository.getDialogMessages(dialog) } }
                        .getOrDefault(_uiState.value.messages)
                    _uiState.value = _uiState.value.copy(
                        messages = refreshed,
                        isSending = false
                    )
                }
                .onFailure { t ->
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        error = t.message
                    )
                }
        }
    }

    fun startDialogWith(trainerQuickbloxId: Int) {
        viewModelScope.launch {
            runCatching { quickbloxManager.runWithSessionRetry { repository.createPrivateDialog(trainerQuickbloxId) } }
                .onSuccess { dialog ->
                    _uiState.value = _uiState.value.copy(
                        dialogs = (listOf(dialog) + _uiState.value.dialogs).distinctBy { it.dialogId }
                    )
                    openDialog(dialog)
                }
                .onFailure { t ->
                    _uiState.value = _uiState.value.copy(error = t.message)
                }
        }
    }
}
