package com.example.tenee.inbox.data

import android.os.Bundle
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.utils.DialogUtils
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Thin wrapper around the QuickBlox REST APIs used by the Inbox feature.
 */
class InboxRepository {

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
}
