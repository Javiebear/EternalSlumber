package com.example.eternalslumber

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eternalslumber.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: EternalSlumberDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // will implement later, can be used to show name of user and name of who put the property up later on
        //val fName = findViewById<TextInputEditText>(R.id.fName)
        //val lName = findViewById<TextInputEditText>(R.id.lName)

        // being used
        val inputUsername = findViewById<EditText>(R.id.newUsername)
        val inputPass = findViewById<EditText>(R.id.newPass)
        val registerButton = findViewById<Button>(R.id.registerButton)
        db = EternalSlumberDatabaseHelper(this)

        registerButton.setOnClickListener {

            // currently database only takes username and pass, will fix this later, its an easy fix too tired rn
            val username = inputUsername.text.toString()
            val pass = inputPass.text.toString()

            // runs the reigster function, inputs username and password into the funtion, spits out boolean, true if success, false if failed
            val dbSuccess = db.registerUser(username, pass)

            // finishes when registration is successful, if false, lets user know
            if (dbSuccess) {
                Toast.makeText(this, "You have Registered Successfully!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Failed!", Toast.LENGTH_LONG).show()
            }
        }
    }
}