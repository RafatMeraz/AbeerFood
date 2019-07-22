package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.practise.eatit.R;
import com.practise.eatit.ViewHolder.CartAdapter;
import com.practise.eatit.database.DatabaseHandler;
import com.practise.eatit.model.MyResponse;
import com.practise.eatit.model.Notification;
import com.practise.eatit.model.Order;
import com.practise.eatit.model.Request;
import com.practise.eatit.model.Sender;
import com.practise.eatit.model.Token;
import com.practise.eatit.model.User;
import com.practise.eatit.remote.APISerivice;
import com.practise.eatit.utils.Common;
import com.practise.eatit.utils.CurrentUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference, userDataRef;
    public TextView totalPriceTextView;
    private Button placeOrderButton;
    private List<Order> carts = new ArrayList<>();
    private CartAdapter adapter;
    private DatabaseHandler db;
    private FirebaseAuth firebaseAuth;
    private User currentUser;
    private APISerivice mService;
    private Place shippingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initialization();

    }

    private void initialization() {
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Requests");
        userDataRef = database.getReference("User");
        db = new DatabaseHandler(this);
        currentUser = new CurrentUser().getUserData();
        String apiKey = "AIzaSyB-4snKGGRTk9svQJyFHIz0P3kcEa16J3k";
        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        mService = Common.getFCMService();

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalPriceTextView = findViewById(R.id.totalTextView);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carts.size() > 0){
                    showAlertDialouge();
                } else {
                    DynamicToast.makeError(getApplicationContext(), "You haven't choose any food for order!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loadUserData();
        loadFoodList();

    }

    private void loadUserData() {
        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.child(firebaseAuth.getCurrentUser().getUid()).getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showAlertDialouge() {

        AlertDialog.Builder alertDia = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alertDia.setTitle("One more step!");
        alertDia.setMessage("Enter your address: ");

        View order_address_comment_view = this.getLayoutInflater().inflate(R.layout.order_address_comment, null);


        final EditText addressET = order_address_comment_view.findViewById(R.id.orderAddressET);


        //Start

        // Initialize the AutocompleteSupportFragment.
//        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//        autocompleteFragment.setCountry("BD");
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                shippingAddress = place;
//                DynamicToast.makeSuccess(getApplicationContext(), ""+place.getAddress(), Toast.LENGTH_SHORT).show();
//                getSupportFragmentManager().beginTransaction()
//                        .remove(getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment))
//                        .commit();
//
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                DynamicToast.makeSuccess(getApplicationContext(), ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//                getSupportFragmentManager().beginTransaction()
//                        .remove(getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment))
//                        .commit();
//            }
//        });

        //Finish


        final EditText commentET = order_address_comment_view.findViewById(R.id.commentET);

        alertDia.setView(order_address_comment_view);
        alertDia.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDia.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        currentUser.getUserPhoneNum(),
                        currentUser.getUserName(),
                        addressET.getText().toString(),
                        totalPriceTextView.getText().toString(),
                        "0",
                        firebaseAuth.getCurrentUser().getUid(),
                        commentET.getText().toString(),
                        carts
                );
                String orderNumber = String.valueOf(System.currentTimeMillis());
                databaseReference.child(orderNumber)
                        .setValue(request);
                db.deleteCarts();
                finish();
                sendNotificationOrder(orderNumber);
            }
        });

        alertDia.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                getSupportFragmentManager().beginTransaction()
//                        .remove(getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment))
//                        .commit();
            }
        });

        alertDia.show();
    }

    private void sendNotificationOrder(final String orderNumber) {
        DatabaseReference tokenDR = FirebaseDatabase.getInstance().getReference("Tokens");
        Query dataQuery = tokenDR.orderByChild("serverToken").equalTo(true);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postDatasnapShot: dataSnapshot.getChildren()){
                    Token serverToken = postDatasnapShot.getValue(Token.class);
                    //create raw playload to send
                    Notification notification = new Notification("Abeer Food", "You have new order "+orderNumber);
                    Sender content = new Sender(serverToken.getToken(), notification);

//                    mService.sendNotification(content)
//                            .enqueue(new Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                                    if (response.code() == 200) {
//                                        if (response.body().success == 1) {
//                                            DynamicToast.makeSuccess(getApplicationContext(), "Thank you , Order Place", Toast.LENGTH_SHORT).show();
//                                            finish();
//                                        } else {
//                                            DynamicToast.makeError(getApplicationContext(), "Failed!!!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//                                    Log.e("ERROR", t.getMessage());
//                                }
//                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFoodList() {
        carts = db.getAllOrders();
        adapter = new CartAdapter(carts, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for (Order order: carts){
            total+=(Integer.parseInt(order.getPrice())*(Integer.parseInt(order.getQuantity())));
        }
        Locale locale = new Locale("en", "US");
        NumberFormat frm = NumberFormat.getCurrencyInstance(locale);
        totalPriceTextView.setText(frm.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int order) {
        carts.remove(order);
        db.deleteCarts();
        for (Order o : carts){
            db.addCart(o);
        }
        loadFoodList();
    }
}
