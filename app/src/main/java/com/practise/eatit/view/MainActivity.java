package com.practise.eatit.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.practise.eatit.R;
import com.practise.eatit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private FirebaseUser  currentUser;

    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initialization();
    }

    private void initialization() {
        //Getting current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //adding font in activity
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.rar");
        mainBinding.taglineTextView.setTypeface(typeface);

        mainBinding.signInButton.setOnClickListener(this);
        mainBinding.signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.signInButton:
                intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.signUpButton:
                intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser!=null){
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }
}