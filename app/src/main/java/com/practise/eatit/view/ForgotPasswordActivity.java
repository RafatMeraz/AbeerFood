package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.practise.eatit.R;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private AppCompatEditText emailET;
    private Button resetPassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_layout);

        initialization();
    }

    private void initialization() {

        emailET = findViewById(R.id.forgotPassEmailET);
        resetPassButton = findViewById(R.id.resetPassButton);

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()){
                    emailET.setError("Enter a valid email address!");
                } else if (emailET.getText().toString().isEmpty()){
                    emailET.setError("Enter your email address!");
                } else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DynamicToast.makeSuccess(getApplicationContext(), "Reset password link has been send to your email. Please check your Mail.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    DynamicToast.makeError(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }
}
