package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
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
import com.practise.eatit.model.Order;
import com.practise.eatit.utils.Common;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;

public class FoodList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String categoryId = "";
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    List<String> suggestList = new ArrayList<>();
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    MaterialSearchBar materialSearchBar;
    private DatabaseHandler localDatabase;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        initialization();
    }

    @SuppressLint("ResourceAsColor")
    private void initialization() {
        //Firebase initialization
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Foods");
        localDatabase = new DatabaseHandler(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        swipeRefreshLayout = findViewById(R.id.foodListSwipeLayout);
        swipeRefreshLayout.setColorSchemeColors(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        recyclerView = findViewById(R.id.foodListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null){
                    categoryId = getIntent().getStringExtra("categoryId");
                }
                if ( !categoryId.isEmpty() && categoryId != null){
                    if (Common.isConnectedToInternet(getApplicationContext())){
                        loadListFood(categoryId);
                        adapter.startListening();
                    } else {
                        DynamicToast.makeError(getApplicationContext(), "Please turn on your internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("categoryId");
        }
        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isConnectedToInternet(getApplicationContext())) {
                loadListFood(categoryId);
            } else {
                DynamicToast.makeError(getApplicationContext(), "Please turn on your internet", Toast.LENGTH_SHORT).show();
                return;
            }
        }

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
        swipeRefreshLayout.setRefreshing(true);
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
                foodViewHolder.priceTV.setText(String.format("$ %s", food.getPrice()));
                Picasso.get().load(food.getImage()).into(foodViewHolder.foodImageView);

                if (localDatabase.isFav(adapter.getRef(i).getKey())){
                    foodViewHolder.favIV.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                foodViewHolder.favIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDatabase.isFav(adapter.getRef(i).getKey())){
                            localDatabase.addFav(adapter.getRef(i).getKey());
                            foodViewHolder.favIV.setImageResource(R.drawable.ic_favorite_black_24dp);
                            DynamicToast.makeSuccess(getApplicationContext(), ""+ food.getName()+" was added to Favourites", Toast.LENGTH_SHORT).show();
                        } else {
                            localDatabase.removeToFavourites(adapter.getRef(i).getKey());
                            foodViewHolder.favIV.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            DynamicToast.makeSuccess(getApplicationContext(), ""+ food.getName()+" was removed from Favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                foodViewHolder.shareIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DynamicToast.makeSuccess(getApplicationContext(), "Share button clicked!", Toast.LENGTH_SHORT).show();
                        shareImage(food.getImage(), getApplicationContext());
                    }
                });

                foodViewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Order order = new Order(
                                adapter.getRef(i).getKey(),
                                food.getName(),
                                "1",
                                food.getPrice(),
                                food.getDiscount(),
                                food.getImage()
                        );
                        localDatabase.addCart(order);
                        DynamicToast.makeSuccess(getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();

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
        swipeRefreshLayout.setRefreshing(false);
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

    static public void shareImage(String url, final Context context) {
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i =
                        new Intent(Intent.ACTION_SEND);
                i.setType(
                        "image/*");
                i.putExtra(Intent.
                        EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                context.startActivity(Intent.
                        createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }
    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri =
                null;

        try {
            File file =
                    new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out =
                    new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.
                    PNG, 90, out);
            out.close();
            bmpUri = Uri.
                    fromFile(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return bmpUri;
    }
}
