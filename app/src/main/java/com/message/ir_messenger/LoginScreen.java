package com.message.ir_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    TextView lstxtSignUp;
    EditText lsedtTxtMail, lsedtTxtPass;
    Button lsbtnLogin;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        // If user is already logged in, navigate to MainActivity
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        lsedtTxtMail = findViewById(R.id.lsedtTxtMail);
        lsedtTxtPass = findViewById(R.id.lsedtTxtPass);
        lsbtnLogin = findViewById(R.id.lsbtnLogin);
        lstxtSignUp = findViewById(R.id.lstxtSignUp);

        lstxtSignUp.setOnClickListener(v -> {
            Intent LSIntent = new Intent(LoginScreen.this, SignUpScreen.class);
            startActivity(LSIntent);
            finish();
        });

        lsbtnLogin.setOnClickListener(view -> {
            String Email = lsedtTxtMail.getText().toString();
            String Pass = lsedtTxtPass.getText().toString();

            if (TextUtils.isEmpty(Email)) {
                lsedtTxtMail.setError("Please enter your email");
            } else if (TextUtils.isEmpty(Pass)) {
                lsedtTxtPass.setError("Please enter your password");
            } else if (!Email.matches(emailPattern)) {
                lsedtTxtMail.setError("Please provide a valid email address");
            } else if (lsedtTxtPass.length() < 6) {
                lsedtTxtPass.setError("Password should be more than 6 characters");
            } else {
                progressDialog.show();
                auth.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Intent LMintent = new Intent(LoginScreen.this, MainActivity.class);
                        startActivity(LMintent);
                        finish();
                    } else {
                        Toast.makeText(LoginScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }); 
    }
}
