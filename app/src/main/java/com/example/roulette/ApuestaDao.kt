package com.example.roulette

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ApuestaDao {

    @Query("SELECT * FROM apuesta")
    fun getAll(): List<Apuesta>

    @Insert
    fun insert(apuesta: Apuesta)

    @Query("SELECT dinero FROM apuesta ORDER BY id DESC LIMIT 1")
    fun obtenerDineroDisponible():Int

    @Query("SELECT * FROM apuesta WHERE latitud AND longitud IS NOT NULL")
    fun obtenerApuestasGanadas():List<Apuesta>
}