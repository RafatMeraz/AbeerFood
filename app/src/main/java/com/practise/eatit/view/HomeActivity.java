package com.practise.eatit.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.practise.eatit.R;
import com.practise.eatit.ViewHolder.MenuViewHolder;
import com.practise.eatit.interfaces.ItemClickListener;
import com.practise.eatit.model.Category;
import com.practise.eatit.model.Token;
import com.practise.eatit.model.User;
import com.practise.eatit.utils.Common;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userDataRef, categoryDataRef;
    private User user;
    private TextView navEmailTV, navUserNameTV;
    private RecyclerView menuRecyclerView;
    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.cartFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        navEmailTV = headerView.findViewById(R.id.navUserEmailTV);
        navUserNameTV = headerView.findViewById(R.id.navUserNameTV);

        initialization();

    }

    @SuppressLint("ResourceAsColor")
    private void initialization() {
        //firebase initialization
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userDataRef = database.getReference("User");
        categoryDataRef = database.getReference("Categories");
        menuRecyclerView = findViewById(R.id.homeRecyclerView);
        loadUserData();
        menuRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(menuRecyclerView.getContext(),
                R.anim.layout_fall_down);
        menuRecyclerView.setLayoutAnimation(controller);
        swipeRefreshLayout = findViewById(R.id.homeSwipeLayout);
        swipeRefreshLayout.setColorSchemeColors(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getApplicationContext())){
                    loadMenu();
                    adapter.startListening();
                } else {
                    DynamicToast.makeError(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (Common.isConnectedToInternet(getApplicationContext())){
            loadMenu();
        } else {

        }
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference("Tokens");
        Token tokenData = new Token(token, false);
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(tokenData);
    }


    private void loadMenu() {

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(categoryDataRef, Category.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int i, @NonNull Category category) {

                Log.e("DATA: ", category.getName());

                menuViewHolder.menuTV.setText(category.getName());
                Picasso.get().load(category.getImage()).into(menuViewHolder.menuIV);

                final Category clickItem = category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {
                    Intent foodListIntent = new Intent(getApplicationContext(), FoodList.class);
                    foodListIntent.putExtra("categoryId", adapter.getRef(position).getKey());
                    startActivity(foodListIntent);
                    }
                });
            }
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);

                return new MenuViewHolder(view);
            }

        };
        adapter.startListening();
        menuRecyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        menuRecyclerView.getAdapter().notifyDataSetChanged();
        menuRecyclerView.scheduleLayoutAnimation();
    }

    private void loadUserData() {
        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                if (user != null){
                    Common.currentUser = user;
                    navUserNameTV.setText(user.getUserName());
                    navEmailTV.setText(mAuth.getCurrentUser().getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadMenu();
            adapter.startListening();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            intent = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_orders) {
            intent = new Intent(getApplicationContext(), OrderStatusActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_log_out) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        if (Common.isConnectedToInternet(getApplicationContext())){
            adapter.startListening();
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Common.currentUser = null;
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Common.isConnectedToInternet(getApplicationContext())){
            adapter.stopListening();
        }
    }
}
