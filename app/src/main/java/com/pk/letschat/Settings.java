package com.pk.letschat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class Settings extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle Toggle;
    private FirebaseAuth mAuth;
    ImageView profileImg,cameraPic;
    static boolean isLogged=false;
    SQLiteDatabase sqLiteDatabase;
    LinearLayout nameLayout,aboutLayout;
    TextView nameActual,aboutActual,phoneNumberActual;

    private ProgressDialog mProgressDialog,loadProcess;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;

    protected static final int CAMERA_REQUEST = 1888;
    private static final int CHOOSE_IMAGE = 101;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    Bitmap bitmap;

    String selectedImagePath;
    private static final int GALLERY_PICK = 1;

    Toolbar mToolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar=findViewById(R.id.toolbarSettings);
        setSupportActionBar(mToolbar);



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FirebaseApp.initializeApp(Settings.this);
        mAuth = FirebaseAuth.getInstance();

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        sqLiteDatabase=openOrCreateDatabase("CHAT",MODE_PRIVATE,null);
        drawerLayout=findViewById(R.id.DrawerLayoutSettings);
        Toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        Toggle.setHomeAsUpIndicator(0);
        Toggle.syncState();

        cameraPic=findViewById(R.id.cameraPic);
        profileImg=findViewById(R.id.profileImage);
        phoneNumberActual=findViewById(R.id.PhoneNumberActual);
        nameLayout=findViewById(R.id.NameLayoutHorizontal);
        aboutLayout=findViewById(R.id.StatusLayoutHorizontal);
        nameActual=findViewById(R.id.NameActual);
        aboutActual=findViewById(R.id.StatusActual);

        final String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadProcess = new ProgressDialog(Settings.this);
                loadProcess.setTitle("Loading Data");
                loadProcess.setMessage("Please wait your info is being loaded");
                loadProcess.setCanceledOnTouchOutside(false);
                loadProcess.show();

                final String name=dataSnapshot.child("userName").getValue().toString();
                final String status=dataSnapshot.child("status").getValue().toString();
                final String phoneNumber=dataSnapshot.child("phoneNumber").getValue().toString();
                final String thumbNail=dataSnapshot.child("thumbNail").getValue().toString();
                final String profilePic=dataSnapshot.child("profilePic").getValue().toString();

                if(profilePic.equals("null")&&thumbNail.equals("null")){
                    nameActual.setText(name);
                    aboutActual.setText(status);
                    phoneNumberActual.setText(phoneNumber);
                    loadProcess.dismiss();
                    return;
                }
                else {


                    mImageStorage.child("profile_images").child("thumbs").child(current_uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            nameActual.setText(name);
                            aboutActual.setText(status);
                            phoneNumberActual.setText(phoneNumber);
                            Picasso.get().load(uri.toString()).placeholder(R.drawable.propic).into(profileImg);
                            loadProcess.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            nameActual.setText(name);
                            aboutActual.setText(status);
                            phoneNumberActual.setText(phoneNumber);
                            loadProcess.dismiss();
                        }
                    });
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });



        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunctions.goToImgView(Settings.this,current_uid,nameActual.getText().toString());


            }
        });

        cameraPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder  builder=new AlertDialog.Builder(Settings.this);
                builder.setTitle("Change Your Name");

                final EditText inputName=new EditText(Settings.this);
                final ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(50,30);
                inputName.setLayoutParams(layoutParams);
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);
                inputName.setHint(nameActual.getText().toString().trim());
                builder.setView(inputName);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String Name=inputName.getText().toString().trim();
                        boolean execute=true;
                        if(Name.isEmpty()){
                            Toast.makeText(Settings.this, "User Name Can't be empty", Toast.LENGTH_SHORT).show();
                            execute=false;

                        }
                        else if(Name.length()<5){
                            Toast.makeText(Settings.this, "User Name is too short", Toast.LENGTH_SHORT).show();
                            execute=false;

                        }
                        if(execute){
                            Map updateMap=new HashMap();
                            updateMap.put("userName",Name);
                            mUserDatabase.updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        nameActual.setText(Name);
                                    }
                                    else{
                                        Toast.makeText(Settings.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }



                        dialog.cancel();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();

            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder  builder=new AlertDialog.Builder(Settings.this);
                builder.setTitle("Change Your Status!");

                final EditText inputAbout=new EditText(Settings.this);
                final ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(50,30);
                inputAbout.setLayoutParams(layoutParams);
                inputAbout.setInputType(InputType.TYPE_CLASS_TEXT);
                inputAbout.setHint(aboutActual.getText().toString().trim());
                builder.setView(inputAbout);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String Status=inputAbout.getText().toString().trim();
                        boolean execute=true;
                        if(Status.isEmpty()){
                            Toast.makeText(Settings.this, "Status Can't be empty", Toast.LENGTH_SHORT).show();
                            execute=false;
                        }
                        if(Status.length()>40){
                            Toast.makeText(Settings.this, "Status can't be too large", Toast.LENGTH_SHORT).show();
                            execute=false;
                        }
                        if(execute){
                            Map update_map=new HashMap();
                            update_map.put("status",Status);
                            mUserDatabase.updateChildren(update_map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        aboutActual.setText(Status);
                                    }
                                    else{
                                        Toast.makeText(Settings.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                        dialog.cancel();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();
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


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        if(isLogged){
            MenuItem item=menu.findItem(R.id.logIn);
            item.setVisible(false);
            item=menu.findItem(R.id.settingsMenu);
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

    private void testPushNotification(Context ctx){
        Intent intent=new Intent(ctx,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = "some_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        androidx.core.app.NotificationCompat.Builder notificationBuilder =
                new androidx.core.app.NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Message from Ex")
                        .setContentText("This is an important Notification")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setBadgeIconType(androidx.core.app.NotificationCompat.BADGE_ICON_SMALL)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }


        assert notificationManager != null;
        int uniqueNotificationNumber=(int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(uniqueNotificationNumber /* ID of notification */, notificationBuilder.build());
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, GALLERY_PICK);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), GALLERY_PICK);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                mProgressDialog = new ProgressDialog(Settings.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();
                final File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                Bitmap thumb_bitmap=null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");


                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final String download_url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        UploadTask uploadTask=thumb_filepath.putBytes(thumb_byte);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot thumb_taskSnapshot) {
                                final String thumb_download_url=thumb_taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                //final String tdu=mImageStorage.child("profile_images").child("thumbs").child(mCurrentUser.getUid()+".jpg").getDownloadUrl().toString();
                                Map update_hashMap = new HashMap();
                                update_hashMap.put("profilePic", download_url);
                                update_hashMap.put("thumbNail", thumb_download_url);

                                mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            mProgressDialog.dismiss();
                                            Toast.makeText(Settings.this, "Success Uploading.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Settings.this, "Error while uploading Thumb Nail. Please try again", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this, "Error while uploading profile pic. Please Try again", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }

    }
}
