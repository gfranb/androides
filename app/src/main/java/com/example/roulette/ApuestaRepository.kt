package com.example.roulette

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class ApuestaRepository(private val apuestaDao: ApuestaDao) {
    fun guardaApuesta(apuesta:Apuesta): Completable {
        return Completable.fromCallable{
            apuestaDao.insert(apuesta)
        }.subscribeOn(Schedulers.io())
    }
}
