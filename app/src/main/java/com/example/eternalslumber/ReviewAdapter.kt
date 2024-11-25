package com.example.eternalslumber

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(
    private var reviewList: List<Review>,
    private val context: Context
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    // ViewHolder class for Review
    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.reviewTitleTextView)
        val rating: TextView = itemView.findViewById(R.id.ratingTextView)
        val description: TextView = itemView.findViewById(R.id.reviewDescriptionTextView)
        val userId: TextView = itemView.findViewById(R.id.userIdTextView)
        val propertyId: TextView = itemView.findViewById(R.id.propertyIdTextView)
    }

    // Creates a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    // Returns the size of the list
    override fun getItemCount(): Int = reviewList.size

    // Bind the review data to the views
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]

        holder.title.text = review.title
        holder.rating.text = "Rating: ${review.rating}/5"
        holder.description.text = review.description
        holder.userId.text = "User ID: ${review.userId}"
        holder.propertyId.text = "Property ID: ${review.propertyId}"

        // Handle item click if needed
        holder.itemView.setOnClickListener {
            // Handle the click event (e.g., open detailed view for the review)
        }
    }

    // Method to refresh the data when the list changes
    fun refreshData(reviewList: List<Review>) {
        this.reviewList = reviewList
        notifyDataSetChanged()
    }
}