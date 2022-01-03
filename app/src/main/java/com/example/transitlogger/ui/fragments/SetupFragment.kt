package com.example.transitlogger.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.transitlogger.R
import com.example.transitlogger.ui.fragments.SetupFragment.Variables.Fare
import com.example.transitlogger.ui.fragments.SetupFragment.Variables.Sacco
import com.example.transitlogger.ui.fragments.SetupFragment.Variables.SelectedDirection
import com.example.transitlogger.ui.fragments.SetupFragment.Variables.SelectedRoute
import com.example.transitlogger.ui.fragments.SetupFragment.Variables.SelectedVehicle
import com.example.transitlogger.ui.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_setup.*
import timber.log.Timber
import java.io.InputStreamReader

class SetupFragment:Fragment(R.layout.fragment_setup) {

    object Variables {
        lateinit var SelectedRoute:String
        lateinit var SelectedDirection:String
        lateinit var SelectedVehicle:String
        lateinit var Fare:String
        lateinit var Sacco:String
        lateinit var Condition:String
    }


    //private val viewModel: MainViewModel by viewModels()
 override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner1=view.findViewById<Spinner>(R.id.spinnerRoute)

        val routes = resources.getStringArray(R.array.Routes)
        spinner1?.adapter = activity?.applicationContext?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, routes) } as SpinnerAdapter
        spinner1?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val type = parent?.getItemAtPosition(position).toString()
                //Toast.makeText(activity, type, Toast.LENGTH_LONG).show()
                //println(type)
                SelectedRoute=type

            }
        }

        val spinner2=view.findViewById<Spinner>(R.id.spinnerDir)

        val direction = resources.getStringArray(R.array.direction)
        spinner2?.adapter = activity?.applicationContext?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, direction) } as SpinnerAdapter
        spinner2?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val dir = parent?.getItemAtPosition(position).toString()

                SelectedDirection=dir

            }
        }

        val spinner3=view.findViewById<Spinner>(R.id.spinnerVehicle)

        val vehicle = resources.getStringArray(R.array.vehicles)
        spinner3?.adapter = activity?.applicationContext?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, vehicle) } as SpinnerAdapter
        spinner3?.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val veh = parent?.getItemAtPosition(position).toString()

                SelectedVehicle=veh

            }
        }



        tvContinue.setOnClickListener {
            val fare=textinputFare.text.toString()
            Fare=fare
            val sco=textInputSacco.text.toString()
            Sacco=sco
            println(Fare)

             findNavController().navigate(R.id.action_setupFragment_to_tripFragment)
        }

    }
}


