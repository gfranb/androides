package com.example.roulette.Model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FirebaseApiService {

    @GET("https://androides-94b4a-default-rtdb.europe-west1.firebasedatabase.app/clasificacion/jugadores.json")
    fun getJugadores(): Call<List<JugadorModel>>

    @POST("https://androides-94b4a-default-rtdb.europe-west1.firebasedatabase.app/clasificacion/jugadores.json")
    fun postNewJugador(@Body jugador: JugadorModel): Call<JugadorModel>

}