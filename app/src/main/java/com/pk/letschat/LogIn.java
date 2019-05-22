package com.pk.letschat;

import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity{


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle Toggle;
    TextInputLayout userNameLayout,passwordLayout;
    TextView txtNoAccount;
    Button loginButton;
    ProgressBar progressBar;
    Toolbar mToolbar;
    private FirebaseAuth mAuth;
    static boolean isLogged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mToolbar=findViewById(R.id.toolbarLogin);
        setSupportActionBar(mToolbar);

        FirebaseApp.initializeApp(LogIn.this);
        mAuth = FirebaseAuth.getInstance();

        drawerLayout=findViewById(R.id.DrawerLayoutLogin);
        Toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        Toggle.syncState();




        progressBar=findViewById(R.id.progressBarLogin);
        userNameLayout= findViewById(R.id.userNameLogin);
        passwordLayout=findViewById(R.id.passwordLogin);

        txtNoAccount=findViewById(R.id.signUpTxt);
        loginButton= findViewById(R.id.logInButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add code to login
                String email=userNameLayout.getEditText().getText().toString().trim();
                String password=passwordLayout.getEditText().getText().toString().trim();
                if(email.isEmpty()){
                    userNameLayout.getEditText().setError("Email is required");
                    userNameLayout.requestFocus();
                    return;
                }
                if(! Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    userNameLayout.setError("Please enter Valid Email");
                    userNameLayout.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    passwordLayout.getEditText().setError("Password is required");
                    passwordLayout.requestFocus();
                    return;
                }

                if(password.length()<6){
                    passwordLayout.getEditText().setError("Length of password should be minimum 6 characters");
                    passwordLayout.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            isLogged=false;
        }
    }
}
