package com.example.transitlogger.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.transitlogger.R
import com.example.transitlogger.ui.viewmodels.MainViewModel

class SettingsFragment:Fragment(R.layout.fragment_settings) {
    private val viewModel: MainViewModel by viewModels()
}
