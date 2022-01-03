package com.example.transitlogger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transitlogger.db.Trip
import com.example.transitlogger.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
):ViewModel() {

    val TripSortedByDate=mainRepository.getAllTripsSortedByDate()
    fun insertTrip(trip: Trip)=viewModelScope.launch {
        mainRepository.insertTrip(trip)
    }
}