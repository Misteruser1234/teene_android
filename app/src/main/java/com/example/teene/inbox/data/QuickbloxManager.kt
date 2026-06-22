package com.example.teene.inbox.data

import android.content.Context
import android.os.Bundle
import android.util.Log
import UsersRepository
import com.example.teene.data.UserDataStore
import com.quickblox.auth.session.QBSession
import com.quickblox.auth.session.QBSessionManager
import com.quickblox.auth.session.QBSettings
import com.quickblox.chat.QBChatService
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Centralizes QuickBlox SDK lifecycle:
 *  - one-time SDK init with our app credentials,
 *  - restoring an existing server-issued session token,
 *  - connecting the chat XMPP service for the logged-in user.
 *
 * The session token comes from the Tenee backend (`authorization.quickblox_session_token`),
 * so we never embed the auth secret in the app at runtime.
 */
class QuickbloxManager(
    private val appContext: Context,
    private val userDataStore: UserDataStore,
    private val usersRepository: UsersRepository
) {

    private val _state = MutableStateFlow(State.IDLE)
    val state: StateFlow<State> = _state

    enum class State { IDLE, INITIALIZED, CHAT_CONNECTED, ERROR }

    /** Initialize the SDK. Safe to call multiple times. */
    fun init() {
        QBSettings.getInstance().init(appContext, APPLICATION_ID, AUTH_KEY, AUTH_SECRET)
        QBSettings.getInstance().accountKey = ACCOUNT_KEY
        if (_state.value == State.IDLE) _state.value = State.INITIALIZED
    }

    /**
     * Restore the QuickBlox session using a token previously issued by our backend
     * (returned in `authorization.quickblox_session_token`).
     *
     * `QBSessionParameters` in the SDK is read-only (getters only), so we cannot
     * build one directly. Instead we construct a `QBSession`, populate it with the
     * server-issued token + the QB user id, mark it as manually created, and
     * register it as the active session on `QBSessionManager`.
     */
    fun restoreSession(token: String, quickbloxUserId: Int) {
        val session = QBSession().apply {
            setUserId(quickbloxUserId)
            setToken(token)
            setManuallyCreated(true)
        }
        val manager = QBSessionManager.getInstance()
        manager.setActiveSession(session, true)
        manager.setToken(token)
        Log.d(TAG, "QuickBlox session restored for userId=$quickbloxUserId")
    }

    /**
     * Connect to QuickBlox chat (XMPP) for the current user.
     * The password sent here is the QuickBlox session token (per QB recommendation
     * when the app does not store the user's QB password).
     */
    suspend fun connectChat(): Result<Unit> = runCatching {
        val token = userDataStore.quickbloxSessionTokenFlow.first()
            ?: error("No QuickBlox session token available")
        val qbId = userDataStore.quickbloxIdFlow.first()
            ?: error("No QuickBlox user id available")

        // Make REST calls authenticated using the server-issued QB session token.
        restoreSession(token, qbId)

        val user = QBUser().apply {
            id = qbId
            password = token
        }

        val chatService = QBChatService.getInstance()
        if (chatService.isLoggedIn) {
            _state.value = State.CHAT_CONNECTED
            return@runCatching
        }

        suspendCancellableCoroutine<Unit> { cont ->
            chatService.login(user, object : QBEntityCallback<Void> {
                override fun onSuccess(aVoid: Void?, bundle: Bundle?) {
                    _state.value = State.CHAT_CONNECTED
                    cont.resume(Unit)
                }

                override fun onError(exception: QBResponseException?) {
                    _state.value = State.ERROR
                    cont.resumeWithException(
                        exception ?: RuntimeException("Unknown QB chat login error")
                    )
                }
            })
        }
    }

    fun disconnectChat() {
        runCatching { QBChatService.getInstance().logout() }
        _state.value = State.INITIALIZED
    }

    /**
     * Calls the Tenee backend to refresh the QuickBlox session token, persists
     * the new token + qb id, restores the SDK session, and reconnects chat.
     *
     * Returns true on success, false otherwise.
     */
    suspend fun refreshSession(): Boolean {
        return try {
            val result = usersRepository.refreshQuickbloxSession().first()
            val body = result.getOrNull() ?: return false
            val newToken = body.authorization.quickbloxSessionToken ?: return false
            val newQbId = body.quickbloxId ?: return false
            userDataStore.saveQuickbloxSessionToken(newToken)
            userDataStore.saveQuickbloxId(newQbId)
            restoreSession(newToken, newQbId)
            // Reconnect XMPP chat with the fresh token.
            runCatching { QBChatService.getInstance().logout() }
            connectChat().isSuccess
        } catch (t: Throwable) {
            Log.w(TAG, "refreshSession failed", t)
            false
        }
    }

    /**
     * Runs the given block; if it fails with what looks like an expired/unauthorized
     * QuickBlox session, refreshes the session via the backend and retries once.
     */
    suspend fun <T> runWithSessionRetry(block: suspend () -> T): T {
        return try {
            block()
        } catch (t: Throwable) {
            if (isSessionExpired(t) && refreshSession()) {
                block()
            } else {
                throw t
            }
        }
    }

    private fun isSessionExpired(t: Throwable): Boolean {
        if (t is QBResponseException) {
            // QB returns HTTP 401 / 422 for expired or invalid token.
            // The SDK exposes errors as message strings, so check defensively.
            val msg = t.message?.lowercase().orEmpty()
            if ("token" in msg && ("expired" in msg || "invalid" in msg || "unauthorized" in msg)) {
                return true
            }
        }
        val msg = t.message?.lowercase().orEmpty()
        return "401" in msg || "unauthorized" in msg || "token has expired" in msg
    }

    companion object {
        private const val TAG = "QuickbloxManager"

        // Tenee app credentials
        private const val APPLICATION_ID = "108045"
        private const val AUTH_KEY = "ak_nF5ZHmpNyDByNbm"
        private const val AUTH_SECRET = "" // Not used at runtime; sessions come from backend.
        private const val ACCOUNT_KEY = "ack_VSrS_FRPkT1BuxWzZMR2"
    }
}
