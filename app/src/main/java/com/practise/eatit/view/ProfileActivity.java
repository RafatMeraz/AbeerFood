package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.practise.eatit.R;
import com.practise.eatit.model.User;
import com.practise.eatit.utils.Common;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fullNameET, phoneET, addressET, mailET;
    private TextView changePasswordTV;
    private Button saveButton;
    private ImageView backButtonIV;
    private CircleImageView userImgIV;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialization();
    }

    private void initialization() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference("User");
        backButtonIV = findViewById(R.id.backButtonIV);
        fullNameET = findViewById(R.id.editProfileFullNameET);
        phoneET = findViewById(R.id.editProfilePhoneET);
        addressET = findViewById(R.id.editProfileAddressET);
        mailET = findViewById(R.id.editProfileEmailET);
        changePasswordTV = findViewById(R.id.changePassTV);
        saveButton = findViewById(R.id.saveButton);
        userImgIV = findViewById(R.id.circleImageView);

        mailET.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        mailET.setEnabled(false);
        fullNameET.setText(Common.currentUser.getUserName());
        phoneET.setText(Common.currentUser.getUserPhoneNum());
        addressET.setText(Common.currentUser.getAddress());

        backButtonIV.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        Picasso.get().load(Common.currentUser.getImage()).into(userImgIV);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButtonIV:
                finish();
                break;
            case R.id.saveButton:
                saveChanges();
                break;
        }
    }

    private void saveChanges() {
        if (!TextUtils.isEmpty(phoneET.getText().toString()) ) {
            if (!TextUtils.isEmpty(fullNameET.getText().toString())) {
                if (!TextUtils.isEmpty(addressET.getText().toString())) {
                    saveUserData();
                } else {
                    addressET.setError("Enter your address!");
                }
            } else {
                fullNameET.setError("Enter your full name");
            }
        } else {
            phoneET.setError("Please enter your phone number");
        }

    }

    private void saveUserData() {
        final ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
        dialog.setMessage("Updating...");
        dialog.show();

        saveButton.setEnabled(false);
        User user = new User(
                fullNameET.getText().toString(),
                Common.currentUser.getUserPassword(),
                phoneET.getText().toString(),
                Common.currentUser.getEmail(),
                addressET.getText().toString(),
                Common.currentUser.getImage(),
                false
        );
        mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                DynamicToast.makeSuccess(getApplicationContext(), "User info updated successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                DynamicToast.makeError(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        saveButton.setEnabled(true);
    }
}
