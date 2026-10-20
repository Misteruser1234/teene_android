package com.example.tenee

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tenee.ui.theme.TeneeTheme
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.tenee.authentication.AppleSignInHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            TeneeTheme {
                enableEdgeToEdge()
                TeneeApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "tenee" && uri.host == "auth" && uri.path == "/callback") {
                val token = uri.getQueryParameter("token")
                val appleUserId = uri.getQueryParameter("apple_user_id")
                val identityToken = uri.getQueryParameter("identity_token")
                val firstName = uri.getQueryParameter("first_name")
                val lastName = uri.getQueryParameter("last_name")

                // Google now uses the native Credential Manager flow (no deep link).
                // The deep-link callback is only used by the Apple web OAuth flow.
                lifecycleScope.launch {
                    AppleSignInHelper.onAuthReceived(
                        appleUserId = appleUserId,
                        identityToken = identityToken,
                        firstName = firstName,
                        lastName = lastName,
                        token = token
                    )
                }
            }
        }
    }
}

