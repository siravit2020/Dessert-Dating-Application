package com.maiguy.dessert.services

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.show_gps_open.view.ShowGpsOpen
import com.maiguy.dessert.constants.Status

class LocationService(private var activity: Activity) {
    private lateinit var mLocationManager: LocationManager
    fun checkPermission(notChange:Boolean = false): Status {
        mLocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledDialog(notChange)
            return Status.LOADING
        } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1)
            return Status.LOADING
        }
        return Status.SUCCESS
    }

    private fun showGPSDisabledDialog(notChange:Boolean= false) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.gps_disabled)
        builder.setMessage(R.string.please_open_gps)
        builder.setPositiveButton(R.string.open_gps) { _, _ ->
            (activity)
                    .startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
        }
                .setNegativeButton(R.string.close) { dialog, which ->
                    if(notChange) return@setNegativeButton
                    val intent = Intent(activity, ShowGpsOpen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    (activity).finish()
                    activity.startActivity(intent)
                }
        val mGPSDialog: Dialog = builder.create()
        mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.myrect2))
        mGPSDialog.show()
    }


}