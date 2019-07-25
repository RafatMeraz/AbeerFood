package com.practise.eatit.ViewHolder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practise.eatit.R;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {
    public TextView userEmailTV, userCommentTV;
    public RatingBar ratingBar;

    public ShowCommentViewHolder(@NonNull View itemView) {
        super(itemView);
        userEmailTV = itemView.findViewById(R.id.showCommentEmailTV);
        userCommentTV = itemView.findViewById(R.id.showCommentCmtTV);
        ratingBar = itemView.findViewById(R.id.showCommentRatingBar);
    }
}
