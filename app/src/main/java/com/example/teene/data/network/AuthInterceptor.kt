package com.example.teene.data.network

import com.example.teene.data.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that attaches the latest Authorization header from TokenManager
 * for every outgoing request. Reads from DataStore at request time to avoid
 * capturing a stale token during DI initialization.
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        // Blocking here is acceptable: OkHttp runs interceptors off the main thread.
        val token = runBlocking { tokenManager.getToken.firstOrNull() }
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}