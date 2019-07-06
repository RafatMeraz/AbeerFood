package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.practise.eatit.R;
import com.practise.eatit.ViewHolder.FoodViewHolder;
import com.practise.eatit.ViewHolder.MenuViewHolder;
import com.practise.eatit.database.DatabaseHandler;
import com.practise.eatit.interfaces.ItemClickListener;
import com.practise.eatit.model.Category;
import com.practise.eatit.model.Food;
import com.practise.eatit.utils.Common;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String categoryId = "";
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    List<String> suggestList = new ArrayList<>();
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    MaterialSearchBar materialSearchBar;
    private DatabaseHandler localDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        initialization();
    }

    private void initialization() {
        //Firebase initialization
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Foods");
        localDatabase = new DatabaseHandler(this);

        recyclerView = findViewById(R.id.foodListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent() != null){
            categoryId = getIntent().getStringExtra("categoryId");
        }
        if ( !categoryId.isEmpty() && categoryId != null){
            if (Common.isConnectedToInternet(getApplicationContext())){
                loadListFood(categoryId);
            } else {
                DynamicToast.makeError(getApplicationContext(), "Please turn on your internet", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search: suggestList){
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);

                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        FirebaseRecyclerOptions<Food> searchOptions =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(
                                databaseReference.orderByChild("name").equalTo(text.toString())
                                , Food.class)
                        .build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(searchOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder foodViewHolder, final int i, @NonNull final Food food) {
                foodViewHolder.foodNameTV.setText(food.getName());
                Picasso.get().load(food.getImage()).into(foodViewHolder.foodImageView);

                final Food local = food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {
                        Intent foodDetailIntent = new Intent(getApplicationContext(), FoodDetail.class);
                        foodDetailIntent.putExtra("foodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetailIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(view);
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        databaseReference.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood(String categoryId) {
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(
                                databaseReference.orderByChild("menuId").equalTo(categoryId)
                                , Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder foodViewHolder, final int i, @NonNull final Food food) {
                foodViewHolder.foodNameTV.setText(food.getName());
                Picasso.get().load(food.getImage()).into(foodViewHolder.foodImageView);

                if (localDatabase.isFav(adapter.getRef(i).getKey())){
                    foodViewHolder.favIV.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                foodViewHolder.favIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DynamicToast.makeSuccess(getApplicationContext(), "Yayyy!", Toast.LENGTH_SHORT).show();
                        if (!localDatabase.isFav(adapter.getRef(i).getKey())){
                            localDatabase.addFav(adapter.getRef(i).getKey());
                            foodViewHolder.favIV.setImageResource(R.drawable.ic_favorite_black_24dp);
                            DynamicToast.makeSuccess(getApplicationContext(), ""+ food.getName()+" was added to Favourites", Toast.LENGTH_SHORT).show();
                        } else {
                            localDatabase.removeToFavourites(adapter.getRef(i).getKey());
                            foodViewHolder.favIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            DynamicToast.makeSuccess(getApplicationContext(), ""+ food.getName()+" was removed Favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local = food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {
                        Intent foodDetailIntent = new Intent(getApplicationContext(), FoodDetail.class);
                        foodDetailIntent.putExtra("foodId", adapter.getRef(position).getKey());
                        startActivity(foodDetailIntent);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Common.isConnectedToInternet(getApplicationContext())){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Common.isConnectedToInternet(getApplicationContext())){
            adapter.stopListening();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fav){
            DynamicToast.makeSuccess(getApplicationContext(), "Yayyy!", Toast.LENGTH_SHORT).show();
        }
    }
}
