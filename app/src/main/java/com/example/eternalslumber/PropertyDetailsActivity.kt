package com.example.eternalslumber

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

class PropertyDetailsActivity : AppCompatActivity() {
    private lateinit var dbHelper: EternalSlumberDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details) // Replace with your actual layout

        dbHelper = EternalSlumberDatabaseHelper(this)

        // Retrieve the property ID
        val propertyId = intent.getIntExtra("PROPERTY_ID", -1)
        if (propertyId != -1) {
            displayPropertyDetails(propertyId)
            displayReviews(propertyId)
        } else {
            // Handle error: Property ID not found
            Toast.makeText(this, "Error: Property not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayPropertyDetails(propertyId: Int) {
        val property = dbHelper.getAllProperties().find { it.id == propertyId } // Modify to query directly
        if (property != null) {
            // Bind property details to your views
            findViewById<TextView>(R.id.propertyTitle).text = property.title
            findViewById<TextView>(R.id.propertyLocation).text = property.location
            findViewById<TextView>(R.id.propertyCost).text = property.cost
            findViewById<TextView>(R.id.propertyDescription).text = property.description
            val imageView = findViewById<ImageView>(R.id.propertyImage)
            property.propertyImage?.let { imageView.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size)) }
        } else {
            // Handle error: Property not found
            Toast.makeText(this, "Property details not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayReviews(propertyId: Int) {
        // Fetch reviews for the specific property ID
        val reviews = dbHelper.getReviewsForProperty(propertyId)

        // Find the RecyclerView and set it up
        val reviewsRecyclerView = findViewById<RecyclerView>(R.id.reviewsRecyclerView)
        reviewsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with the review list
        val reviewsAdapter = ReviewAdapter(reviews, this)
        reviewsRecyclerView.adapter = reviewsAdapter
    }
}
