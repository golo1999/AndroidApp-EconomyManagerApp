package com.example.economy_manager.feature.editphoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditPhotoViewModel extends ViewModel {

    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private Uri imageUri;

    public int getRequestId() {
        return 2;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public void openFileChooser(final @NonNull Activity activity) {
        final Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, getRequestId());
    }

    public void showPhoto(final ImageView uploadedPhoto) {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("personalInformation")
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
                                            .into(uploadedPhoto);
                                } else {
                                    uploadedPhoto.setImageResource(R.drawable.ic_add_photo);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        }
    }
}