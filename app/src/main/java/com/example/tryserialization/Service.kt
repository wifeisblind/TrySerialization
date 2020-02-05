package com.example.tryserialization

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST

interface Service {

    @POST("api/get_sample")
    fun getSample(): Single<Sample>
}