package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practise.eatit.R;
import com.practise.eatit.ViewHolder.CartAdapter;
import com.practise.eatit.database.DatabaseHandler;
import com.practise.eatit.model.Order;
import com.practise.eatit.model.Request;
import com.practise.eatit.model.User;
import com.practise.eatit.utils.Common;
import com.practise.eatit.utils.CurrentUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference, userDataRef;
    private TextView totalPriceTextView;
    private Button placeOrderButton;
    private List<Order> carts = new ArrayList<>();
    private CartAdapter adapter;
    private DatabaseHandler db;
    private FirebaseAuth firebaseAuth;
    private User currentUser;

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
        final EditText addressET = order_address_comment_view.findViewById(R.id.addressET);
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
                databaseReference.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                db.deleteCarts();
                DynamicToast.makeSuccess(getApplicationContext(),"Thank you, Order Placed Sucess!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDia.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDia.show();
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
