package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.practise.eatit.R;
import com.practise.eatit.databinding.ActivitySignInBinding;
import com.practise.eatit.utils.Common;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

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

        signInBinding.signInEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signInBinding.signInPassEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signInBinding.signInSignButton.setOnClickListener(this);
        signInBinding.forgotPassTV.setOnClickListener(this);
        signInBinding.signInBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(signInBinding.signInEmailEditText.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(signInBinding.signInEmailEditText.getText().toString()).matches()) {
            if (!TextUtils.isEmpty(signInBinding.signInPassEditText.getText().toString()) && (signInBinding.signInPassEditText.getText().toString().length() >= 6)) {
                signInBinding.signInSignButton.setEnabled(true);
                signInBinding.signInSignButton.setTextColor(getApplication().getResources().getColor(android.R.color.white));

            } else {
                signInBinding.signInSignButton.setEnabled(false);
                signInBinding.signInPassEditText.setError("Enter your password more than 6 characters.");
                signInBinding.signInSignButton.setTextColor(getApplication().getResources().getColor(R.color.softBlack));
            }
        } else {
            signInBinding.signInSignButton.setEnabled(false);
            signInBinding.signInEmailEditText.setError("Enter your valid email address.");
            signInBinding.signInSignButton.setTextColor(getApplication().getResources().getColor(R.color.softBlack));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signInSignButton:
                if (Common.isConnectedToInternet(getApplicationContext())){
                    userSignIn();
                } else {
                    DynamicToast.makeError(getApplicationContext(), "Please turn on your internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.forgotPassTV:
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
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
