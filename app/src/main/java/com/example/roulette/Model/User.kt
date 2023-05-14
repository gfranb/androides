package com.example.roulette.Model

data class User(
    val apuestasGanadas: List<ApuestaGanada>,
    val correo: String,
    val puntos: Int
)
