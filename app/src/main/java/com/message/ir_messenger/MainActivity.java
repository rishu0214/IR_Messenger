package com.message.ir_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainRV;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView mainimgEx;
    ImageView mainimgCa, mainimgCh, mainimgSe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Authentication and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mainimgCa = findViewById(R.id.mainimgCa);
        mainimgSe = findViewById(R.id.mainimgSe);
        mainimgCh = findViewById(R.id.mainimgCh);


        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginScreen.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        mainRV = findViewById(R.id.mainRV);
        mainRV.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ArrayList and Adapter
        usersArrayList = new ArrayList<>();
        adapter = new UserAdapter(MainActivity.this, usersArrayList);
        mainRV.setAdapter(adapter);


        DatabaseReference reference = database.getReference().child("user");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear(); // Clear old data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users != null) { // Ensure the user object is not null
                        usersArrayList.add(users);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error if needed (optional)
            }
        });

        ImageView mainimgEx = findViewById(R.id.mainimgEx);
        mainimgEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
                dialog.setContentView(R.layout.dialog_layout);
                Button No, Yes;
                Yes = dialog.findViewById(R.id.Yesbtn);
                No = dialog.findViewById(R.id.Nobtn);
                Yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        Intent MLIntent = new Intent(MainActivity.this, LoginScreen.class);
                        startActivity(MLIntent);
                        finish();
                    }
                });
                No.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mainimgSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MSIntent = new Intent(MainActivity.this, setting.class);
                startActivity(MSIntent);
            }
        });

        mainimgCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            }
        });

    }
}
