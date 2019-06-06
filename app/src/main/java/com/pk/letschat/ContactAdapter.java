package com.pk.letschat;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pk.letschat.DBCalls.LetsChatDataBase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {
    private List<Contact>contactList;
    private List<Contact>contactListFull=new ArrayList<>();




    public class ContactViewHolder extends RecyclerView.ViewHolder{
        private TextView contactName;
        private TextView contactPhoneNumber;
        private LinearLayout contactFull;
        LetsChatDataBase letsChatLetsChatDataBase;


        public ContactViewHolder(@NonNull final View itemView) {
            super(itemView);
            letsChatLetsChatDataBase =new LetsChatDataBase(itemView.getContext());

            contactName=itemView.findViewById(R.id.contactName);
            contactPhoneNumber=itemView.findViewById(R.id.contactPhoneNumber);
            contactFull=itemView.findViewById(R.id.ContactFull);
            contactFull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String phoneNumber=contactPhoneNumber.getText().toString().trim();
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
                                            try {
                                                letsChatLetsChatDataBase.insertDataToContacts(contactUID,contactName.getText().toString(),phoneNumber);

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }


                                            Toast.makeText(itemView.getContext(), "Contact exists : "+contactUID, Toast.LENGTH_SHORT).show();
                                            Intent individualMsgIntent=new Intent(itemView.getContext(),IndividualMessage.class);
                                            individualMsgIntent.putExtra("ContactUID",contactUID);
                                            individualMsgIntent.putExtra("ContactName",contactName.getText().toString());
                                            itemView.getContext().startActivity(individualMsgIntent);

                                        }

                                    }
                                    else{
                                       // Toast.makeText(itemView.getContext(), "Contact doesn't exists", Toast.LENGTH_SHORT).show();
                                         AlertDialog.Builder  builder=new AlertDialog.Builder(itemView.getContext());
                                        builder.setTitle("No Account for this contact");
                                        builder.setMessage("Oops! Looks like there is no Let's chat accoount related to this contact. Do you want to invite them via SMS?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                               // Toast.makeText(itemView.getContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                                                /*Intent smsIntent=new Intent(Intent.ACTION_VIEW);
                                                smsIntent.setData(Uri.parse("smsto:"));
                                                smsIntent.putExtra("address"  , contactPhoneNumber.getText().toString());
                                                smsIntent.putExtra("sms_body"  , "Hey install Let's Chat");*/

                                                try{
                                                    /*String smsNumber=contactPhoneNumber.getText().toString();
                                                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                                                    //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                                                    sendIntent.setAction(Intent.ACTION_SEND);
                                                    sendIntent.setType("text/plain");
                                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                                                    sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
                                                    sendIntent.setPackage("com.whatsapp");
                                                    itemView.getContext().startActivity(sendIntent);*/

                                                    /*Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=91"+contactPhoneNumber.getText().toString()+"&text=Hello");
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                    itemView.getContext().startActivity(intent);*/

                                                    Intent i=new Intent(android.content.Intent.ACTION_SEND);
                                                    i.setType("text/plain");
                                                    i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject test");
                                                    i.putExtra(android.content.Intent.EXTRA_TEXT, "Hey Let's Download Lets chat and chat there!!");
                                                    itemView.getContext().startActivity(Intent.createChooser(i,"Share via"));
                                                }catch (Exception e){
                                                    Toast.makeText(itemView.getContext(),
                                                            "SMS failed, please try again later.", Toast.LENGTH_SHORT).show();
                                                }

                                                }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        builder.create();
                                        builder.show();

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
    public ContactAdapter(List<Contact>contactList,List<Contact>contactListFull){
        this.contactList=contactList;
        this.contactListFull=contactListFull;
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

    @Override
    public Filter getFilter() {
        return contactFilter;
    }
    private Filter contactFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Contact>filteredList=new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                filteredList.addAll(contactListFull);
            }else{
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(Contact item:contactListFull){
                    if(item.getContactName().toLowerCase().contains(filterPattern)||item.getPhoneNumber().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactList.clear();
            contactList.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };


}
