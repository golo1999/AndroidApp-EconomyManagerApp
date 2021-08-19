package com.example.economy_manager.main_part.views.activities;

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

import com.example.economy_manager.R;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditPhotoActivity extends AppCompatActivity {
    private ImageView goBack;

    private ImageView uploadedFile;

    private TextView text;

    private ProgressBar progressBar;

    private Uri imageUri;

    private Button chooseFileButton;

    private Button uploadFileButton;

    private StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_photo_activity);
        setVariables();
        setTheme();
        setOnClickListeners();
        setText();
        setInitialProgressBar();
        setPhoto();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables() {
        goBack = findViewById(R.id.editPhotoBack);
        text = findViewById(R.id.editPhotoText);
        progressBar = findViewById(R.id.editPhotoProgressBar);
        chooseFileButton = findViewById(R.id.editPhotoChooseFile);
        uploadFileButton = findViewById(R.id.editPhotoUploadFile);
        uploadedFile = findViewById(R.id.editPhotoFileUploaded);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void setOnClickListeners() {
        goBack.setOnClickListener(v -> onBackPressed());

        chooseFileButton.setOnClickListener(v -> openFileChooser());

        uploadFileButton.setOnClickListener(v -> uploadFile());
    }

    private void setText() {
        String textToBeShown = getResources().getString(R.string.edit_photo_title).trim();
        text.setText(textToBeShown);
        text.setTextSize(20);
        text.setTextColor(Color.WHITE);
    }

    private void setInitialProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST &&
                data != null &&
                data.getData() != null) {
            imageUri = data.getData();
            uploadedFile.setImageURI(imageUri);
            uploadedFile.getLayoutParams().width = (int) (0.75 * getResources().getDisplayMetrics().widthPixels);
        }
    }

    private void uploadFile() {
        if (imageUri != null) {
            if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                progressBar.setVisibility(View.VISIBLE);

                storageReference.child(MyCustomVariables.getFirebaseAuth().getUid())
                        .putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                progressBar.setProgress(0);
                                progressBar.setVisibility(View.INVISIBLE);
                            }, 500);
                            Toast.makeText(EditPhotoActivity.this,
                                    getResources().getString(R.string.edit_photo_upload_successful),
                                    Toast.LENGTH_SHORT).show();

                            final Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isComplete()) ;

                            final Uri url = uriTask.getResult();

                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalInformation")
                                    .child("photoURL")
                                    .setValue(String.valueOf(url));
                        })
                        .addOnFailureListener(e -> Toast.makeText(EditPhotoActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show())
                        .addOnProgressListener(taskSnapshot -> {
                            final float progress =
                                    100 * taskSnapshot.getBytesTransferred() / (float) taskSnapshot.getTotalByteCount();

                            progressBar.setProgress((int) progress);
                        });
            }
        } else
            Toast.makeText(this, getResources().getString(R.string.edit_photo_select_file), Toast.LENGTH_SHORT).show();
    }

    private void setPhoto() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("PersonalInformation")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("photoURL")) {
                                final String URL = String.valueOf(snapshot.child("photoURL").getValue());

                                if (!URL.trim().isEmpty()) {
                                    Picasso.get()
                                            .load(URL)
                                            .placeholder(R.drawable.ic_add_photo)
                                            .fit()
                                            .into(uploadedFile);
                                } else {
                                    uploadedFile.setImageResource(R.drawable.ic_add_photo);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setTheme() {
        final int theme = R.drawable.ic_yellow_gradient_soda;

        getWindow().setBackgroundDrawableResource(theme);
    }
}