package com.practise.eatit.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.practise.eatit.R;
import com.practise.eatit.databinding.ActivitySignUpBinding;
import com.practise.eatit.model.Food;
import com.practise.eatit.model.User;
import com.practise.eatit.utils.Common;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySignUpBinding signUpBinding;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private final int PICK_IMG_REQUEST = 76;
    private Uri saveUri;
    private FirebaseStorage mStorage;
    private StorageReference mfoodSR;
    private String mainUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        initialization();
    }

    private void initialization() {
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mfoodSR = mStorage.getReference();
        databaseReference = database.getReference("User");
        signUpBinding.signUpSignButton.setOnClickListener(this);
        signUpBinding.signUpAddImgIV.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUpSignButton:
                if (Common.isConnectedToInternet(getApplicationContext())){
                    singUp();
                } else {
                    DynamicToast.makeError(getApplicationContext(), "Please turn on your internet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.signUpAddImgIV:
                chooseImage();
                break;

        }
    }

    private void singUp() {
        dialog.setMessage("Please Wait");
        dialog.show();

        mAuth.createUserWithEmailAndPassword(signUpBinding.signUpEmailEditText.getText().toString()
                , signUpBinding.signUpUserPassEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            saveUserData();
                        } else {
                            dialog.dismiss();
                            DynamicToast.makeError(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            saveUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveUri);
                signUpBinding.signUpAddImgIV.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("ERROR : ", e.getMessage());
            }

        }
    }

    private void saveUserData() {
        uploadImage();
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    FirebaseUser crrentUser = mAuth.getCurrentUser();
//                    User user = new User(signUpBinding.signUpFullNameEditText.getText().toString(),
//                            signUpBinding.signUpUserPassEditText.getText().toString(),
//                            signUpBinding.signUpPhoneNumEditText.getText().toString(),
//                            signUpBinding.signUpEmailEditText.getText().toString(),
//                            signUpBinding.signUpAddressEditText.getText().toString(),
//                            mainUri,
//                            false);
//                    databaseReference.child(crrentUser.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            dialog.dismiss();
//                            DynamicToast.makeSuccess(getApplicationContext(), "SignUp Successful!", Toast.LENGTH_SHORT).show();
//                            finish();
//
//                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            dialog.dismiss();
//                            DynamicToast.makeError(getApplicationContext(), "SignUp with Data is failed!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                 }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void uploadImage(){
        if (saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            final String imageName = UUID.randomUUID().toString();
            final StorageReference imgFolder = mfoodSR.child("users_img/"+imageName);
            imgFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            DynamicToast.makeSuccess(getApplicationContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
                            imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    mainUri = uri.toString();
                                    Log.e("IMG URL: ", mainUri);
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            FirebaseUser crrentUser = mAuth.getCurrentUser();
                                            User user = new User(signUpBinding.signUpFullNameEditText.getText().toString(),
                                                    signUpBinding.signUpUserPassEditText.getText().toString(),
                                                    signUpBinding.signUpPhoneNumEditText.getText().toString(),
                                                    signUpBinding.signUpEmailEditText.getText().toString(),
                                                    signUpBinding.signUpAddressEditText.getText().toString(),
                                                    mainUri,
                                                    false);
                                            databaseReference.child(crrentUser.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dialog.dismiss();
                                                    DynamicToast.makeSuccess(getApplicationContext(), "SignUp Successful!", Toast.LENGTH_SHORT).show();
                                                    finish();

                                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog.dismiss();
                                                    DynamicToast.makeError(getApplicationContext(), "SignUp with Data is failed!"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            DynamicToast.makeError(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+"%");
                        }
                    });
        }
        else {
            DynamicToast.makeError(getApplicationContext(), "Please select an image!", Toast.LENGTH_SHORT).show();
        }
    }

}
