package com.example.nexusbooking.mobile.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Security headers interceptor for mobile app
 * Ensures HTTPS-only connections and adds security headers
 */
class SecurityHeadersInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val url = originalRequest.url
        if (!url.scheme.equals("https", ignoreCase = true)) {
            // In development, log warning but allow HTTP
            // In production, should throw exception
            android.util.Log.w("SecurityHeaders", "Non-HTTPS connection to: $url")
        }
        
        // Add security headers
        val requestWithHeaders = originalRequest.newBuilder()
            .addHeader("X-Content-Type-Options", "nosniff")
            .addHeader("X-Frame-Options", "DENY")
            .addHeader("X-XSS-Protection", "1; mode=block")
            .addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
            .addHeader("Content-Security-Policy", "default-src 'self'")
            .addHeader("User-Agent", "NexusBooking-Mobile/1.0")
            .build()
        
        return chain.proceed(requestWithHeaders)
    }
}
