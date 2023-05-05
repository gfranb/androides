package com.example.roulette

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Apuesta::class], version = 1)
abstract class ApuestaDB : RoomDatabase() {

    abstract fun apuestaDao(): ApuestaDao

}