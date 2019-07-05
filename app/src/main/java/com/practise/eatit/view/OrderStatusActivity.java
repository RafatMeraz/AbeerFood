package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.practise.eatit.R;
import com.practise.eatit.ViewHolder.OrderViewHolder;
import com.practise.eatit.interfaces.ItemClickListener;
import com.practise.eatit.model.Request;
import com.practise.eatit.utils.Common;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class OrderStatusActivity extends AppCompatActivity {
    public RecyclerView.LayoutManager layoutManager;

    public RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Init Firebase
        database  = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.ordersRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (Common.isConnectedToInternet(getApplicationContext())) {
            loadOrders(Common.currentUser.getUserPhoneNum());
        }
    }

    private void loadOrders(String phone) {

        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(requests.orderByChild("phone").equalTo(phone),
                                Request.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i, @NonNull final Request request) {
                orderViewHolder.orderIdTV.setText(adapter.getRef(i).getKey());
                orderViewHolder.orderStatusTV.setText(convertCodeToStatus(request.getStatus()));
                orderViewHolder.orderAddressTV.setText(request.getAddress());
                orderViewHolder.orderPhoneTV.setText(request.getPhone());

                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {
                        //Do nothing
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private String convertCodeToStatus(String status) {
        if (status.equals("0")){
            return "Placed";
        } else if (status.equals("1")){
            return "On my Way";
        } else {
            return "Shipped";
        }
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
}
