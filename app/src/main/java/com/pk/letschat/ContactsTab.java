package com.pk.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsTab extends Fragment {

    ListView listView;
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name,phonenumber;
    public static final int RequestPermissionCode=1;
    Button button;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_contacts_tab,container,false);

        listView=view.findViewById(R.id.listview1);
        button=view.findViewById(R.id.button1);
        StoreContacts=new ArrayList<String>();

        EnableRuntimePermission();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetContactsIntoArrayList();

                arrayAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.contact_items_listview,
                        R.id.textView, StoreContacts
                );



                listView.setAdapter(arrayAdapter);

            }
        });




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
        cursor = getActivity().getApplication().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            StoreContacts.add(name + " "  + ":" + " " + phonenumber);
            //Toast.makeText(getActivity(), name + " "  + ":" + " " + phonenumber, Toast.LENGTH_SHORT).show();
        }

        cursor.close();

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





}
