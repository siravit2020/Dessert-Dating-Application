package com.jabirdeveloper.tinderswipe.services

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.ShowGpsOpen
import com.jabirdeveloper.tinderswipe.utils.Status

class LocationService(private var application: Application) {
    private lateinit var mLocationManager: LocationManager
    fun checkPermission(): Status {
        mLocationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledDialog()
            return Status.LOADING
        } else if (ActivityCompat.checkSelfPermission(application.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(application.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(application.applicationContext as Activity, arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1)
            return Status.LOADING
        }
        return Status.SUCCESS
    }

    private fun showGPSDisabledDialog() {
        val builder = AlertDialog.Builder(application)
        builder.setTitle(R.string.GPS_Disabled)
        builder.setMessage(R.string.GPS_open)
        builder.setPositiveButton(R.string.open_gps) { _, _ ->
            (application.applicationContext as Activity)
                    .startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
        }
                .setNegativeButton(R.string.report_close) { dialog, which ->
                    val intent = Intent(application, ShowGpsOpen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    (application.applicationContext as Activity).finish()
                    application.startActivity(intent)
                }
        val mGPSDialog: Dialog = builder.create()
        mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(application, R.drawable.myrect2))
        mGPSDialog.show()
    }
}