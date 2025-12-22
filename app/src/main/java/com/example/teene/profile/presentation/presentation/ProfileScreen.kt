package com.example.teene.profile.presentation.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.home.presentation.composables.CommonHeader
import com.example.teene.ui.animations.AuthorizationNavigationAnimations
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * Created by 3100lari on 2025/02/12
 */
@Destination<RootGraph>(style = AuthorizationNavigationAnimations::class)
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator,
    onProfileDetailsClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onPaymentMethodClick: () -> Unit = {},
    onTransactionHistoryClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onPrivacySecurityClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
)
{
    val profileViewModel = koinViewModel<ProfileViewModel>()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header same as Explore
        CommonHeader("Profile")

        val items = listOf(
            "Profile Details" to onProfileDetailsClick,
            "Change Password" to onChangePasswordClick,
            "Payment method" to onPaymentMethodClick,
            "Transaction History" to onTransactionHistoryClick,
            "Language" to onLanguageClick,
            "Support" to onSupportClick,
            "Privacy & Security" to onPrivacySecurityClick,
            "Log Out" to {
                profileViewModel.logout()
                navigator.navigate(LoginScreenDestination)
            },
            "Delete Account" to onDeleteAccountClick,
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            // Top divider above the first item
            item {
                HorizontalDivider(color = Color(0x1A000000))
            }
            itemsIndexed(items) { index, (label, action) ->
                val isDelete = label == "Delete Account"
                SettingsRow(
                    label = label,
                    labelColor = if (isDelete) Color(0xFFC5312B) else Color(0xFF000000),
                    onClick = action
                )
                if (index < items.lastIndex) {
                    HorizontalDivider(color = Color(0x1A000000))
                }
            }
            // Bottom divider below the last item
            item {
                HorizontalDivider(color = Color(0x1A000000))
            }
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    onClick: () -> Unit,
    labelColor: Color = Color(0xFF000000)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = labelColor
            )
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF3F3F3F)
        )
    }
}