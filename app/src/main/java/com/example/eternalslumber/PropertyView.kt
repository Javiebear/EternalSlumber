package com.example.eternalslumber

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eternalslumber.databinding.ActivityPropertyViewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PropertyView : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPropertyViewBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var db: EternalSlumberDatabaseHelper
    private val apiKey = "AIzaSyC9itYsRPP3dTSbM3NpRDaEAlpwFj0hNnQ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = EternalSlumberDatabaseHelper(this)

        // Retrieving the data from intent
        val id = intent.getIntExtra("id", 0)
        val property = db.matchPropertyByID(id)
        if (property != null) {
            // Bind property details to views
            binding.titleTextView.text = property.title
            binding.locationTextView.text = property.location
            binding.costTextView.text = "$${property.cost}"
            binding.descriptionTextView.text = property.description
            binding.usernameTextView.text = "Created by: ${property.username}"

            // Display property image
            property.propertyImage?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                binding.propertyImageView.setImageBitmap(bitmap)
            }

            // Fetch and display map location
            fetchCoordinatesAndShowOnMap(property.location)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up Home button functionality
        binding.homeButton.setOnClickListener { goHome(it) }

        // Set up Google Map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun fetchCoordinatesAndShowOnMap(address: String) {
        val formattedAddress = address.replace(" ", "+")
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$formattedAddress&key=$apiKey"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            try {
                val jsonObject = JSONObject(response)

                // Parse latitude and longitude
                val results = jsonObject.getJSONArray("results")
                if (results.length() > 0) {
                    val location = results
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location")

                    val latitude = location.getDouble("lat")
                    val longitude = location.getDouble("lng")
                    val latLng = LatLng(latitude, longitude)

                    // Add a marker and move the camera
                    googleMap.addMarker(MarkerOptions().position(latLng).title(address))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
                    Toast.makeText(this, "No location found for the address", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to parse map data", Toast.LENGTH_SHORT).show()
            }
        }, { error ->
            error.printStackTrace()
            Toast.makeText(this, "Error fetching map data: ${error.message}", Toast.LENGTH_SHORT).show()
        })

        queue.add(stringRequest)
    }


    fun goHome(view: View?) {
        finish()
    }
}
