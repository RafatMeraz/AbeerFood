package com.practise.eatit.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.practise.eatit.R;
import com.practise.eatit.utils.Common;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fullNameET, phoneET, addressET, mailET;
    private TextView changePasswordTV;
    private Button saveButton;
    private ImageView backButtonIV;
    private CircleImageView userImgIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialization();
    }

    private void initialization() {
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
        Picasso.get().load(Common.currentUser.getImage()).into(userImgIV);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButtonIV:
                finish();
                break;
        }
    }
}
