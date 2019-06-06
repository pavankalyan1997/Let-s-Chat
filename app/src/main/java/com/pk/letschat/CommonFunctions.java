package com.pk.letschat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.pk.letschat.ContactsTab.RequestPermissionCode;

public class CommonFunctions {
    private static final int SMS_PERMISSION_CODE =101 ;

    public static void goToMain(Context ctx){
        Intent intent= new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }
    public static void goToMain(Context ctx,boolean clear){
        if(clear==true){
            goToMain(ctx);
        }
        else{
            Intent intent= new Intent(ctx, MainActivity.class);
            ctx.startActivity(intent);
        }
    }
    public static void goToLogin(Context ctx){
        Intent intent=new Intent(ctx,LogIn.class);
        ctx.startActivity(intent);
    }
    public static void goToSignUp(Context ctx){
        Intent intent=new Intent(ctx,SignUp.class);
        ctx.startActivity(intent);
    }
    public static void goToSettings(Context ctx){
        Intent intent=new Intent(ctx,Settings.class);
        ctx.startActivity(intent);
    }
    public static void goToIndividualMessage(Context ctx){
        Intent intent=new Intent(ctx,IndividualMessage.class);
        ctx.startActivity(intent);
    }
    public static void LogOut(Context ctx){
        final Context CTX=ctx;
        final AlertDialog.Builder  builder=new AlertDialog.Builder(ctx);
        builder.setTitle("Log Out??");
        builder.setMessage("Are you sure You want to Log Out?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                ((Activity)CTX).finish();
                goToMain(CTX,false);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();


    }

    public static void goToImgView(Context ctx, String current_uid,String name){
        Intent intent=new Intent(ctx,ImgView.class);
        intent.putExtra("name",name);
        intent.putExtra("userUid",current_uid);
        ctx.startActivity(intent);

    }


    public static void populateMenuItems(Context ctx, MenuItem item){
        switch(item.getItemId()){
            case R.id.logIn:
                CommonFunctions.goToLogin(ctx);
                break;
            case R.id.main:
                CommonFunctions.goToMain(ctx,false);
                break;
            case R.id.logOut:
                CommonFunctions.LogOut(ctx);
                break;
            case R.id.settingsMenu:
                CommonFunctions.goToSettings(ctx);
                break;
            case R.id.individualMessage:
                CommonFunctions.goToIndividualMessage(ctx);
                break;
        }
    }


    public static void hideMenuItems(Menu menu) {
        menu.findItem(R.id.main).setVisible(false);
        menu.findItem(R.id.logOut).setVisible(false);
        menu.findItem(R.id.settingsMenu).setVisible(false);
        menu.findItem(R.id.individualMessage).setVisible(false);
        menu.findItem(R.id.refreshContacts).setVisible(false);
    }

    public static ArrayList<Contact>GetContactsIntoArrayList(Context ctx){
        ArrayList<String> StoreContacts;
        Cursor cursor;
        String name,phonenumber;
        ArrayList<Contact>storeContactsList=new ArrayList<>();
        StoreContacts=new ArrayList<String>();
        try{
            Activity activity=(Activity)ctx;
            cursor = activity.getApplication().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

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
            }
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


        return storeContactsList;
    }
    public boolean isSmsPermissionGranted(Context ctx) {
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {

        }
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
    }




}
