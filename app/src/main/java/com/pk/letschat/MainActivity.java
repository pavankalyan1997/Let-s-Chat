package com.pk.letschat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle Toggle;
    static boolean isLogged=false;
    SQLiteDatabase sqLiteDatabase;
    Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=findViewById(R.id.toolbarMain);
        setSupportActionBar(mToolbar);


        //setActionBar(mToolbar);
        //setSupportActionBar(mToolbar);
        sqLiteDatabase=openOrCreateDatabase("CHAT",MODE_PRIVATE,null);
        FirebaseApp.initializeApp(this);
        FirebaseApp.initializeApp(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        drawerLayout=findViewById(R.id.DrawerLayoutMain);
        Toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        Toggle.syncState();
        String createSQL="create table if not exists userDetails(Name varchar,phoneNumber varchar,eMail varchar)";
        sqLiteDatabase.execSQL(createSQL);

        TabLayout tabLayout=findViewById(R.id.tab_layout_main);
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Group Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager=findViewById(R.id.pager);
        final PagerAdapter adapter=new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseApp.initializeApp(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            Toast.makeText(this, "User is not Authenticated", Toast.LENGTH_SHORT).show();
            CommonFunctions.goToLogin(this);
        }else{
            isLogged=true;
            Toast.makeText(this, "welcome user : "+currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        if(isLogged){
            MenuItem item=menu.findItem(R.id.logIn);
            item.setVisible(false);
        }
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
