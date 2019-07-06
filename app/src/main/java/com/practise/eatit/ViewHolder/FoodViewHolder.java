package com.practise.eatit.ViewHolder;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practise.eatit.R;
import com.practise.eatit.interfaces.ItemClickListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView foodNameTV;
    public ImageView foodImageView, favIV;

    public ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        foodNameTV = itemView.findViewById(R.id.foodNameTextView);
        foodImageView = itemView.findViewById(R.id.foodImageView);
        favIV = itemView.findViewById(R.id.fav);

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
