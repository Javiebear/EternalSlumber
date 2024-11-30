package com.example.eternalslumber

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var reviewAdapter: ReviewAdapter
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
            googleApi(property.location)
        }

        // Setting up the all properties RecyclerView with vertical scrolling
        reviewAdapter = ReviewAdapter(db.getReviewsForProperty(id), this)
        binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reviewsRecyclerView.adapter = reviewAdapter

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

        binding.finishButton.setOnClickListener { addReview(it) }

    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    // this function will refresh the data when called
    override fun onResume() {
        super.onResume()
        reviewAdapter.refreshData(db.getReviewsForProperty(intent.getIntExtra("id", 0)))
    }

    // this will add a new item to the database
    fun addReview(view: View?){
        val title = binding.titleEditText.text.toString()
        val content = binding.reviewContentEditText.text.toString()

        val username = intent.getStringExtra("user")
        val id = intent.getIntExtra("id", 0)

        val review = Review(0, title, content, username.toString(), id)
        db.insertReview(review)

        finish()

    }

    // this function will connect to the google map api, take the given address, change it to lat and long, then input it into the goolge api to be read
    private fun googleApi(address: String) {

        // prepares the inputted address into a format for google api, then connects to it
        val addressFormat = address.replace(" ", "+")
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$addressFormat&key=$apiKey"


        // initializes variable to hold output
        val queue = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url, { response ->

            // trys the connection
            try {
                // this will hold the json object and teh array for the results from the json object
                val json = JSONObject(response)
                val result = json.getJSONArray("results")
                if (result.length() > 0) {

                    // initializes variables for the outputted latitude and longitude outputs
                    val location = result.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                    val fullCoordinate = LatLng(location.getDouble("lat"), location.getDouble("lng"))

                    // this section adds the red marker onto the map
                    googleMap.addMarker(MarkerOptions().position(fullCoordinate).title(address))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fullCoordinate, 12f))
                } else {
                    Toast.makeText(this, "Was not able to find associated location to this address!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to processes data!", Toast.LENGTH_LONG).show()
            }
        }, { error ->
            error.printStackTrace()
            Toast.makeText(this, "Failed to get get map data!", Toast.LENGTH_LONG).show()
        })
        // calls the request for the google api
        queue.add(request)
    }

    fun goHome(view: View?) {
        finish()
    }
}
