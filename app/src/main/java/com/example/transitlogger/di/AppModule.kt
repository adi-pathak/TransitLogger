package com.example.transitlogger.di

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.transitlogger.db.TripDatabase
import com.example.transitlogger.other.Constants.TRIP_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideTripDatabase(
       @ApplicationContext app: Context
    )= Room.databaseBuilder(
        app,
        TripDatabase::class.java,
        TRIP_DATABASE_NAME

    ).build()

    @Singleton
    @Provides
    fun provideTripDAO(db:TripDatabase)=db.getTripDAO()

}