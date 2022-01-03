package com.example.transitlogger.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.transitlogger.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val mainRepository: MainRepository
):ViewModel() {
}