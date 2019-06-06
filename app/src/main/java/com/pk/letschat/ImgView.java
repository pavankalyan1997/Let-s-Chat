package com.pk.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ImgView extends AppCompatActivity {

    ImageView backButton,zoomedImage;
    TextView imageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_view);

        backButton=findViewById(R.id.backButtonImgView);
        zoomedImage=findViewById(R.id.zoomedImageImgView);
        imageName=findViewById(R.id.imgViewName);

        String current_uid=getIntent().getStringExtra("userUid");
        String userName=getIntent().getStringExtra("name");

        imageName.setText(userName);
        final ProgressDialog loadProcess;
        loadProcess = new ProgressDialog(ImgView.this);
        loadProcess.setTitle("Loading Image");
        loadProcess.setMessage("Please wait Image is being loaded");
        loadProcess.setCanceledOnTouchOutside(false);
        loadProcess.show();

        FirebaseStorage.getInstance().getReference().child("profile_images").child(current_uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Picasso.get().load(uri.toString()).placeholder(R.drawable.propic).into(zoomedImage);
                loadProcess.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadProcess.dismiss();

            }
        });




        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
