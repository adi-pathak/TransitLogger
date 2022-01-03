package com.example.transitlogger.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TripDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)
    @Delete
    suspend fun deleteTrip(trip: Trip)
    @Query("SELECT * FROM trip_table ORDER BY timestamp DESC")
    fun getAllTripsSortedByDate():LiveData<List<Trip>>
    @Query("SELECT * FROM trip_table ORDER BY route DESC")
    fun getAllTripsSortedByRoute():LiveData<List<Trip>>
    @Query("SELECT * FROM trip_table ORDER BY distm DESC")
    fun getAllTripsSortedByDistance():LiveData<List<Trip>>
    @Query("SELECT * FROM trip_table ORDER BY avgSpeed DESC")
    fun getAllTripsSortedBySpeed():LiveData<List<Trip>>
    @Query("SELECT * FROM trip_table ORDER BY timeinmillis DESC")
    fun getAllTripsSortedByTime():LiveData<List<Trip>>

    // get data values for statistics
    @Query("SELECT SUM(distm)  FROM trip_table")
    fun getTotalTripDistance():LiveData<Int>
    @Query("SELECT AVG(avgSpeed)  FROM trip_table")
    fun getAverageSpeed():LiveData<Int>

}