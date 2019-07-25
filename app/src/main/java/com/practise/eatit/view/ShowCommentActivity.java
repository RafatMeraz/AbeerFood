package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.internal.CallbackManagerImpl;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.practise.eatit.R;
import com.practise.eatit.ViewHolder.ShowCommentViewHolder;
import com.practise.eatit.model.Rating;
import com.practise.eatit.utils.Common;

public class ShowCommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;
    String foodId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);

        initialization();
    }

    private void initialization() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference("Rating");
        recyclerView = findViewById(R.id.showCommentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSwipeRefreshLayout = findViewById(R.id.showCommentSwipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null) {
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                }
                if (!foodId.isEmpty() && foodId!=null) {
                    Query query = mDatabaseRef.orderByChild("foodId").equalTo(foodId);
                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query, Rating.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder showCommentViewHolder, int i, @NonNull Rating rating) {
                            showCommentViewHolder.userEmailTV.setText(rating.getEmail());
                            showCommentViewHolder.userCommentTV.setText(rating.getComment());
                            showCommentViewHolder.ratingBar.setRating(Float.parseFloat(rating.getRateValue()));
                        }

                        @NonNull
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_comment_layout, parent, false);
                            return new ShowCommentViewHolder(view);
                        }
                    };

                    loadComment(foodId);
                }
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (getIntent() != null) {
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                }
                if (!foodId.isEmpty() && foodId!=null) {
                    Query query = mDatabaseRef.orderByChild("foodId").equalTo(foodId);
                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query, Rating.class)
                            .build();

                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder showCommentViewHolder, int i, @NonNull Rating rating) {
                            showCommentViewHolder.userEmailTV.setText(rating.getEmail());
                            showCommentViewHolder.userCommentTV.setText(rating.getComment());
                            showCommentViewHolder.ratingBar.setRating(Float.parseFloat(rating.getRateValue()));
                        }

                        @NonNull
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_comment_layout, parent, false);
                            return new ShowCommentViewHolder(view);
                        }
                    };

                    loadComment(foodId);
                }
            }
        });
    }

    private void loadComment(String foodId) {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.startListening();
    }
}
