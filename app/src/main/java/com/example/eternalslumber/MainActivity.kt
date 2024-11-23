package com.example.eternalslumber

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eternalslumber.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: EternalSlumberDatabaseHelper
    private lateinit var propertyAdapterAll: PropertyAdapter
    private lateinit var propertyAdapterFeatured: PropertyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing the database
        db = EternalSlumberDatabaseHelper(this)
        db.insertPropertiesFromFile(this)

        // Setting up the featured properties RecyclerView to scroll horizontally
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.featuredPropRecyclerView.layoutManager = layoutManager
        propertyAdapterFeatured = PropertyAdapter(db.getAllProperties(), this)
        binding.featuredPropRecyclerView.adapter = propertyAdapterFeatured

        // Setting up the all properties RecyclerView with vertical scrolling
        propertyAdapterAll = PropertyAdapter(db.getAllProperties(), this)
        binding.allPropRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.allPropRecyclerView.adapter = propertyAdapterAll

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("user")

        // Setting up the button and their functions
        binding.addPropertyButton.setOnClickListener{
            addProperty(it)
        }

        // Binding the search bar to allow users to search for specified elements
        binding.searchProperties.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(search: String?): Boolean {
                search(search)
                return true
            }
        })
    }

    //this function will refresh the data when called
    override fun onResume() {
        super.onResume()
        propertyAdapterAll.refreshData(db.getAllProperties())
        propertyAdapterFeatured.refreshData(db.getAllProperties())
    }

    // this function will open up a new activity to add a property
    fun addProperty(view: View?){
        val intent = Intent(this, NewProperty::class.java)
        startActivity(intent)
    }

    // this function will handle the searching of items in the database
    fun search(search: String?){
        if(search.isNullOrEmpty()){
            // if nothing is searched then all the notes are displayed
            propertyAdapterAll.refreshData(db.getAllProperties())

        }
        else
        {
            propertyAdapterAll.refreshData(db.getQueriedProperties(search))
        }

    }
}