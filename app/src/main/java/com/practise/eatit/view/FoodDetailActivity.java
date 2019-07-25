package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.practise.eatit.R;
import com.practise.eatit.database.DatabaseHandler;
import com.practise.eatit.model.Food;
import com.practise.eatit.model.Order;
import com.practise.eatit.model.Rating;
import com.practise.eatit.utils.Common;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetailActivity extends AppCompatActivity implements RatingDialogListener {

    private TextView foodNameTV, foodPriceTv, foodDescription;
    private ImageView foodImageView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton ratingButton;
    private CounterFab cartButton;
    ElegantNumberButton numberButton;
    private RatingBar ratingBar;
    private Button showCommentButton;

    String foodId = "";
    Food currentFood;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference, ratingDR;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        initialization();
    }

    private void initialization() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Foods");
        ratingDR = database.getReference("Rating");
        databaseHandler = new DatabaseHandler(this);

        numberButton = findViewById(R.id.numberButton);
        cartButton = findViewById(R.id.btnCart);
        foodDescription = findViewById(R.id.food_description);
        foodNameTV = findViewById(R.id.food_name);
        foodPriceTv = findViewById(R.id.food_price);
        foodImageView = findViewById(R.id.img_food);
        ratingButton = findViewById(R.id.btnRating);
        ratingBar = findViewById(R.id.ratingBar);
        showCommentButton = findViewById(R.id.showCommentButton);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppBar);

        if (getIntent() != null){
            foodId = getIntent().getStringExtra("foodId");
        }
        if (!foodId.isEmpty()){
            if (Common.isConnectedToInternet(getApplicationContext())) {
                getFoodDetails(foodId);
                getRatingFood(foodId);
            } else {
                DynamicToast.makeError(getApplicationContext(), "Please turn on your internet", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                );
                databaseHandler.addCart(order);
                DynamicToast.makeSuccess(getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        cartButton.setCount(databaseHandler.getCountCart());

        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
        showCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FoodDetailActivity.this, ShowCommentActivity.class);
                intent.putExtra(Common.INTENT_FOOD_ID, foodId);
                startActivity(intent);
            }
        });

    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingDR.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum =0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum +=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0){
                    float average = sum/count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.biscay)
                .setDescriptionTextColor(R.color.tel)
                .setHint("Please write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentBackgroundColor(R.color.glass_white)
                .create(this)
                .show();
    }

    private void getFoodDetails(final String foodId) {
        databaseReference.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                collapsingToolbarLayout.setTitle(currentFood.getName());
                Picasso.get().load(currentFood.getImage()).into(foodImageView);
                foodNameTV.setText(currentFood.getName());
                foodPriceTv.setText(currentFood.getPrice());
                foodDescription.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        final Rating rating = new Rating(Common.currentUser.getUserPhoneNum(),
                foodId,
                String.valueOf(i),
                s,
                FirebaseAuth.getInstance().getCurrentUser().getEmail());

        ratingDR.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DynamicToast.makeSuccess(getApplicationContext(), "Thanks for your Feedback!", Toast.LENGTH_SHORT).show();

                    }
                });

/*        ratingDR.child(Common.currentUser.getUserPhoneNum()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getUserPhoneNum()).exists()){
                    ratingDR.child(Common.currentUser.getUserPhoneNum()).removeValue();
                    ratingDR.child(Common.currentUser.getUserPhoneNum()).setValue(rating);
                } else {
                    ratingDR.child(Common.currentUser.getUserPhoneNum()).setValue(rating);
                }
                DynamicToast.makeSuccess(getApplicationContext(), "Thanks for your review!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


    }

    public void showRatingDialog(View view) {
        showRatingDialog();
    }
}
