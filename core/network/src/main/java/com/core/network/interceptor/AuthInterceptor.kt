package com.core.network.interceptor

import com.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", BuildConfig.API_KEY)
            .build()

        return chain.proceed(newRequest)
    }
}