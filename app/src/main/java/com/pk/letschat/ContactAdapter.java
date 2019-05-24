package com.pk.letschat;

import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<Contact>contactList;



    public class ContactViewHolder extends RecyclerView.ViewHolder{
        private TextView contactName;
        private TextView contactPhoneNumber;
        private LinearLayout contactFull;


        public ContactViewHolder(@NonNull final View itemView) {
            super(itemView);
            contactName=itemView.findViewById(R.id.contactName);
            contactPhoneNumber=itemView.findViewById(R.id.contactPhoneNumber);
            contactFull=itemView.findViewById(R.id.ContactFull);
            contactFull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber=contactPhoneNumber.getText().toString().trim();
                    DatabaseReference contactRef=FirebaseDatabase.getInstance().getReference().child("Users");
                    contactRef.orderByChild("phoneNumber").equalTo(phoneNumber)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        //Toast.makeText(itemView.getContext(), "Contact exists : "+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                        String contactUID;
                                        for(DataSnapshot childSnapShSnapshot:dataSnapshot.getChildren()){
                                            contactUID=childSnapShSnapshot.child("uid").getValue().toString();
                                            Toast.makeText(itemView.getContext(), "Contact exists : "+contactUID, Toast.LENGTH_SHORT).show();
                                            Intent individualMsgIntent=new Intent(itemView.getContext(),IndividualMessage.class);
                                            individualMsgIntent.putExtra("ContactUID",contactUID);
                                            itemView.getContext().startActivity(individualMsgIntent);

                                        }

                                    }
                                    else{
                                        Toast.makeText(itemView.getContext(), "Contact doesn't exists", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            });
        }


    }
    public ContactAdapter(ArrayList<Contact>contactList){
        this.contactList=contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_view,parent,false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact=contactList.get(position);
        holder.contactName.setText(contact.getContactName());
        holder.contactPhoneNumber.setText(contact.getPhoneNumber());

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
