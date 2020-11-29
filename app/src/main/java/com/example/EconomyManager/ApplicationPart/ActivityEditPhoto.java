package com.example.EconomyManager.ApplicationPart;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.EconomyManager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ActivityEditPhoto extends AppCompatActivity
{
    private ImageView goBack, uploadedFile;
    private TextView text;
    private ProgressBar progressBar;
    private Uri imageUri;
    private Button chooseFile, uploadFile;
    private DatabaseReference myRef;
    private StorageReference myStorage;
    private FirebaseAuth fbAuth;
    private static final int PICK_IMAGE_REQUEST=2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);
        setVariables();
        setTheme();
        setOnClickListeners();
        setText();
        setInitialProgressBar();
        setPhoto();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables()
    {
        goBack=findViewById(R.id.editPhotoBack);
        text=findViewById(R.id.editPhotoText);
        progressBar=findViewById(R.id.editPhotoProgressBar);
        chooseFile=findViewById(R.id.editPhotoChooseFile);
        uploadFile=findViewById(R.id.editPhotoUploadFile);
        uploadedFile=findViewById(R.id.editPhotoFileUploaded);
        fbAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        myStorage= FirebaseStorage.getInstance().getReference();
    }

    private void setOnClickListeners()
    {
        goBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        chooseFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openFileChooser();
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uploadFile();
            }
        });
    }

    private void setText()
    {
        String textToBeShown=getResources().getString(R.string.edit_photo_title).trim();
        text.setText(textToBeShown);
        text.setTextSize(20);
        text.setTextColor(Color.WHITE);
    }

    private void setInitialProgressBar()
    {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void openFileChooser()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && data!=null && data.getData()!=null)
        {
            imageUri=data.getData();
            uploadedFile.setImageURI(imageUri);
            uploadedFile.getLayoutParams().width=(int)(0.75*getResources().getDisplayMetrics().widthPixels);
        }
    }

    private void uploadFile()
    {
        if(imageUri!=null)
        {
            if(fbAuth.getUid()!=null)
            {
                progressBar.setVisibility(View.VISIBLE);
                myStorage.child(fbAuth.getUid()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                progressBar.setProgress(0);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }, 500);
                        Toast.makeText(ActivityEditPhoto.this, getResources().getString(R.string.edit_photo_upload_successful), Toast.LENGTH_SHORT).show();
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri url=uriTask.getResult();
                        myRef.child(fbAuth.getUid()).child("PersonalInformation").child("photoURL").setValue(String.valueOf(url));
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(ActivityEditPhoto.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot)
                    {
                        float progress=100*taskSnapshot.getBytesTransferred()/(float)taskSnapshot.getTotalByteCount();
                        progressBar.setProgress((int)progress);
                    }
                });
            }
        }
        else Toast.makeText(this, getResources().getString(R.string.edit_photo_select_file), Toast.LENGTH_SHORT).show();
    }

    private void setPhoto()
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("PersonalInformation").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("photoURL"))
                    {
                        String URL=String.valueOf(snapshot.child("photoURL").getValue());
                        if(!URL.trim().equals(""))
                            Picasso.get().load(URL).placeholder(R.drawable.ic_add_photo).fit().into(uploadedFile);
                        else uploadedFile.setImageResource(R.drawable.ic_add_photo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void setTheme()
    {
        int theme=R.drawable.ic_yellow_gradient_soda;
        getWindow().setBackgroundDrawableResource(theme);
    }
}