package com.example.eternalslumber

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class LoginActivity : AppCompatActivity() {


    private lateinit var db: EternalSlumberDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonRegister = findViewById<Button>(R.id.button2)
        val buttonGuest = findViewById<Button>(R.id.guestButton)
        val buttonLogin = findViewById<Button>(R.id.loginButton)
        val username = findViewById<EditText>(R.id.emailInput)
        val pass = findViewById<EditText>(R.id.passInput)
        val failed = findViewById<TextView>(R.id.wrongInput)
        db = EternalSlumberDatabaseHelper(this)

        // when user presses login button
        buttonLogin.setOnClickListener {

            // takes inputted username and pass and turns it to string
            val inputUsername = username.text.toString()
            val inputPass = pass.text.toString()
            val user1 = inputUsername
            val pass1 = inputPass

            // runs the username and password through the login function in the database
            val valid = db.loginUser(user1, pass1)

            // if the login is valid, start intent
            if (valid) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("user", username.text.toString())
                startActivity(intent)
            }else {
                // red text tells user wrong pass or username
                failed.setTextColor(Color.RED)
            }


        }
        // register button takes user to register activity
        buttonRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        // user can continue as guest, will not pass any username over to main activity
        buttonGuest.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }
}