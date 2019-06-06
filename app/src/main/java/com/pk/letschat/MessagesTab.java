package com.pk.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.pk.letschat.DBCalls.ContactsTable;
import com.pk.letschat.DBCalls.LetsChatDataBase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.pk.letschat.ContactsTab.RequestPermissionCode;

public class MessagesTab extends Fragment {
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name,phonenumber;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    LetsChatDataBase letsChat;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_messages_tab,container,false);
        letsChat=new LetsChatDataBase(getContext());


        //EnableRuntimePermission();
        //Recyler view initialization
        recyclerView=view.findViewById(R.id.messageRecyclerView);

        //firebase app initialization
        FirebaseApp.initializeApp(getContext());

        //firebase current user initialization
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();



        // linear layout initialization for recycler view
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setAdapter();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.hasFixedSize();



        if(currentUser!=null){
            fetchMessagesFromContacts();
        }



        return view;
    }
    private void getMessagesFromDifferentIndividuals(){
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(currentUser.getUid()).orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot childSnapShot:dataSnapshot.getChildren()){
                    Log.i("SnapShot Count",String.valueOf(childSnapShot.getKey()+" "+childSnapShot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void fetchMessagesFromContacts() {
        Query query=FirebaseDatabase.getInstance().getReference().child("Tokens").child(currentUser.getUid()).orderByValue();
        FirebaseRecyclerOptions<RecentMessageToken>messageViewOptions=new FirebaseRecyclerOptions.Builder<RecentMessageToken>()
                .setQuery(query, new SnapshotParser<RecentMessageToken>() {
                    @NonNull
                    @Override
                    public RecentMessageToken parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new RecentMessageToken(snapshot.getKey(),snapshot.getValue().toString());
                    }
                }).build();
        adapter=new FirebaseRecyclerAdapter<RecentMessageToken,ViewHolderMessage>(messageViewOptions){
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolderMessage viewHolderMessage, int i, @NonNull RecentMessageToken recentMessageToken) {
                //viewHolderMessage.setIsRecyclable(false);
                String token=recentMessageToken.getToken();
                String timeStamp=recentMessageToken.getTimeStamp();
                FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUser.getUid()).child(token).child(timeStamp).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        viewHolderMessage.setContactRecentMessage(dataSnapshot.child("message").getValue().toString());
                        String receiver=dataSnapshot.child("receiver").getValue().toString();
                        String sender=dataSnapshot.child("sender").getValue().toString();
                        String secondPerson;
                        if(receiver.equals(currentUser.getUid())){
                            secondPerson=sender;
                        }
                        else{
                            secondPerson=receiver;
                        }
                        ContactsTable contact=null;
                        try{
                            contact=letsChat.getContactFromUID(secondPerson);
                            viewHolderMessage.setContactName(contact.getName());
                            viewHolderMessage.setContactUID(contact.getUid());
                            viewHolderMessage.setProfileImage(contact.getUid());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_tab_view, parent, false);

                return new ViewHolderMessage(view);
            }

        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
    private void LoadContacts(){
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Loading contacts...");
        mProgressDialog.setMessage("Please wait while we load the contacts");
        mProgressDialog.setCanceledOnTouchOutside(false);
        ArrayList<Contact> contactList=CommonFunctions.GetContactsIntoArrayList(getActivity());


        for(final Contact contact:contactList){
//            Log.i("Contact",contact.getPhoneNumber());
            final String phoneNumber=contact.getPhoneNumber();
            DatabaseReference contactRef= FirebaseDatabase.getInstance().getReference().child("Users");

            contactRef.orderByChild("phoneNumber").equalTo(phoneNumber)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                //mProgressDialog.show();
                                //Toast.makeText(itemView.getContext(), "Contact exists : "+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                String contactUID;
                                for(DataSnapshot childSnapShSnapshot:dataSnapshot.getChildren()){
                                    contactUID=childSnapShSnapshot.child("uid").getValue().toString();
                                    try {
                                        letsChat.insertDataToContacts(contactUID,contact.getContactName(),contact.getPhoneNumber());

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }



                                    //Log.i("Contact-exist",phoneNumber);

                                }


                            }
                            else{
                               // Log.i("Contact Doesn't exist",phoneNumber);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {


                        }
                    });
            //mProgressDialog.dismiss();
        }



    }
    private void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                getActivity(),
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(getActivity(),"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(currentUser==null){
            CommonFunctions.goToLogin(getActivity());
            return;
        }
        EnableRuntimePermission();
        LoadContacts();
        adapter.startListening();
        getMessagesFromDifferentIndividuals();


    }

    @Override
    public void onStop() {
        super.onStop();
        if(currentUser==null){
            CommonFunctions.goToLogin(getActivity());
            return;
        }
        adapter.stopListening();
    }


}
class ViewHolderMessage extends RecyclerView.ViewHolder{
    LinearLayout messageFull;
    TextView contactName;
    TextView contactRecentMessage;
    private FirebaseAuth mAuth;
    FirebaseUser CurrentUser;
    private String contactUID;
    ImageView profileImage;

    public ViewHolderMessage(@NonNull final View itemView){
        super(itemView);
        mAuth=FirebaseAuth.getInstance();
        CurrentUser=mAuth.getCurrentUser();

        messageFull=itemView.findViewById(R.id.MessageFull);
        contactName=itemView.findViewById(R.id.contactNameMessageTab);
        contactRecentMessage=itemView.findViewById(R.id.contactRecentMessage);
        profileImage=itemView.findViewById(R.id.profileImageMessageTab);
        messageFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent individualMsgIntent=new Intent(itemView.getContext(),IndividualMessage.class);
                individualMsgIntent.putExtra("ContactUID",contactUID);
                individualMsgIntent.putExtra("ContactName",contactName.getText().toString().trim());
                itemView.getContext().startActivity(individualMsgIntent);
                //Toast.makeText(itemView.getContext(), "We shall add this feature later", Toast.LENGTH_SHORT).show();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunctions.goToImgView(itemView.getContext(),contactUID);

            }
        });
    }
    public void setContactName(String stringContactName){
        if(stringContactName==null){
            contactName.setText("Contact not added");

        }
        else{
            contactName.setText("");
            contactName.setText(stringContactName);
        }

    }
    public void setContactRecentMessage(String stringContactRecentMessage){
        contactRecentMessage.setText("");
        contactRecentMessage.setText(stringContactRecentMessage);
    }
    public void setContactUID(String contactUID){
        this.contactUID=contactUID;
    }

    public void setProfileImage(String contactUID){
        try{
            FirebaseStorage.getInstance().getReference().child("profile_images").child("thumbs").child(contactUID+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri.toString()).placeholder(R.drawable.propic).into(profileImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseStorage.getInstance().getReference().child("profile_images").child("propic.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri.toString()).placeholder(R.drawable.propic).into(profileImage);
                        }
                    });
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

}
