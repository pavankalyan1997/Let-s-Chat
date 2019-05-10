package com.pk.letschat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import static android.support.v4.content.ContextCompat.startActivity;

public class CommonFunctions {
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
    public static void navigationMenu(Context ctx, MenuItem item){
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
        }
    }
    public static void hideNavigationItems(NavigationView navigationView){
        Menu  menu=navigationView.getMenu();
        menu.findItem(R.id.logIn).setVisible(false);
    }



}
