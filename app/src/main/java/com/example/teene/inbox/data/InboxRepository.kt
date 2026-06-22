package com.example.teene.inbox.data

import android.os.Bundle
import com.quickblox.chat.QBChatService
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.chat.utils.DialogUtils
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.core.request.QBRequestGetBuilder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Thin wrapper around the QuickBlox REST/Chat APIs used by the Inbox feature.
 *
 * - [getDialogs]      → list current user's dialogs.
 * - [createPrivateDialog] → create / fetch a 1-1 dialog with a recipient (e.g. trainer).
 * - [getDialogMessages]   → fetch message history for a dialog.
 * - [sendMessage]     → send a message into an opened [QBChatDialog].
 */
class InboxRepository {

    suspend fun getDialogs(limit: Int = 100): List<QBChatDialog> =
        suspendCancellableCoroutine { cont ->
            val req = QBRequestGetBuilder().apply {
                this.limit = limit
            }
            QBRestChatService.getChatDialogs(null, req)
                .performAsync(object : QBEntityCallback<ArrayList<QBChatDialog>> {
                    override fun onSuccess(result: ArrayList<QBChatDialog>?, bundle: Bundle?) {
                        cont.resume(result ?: emptyList())
                    }

                    override fun onError(exception: QBResponseException?) {
                        cont.resumeWithException(
                            exception ?: RuntimeException("Failed to load dialogs")
                        )
                    }
                })
        }

    suspend fun createPrivateDialog(recipientQuickbloxId: Int): QBChatDialog =
        suspendCancellableCoroutine { cont ->
            val dialog = DialogUtils.buildPrivateDialog(recipientQuickbloxId)
            QBRestChatService.createChatDialog(dialog)
                .performAsync(object : QBEntityCallback<QBChatDialog> {
                    override fun onSuccess(result: QBChatDialog?, bundle: Bundle?) {
                        if (result != null) cont.resume(result)
                        else cont.resumeWithException(RuntimeException("Empty dialog response"))
                    }

                    override fun onError(exception: QBResponseException?) {
                        cont.resumeWithException(
                            exception ?: RuntimeException("Failed to create dialog")
                        )
                    }
                })
        }

    suspend fun getDialogMessages(dialog: QBChatDialog, limit: Int = 50): List<QBChatMessage> =
        suspendCancellableCoroutine { cont ->
            val req = QBRequestGetBuilder().apply { this.limit = limit }
            QBRestChatService.getDialogMessages(dialog, req)
                .performAsync(object : QBEntityCallback<ArrayList<QBChatMessage>> {
                    override fun onSuccess(result: ArrayList<QBChatMessage>?, bundle: Bundle?) {
                        cont.resume(result ?: emptyList())
                    }

                    override fun onError(exception: QBResponseException?) {
                        cont.resumeWithException(
                            exception ?: RuntimeException("Failed to load messages")
                        )
                    }
                })
        }

    /**
     * Join the dialog (XMPP room for group dialogs) and send a text message.
     * For private dialogs, joining is a no-op.
     */
    suspend fun sendMessage(dialog: QBChatDialog, body: String) =
        suspendCancellableCoroutine<Unit> { cont ->
            val message = QBChatMessage().apply {
                this.body = body
                setSaveToHistory(true)
                dateSent = System.currentTimeMillis() / 1000
            }
            try {
                dialog.initForChat(QBChatService.getInstance())
                dialog.sendMessage(message, object : QBEntityCallback<Void> {
                    override fun onSuccess(aVoid: Void?, bundle: Bundle?) {
                        cont.resume(Unit)
                    }

                    override fun onError(exception: QBResponseException?) {
                        cont.resumeWithException(
                            exception ?: RuntimeException("Failed to send message")
                        )
                    }
                })
            } catch (t: Throwable) {
                cont.resumeWithException(t)
            }
        }
}
