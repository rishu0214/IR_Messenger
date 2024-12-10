package com.message.ir_messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpScreen extends AppCompatActivity {

    TextView sstxtLogIn;
    CircleImageView profilerg;
    EditText ssedtTxtUserName, ssedtTxtMail, ssedtTxtPass, ssedtTxtReEPass;
    Button ssbtnSignUp;
    FirebaseAuth auth;
    Uri imageURI;
    String imageuri;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Account Creating...");
        progressDialog.setCancelable(false);

        // Initialize views
        sstxtLogIn = findViewById(R.id.sstxtLogIn);
        ssedtTxtUserName = findViewById(R.id.ssedtTxtUserName);
        ssedtTxtMail = findViewById(R.id.ssedtTxtMail);
        ssedtTxtPass = findViewById(R.id.ssedtTxtPass);
        ssedtTxtReEPass = findViewById(R.id.ssedtTxtReEPass);
        ssbtnSignUp = findViewById(R.id.ssbtnSignUp);
        profilerg = findViewById(R.id.profilerg);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Redirect to Login Screen
        sstxtLogIn.setOnClickListener(v -> {
            Intent SLIntent = new Intent(SignUpScreen.this, LoginScreen.class);
            startActivity(SLIntent);
            finish();
        });

        // Sign Up Button
        ssbtnSignUp.setOnClickListener(view -> {
            String namee = ssedtTxtUserName.getText().toString();
            String emaill = ssedtTxtMail.getText().toString();
            String password = ssedtTxtPass.getText().toString();
            String rPassword = ssedtTxtReEPass.getText().toString();

            if (TextUtils.isEmpty(namee) || TextUtils.isEmpty(emaill) || TextUtils.isEmpty(password) || TextUtils.isEmpty(rPassword)) {
                Toast.makeText(SignUpScreen.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
            } else if (!emaill.matches(emailPattern)) {
                ssedtTxtMail.setError("Type A Valid Email Here");
            } else if (password.length() < 6) {
                ssedtTxtPass.setError("Password must be 6 characters or more");
            } else if (!password.equals(rPassword)) {
                ssedtTxtReEPass.setError("Passwords do not match");
            } else {
                progressDialog.show();
                auth.createUserWithEmailAndPassword(emaill, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("upload").child(id);

                                if (imageURI != null) {
                                    // Upload profile image
                                    storageReference.putFile(imageURI)
                                            .addOnCompleteListener(uploadTask -> {
                                                if (uploadTask.isSuccessful()) {
                                                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                        imageuri = uri.toString();
                                                        Users users = new Users(id, namee, emaill, password, imageuri, "Hey!! I am online");
                                                        createUser(reference, users);
                                                    }).addOnFailureListener(e -> {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SignUpScreen.this, "Error getting image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SignUpScreen.this, "Image upload failed: " + uploadTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Use default image
                                    imageuri = Uri.parse("android.resource://" + getPackageName() + "/drawable/man").toString();
                                    Users users = new Users(id, namee, emaill, password, imageuri, "Hey!! I am online");
                                    createUser(reference, users);
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Profile image selection
        profilerg.setOnClickListener(view -> {
            Intent GIntent = new Intent();
            GIntent.setType("image/*");
            GIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(GIntent, "Select Picture"), 10);
        });
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            imageURI = data.getData();
            profilerg.setImageURI(imageURI);
        }
    }

    // Create user in database
    private void createUser(DatabaseReference reference, Users users) {
        reference.setValue(users).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(SignUpScreen.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                Intent SMIntent = new Intent(SignUpScreen.this, MainActivity.class);
                startActivity(SMIntent);
                finish();
            } else {
                Toast.makeText(SignUpScreen.this, "Error in creating user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
