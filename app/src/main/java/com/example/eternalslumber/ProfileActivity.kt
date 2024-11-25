package com.example.eternalslumber

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eternalslumber.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var db: EternalSlumberDatabaseHelper
    private lateinit var propertyAdapter: PropertyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // database connnection
        db = EternalSlumberDatabaseHelper(this)

        // setting up binding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initializes username and calls properties to be displayed equal to username
        val username = intent.getStringExtra("user") ?: ""
        val property = db.getUserProperties(username)

        // setting up as format of property item to be displayed in recycler
        propertyAdapter = PropertyAdapter(property, this)
        binding.allPropRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.allPropRecyclerView.adapter = propertyAdapter
        propertyAdapter.notifyDataSetChanged()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // changes default username display to the actually username
        val displayName = findViewById<TextView>(R.id.loggedName2)
        displayName.text = username

        // home button to end activity and bring back to main screen when done
        val homeButton = findViewById<Button>(R.id.homeButton2)
        homeButton.setOnClickListener {
            finish()
        }
    }
}
