package com.olympos.tripbook.src.trip.model

import retrofit2.Call
import retrofit2.http.*

interface TripRetrofitInterface {
    @POST("/app/trip")
    fun postTrip(): Call<TripResponse>
}