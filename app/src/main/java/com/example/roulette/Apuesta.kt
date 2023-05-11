package com.example.roulette

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apuesta")
data class Apuesta(
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        var seleccion: String,
        var montoApostado: Int,
        var dinero: Int,
        var latitud: Double?,
        var longitud: Double?
)