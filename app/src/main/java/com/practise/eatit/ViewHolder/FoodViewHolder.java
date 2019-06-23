package com.practise.eatit.ViewHolder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practise.eatit.R;
import com.practise.eatit.interfaces.ItemClickListener;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView foodNameTV;
    public ImageView foodImageView;

    public ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        foodNameTV = itemView.findViewById(R.id.foodNameTextView);
        foodImageView = itemView.findViewById(R.id.foodImageView);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.OnClick(v, getAdapterPosition(), false);
    }
}
