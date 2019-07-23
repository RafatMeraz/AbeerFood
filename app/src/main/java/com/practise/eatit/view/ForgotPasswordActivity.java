package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.practise.eatit.R;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private AppCompatEditText emailET;
    private Button resetPassButton;
    private ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_layout);

        initialization();
    }

    private void initialization() {

        emailET = findViewById(R.id.forgotPassEmailET);
        resetPassButton = findViewById(R.id.resetPassButton);
        backIV = findViewById(R.id.backButtonIV);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetMail();
            }
        });

    }

    private void sendResetMail() {
        final ProgressDialog mDialog = new ProgressDialog(ForgotPasswordActivity.this);
        mDialog.setMessage("Sending Mail...");
        mDialog.show();

        resetPassButton.setEnabled(false);
        resetPassButton.setTextColor(getApplication().getResources().getColor(R.color.softBlack));
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailET.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDialog.dismiss();
                        DynamicToast.makeSuccess(getApplicationContext(), "Reset password link has been send to your email. Please check your Mail.", Toast.LENGTH_SHORT).show();
                        resetPassButton.setEnabled(true);
                        resetPassButton.setTextColor(getApplication().getResources().getColor(android.R.color.white));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        DynamicToast.makeError(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        resetPassButton.setEnabled(true);
                        resetPassButton.setTextColor(getApplication().getResources().getColor(android.R.color.white));
                    }
                });
    }

    private void inputCheck() {
        if (!TextUtils.isEmpty(emailET.getText().toString())) {
            if (Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()) {
                resetPassButton.setEnabled(true);
                resetPassButton.setTextColor(getApplication().getResources().getColor(android.R.color.white));
            } else {
                resetPassButton.setEnabled(false);
                emailET.setError("Enter a valid Email address!");
                resetPassButton.setTextColor(getApplication().getResources().getColor(R.color.softBlack));
            }
        } else {
            resetPassButton.setEnabled(false);
            emailET.setError("Enter your valid Email address!");
            resetPassButton.setTextColor(getApplication().getResources().getColor(R.color.softBlack));
        }
    }
}
