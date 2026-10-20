package com.example.tenee.inbox.presentation

import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tenee.home.presentation.composables.CommonHeader
import com.example.tenee.ui.animations.AuthorizationNavigationAnimations
import com.quickblox.android_ui_kit.domain.entity.DialogEntity
import com.quickblox.android_ui_kit.presentation.screens.chat.group.GroupChatFragment
import com.quickblox.android_ui_kit.presentation.screens.chat.group.GroupChatScreenSettings
import com.quickblox.android_ui_kit.presentation.screens.chat.individual.PrivateChatFragment
import com.quickblox.android_ui_kit.presentation.screens.chat.individual.PrivateChatScreenSettings
import com.quickblox.android_ui_kit.presentation.screens.dialogs.DialogsFragment
import com.quickblox.android_ui_kit.presentation.screens.dialogs.DialogsScreenSettings
import com.quickblox.chat.model.QBDialogType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Inbox screen — shows QuickBlox dialogs using the QuickBlox UI Kit.
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun InboxScreen(
    navigator: DestinationsNavigator,
    navController: NavController,
    trainerQuickbloxId: Int? = null
) {
    val viewModel: InboxViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle reset signal from bottom bar
    val resetInbox = navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("reset_inbox")
    LaunchedEffect(resetInbox) {
        if (resetInbox == true) {
            viewModel.closeDialog()
            navController.currentBackStackEntry?.savedStateHandle?.set("reset_inbox", false)
        }
    }

    // If we were navigated here with a trainer's QuickBlox ID, start (or open)
    // a private dialog with that trainer once the chat connection is up.
    LaunchedEffect(trainerQuickbloxId, state.isConnected) {
        if (trainerQuickbloxId != null && state.isConnected && state.selectedDialog == null) {
            viewModel.startDialogWith(trainerQuickbloxId)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CommonHeader("Inbox")

        Box(modifier = Modifier.weight(1f)) {
            if (state.isLoading) {
                LoadingState()
            } else {
                AndroidView(
                    factory = { ctx ->
                        FragmentContainerView(ctx).apply {
                            id = View.generateViewId()
                        }
                    },
                    update = { containerView ->
                        val activity = context as? FragmentActivity
                        val fragmentManager = activity?.supportFragmentManager ?: return@AndroidView

                        val currentDialog = state.selectedDialog
                        val tag = if (currentDialog == null) "DIALOGS" else "CHAT_${currentDialog.dialogId}"

                        val existingFragment = fragmentManager.findFragmentByTag(tag)
                        if (existingFragment != null) return@AndroidView

                        val fragment = if (currentDialog == null) {
                            val settings = DialogsScreenSettings.Builder(context)
                                .showSearch(false)
                                .showHeader(false)
                                .build()
                            DialogsFragment.newInstance(settings)
                        } else {
                            val dialogId = currentDialog.dialogId
                                val settings = PrivateChatScreenSettings.Builder(context).build()
                                settings.getHeaderComponent()?.setLeftButtonClickListener {
                                    viewModel.closeDialog()
                                }
                                PrivateChatFragment.newInstance(dialogId, settings)
                        }

                        fragmentManager.beginTransaction()
                            .replace(containerView.id, fragment, tag)
                            .commit()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
