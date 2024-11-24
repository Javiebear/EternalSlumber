package com.example.eternalslumber

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eternalslumber.databinding.ActivityNewPropertyBinding
import java.io.ByteArrayOutputStream


class NewProperty : AppCompatActivity() {

    // setting up binding and the connection to the database
    private lateinit var binding: ActivityNewPropertyBinding
    private lateinit var db: EternalSlumberDatabaseHelper

    // passing user's username


    // setting up the image variable to store the users images
    var image: ByteArray? = null

    // this will launch an activity to get an image from the gallery
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream = contentResolver.openInputStream(uri) // getting the image inputted
            val byteArray = inputStream?.readBytes() //reading the image into a BytaArray

            // saving the inputted image to a ByteArray in noteImage
            image = byteArray ?: ByteArray(0)

            // this will set the default image to the selected one
            binding.propertyAddImageView.setImageURI(uri)
        }
    }

    // Declare the activity result launcher for capturing an image from the camera
    private val captureImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val stream = ByteArrayOutputStream() //getting the captured image
            it.compress(Bitmap.CompressFormat.PNG, 100, stream)

            // converting the inputed image to a ByteArray
            image = stream.toByteArray()

            // this will set the default image to the selected one
            binding.propertyAddImageView.setImageBitmap(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initializing the database
        db = EternalSlumberDatabaseHelper(this)

        binding = ActivityNewPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // setting up the button functions
        binding.homeButton.setOnClickListener {
            returnHome(it)
        }

        binding.finishButton.setOnClickListener {
            addProperty(it)
        }

        binding.propertyAddImageView.setOnClickListener {
            displayImagePopUp(it)
        }
    }

    // this will send the user back home
    fun returnHome(view: View?){
        finish()
    }

    // this will add a new item to the database
    fun addProperty(view: View?){
        val title = binding.titleEditText.text.toString()
        val location = binding.locationEditText.text.toString()
        val cost = binding.costEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()

        val username = intent.getStringExtra("user")

        // check if everything is filled in
        if(title != "" && location != "" && cost != "" && description != "" && image != null){
            val property = Property(0, title, location, cost, description, image, username.toString())
            db.insertProperties(property)

            Toast.makeText(this, "Property Created", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(applicationContext, "Failed to create your property!\nYou are missing some entries!", Toast.LENGTH_LONG).show()
        }
    }

    // this function will display a popup to allow the user to select if they want to open a
    fun displayImagePopUp(view: View?){
        val popupMenu = PopupMenu(this, view)

        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.imagepopup, popupMenu.menu)

        popupMenu.gravity = Gravity.END

        // this will figure out what the user selected from the menu
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                // when the select image is clicked a function will be called to handle it
                R.id.selectImage -> {
                    selectImage()
                    true
                }

                // when capture image is clicked a function will be called to handle it
                R.id.captureImage -> {
                    captureImage()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    // this function handles when select image is clicked
    fun selectImage(){
        pickImageLauncher.launch("image/*")
    }

    // this function handles when capture image is clicked
    fun captureImage(){
        captureImageLauncher.launch(null)
    }
}