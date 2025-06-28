package com.adkins.msafari.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // ✅ Check for null first to avoid crash
        if (geofencingEvent == null) {
            Log.e("Geofence", "Received null GeofencingEvent")
            return
        }

        if (geofencingEvent.hasError()) {
            Log.e("Geofence", "Error: ${geofencingEvent.errorCode}")
            return
        }

        val transitionType = geofencingEvent.geofenceTransition
        val geofenceId = geofencingEvent.triggeringGeofences?.firstOrNull()?.requestId

        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(context, "You entered the destination!", Toast.LENGTH_LONG).show()
                Log.d("Geofence", "ENTER: $geofenceId")
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Toast.makeText(context, "You left the destination!", Toast.LENGTH_LONG).show()
                Log.d("Geofence", "EXIT: $geofenceId")
            }

            else -> {
                Log.d("Geofence", "Unhandled transition type: $transitionType")
            }
        }
    }
}