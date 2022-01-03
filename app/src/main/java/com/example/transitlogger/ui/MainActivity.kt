package com.example.transitlogger.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.transitlogger.R
import com.example.transitlogger.db.TripDAO
import com.example.transitlogger.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.transitlogger.ui.fragments.SetupFragment
import com.example.transitlogger.ui.fragments.TrackingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tracking.*
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


   // private val inputRoutes= InputStreamReader(assets.open("routes.txt"))
    //val reader=BufferedReader(inputRoutes)
    //var line:String? = null

    var buscondition="None"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        navigateToTrackingFragmentIfNeeded(intent)

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())


        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id){
                    R.id.settingsFragment,R.id.statisticsFragment,R.id.tripFragment ->
                        bottomNavigationView.visibility= View.VISIBLE
                    else -> bottomNavigationView.visibility=View.GONE

                }

            }



    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if(intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

    fun onRadioButtonClicked(view: android.view.View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioNew ->
                    if (checked) {
                        SetupFragment.Variables.Condition="New"
                    }

                R.id.radioGood ->
                    if (checked) {
                        SetupFragment.Variables.Condition="Good"
                    }

                R.id.radioAvg ->
                    if (checked) {
                        SetupFragment.Variables.Condition="Average"
                    }

                R.id.radioPoor ->
                    if (checked) {
                        SetupFragment.Variables.Condition="Old"
                    }

            }
        }

    }

}