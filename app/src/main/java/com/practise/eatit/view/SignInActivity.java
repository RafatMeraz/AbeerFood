package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practise.eatit.HomeActivity;
import com.practise.eatit.R;
import com.practise.eatit.databinding.ActivitySignInBinding;
import com.practise.eatit.model.User;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import javax.xml.datatype.Duration;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivitySignInBinding signInBinding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        initialization();
    }

    private void initialization() {
        mAuth = FirebaseAuth.getInstance();

        signInBinding.signInSignButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signInSignButton:
                userSignIn();
                break;
        }
    }

    private void userSignIn() {
        final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(signInBinding.signInEmailEditText.getText().toString(), signInBinding.signInPassEditText.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        DynamicToast.makeSuccess(getApplicationContext(), "Sign In Success!", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                DynamicToast.makeError(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
