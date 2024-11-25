package com.example.eternalslumber

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getBlobOrNull
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import android.util.Log

class EternalSlumberDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION ){

    companion object{
        private const val DATABASE_NAME = "eternalslumber.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "properties"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_COST = "cost"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PROPERTY_IMAGE = "image"
        private const val COLUMN_USERNAME = "username"
        private const val TABLE_REVIEWS = "reviews"
        private const val COLUMN_REVIEW_ID = "review_id"
        private const val COLUMN_REVIEW_TITLE = "title"
        private const val COLUMN_RATING = "rating"
        private const val COLUMN_REVIEW_DESCRIPTION = "description"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_PROPERTY_ID = "property_id"
    }

    // this function creates the tables of the database
    override fun onCreate(db: SQLiteDatabase?) {

        // property table
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_LOCATION TEXT, $COLUMN_COST TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_PROPERTY_IMAGE BLOB, $COLUMN_USERNAME TEXT)"

        // user table
        val createUserTableQuery = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)"

        // reviews table
        val createReviewsTableQuery = "CREATE TABLE reviews (review_id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,rating INTEGER CHECK(rating >= 1 AND rating <= 5), description TEXT,user_id INTEGER, property_id INTEGER, FOREIGN KEY(user_id) REFERENCES users(id),FOREIGN KEY(property_id) REFERENCES properties(id))"

        db?.execSQL(createTableQuery)
        db?.execSQL(createUserTableQuery)
        db?.execSQL(createReviewsTableQuery)
    }

    // updates a table
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertPropertiesFromFile(context: Context) {
        val assetManager = context.assets
        try {
            val inputStream = assetManager.open("houses.txt") // Open the file from assets
            inputStream.bufferedReader().forEachLine { line ->
                val parts = line.split(",")

                // Ensure there are enough parts in the line
                if (parts.size < 7) {
                    return@forEachLine // Change to return if not enough parts
                }

                val id = parts[0].toIntOrNull() ?: run {
                    return@forEachLine // Skip this line if ID is invalid
                }
                val title = parts[1]
                val location = parts[2]
                val cost = parts[3]
                val description = parts[4]
                val imagePath = parts[5]
                val username = parts[6].trim()

                // Check if property already exists
                if (propertyExists(id)) {
                    return@forEachLine // Skip insertion if it already exists
                }

                // Load image as BLOB
                val imageBlob = getImageAsByteArray(imagePath.trim(), context)

                // Create the Property object and insert it into the DB
                val property = Property(id, title, location, cost, description, imageBlob, username)
                insertProperties(property)
            }
            inputStream.close() // Close the input stream
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting properties from file: ${e.message}")
        }
    }

    private fun getImageAsByteArray(imagePath: String, context: Context): ByteArray? {
        return try {
            // Accessing the AssetManager
            val assetManager = context.assets
            assetManager.open(imagePath).use { inputStream ->
                inputStream.readBytes() // Read bytes from the input stream
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error loading image from path $imagePath: ${e.message}")
            null // Return null if there's an error
        }
    }

    fun getUserProperties(username: String): List<Property> {
        val propertyList = mutableListOf<Property>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_USERNAME = ?" // Query with parameter placeholder
        val cursor = db.rawQuery(query, arrayOf(username))

        while (cursor.moveToNext()) { // Iterate through the cursor to fetch results
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
            val cost = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COST))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val image = cursor.getBlobOrNull(cursor.getColumnIndexOrThrow(COLUMN_PROPERTY_IMAGE))
            val user = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))

            // Create a Property object and add it to the list
            val property = Property(id, title, location, cost, description, image, user)
            propertyList.add(property)
        }

        cursor.close()
        db.close()

        return propertyList // Return the list of properties for the user
    }


    // this function is to inset nodes into the database
    fun insertProperties(property: Property){
        val db = writableDatabase
        val values = ContentValues().apply{ // Content values is used to store values associated with column names
            put(COLUMN_TITLE, property.title) // apply allows for operations to occur and put allows you to add to the database
            put(COLUMN_LOCATION, property.location)
            put(COLUMN_COST, property.cost)
            put(COLUMN_DESCRIPTION, property.description)
            put(COLUMN_PROPERTY_IMAGE, property.propertyImage)
            put(COLUMN_USERNAME, property.username)

        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // this function is used to query all the elements in the database
    fun getAllProperties(): List<Property>{
        val propertyList = mutableListOf<Property>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME" //getting all values from the table
        val cursor = db.rawQuery(query, null) // result is stored in the cursor

        while(cursor.moveToNext()) { // we iterate through the cursor to see the results
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
            val cost = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COST))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val image = cursor.getBlobOrNull(cursor.getColumnIndexOrThrow(COLUMN_PROPERTY_IMAGE))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))

            val property = Property(id, title, location, cost, description, image, username)
            propertyList.add(property)
        }
        cursor.close()
        db.close()
        return propertyList

    }

    // this function is used to query items that is searched in the search bar
    fun getQueriedProperties(search: String): List<Property> {
        val propertyList = mutableListOf<Property>()
        val db = readableDatabase

        // Modify the query to search through title, location, cost, and username (seller name)
        val query = """
        SELECT * FROM $TABLE_NAME
        WHERE $COLUMN_TITLE LIKE ? OR
              $COLUMN_LOCATION LIKE ? OR
              $COLUMN_COST LIKE ? OR
              $COLUMN_USERNAME LIKE ?
    """

        // Using the same search string for all four fields
        val cursor = db.rawQuery(query, arrayOf("%$search%", "%$search%", "%$search%", "%$search%"))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
            val cost = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COST))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val image = cursor.getBlobOrNull(cursor.getColumnIndexOrThrow(COLUMN_PROPERTY_IMAGE))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))

            val property = Property(id, title, location, cost, description, image, username)
            propertyList.add(property)
        }

        cursor.close()
        db.close()
        return propertyList
    }

    // this function will delete a specified item within the database
    fun deleteItem(id: Int){
        val db = this.writableDatabase

        val clause = "id = ?"
        val noteID = arrayOf(id.toString())

        // deleting the item with this ID
        db.delete("eternalslumber", clause, noteID)

        db.close()
    }

    //this function will edit a specified item within the database
    fun updateProperty( id : Int, title: String, location: String, cost: String, description: String, propertyImage: ByteArray?){
        val db = this.writableDatabase //setting up to write to the database

        // storing all the values associated with their column names
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_LOCATION, location)
            put(COLUMN_COST, cost)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_PROPERTY_IMAGE, propertyImage)
        }

        // updating the note based on the id passed in
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        // updating the note
        db.update(TABLE_NAME, values, selection, selectionArgs)

        db.close()
    }

    // This function checks if a property with the given ID exists
    fun propertyExists(id: Int): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        val exists = cursor.moveToFirst() && cursor.getInt(0) > 0
        cursor.close()
        db.close()
        return exists
    }

    fun registerUser (username: String, password: String): Boolean {
        val db = writableDatabase

        // checks if user exists in the database
        val cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", arrayOf(username))
        if (cursor.count > 0) {
            cursor.close()
            db.close()
            return false
        }
        cursor.close()

        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }

        val result = db.insert("users", null, values)

        db.close()

        // checks if the operation above was successful. true if successes, false if failed
        return result != -1L
    }

    fun loginUser(username: String, password: String): Boolean {

        val db = readableDatabase
        val query = "SELECT * FROM users WHERE username = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        // If entry exists, login successful, return true
        val loginSuccess = cursor.moveToFirst()

        cursor.close()
        db.close()

        return loginSuccess
    }

    fun insertReview(title: String, rating: Int, description: String, userId: Int, propertyId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("rating", rating)
            put("description", description)
            put("user_id", userId)
            put("property_id", propertyId)
        }
        db.insert("reviews", null, values)
        db.close()
    }

    fun getReviewsForProperty(propertyId: Int): List<Review> {
        val reviewList = mutableListOf<Review>()
        val db = readableDatabase
        val query = "SELECT * FROM reviews WHERE property_id = ?"
        val cursor = db.rawQuery(query, arrayOf(propertyId.toString()))

        while (cursor.moveToNext()) {
            val reviewId = cursor.getInt(cursor.getColumnIndexOrThrow("review_id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
            val propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"))

            val review = Review(reviewId, title, rating, description, userId, propertyId)
            reviewList.add(review)
        }

        cursor.close()
        db.close()
        return reviewList
    }

    // Delete a review from the reviews table by review_id
    fun deleteReview(reviewId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_REVIEW_ID = ?"
        val whereArgs = arrayOf(reviewId.toString())

        // Perform the delete operation
        db.delete(TABLE_REVIEWS, whereClause, whereArgs)
        db.close()
    }

    data class Review(
        val reviewId: Int,
        val title: String,
        val rating: Int,
        val description: String,
        val userId: Int,
        val propertyId: Int
    )
}