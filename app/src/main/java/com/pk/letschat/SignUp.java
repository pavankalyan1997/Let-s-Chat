package com.pk.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SignUp extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle Toggle;
    TextInputLayout txtEmail,txtUserName,txtPassword,txtPhonenumber;
    TextView txtHasAccount;
    Button signUpButton;
    ProgressBar signUpProgressbar;
    DatabaseReference ref;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        ref=FirebaseDatabase.getInstance().getReference();


        //Display Navigation
        drawerLayout=findViewById(R.id.DrawerLayoutSignUp);
        Toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        Toggle.syncState();

        navigationView=findViewById(R.id.SignUpNavigation);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    txtEmail.setError("Email Can't be empty");
                }
                if(password.isEmpty()){
                    txtPassword.setError("Password can't be empty");
                }
                signUpProgressbar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUp.this,"Account Created Successfully",Toast.LENGTH_SHORT).show();
                            signUpProgressbar.setVisibility(View.VISIBLE);
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid=currentUser.getUid();
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String>user=new HashMap<>();
                            user.put("uid",uid);
                            user.put("id","some unique value");
                            user.put("email",email);
                            user.put("userName",userName);
                            user.put("phoneNumber",phoneNumber);
                            user.put("profilePic","null");
                            user.put("thumbNail","null");
                            ref.setValue(user).addOnCompleteListener(SignUp.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(SignUp.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                            CommonFunctions.goToMain(SignUp.this,true);
                        }else{
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
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        CommonFunctions.navigationMenu(this,menuItem);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(Toggle.onOptionsItemSelected(item)){
            return true;
        }
        CommonFunctions.navigationMenu(this,item);
        return true;
    }

}
