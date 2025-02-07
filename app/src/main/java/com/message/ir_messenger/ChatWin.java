package com.message.ir_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWin extends AppCompatActivity {

    String receiverImg, receiverUid, receiverName, senderUid;
    CircleImageView profile;
    TextView receiverNName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    public static String senderImg;
    public static String receiveIImg;
    String senderRoom, receiverRoom;
    RecyclerView msgAdapter;
    ArrayList<msgModelClass> messagesArrayList;
    messagesAdapter messagesAdapter;

    DatabaseReference chatreference;
    DatabaseReference reference;
    private ValueEventListener messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        msgAdapter = findViewById(R.id.msgAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgAdapter.setLayoutManager(linearLayoutManager);

        messagesArrayList = new ArrayList<>();
        messagesAdapter = new messagesAdapter(ChatWin.this, messagesArrayList);
        msgAdapter.setAdapter(messagesAdapter);

        receiverName = getIntent().getStringExtra("namee");
        receiverImg = getIntent().getStringExtra("receiverImg");
        receiverUid = getIntent().getStringExtra("uid");

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        profile = findViewById(R.id.profilerg);
        receiverNName = findViewById(R.id.receivername);

        Picasso.get().load(receiverImg).into(profile);
        receiverNName.setText(receiverName);

        senderUid = firebaseAuth.getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        reference = database.getReference().child("user").child(senderUid);
        chatreference = database.getReference().child("chats").child(senderRoom).child("messages");

        messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelClass messages = dataSnapshot.getValue(msgModelClass.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        chatreference.addValueEventListener(messageListener);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue(String.class);
                receiveIImg = receiverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(ChatWin.this, "Enter the message", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                Date date = new Date();
                msgModelClass messages = new msgModelClass(message, senderUid, date.getTime());

                database.getReference().child("chats").child(senderRoom).child("messages").push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    database.getReference().child("chats").child(receiverRoom).child("messages").push()
                                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.e("ChatWin", "Error writing to receiverRoom: " + task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Log.e("ChatWin", "Error writing to senderRoom: " + task.getException());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatWin.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatreference != null && messageListener != null) {
            chatreference.removeEventListener(messageListener);
        }
    }
}
