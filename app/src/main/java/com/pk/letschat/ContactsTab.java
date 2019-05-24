package com.pk.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ContactsTab extends Fragment {

    ListView listView;
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name,phonenumber;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ContactAdapter mContactAdapter;
    private ArrayList<Contact>storeContactsList=new ArrayList<>();
    private Adapter adapter;
    public static final int RequestPermissionCode=1;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_contacts_tab,container,false);

        setHasOptionsMenu(true);
        recyclerView=view.findViewById(R.id.contactRecyclerView);
        mContactAdapter=new ContactAdapter(storeContactsList);
        RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mContactAdapter);


        listView=view.findViewById(R.id.listView1);
        StoreContacts=new ArrayList<String>();

        EnableRuntimePermission();




        return view;
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
    private void GetContactsIntoArrayList(){
        try{
            cursor = getActivity().getApplication().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

            while (cursor.moveToNext()) {

                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phonenumber=phonenumber.replace(" ","");
                if(phonenumber.length()>10){
                    phonenumber=phonenumber.replace("+91","");
                    phonenumber=phonenumber.replace("-","");
                }
                Contact contactObj=new Contact(name,phonenumber,"uid","status","contactPic");


                if(phonenumber.length()==10){
                    String contact=name + " "  + ":" + " " + phonenumber;
                    if(!StoreContacts.contains(contact)){
                        StoreContacts.add(contact);
                        storeContactsList.add(contactObj);
                    }

                }






                //Toast.makeText(getActivity(), name + " "  + ":" + " " + phonenumber, Toast.LENGTH_SHORT).show();
            }
            mContactAdapter.notifyDataSetChanged();
            Collections.sort(StoreContacts);
            Collections.sort(storeContactsList, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    return o1.getContactName().compareTo(o2.getContactName());
                }
            });

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }




    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(),"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getActivity(),"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(StoreContacts.isEmpty()){
            RefreshContacts();
        }





        listView.setAdapter(arrayAdapter);

    }

    private void RefreshContacts() {
        ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle("Refreshing Contacts");
        progressDialog.setMessage("Please wait while we refresh your contacts");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        GetContactsIntoArrayList();
        arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.contact_items_listview,
                R.id.textView, StoreContacts
        );
        progressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshContacts:
                RefreshContacts();
                break;

        }

        return false;
    }
}


