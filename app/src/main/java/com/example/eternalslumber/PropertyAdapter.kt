package com.example.eternalslumber

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView

class PropertyAdapter(
    private var propertyList: List<Property>,
    private val context: Context,
) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>(){

    // assigning all the values of the Property data object
    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val propertyImage: ImageView = itemView.findViewById(R.id.propertyImageView)
        val title: TextView = itemView.findViewById(R.id.titleTextView)
        val location: TextView = itemView.findViewById(R.id.locationTextView)
        val cost: TextView = itemView.findViewById(R.id.costTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
        val username: TextView = itemView.findViewById(R.id.usernameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.propertyitem, parent, false)
        return PropertyViewHolder(view)
    }

    // returns the size of the list
    override fun getItemCount(): Int = propertyList.size

    // this method assigns all the values to each note
    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = propertyList[position]

        // converting the BLOB to a bit map so that we can set the imageView
        property.propertyImage?.let { imageBytes ->
            // this decodes the BLOB stored from the database into a bit map
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.propertyImage.setImageBitmap(bitmap)
        }

        holder.title.text = property.title
        holder.location.text = property.location
        holder.cost.text = property.cost
        holder.description.text = property.description
        holder.username.text = "Created by: ${property.username}"

        // making the function to handle if the item has been clicked
        holder.itemView.setOnClickListener(){
            // allow the user to view it add new activity/layout
        }

    }

    // this method is to update the data when a new item is added to the database
    fun refreshData(propertyList: List<Property>){
        this.propertyList = propertyList
        notifyDataSetChanged()
    }
}