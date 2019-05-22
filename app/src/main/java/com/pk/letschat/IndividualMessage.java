package com.pk.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class IndividualMessage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle Toggle;
    EditText typeMessage;
    ImageButton sendButton;
    FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    public static String receiverNameString;
    boolean isChatpageOpened=false;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_message);


        recyclerView=findViewById(R.id.list);

        FirebaseApp.initializeApp(this);
        FirebaseApp.initializeApp(IndividualMessage.this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        drawerLayout=findViewById(R.id.drawerLayoutIndividualMessage);
        Toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        Toggle.syncState();

        final String user1=currentUser.getUid();
        final String user2;
        String myToken=currentUser.getUid();
        String otherToken;
        String mainToken;
        if(currentUser.getUid().equals("WZKQ9qT4qoZ8GvFQquY9HShH2m82")){
            otherToken="CXBRa3GW82SWtqoQF6KzOVDnRNe2";
            user2="CXBRa3GW82SWtqoQF6KzOVDnRNe2";
            mainToken="WZKQ9qT4qoZ8GvFQquY9HShH2m82"+"CXBRa3GW82SWtqoQF6KzOVDnRNe2";

        }else{
            otherToken="WZKQ9qT4qoZ8GvFQquY9HShH2m82";
            user2="WZKQ9qT4qoZ8GvFQquY9HShH2m82";
            mainToken="CXBRa3GW82SWtqoQF6KzOVDnRNe2"+"WZKQ9qT4qoZ8GvFQquY9HShH2m82";
        }
        final String Token1=myToken+otherToken;
        final String Token2=otherToken+myToken;

        try {
            FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUser.getUid()).child(mainToken).limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        String sender=data.child("sender").getValue().toString();
                        if(!sender.equals(currentUser.getUid()) && !isChatpageOpened){
                            String message=data.child("message").getValue().toString();
                            sendPushNotification(IndividualMessage.this,message);
                            Toast.makeText(IndividualMessage.this, "Message : "+data.getKey(), Toast.LENGTH_LONG).show();
                            break;
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
        if(currentUser.getUid().equals("CXBRa3GW82SWtqoQF6KzOVDnRNe2")){
            FirebaseDatabase.getInstance().getReference().child("Users").child("WZKQ9qT4qoZ8GvFQquY9HShH2m82").child("userName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    receiverNameString=dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            FirebaseDatabase.getInstance().getReference().child("Users").child("CXBRa3GW82SWtqoQF6KzOVDnRNe2").child("userName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    receiverNameString=dataSnapshot.getValue().toString();
                    Toast.makeText(IndividualMessage.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



        typeMessage=findViewById(R.id.messageInput);
        sendButton=findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String message=typeMessage.getText().toString().trim();
                if(!message.isEmpty()) {
                    String sender=mAuth.getUid();
                    String receiver;
                    if(sender.equals("WZKQ9qT4qoZ8GvFQquY9HShH2m82")){
                        receiver="CXBRa3GW82SWtqoQF6KzOVDnRNe2";
                    }
                    else{
                        receiver="WZKQ9qT4qoZ8GvFQquY9HShH2m82";
                    }
                    final Message msg = new Message(message,
                            "current Time",
                            "text",
                             receiver,
                             sender,
                            "sent");
                    typeMessage.setText("");
                    final String time = getCurrentTime();
                    DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Messages").child(user1).child(Token1).child(time);
                    db1.setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(user2).child(Token2).child(time);
                                db2.setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Toast.makeText(IndividualMessage.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });

        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.hasFixedSize();
        fetchMessages(mainToken);

    }

    private void fetchMessages(String mainToken) {
        Query query=FirebaseDatabase.getInstance().getReference().child("Messages").child(mAuth.getUid()).child(mainToken);
        FirebaseRecyclerOptions<Message>options=new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(query, new SnapshotParser<Message>() {
                    @NonNull
                    @Override
                    public Message parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Message(snapshot.child("message").getValue().toString(),
                                snapshot.child("messageStatus").getValue().toString(),
                                snapshot.child("messageType").getValue().toString(),
                                snapshot.child("receiver").getValue().toString(),
                                snapshot.child("sender").getValue().toString(),
                                snapshot.child("time").getValue().toString());
                    }
                }).build();

        adapter=new FirebaseRecyclerAdapter<Message,ViewHolder>(options) {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_view, parent, false);

                return new ViewHolder(view);
            }



            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Message message) {
                viewHolder.setIsRecyclable(false);
                String user=mAuth.getUid();
                if(message.getSender().equals(user)){
                    viewHolder.setMyMessage(message.getMessage());
                }
                else{
                    viewHolder.setTheirMessage(message.getMessage());
                }


            }
        };
        recyclerView.setAdapter(adapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS", Locale.ENGLISH));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        Toast.makeText(this, "onStart is executed", Toast.LENGTH_SHORT).show();
        isChatpageOpened=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        Toast.makeText(this, "onStop is executed", Toast.LENGTH_SHORT).show();
        isChatpageOpened=false;
    }
    public  void sendPushNotification(Context ctx,String message){
        Intent intent=new Intent(ctx,IndividualMessage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = "some_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        androidx.core.app.NotificationCompat.Builder notificationBuilder =
                new androidx.core.app.NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Message from Ex")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setBadgeIconType(androidx.core.app.NotificationCompat.BADGE_ICON_SMALL)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }


        assert notificationManager != null;
        int uniqueNotificationNumber=(int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(uniqueNotificationNumber /* ID of notification */, notificationBuilder.build());
    }


}
class ViewHolder extends RecyclerView.ViewHolder{
    private TextView myMessage;
    private TextView theirMessage;
    private RelativeLayout myMessageRel;
    private RelativeLayout theirMessageRel;
    private TextView receiverName;
    private FirebaseAuth mAuth;
    FirebaseUser CurrentUser;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mAuth=FirebaseAuth.getInstance();
        CurrentUser=mAuth.getCurrentUser();

        myMessageRel=itemView.findViewById(R.id.myMessageRel);
        theirMessageRel=itemView.findViewById(R.id.theirMessageRel);
        myMessage=itemView.findViewById(R.id.message_body);
        theirMessage=itemView.findViewById(R.id.their_message);
        receiverName=itemView.findViewById(R.id.receiverName);
        receiverName.setText(IndividualMessage.receiverNameString);





    }
    public void setMyMessage(String stringMsg){
        myMessageRel.setVisibility(View.VISIBLE);
        myMessage.setText(stringMsg);
    }
    public void setTheirMessage(String stringMsg){
        theirMessageRel.setVisibility(View.VISIBLE);
        theirMessage.setText(stringMsg);

    }


}
