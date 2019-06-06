package com.pk.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.pk.letschat.DBCalls.LetsChatDataBase;
import com.pk.letschat.DBCalls.MessagesTable;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IndividualMessage extends AppCompatActivity {
    //Declarations
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
    public  String user1ID;
    public String user2ID;
    Toolbar mToolbar;
    TextView profileNameToolBar;
    ImageView profileImgToolBar;
    ScrollView scrollViewIndMsg;
    LinearLayout profileImgLinearLayout;

    LetsChatDataBase letsChatDBMessages;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_message);

        letsChatDBMessages =new LetsChatDataBase(this);

        scrollViewIndMsg=findViewById(R.id.scrollViewMessage);

        profileImgLinearLayout=findViewById(R.id.profileImgLinearLayoutToolBar);



        //Recyler view initialization
        recyclerView=findViewById(R.id.list);



        //firebase app initialization
        FirebaseApp.initializeApp(this);
        FirebaseApp.initializeApp(IndividualMessage.this);


        //firebase current user initialization
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // drawer layout initialization though we are not using any navigation layout
        drawerLayout=findViewById(R.id.drawerLayoutIndividualMessage);
        Toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        Toggle.syncState();

        mToolbar=findViewById(R.id.toolbarIndividualMessage);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        profileNameToolBar=findViewById(R.id.profileNameToolBar);
        profileImgToolBar=findViewById(R.id.profileImageToolBar);

        final String contactName=getIntent().getStringExtra("ContactName");
        String contactUID=getIntent().getStringExtra("ContactUID");

        profileNameToolBar.setText(contactName);
        profileImgLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try{
            FirebaseStorage.getInstance().getReference().child("profile_images").child("thumbs").child(contactUID+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri.toString()).placeholder(R.drawable.propic).into(profileImgToolBar);
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }



        // user1ID is the person's ID, user2ID is the contact's ID.
        user1ID=currentUser.getUid();
        user2ID=contactUID;


        final String mainToken;
        mainToken=user1ID+user2ID;
        final String Token1=user1ID+user2ID;
        final String Token2=user2ID+user1ID;

        insertIntoMessagesTable(mainToken);

        // on new message is added, this will be executed
        try {
            FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUser.getUid()).child(mainToken).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot data:dataSnapshot.getChildren()){

                        String sender=data.child("sender").getValue().toString();
                        if(!sender.equals(currentUser.getUid()) && !isChatpageOpened){
                            Toast.makeText(IndividualMessage.this, String.valueOf(data.getChildrenCount()), Toast.LENGTH_SHORT).show();
                            String message=data.child("message").getValue().toString();
                            sendPushNotification(IndividualMessage.this,contactName,message);
                            break;
                        }
                        else if(sender.equals(currentUser.getUid()) && isChatpageOpened){

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
        // to fetch the receiver's name
        FirebaseDatabase.getInstance().getReference().child("Users").child(user2ID).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                receiverNameString=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // sending message on clicking the sendButton
        typeMessage=findViewById(R.id.messageInput);
        sendButton=findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // adding message data to Mssage Class.
                final String message=typeMessage.getText().toString().trim();
                if(!message.isEmpty()) {
                    final String sender=user1ID;
                    final String receiver=user2ID;

                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    final String currenttime=dateFormat.format(new Date()).toString();

                    final Message msg = new Message(message,
                            "sent",
                            "text",
                             receiver,
                             sender,
                            currenttime);
                    typeMessage.setText("");
                    // getCurrentTime is the timeStamp for unique message identification
                    final String time = getCurrentTime();
                    //sending the message to user1 time line present in Messages->user1ID->Token1(user1ID+user2ID)->Time(unique time stamp)
                    DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Messages").child(user1ID).child(Token1).child(time);
                    db1.setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // if the message is populated in the above reference, it should go to contact's time line as well which is Messages->user2ID->Token2(user2ID+user1ID)->Time(unique time stamp)
                                DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(user2ID).child(Token2).child(time);
                                db2.setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Message is successfully sent
                                            // After sending the message, message sent tick sound should come
//                                            MessagesTable table=new MessagesTable();
//                                            table.setAccountID(currentUser.getUid());
//                                            table.setToken(Token1);
//                                            table.setMessage(message);
//                                            table.setTIME(currenttime);
//                                            table.setSENDER(sender);
//                                            table.setRECEIVER(receiver);
//                                            table.setMESSAGE_STATUS("sent");
//                                            table.setMESSAGE_TYPE("text");
//                                            table.setTimeStamp(time);
//                                            letsChatDBMessages.insertMessages(table);

                                            ArrayList<MessagesTable>list=letsChatDBMessages.getMessagesOfToken(Token1,currentUser.getUid());
                                            Toast.makeText(IndividualMessage.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
                                            try {
                                                MediaPlayer alertSound= MediaPlayer.create(IndividualMessage.this,R.raw.text_message_alert);
                                                alertSound.start();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                    final DatabaseReference db2=FirebaseDatabase.getInstance().getReference().child("Tokens");
                    final Map updateMap1=new HashMap<>();
                    updateMap1.put(Token1,time);
                    FirebaseDatabase.getInstance().getReference().child("Tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Log.i("snapshot",String.valueOf(dataSnapshot.getChildrenCount())+" "+dataSnapshot.getKey()+" "+dataSnapshot.getValue());

                            if(dataSnapshot.hasChild(currentUser.getUid())){
                                Log.i("snapshot User status","User exists.");
                                dataSnapshot.child(currentUser.getUid()).getRef().updateChildren(updateMap1);

                            }else{
                                Log.i("snapshot User status","User doesn't exists");
                                db2.child(currentUser.getUid()).setValue(updateMap1);
                            }
                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final Map updateMap2=new HashMap<>();
                    updateMap2.put(Token2,time);
                    FirebaseDatabase.getInstance().getReference().child("Tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(user2ID)){
                                dataSnapshot.child(user2ID).getRef().updateChildren(updateMap2);
                            }else{
                                FirebaseDatabase.getInstance().getReference().child("Tokens").child(user2ID).setValue(updateMap2);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


            }
        });


        // linear layout initialization for recycler view
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.hasFixedSize();


        // main token is user1ID+user2ID
        fetchMessages(mainToken);

    }

    //fetch messages and populate recycler view
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
                                snapshot.child("time").getValue().toString()
                                );
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
                    String Message=message.getMessage();
                    if(Message.length()<8){
                        viewHolder.setMyMessageLinearLayoutOrientation();

                    }
                    viewHolder.setMyMessageLinearLayoutVisible();
                    viewHolder.setMyMessage(Message);
                    viewHolder.setMyCurrentTime(message.getTime());
                }
                else{
                    String Message=message.getMessage();
                    if(Message.length()<8){
                        viewHolder.setTheirMessageLinearLayoutOrientation();


                    }
                    viewHolder.setTheirMessageLinearLayoutVisible();
                    viewHolder.setTheirMessage(Message);
                    viewHolder.setTheirCurrentTime(message.getTime());


                }
                scrollViewIndMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollViewIndMsg.scrollTo(0,scrollViewIndMsg.getBottom());
                    }
                });


            }
        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(itemCount);
            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void insertIntoMessagesTable(final String mainToken){
        try {
            FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUser.getUid()).child(mainToken).limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        Log.i("New Messages",dataSnapshot.getKey()+" "+data.getKey());
                        Log.i("New Messages before",String.valueOf(letsChatDBMessages.checkMessage(dataSnapshot.getKey(),data.getKey())));

                        try{
                            boolean messagePresent=letsChatDBMessages.checkMessage(dataSnapshot.getKey(),data.getKey());
                            if(messagePresent==false){
                                MessagesTable table=new MessagesTable();
                                table.setAccountID(currentUser.getUid());
                                table.setToken(mainToken);
                                table.setMessage(data.child("message").getValue().toString());
                                table.setMESSAGE_TYPE(data.child("messageType").getValue().toString());
                                table.setTimeStamp(data.getKey());
                                table.setRECEIVER(data.child("receiver").getValue().toString());
                                table.setSENDER(data.child("sender").getValue().toString());
                                table.setTIME(data.child("time").getValue().toString());
                                table.setMESSAGE_STATUS(data.child("messageStatus").getValue().toString());
                                letsChatDBMessages.insertMessages(table);

                                ArrayList<MessagesTable>list=letsChatDBMessages.getMessagesOfToken(mainToken,currentUser.getUid());
                                Toast.makeText(IndividualMessage.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();

                                Log.i("New Messages after",String.valueOf(letsChatDBMessages.checkMessage(dataSnapshot.getKey(),data.getKey())));

                            }

                        }catch (Exception e){
                            e.printStackTrace();
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
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentTime() {
        //LocalDateTime now = LocalDateTime.now();
        //return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS", Locale.ENGLISH));
        return new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();


        //Toast.makeText(this, "onStart is executed", Toast.LENGTH_SHORT).show();
        isChatpageOpened=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        //Toast.makeText(this, "onStop is executed", Toast.LENGTH_SHORT).show();
        isChatpageOpened=false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        CommonFunctions.populateMenuItems(this,item);
        return true;
    }

    public  void sendPushNotification(Context ctx,String contactName,String message){
        Intent intent=new Intent(ctx,IndividualMessage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = "some_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        androidx.core.app.NotificationCompat.Builder notificationBuilder =
                new androidx.core.app.NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(contactName)
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
    private LinearLayout myMessageLinearLayout,theirMessageLinearLayout;
    private TextView myCurrentTime;
    private TextView theirCurrentTime;


    public ViewHolder(@NonNull final View itemView) {
        super(itemView);
        mAuth=FirebaseAuth.getInstance();
        CurrentUser=mAuth.getCurrentUser();

        myMessageRel=itemView.findViewById(R.id.myMessageRel);
        theirMessageRel=itemView.findViewById(R.id.theirMessageRel);
        myMessage=itemView.findViewById(R.id.message_body);
        theirMessage=itemView.findViewById(R.id.their_message);
        receiverName=itemView.findViewById(R.id.receiverName);
        myCurrentTime=itemView.findViewById(R.id.my_message_current_time);
        receiverName.setText(IndividualMessage.receiverNameString);
        myMessageLinearLayout=itemView.findViewById(R.id.LinearLayoutMyMessage);
        theirCurrentTime=itemView.findViewById(R.id.their_message_current_time);
        theirMessageLinearLayout=itemView.findViewById(R.id.LinearLayoutTheirMessage);

        theirMessageRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setMyMessage(String stringMsg){
        myMessageRel.setVisibility(View.VISIBLE);
        myMessage.setText(stringMsg);
    }
    public void setMyMessageLinearLayoutVisible(){
        myMessageLinearLayout.setVisibility(View.VISIBLE);
    }
    public void setTheirMessageLinearLayoutVisible(){
        theirMessageRel.setVisibility(View.VISIBLE);
    }

    public void setTheirMessage(String stringMsg){
        theirMessage.setText(stringMsg);
    }
    public void setMyCurrentTime(String currentTime){
        myCurrentTime.setText(currentTime);
    }
    public void setTheirCurrentTime(String currentTime){
        theirCurrentTime.setText(currentTime);
    }
    public void setMyMessageLinearLayoutOrientation(){
        myMessageLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
    }
    public void setTheirMessageLinearLayoutOrientation(){
        theirMessageLinearLayout.setOrientation(LinearLayout.HORIZONTAL);


    }



}
