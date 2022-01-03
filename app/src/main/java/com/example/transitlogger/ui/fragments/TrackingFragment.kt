package com.example.transitlogger.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.transitlogger.R
import com.example.transitlogger.db.Trip
import com.example.transitlogger.other.Constants.ACTION_PAUSE_SERVICE
import com.example.transitlogger.other.Constants.ACTION_START_RESUME_SERVICE
import com.example.transitlogger.other.Constants.ACTION_STOP_SERVICE
import com.example.transitlogger.other.Constants.MAP_ZOOM
import com.example.transitlogger.other.Constants.POLYLINE_COLOR
import com.example.transitlogger.other.Constants.POLYLINE_WIDTH
import com.example.transitlogger.other.TrackingUtility
import com.example.transitlogger.services.Polyline
import com.example.transitlogger.services.TrackingService
import com.example.transitlogger.ui.MainActivity
import com.example.transitlogger.ui.MainActivity_GeneratedInjector
import com.example.transitlogger.ui.viewmodels.MainViewModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tracking.*
import kotlinx.serialization.json.JsonNull.content
import timber.log.Timber
import java.io.*
import java.util.*
import kotlin.math.round
import kotlin.system.exitProcess


@AndroidEntryPoint
class TrackingFragment:Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()

    private var isTracking=false
    private var pathPoints= mutableListOf<Polyline>()
   // private var speed= mutableListOf<String>()
   // private var elevation= mutableListOf<String>()
   // private var elevation= mutableListOf<String>()
    private var curTimeInMillis=0L
    private var speedms=0f

    private var menu: Menu?=null

    public var sound=0
    public var wifi=0
    public var tv=0
    public var payment=0



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private var map: GoogleMap?=null
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)


        btnToggleRun.setOnClickListener {
            toggleRun()
        }
        btnFinishRun.setOnClickListener {
            zoomToSeeTrack()
            endRunAndSaveToDb()
            //exitProcess(0)
        }
        mapView.getMapAsync(){
            onMapReady(it)
            map=it
            map?.isMyLocationEnabled = true
            addAllPolyLines()
        }
        subscribeToObservers()

        btnPassIn.setOnClickListener {

            if (TrackingService.passengers.isEmpty()){
                Timber.d("Passengers Empty")
                var passengers=0.0
                if(SetupFragment.Variables.SelectedVehicle=="14 Seater"){
                    passengers= 14.0
                } else if(SetupFragment.Variables.SelectedVehicle=="25 Seater"){
                    passengers= 25.0
                } else if(SetupFragment.Variables.SelectedVehicle=="33 Seater"){
                    passengers= 33.0
                } else if(SetupFragment.Variables.SelectedVehicle=="40 Seater"){
                    passengers= 40.0
                } else if(SetupFragment.Variables.SelectedVehicle=="50 Seater"){
                    passengers= 50.0
                } else if(SetupFragment.Variables.SelectedVehicle==""){
                    passengers= 55.0
                }

                tvPassengers.text=(passengers+1).toInt().toString()
                TrackingService.passengers.add(passengers+1)
                TrackingService.passengerlatitude.add(TrackingService.latitude.last())
                TrackingService.passengerlongitude.add(TrackingService.longitude.last())
                TrackingService.passengertime.add(TrackingService.gtime.last())
            }else{
                val passengers= TrackingService.passengers.last()
                tvPassengers.text=(passengers+1).toInt().toString()
                TrackingService.passengers.add(passengers+1)
                TrackingService.passengerlatitude.add(TrackingService.latitude.last())
                TrackingService.passengerlongitude.add(TrackingService.longitude.last())
                TrackingService.passengertime.add(TrackingService.gtime.last())
            }



        }
        btnPassOut.setOnClickListener {
            if (TrackingService.passengers.isEmpty()){
                Timber.d("Passengers Empty")
                var passengers=0.0
                if(SetupFragment.Variables.SelectedVehicle=="14 Seater"){
                    passengers= 14.0
                } else if(SetupFragment.Variables.SelectedVehicle=="25 Seater"){
                    passengers= 25.0
                } else if(SetupFragment.Variables.SelectedVehicle=="33 Seater"){
                    passengers= 33.0
                } else if(SetupFragment.Variables.SelectedVehicle=="40 Seater"){
                    passengers= 40.0
                } else if(SetupFragment.Variables.SelectedVehicle=="50 Seater"){
                    passengers= 50.0
                } else if(SetupFragment.Variables.SelectedVehicle==""){
                    passengers= 55.0
                }

                tvPassengers.text=(passengers-1).toInt().toString()
                TrackingService.passengers.add(passengers-1)
                TrackingService.passengerlatitude.add(TrackingService.latitude.last())
                TrackingService.passengerlongitude.add(TrackingService.longitude.last())
                TrackingService.passengertime.add(TrackingService.gtime.last())
            }else{
                val passengers= TrackingService.passengers.last()
                if (passengers>0){
                    tvPassengers.text=(passengers-1).toInt().toString()
                    TrackingService.passengers.add(passengers-1)
                    TrackingService.passengerlatitude.add(TrackingService.latitude.last())
                    TrackingService.passengerlongitude.add(TrackingService.longitude.last())
                    TrackingService.passengertime.add(TrackingService.gtime.last())
                }
            }


        }

        btnBump.setOnClickListener {
            if (TrackingService.bumps.isEmpty()){
                Timber.d("Bumps Empty")
                val bumps=0.0
                tvBump.text=(bumps+1).toInt().toString()
                TrackingService.bumps.add(bumps+1)
                TrackingService.bumpslatitude.add(TrackingService.latitude.last())
                TrackingService.bumpslongitude.add(TrackingService.longitude.last())
                TrackingService.bumpstime.add(TrackingService.gtime.last())
            }else{
                val bumps=TrackingService.bumps.last()
                tvBump.text=(bumps+1).toInt().toString()
                TrackingService.bumps.add(bumps+1)
                TrackingService.bumpslatitude.add(TrackingService.latitude.last())
                TrackingService.bumpslongitude.add(TrackingService.longitude.last())
                TrackingService.bumpstime.add(TrackingService.gtime.last())
                println(TrackingService.bumpslatitude.last().toString()+"\n"+TrackingService.bumpslongitude.last().toString())
            }

        }
        btnTflight.setOnClickListener {
            TrackingService.tflight.add(1.0)
            TrackingService.tflightlatitude.add(TrackingService.latitude.last())
            TrackingService.tflightlongitude.add(TrackingService.longitude.last())
            TrackingService.tflighttime.add(TrackingService.gtime.last())

        }
        btnRumble.setOnClickListener {
            TrackingService.rumble.add(1.0)
            TrackingService.rumblelatitude.add(TrackingService.latitude.last())
            TrackingService.rumblelongitude.add(TrackingService.longitude.last())
            TrackingService.rumbletime.add(TrackingService.gtime.last())
        }
        checkBoxsound.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked==true){
               Timber.d("Sound is checked")
                sound=1
            }else{
                Timber.d("sound in uncjedced")
                sound=0
            }

        }
        checkBoxtv.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked==true){
                Timber.d("tc is checked")
                tv=1
            }else{
                Timber.d("rc in uncjedced")
                tv=0
            }
        }
        checkBoxwifi.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked==true){
                Timber.d("widi is checked")
                wifi=1
            }else{
                Timber.d("wifi in uncjedced")
                wifi=0
            }
        }
        checkBoxPayment.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked==true){
                Timber.d("widi is checked")
                payment=1
            }else{
                Timber.d("wifi in uncjedced")
                payment=0
            }
        }

    }
    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints=it
            addLatestPolyLine()
            moveCameraToUser()
        })
        TrackingService.timeTripInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis=it
            val formattedTime=TrackingUtility.getFormattedStopWatchTime(curTimeInMillis,true)
            tvTimer.text=formattedTime
        })
        TrackingService.speed.observe(viewLifecycleOwner, Observer{
            speedms=it
            tvSpeed.text= round(3.6*speedms).toString()
            if (TrackingService.elevation.isEmpty()){

            }else{
                tvElevation.text= round(TrackingService.elevation.last()).toString()
            }

        })
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible=true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu=menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis>0L){
            this.menu?.getItem(0)?.isVisible=true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking->{
                showCancelTrackingDialog()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog(){
        val dialog=MaterialAlertDialogBuilder(requireContext(),R.style.ThemeOverlay_AppCompat)
            .setTitle("Cancel Trip")
            .setMessage("Do you want to cancel trip and delete data")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_,_ ->
                stopRun()
            }
            .setNegativeButton("No"){dialogInterface,_->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_setupFragment)
        println(TrackingService.elevation)
        Timber.d(TrackingService.passengers.toString())
        Timber.d(TrackingService.bumps.size.toString())
        Timber.d(TrackingService.bumpslongitude.size.toString())
        Timber.d(TrackingService.bumpstime.size.toString())
        Timber.d("Passengers")
        Timber.d(TrackingService.passengers.size.toString())
        Timber.d(TrackingService.passengerlatitude.size.toString())

        //printWriter.println(TrackingService.passengers.toString())
        //printWriter.flush()
        toCSVRow(
            TrackingService.time,
            TrackingService.latitude,
            TrackingService.longitude,
            TrackingService.speedms,
            TrackingService.elevation,
            TrackingService.bearing,
            TrackingService.accuracy,
            TrackingService.gtime,
            TrackingService.provider,
            TrackingService.passengers,
            TrackingService.bumps,
            TrackingService.passengerlatitude,
            TrackingService.passengerlongitude,
            TrackingService.passengertime,
            TrackingService.bumpslatitude,
            TrackingService.bumpslongitude,
            TrackingService.bumpstime,
            TrackingService.tflight,
            TrackingService.tflightlatitude,
            TrackingService.tflightlongitude,
            TrackingService.tflighttime,
            TrackingService.rumble,
            TrackingService.rumblelatitude,
            TrackingService.rumblelongitude,
            TrackingService.rumbletime,
            wifi,
            tv,
            sound,
            payment
        )

    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking){
            btnToggleRun.text="Start"
            //btnFinishRun.visibility=View.VISIBLE
            btnBump.visibility=View.GONE
            btnTflight.visibility=View.GONE
            btnPassIn.visibility=View.GONE
            btnPassOut.visibility=View.GONE
            tvRecordPass.visibility=View.GONE
            layoutStats.visibility=View.GONE
            btnRumble.visibility=View.GONE

            linearLayoutcheckbox.visibility=View.GONE
            checkBoxsound.visibility=View.GONE
            checkBoxtv.visibility=View.GONE
            checkBoxwifi.visibility=View.GONE
            textViewVehicleCondition.visibility=View.GONE
            tvVehicleFeatures.visibility=View.GONE
            radioCondition.visibility=View.GONE
        }else{
            btnToggleRun.text="Pause"
            menu?.getItem(0)?.isVisible=true
            btnFinishRun.visibility=View.VISIBLE
            btnBump.visibility=View.VISIBLE
            btnTflight.visibility=View.VISIBLE
            btnPassIn.visibility=View.VISIBLE
            btnPassOut.visibility=View.VISIBLE
            tvRecordPass.visibility=View.VISIBLE
            layoutStats.visibility=View.VISIBLE
            btnRumble.visibility=View.VISIBLE

            linearLayoutcheckbox.visibility=View.VISIBLE
            checkBoxsound.visibility=View.VISIBLE
            checkBoxtv.visibility=View.VISIBLE
            checkBoxwifi.visibility=View.VISIBLE
            textViewVehicleCondition.visibility=View.VISIBLE
            tvVehicleFeatures.visibility=View.VISIBLE
            radioCondition.visibility=View.VISIBLE

        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            val cameraPosition=CameraPosition.builder()
                .target(pathPoints.last().last())
                .zoom(MAP_ZOOM)
                .bearing(TrackingService.bearing.last())
                .build()

            map?.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                  cameraPosition)
            )

        }

    }

    private fun zoomToSeeTrack(){
        val bounds=LatLngBounds.Builder()
        for(Polyline in pathPoints){
            for (pos in Polyline){
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height*0.05f).toInt() //padding
            )
        )
    }

    private fun endRunAndSaveToDb(){
        map?.snapshot { bmp ->
            var distanceinm=0
            for (polyline in pathPoints){
                distanceinm+=TrackingUtility.calculatePolylineDistance(polyline).toInt()
            }
            val avgSpeed= round((distanceinm/1000f) /(curTimeInMillis/1000f/60/60)*10)/10f
            val datetimestamp=Calendar.getInstance().timeInMillis
            val coords: List<String> = listOf("1","2")
            val trip=Trip(bmp,datetimestamp,avgSpeed,distanceinm,curTimeInMillis,0,"",
            0,0,coords)
            viewModel.insertTrip(trip)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Trip Saved Successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()



        }

    }

    private fun addAllPolyLines() {
        for (polyline in pathPoints){
            val polylineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyLine() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size>1){
            val preLastLatLng=pathPoints.last()[pathPoints.last().size -2]
            val lastLatLng=pathPoints.last().last()
            val polylineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }


    private fun sendCommandToService(action:String)=
        Intent(requireContext(),TrackingService::class.java).also {
            it.action=action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()


    }

    override fun onStop() {
        super.onStop()
        //mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun toCSVRow(Time:List<Long>,Latitude:List<Double>,Longitude:List<Double>,
                         Speed:List<Float>,Elevation:List<Double>,Bearing:List<Float>
                         ,Accuracy:List<Float>,Time_sec:List<Long>,Provider:List<String>
                         ,Passengers:List<Double>,Bumps:List<Double>,PassLat:List<Double>,
                         PassLon:List<Double>,PassTime:List<Long>, BumpsLat:List<Double>,
                         BumpsLon:List<Double>, BumpsTime:List<Long>,TFlight:List<Double>, TFLat:List<Double>,
                         TFLon:List<Double>, TFTime:List<Long>,Rumble:List<Double>,Rumblelat:List<Double>,
                         Rumblelon:List<Double>,Rumbletime:List<Long>, wifi:Int,tv:Int,sound:Int,payment:Int){


        val path= requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()
        val header = "Time,Latitude,Longitude,Speed,Elevation,Bearing," +
                "Accuracy,Time in Seconds,Provider"


        try {
            val dir:String
            if (SetupFragment.Variables.SelectedDirection=="Outbound"){
                dir="o"
            } else{
                dir="i"
            }
            var fileWriter = FileWriter(File(path, SetupFragment.Variables.SelectedRoute+"_"+dir+"_.csv"))
            fileWriter.append(SetupFragment.Variables.SelectedRoute)
            fileWriter.append(',')
            fileWriter.append(SetupFragment.Variables.SelectedDirection)
            fileWriter.append(',')
            fileWriter.append(SetupFragment.Variables.SelectedVehicle)
            fileWriter.append(',')
            fileWriter.append("Fare")
            fileWriter.append(',')
            fileWriter.append(SetupFragment.Variables.Fare)
            fileWriter.append(',')
            fileWriter.append("SACCO")
            fileWriter.append(',')
            fileWriter.append(SetupFragment.Variables.Sacco)
            fileWriter.append(',')
            fileWriter.append('\n')
            fileWriter.append('\n')
            fileWriter.append(header)
            fileWriter.append('\n')
            // begin for loop
            for (i in Time.indices){
                fileWriter.append(Time[i].toString())
                fileWriter.append(',')
                fileWriter.append(Latitude[i].toString())
                fileWriter.append(',')
                fileWriter.append(Longitude[i].toString())
                fileWriter.append(',')
                fileWriter.append(Speed[i].toString())
                fileWriter.append(',')
                fileWriter.append(Elevation[i].toString())
                fileWriter.append(',')
                fileWriter.append(Bearing[i].toString())
                fileWriter.append(',')
                fileWriter.append(Accuracy[i].toString())
                fileWriter.append(',')
                fileWriter.append(Time_sec[i].toString())
                fileWriter.append(',')
                fileWriter.append(Provider[i])
                fileWriter.append('\n')
            }
            fileWriter.append('\n')
            fileWriter.append("Passenger Counts")
            fileWriter.append('\n')
            try{
                for (i in Passengers.indices){
                    fileWriter.append(PassTime[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(PassLat[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(PassLon[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(Passengers[i].toString())
                    fileWriter.append('\n')
                }
                TrackingService.passengers.clear()
                TrackingService.passengerlatitude.clear()
                TrackingService.passengerlongitude.clear()
                TrackingService.passengertime.clear()

            } catch (e: Exception) {
                println("Passenger error!")
                Timber.d("Error Saving passe")
            }


            fileWriter.append('\n')
            fileWriter.append('\n')

            fileWriter.append("Speed Bumps")
            fileWriter.append('\n')

            //println(Bumps.size.toString())
            //Timber.d(Bumps.size.toString())

            try{
                for (i in Bumps.indices){
                    fileWriter.append(BumpsTime[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(BumpsLat[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(BumpsLon[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(Bumps[i].toString())
                    fileWriter.append('\n')
                }
                TrackingService.bumps.clear()
                TrackingService.bumpstime.clear()
                TrackingService.bumpslongitude.clear()
                TrackingService.bumpslatitude.clear()
            }catch (e: Exception) {
                println("bumps error!")
                Timber.d("Error Saving bumps")
            }



            fileWriter.append('\n')
            fileWriter.append('\n')

            fileWriter.append("Traffic Lights")
            fileWriter.append('\n')


            try{
                for (i in TFlight.indices){
                fileWriter.append(TFlight[i].toString())
                fileWriter.append(',')
                fileWriter.append(TFLat[i].toString())
                fileWriter.append(',')
                fileWriter.append(TFLon[i].toString())
                fileWriter.append(',')
                fileWriter.append(TFTime[i].toString())
                fileWriter.append('\n')

            }
                TrackingService.tflight.clear()
                TrackingService.tflightlongitude.clear()
                TrackingService.tflightlatitude.clear()
                TrackingService.tflighttime.clear()
            }catch (e: Exception) {
                println("TF light error!")
                Timber.d("Error Saving File")
            }

            fileWriter.append('\n')
            fileWriter.append('\n')

            fileWriter.append("Rumble Strips")
            fileWriter.append('\n')


            try{
                for (i in Rumble.indices){
                    fileWriter.append(Rumble[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(Rumblelat[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(Rumblelon[i].toString())
                    fileWriter.append(',')
                    fileWriter.append(Rumbletime[i].toString())
                    fileWriter.append('\n')
                }
                TrackingService.rumble.clear()
                TrackingService.rumblelatitude.clear()
                TrackingService.rumblelongitude.clear()
                TrackingService.rumbletime.clear()


            }catch (e: Exception) {
                println("Rumble strips error!")
                Timber.d("Error Saving File")
            }
            fileWriter.append(',')
            fileWriter.append('\n')
            try {
                fileWriter.append("Vehicle Condition:")
                fileWriter.append(',')
                fileWriter.append(SetupFragment.Variables.Condition)
            }catch (e:java.lang.Exception){
                println("error saving condition")
            }
            fileWriter.append(',')
            fileWriter.append('\n')
            fileWriter.append("Features")
            fileWriter.append('\n')
            try {
                fileWriter.append("Wifi Availability")
                fileWriter.append(',')
                fileWriter.append(wifi.toString())
                fileWriter.append('\n')
                fileWriter.append("TV Availability")
                fileWriter.append(',')
                fileWriter.append(tv.toString())
                fileWriter.append('\n')
                fileWriter.append("Sound System Availability")
                fileWriter.append(',')
                fileWriter.append(sound.toString())
                fileWriter.append('\n')
            }catch (e:java.lang.Exception){
                println("error saving condition")
            }

            //end for loop
            fileWriter!!.flush()
            fileWriter.close()
            //
            Timber.d("File Saved")

        }catch (e: Exception) {
            println("Writing CSV error!")
            Timber.d("Error Saving File")
        }


    }

        /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this tutorial, we add polylines and polygons to represent routes and areas on the map.
     */
     private  fun onMapReady(googleMap: GoogleMap) {

        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.

        Timber.d(SetupFragment.Variables.SelectedRoute)

            val dir:String
            if (SetupFragment.Variables.SelectedDirection=="Outbound"){
                dir="o"
            } else{
                dir="i"
            }
        val fileName=SetupFragment.Variables.SelectedRoute+"_"+dir+".csv"
            val coords= PolylineOptions()
                  //  mutableListof<LatLng>
        //val readAsset = readAsset(requireContext(), fileeName)
            csvReader().open(requireContext().assets.open(fileName)) {
                readAllAsSequence().forEach { row ->
                    //Do something with the data
                    //println(row[2])
                    coords.add(LatLng(row[0].toDouble(),row[1].toDouble()))
                    //coords.add(row[0].toInt(),row[1].toInt())
                }
            }

            val polyline1 = googleMap.addPolyline(coords)
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.tag = "A"
        // Style the polyline.
        //stylePolyline(polyline1)



        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

    }

    fun readAsset(context: Context, fileName: String): String =
        context
            .assets
            .open(fileName)
            .bufferedReader()
            .use(BufferedReader::readText)





}






