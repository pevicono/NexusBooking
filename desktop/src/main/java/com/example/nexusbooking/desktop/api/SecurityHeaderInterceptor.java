package com.example.nexusbooking.desktop.api;

import okhttp3.Interceptor;
import okhttp3.Response;
import java.io.IOException;

/**
 * Network interceptor para garantir TLS i agregar security headers
 */
public class SecurityHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Enforce HTTPS in production, but allow HTTP on local dev hosts.
        String scheme = chain.request().url().scheme();
        String host = chain.request().url().host();
        boolean isHttps = "https".equalsIgnoreCase(scheme);
        boolean isLocalDevHost = "localhost".equalsIgnoreCase(host)
                || "127.0.0.1".equals(host)
                || "10.0.2.2".equals(host)
                || host.toLowerCase().endsWith(".local");

        if (!isHttps && !isLocalDevHost) {
            throw new IOException("Only HTTPS connections are allowed for non-local hosts. URL: " + chain.request().url());
        }

        // Agregar security headers a todas las peticiones
        okhttp3.Request originalRequest = chain.request();
        okhttp3.Request.Builder requestBuilder = originalRequest.newBuilder();
        
        // Add security headers
        requestBuilder.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        requestBuilder.addHeader("X-Content-Type-Options", "nosniff");
        requestBuilder.addHeader("X-Frame-Options", "DENY");
        requestBuilder.addHeader("X-XSS-Protection", "1; mode=block");
        requestBuilder.addHeader("Content-Security-Policy", "default-src 'self'");
        requestBuilder.addHeader("User-Agent", "NexusBooking-Desktop/1.0");
        
        okhttp3.Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }
}
