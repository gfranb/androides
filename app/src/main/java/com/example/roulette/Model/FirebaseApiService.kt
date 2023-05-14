package com.example.roulette.Model

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FirebaseApiService {

    @GET("jugadores.json")
    suspend fun getJugadores(): Response<Map<String,User>>

    @GET("premio.json")
    suspend fun getPremio(): Response<Int>

}