package com.example.roulette

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Apuesta::class], version = 1)
abstract class ApuestaDB: RoomDatabase() {

    abstract fun apuestaDao(): ApuestaDao

}