package com.pk.letschat;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity{
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle Toggle;
    TextInputLayout txtEmail,txtUserName,txtPassword,txtPhonenumber;
    TextView txtHasAccount;
    Button signUpButton;
    ProgressBar signUpProgressbar;
    DatabaseReference ref;
    Toolbar mToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mToolbar=findViewById(R.id.toolbarSignUp);
        setSupportActionBar(mToolbar);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        ref=FirebaseDatabase.getInstance().getReference();
        drawerLayout=findViewById(R.id.DrawerLayoutSignUp);
        Toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        Toggle.syncState();



        txtEmail=findViewById(R.id.eMailSignUp);
        txtUserName=findViewById(R.id.userNameSignUp);
        txtPassword=findViewById(R.id.passwordSignUp);
        txtPhonenumber=findViewById(R.id.phoneNumberSignUp);
        signUpButton=findViewById(R.id.signUpButton);
        txtHasAccount=findViewById(R.id.signUpTxt);
        signUpProgressbar=findViewById(R.id.progressBarSignUp);

        txtHasAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunctions.goToLogin(SignUp.this);

            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=txtEmail.getEditText().getText().toString().trim();
                final String userName=txtUserName.getEditText().getText().toString().trim();
                final String password=txtPassword.getEditText().getText().toString().trim();
                final String phoneNumber=txtPhonenumber.getEditText().getText().toString().trim();

                if(email.isEmpty()){
                    txtEmail.getEditText().setError("Email is required");
                    txtEmail.requestFocus();
                    return;
                }

                if(! Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    txtEmail.setError("Please enter Valid Email");
                    txtEmail.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    txtPassword.getEditText().setError("Password is required");
                    txtPassword.requestFocus();
                    return;
                }

                if(password.length()<6){
                    txtPassword.getEditText().setError("Length of password should be minimum 6 characters");
                    txtPassword.requestFocus();
                    return;
                }
                //signUpProgressbar.setVisibility(View.VISIBLE);
                final ProgressDialog mProgressDialog;
                mProgressDialog = new ProgressDialog(SignUp.this);
                mProgressDialog.setTitle("Signing UP!");
                mProgressDialog.setMessage("Please wait while you are being Signed up!");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mProgressDialog.dismiss();
                            Toast.makeText(SignUp.this,"Account Created Successfully",Toast.LENGTH_SHORT).show();
                            //signUpProgressbar.setVisibility(View.VISIBLE);
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            final String uid=currentUser.getUid();
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            final HashMap<String,String>user=new HashMap<>();
                            user.put("uid",uid);
                            user.put("id","some unique value");
                            user.put("email",email);
                            user.put("userName",userName);
                            user.put("phoneNumber",phoneNumber);
                            user.put("profilePic","null");
                            user.put("thumbNail","null");
                            user.put("status","Hi There I am using Let's Chat");
                            ref.setValue(user).addOnCompleteListener(SignUp.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(SignUp.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                            CommonFunctions.goToMain(SignUp.this,true);
                        }else{
                            mProgressDialog.dismiss();
                            Toast.makeText(SignUp.this, "Error while creating Account. Please try after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                signUpProgressbar.setVisibility(View.GONE);

            }
        });






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        CommonFunctions.hideMenuItems(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(Toggle.onOptionsItemSelected(item)){
            return true;
        }
        CommonFunctions.populateMenuItems(this,item);
        return true;
    }

}
