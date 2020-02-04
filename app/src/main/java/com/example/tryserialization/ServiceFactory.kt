package com.example.tryserialization

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

object ServiceFactory {

    private const val contentType = "application/json"

    fun getService(): Service {
        return Retrofit.Builder()
            .baseUrl("http://google.com")
            .addConverterFactory(Json.asConverterFactory(MediaType.get(contentType)))
            .build()
            .create(Service::class.java)
    }
}