package com.example.transitlogger.repositories

import com.example.transitlogger.db.Trip
import com.example.transitlogger.db.TripDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val tripDAO: TripDAO
) {
    suspend fun insertTrip(trip:Trip)=tripDAO.insertTrip(trip)
    suspend fun deleteTrip(trip:Trip)=tripDAO.deleteTrip(trip)

    fun getAllTripsSortedByDate()=tripDAO.getAllTripsSortedByDate()
    fun getAllTripsSortedByRoute()=tripDAO.getAllTripsSortedByRoute()
    fun getAllTripsSortedByDistance()=tripDAO.getAllTripsSortedByDistance()
    fun getAllTripsSortedBySpeed()=tripDAO.getAllTripsSortedBySpeed()
    fun getAllTripsSortedByTime()=tripDAO.getAllTripsSortedByTime()
}