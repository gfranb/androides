package com.example.roulette.Model

import com.example.roulette.Apuesta

data class JugadorModel(val correo: String, val apuestasGanadas: List<Apuesta>, val puntos: Int)
