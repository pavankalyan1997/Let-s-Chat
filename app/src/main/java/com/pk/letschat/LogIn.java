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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle Toggle;
    TextInputLayout userNameLayout,passwordLayout;
    TextView txtNoAccount;
    Button loginButton;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        FirebaseApp.initializeApp(LogIn.this);
        mAuth = FirebaseAuth.getInstance();

        drawerLayout=findViewById(R.id.DrawerLayoutLogin);
        Toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        Toggle.syncState();

        // Display Navigation
        navigationView=findViewById(R.id.LoginNavigation);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar=findViewById(R.id.progressBarLogin);
        userNameLayout= findViewById(R.id.userNameLogin);
        passwordLayout=findViewById(R.id.passwordLogin);

        txtNoAccount=findViewById(R.id.signUpTxt);
        loginButton= findViewById(R.id.logInButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add code to login
                progressBar.setVisibility(View.VISIBLE);
                String userName=userNameLayout.getEditText().getText().toString().trim();
                String password=passwordLayout.getEditText().getText().toString().trim();
                mAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            CommonFunctions.goToMain(LogIn.this,true);
                        }
                        else{
                            Toast.makeText(LogIn.this, "Invalid User Name or Password", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                progressBar.setVisibility(View.GONE);
            }
        });

        txtNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunctions.goToSignUp(LogIn.this);
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        CommonFunctions.navigationMenu(this,menuItem);
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
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
