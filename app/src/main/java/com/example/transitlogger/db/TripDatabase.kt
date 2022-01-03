package com.example.transitlogger.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [Trip::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class TripDatabase:RoomDatabase() {

    abstract fun getTripDAO():TripDAO
}