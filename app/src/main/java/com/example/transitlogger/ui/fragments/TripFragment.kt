package com.example.transitlogger.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.transitlogger.R
import com.example.transitlogger.adapters.TripAdapter
import com.example.transitlogger.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.transitlogger.other.TrackingUtility
import com.example.transitlogger.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_trip.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class TripFragment:Fragment(R.layout.fragment_trip), EasyPermissions.PermissionCallbacks {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var tripAdapter: TripAdapter

    private lateinit var permissionsLauncher:ActivityResultLauncher<Array<String>>



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setupRecyclerView()

        viewModel.TripSortedByDate.observe(viewLifecycleOwner, Observer {
            tripAdapter.submitList(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_tripFragment_to_trackingFragment)
        }
    }

    private  fun setupRecyclerView()=rvTrips.apply {
        tripAdapter= TripAdapter()
        adapter=tripAdapter
        layoutManager=LinearLayoutManager(requireContext())
    }

    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }
        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "Accept permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Accept permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else{
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}



}
