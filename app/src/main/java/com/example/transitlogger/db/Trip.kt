package com.example.transitlogger.db
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.transitlogger.services.Polyline

@Entity(tableName = "trip_table")
data class Trip (
    var img: Bitmap?=null,
    var timestamp : Long=0L,
    var avgSpeed: Float=0f,
    var distm: Int=0,
    var timeinmillis : Long=0L,
    var elevation : Int=0,
    var route: String="null",
    var bumps:Int=0,
    var passengers:Int=0,
    var coords: List<String>?=null




    ) {
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null





}