package com.example.tryserialization

import io.reactivex.Single
import retrofit2.http.GET

interface Service {

    @GET
    fun getSample(): Single<Sample>
}