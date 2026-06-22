package com.example.teene.inbox.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Inbox screen — lists QuickBlox dialogs and shows a basic chat view
 * when one is opened. The screen relies on a server-issued QuickBlox
 * session token stored in [com.example.teene.data.UserDataStore].
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun InboxScreen(
    navigator: DestinationsNavigator,
    trainerQuickbloxId: Int? = null
) {
    val viewModel: InboxViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // If we were navigated here with a trainer's QuickBlox ID, start (or open)
    // a private dialog with that trainer once the chat connection is up.
    androidx.compose.runtime.LaunchedEffect(trainerQuickbloxId, state.isConnected) {
        if (trainerQuickbloxId != null && state.isConnected && state.selectedDialog == null) {
            viewModel.startDialogWith(trainerQuickbloxId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingState()
            state.selectedDialog != null -> ChatView(
                dialog = state.selectedDialog!!,
                messages = state.messages,
                isSending = state.isSending,
                onBack = viewModel::closeDialog,
                onSend = viewModel::sendMessage
            )
            else -> DialogsList(
                isConnected = state.isConnected,
                dialogs = state.dialogs,
                error = state.error,
                onDialogClick = viewModel::openDialog,
                onRetry = viewModel::connectAndLoad
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DialogsList(
    isConnected: Boolean,
    dialogs: List<QBChatDialog>,
    error: String?,
    onDialogClick: (QBChatDialog) -> Unit,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Inbox",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        if (error != null) {
            Text(
                text = error,
                color = Color(0xFFB00020),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onRetry() }
            )
            Spacer(Modifier.height(8.dp))
        }
        if (dialogs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isConnected) "You don't have any messages yet."
                    else "Connecting to chat…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(dialogs, key = { it.dialogId ?: it.hashCode().toString() }) { dialog ->
                    DialogRow(dialog = dialog, onClick = { onDialogClick(dialog) })
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun DialogRow(dialog: QBChatDialog, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = dialog.name?.takeIf { it.isNotBlank() } ?: "Chat ${dialog.dialogId ?: ""}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        val last = dialog.lastMessage
        if (!last.isNullOrBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = last,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ChatView(
    dialog: QBChatDialog,
    messages: List<QBChatMessage>,
    isSending: Boolean,
    onBack: () -> Unit,
    onSend: (String) -> Unit
) {
    var draft by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = dialog.name?.takeIf { it.isNotBlank() } ?: "Chat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Divider()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            reverseLayout = false
        ) {
            items(messages, key = { it.id ?: it.hashCode().toString() }) { msg ->
                MessageRow(msg)
            }
        }
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") },
                enabled = !isSending
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val text = draft
                    if (text.isNotBlank() && !isSending) {
                        draft = ""
                        onSend(text)
                    }
                },
                enabled = !isSending && draft.isNotBlank()
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
private fun MessageRow(msg: QBChatMessage) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            text = msg.body.orEmpty(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "from ${msg.senderId ?: "?"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
