package com.gmail.apigeoneer.gmapsgplaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.ConnectionRequest
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val ERROR_DIALOG_REQUEST = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * Check if the user has the apt version of Google Play Services installed
     */
    fun isServicesOK() : Boolean {
        Log.d(TAG,"::: isServicesOK() : Checking Google Play Services version... :::")

        // Storing the state of user's Google Play Services to decide further action based on that
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        if (available == ConnectionResult.SUCCESS) {
            // Everything's fine. The user can make Map requests.
            Log.d(TAG, "::: isServicesOK() : Google Play Services is working! :::")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // An error occured but we can resolve it
            Log.d(TAG, "::: isServicesOK() : An error occured. But we can fix it! :::")
            // Ask Google to give a dialog, where we can find a solution to the problem
            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST)
            dialog.show()
        } else {
            Toast.makeText(this, "You can't make Map request.", Toast.LENGTH_SHORT).show()
        }

        return false
    }
}