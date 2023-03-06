package test.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import java.util.*


class MainActivity : Activity() {
    private var textEnabledGPS: TextView? = null
    private var textStatusGPS: TextView? = null
    private var textLocationGPS: TextView? = null
    private var textEnabledNet: TextView? = null
    private var textStatusNet: TextView? = null
    private var textLocationNet: TextView? = null
    private var locationManager: LocationManager? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textEnabledGPS = findViewById<View>(R.id.text_enabled_gps) as TextView
        textStatusGPS = findViewById<View>(R.id.text_status_gps) as TextView
        textLocationGPS = findViewById<View>(R.id.text_location_gps) as TextView
        textEnabledNet = findViewById<View>(R.id.text_enabled_net) as TextView
        textStatusNet = findViewById<View>(R.id.text_status_net) as TextView
        textLocationNet = findViewById<View>(R.id.text_location_net) as TextView
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        locationManager?.requestLocationUpdates(
            GPS_PROVIDER, 10.toLong(), 10f, locationListener
        )
        locationManager?.requestLocationUpdates(
            NETWORK_PROVIDER, 10.toLong(), 10f,
            locationListener
        )
        checkEnabled()
    }

    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(locationListener)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            showLocation(location)
        }

        override fun onProviderDisabled(provider: String) {
            checkEnabled()
        }

        @SuppressLint("MissingPermission")
        override fun onProviderEnabled(provider: String) {
            checkEnabled()
            showLocation(locationManager?.getLastKnownLocation(provider)!!)
        }

        @Deprecated("Deprecated in Java")
        @SuppressLint("SetTextI18n")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            if (provider == GPS_PROVIDER) {
                textStatusGPS!!.text = "Status: $status"
            } else if (provider == NETWORK_PROVIDER) {
                textStatusNet!!.text = "Status: $status"
            }
        }
    }

    private fun showLocation(location: Location) {
        if (location.provider.equals(GPS_PROVIDER)) {
            Log.d("MainActivity", "showLocation GPS (${formatLocation(location)})")
            textLocationGPS!!.text = formatLocation(location)
        } else if (location.provider.equals(
                NETWORK_PROVIDER
            )
        ) {
            Log.d("MainActivity", "showLocation NET (${formatLocation(location)})")
            textLocationNet!!.text = formatLocation(location)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatLocation(location: Location?): String {
        return if (location == null) "" else {
            val format = java.lang.String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3\$tF %3\$tT",
                location.latitude, location.longitude, Date(
                    location.time
                )
            )
            format
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkEnabled() {
        textEnabledGPS!!.text = ("Enabled: "
                + (locationManager
            ?.isProviderEnabled(GPS_PROVIDER) ?: ""))
        textEnabledNet!!.text = ("Enabled: "
                + (locationManager
            ?.isProviderEnabled(NETWORK_PROVIDER) ?: ""))
    }

    fun onClickLocationSettings() {
        startActivity(
            Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
        )
    }

    fun onClickLocationSettings(view: View) {}
}
