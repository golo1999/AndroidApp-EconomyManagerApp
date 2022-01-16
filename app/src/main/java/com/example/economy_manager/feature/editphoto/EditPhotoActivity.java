package com.example.economy_manager.feature.editphoto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.EditPhotoActivityBinding;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

public class EditPhotoActivity extends AppCompatActivity {
    private EditPhotoActivityBinding binding;
    private EditPhotoViewModel viewModel;
    private SharedPreferences preferences;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithFadeTransition(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVariables();
        setInitialProgressBar();
        viewModel.showPhoto(binding.uploadedPhoto);
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode,
                                    final @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == viewModel.getRequestId() &&
                data != null &&
                data.getData() != null) {
            final int screenWidthPixels = (int) (0.75 * getResources().getDisplayMetrics().widthPixels);

            viewModel.setImageUri(data.getData());
            binding.uploadedPhoto.setImageURI(viewModel.getImageUri());
            binding.uploadedPhoto.getLayoutParams().width = screenWidthPixels;
        }
    }

    private UserDetails retrieveUserDetailsFromSharedPreferences() {
        SharedPreferences preferences =
                getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("currentUserDetails", "");

        return gson.fromJson(json, UserDetails.class);
    }

    private void setInitialProgressBar() {
        binding.progressBar.setVisibility(View.INVISIBLE);
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.edit_photo_activity);
        viewModel = new ViewModelProvider(this).get(EditPhotoViewModel.class);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);

        binding.setActivity(this);
        binding.setViewModel(viewModel);
    }

    public void uploadFile() {
        if (viewModel.getImageUri() != null) {
            if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                binding.progressBar.setVisibility(View.VISIBLE);

                viewModel.getStorageReference().child(MyCustomVariables.getFirebaseAuth().getUid())
                        .putFile(viewModel.getImageUri())
                        .addOnSuccessListener((final UploadTask.TaskSnapshot taskSnapshot) -> {
                            final Handler handler = new Handler();

                            handler.postDelayed(() -> {
                                binding.progressBar.setProgress(0);
                                binding.progressBar.setVisibility(View.INVISIBLE);
                            }, 500);

                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.edit_photo_upload_successful));

                            final Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isComplete()) ;

                            final Uri url = uriTask.getResult();

                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalInformation")
                                    .child("photoURL")
                                    .setValue(String.valueOf(url));

                            final UserDetails userDetailsFromSharedPreferences =
                                    MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);

                            // updating current user's photo url if it's not the same as before
                            if (userDetailsFromSharedPreferences != null) {
                                if (!userDetailsFromSharedPreferences.getPersonalInformation().getPhotoURL()
                                        .equals(String.valueOf(url))) {
                                    userDetailsFromSharedPreferences.getPersonalInformation()
                                            .setPhotoURL(String.valueOf(url));
                                }

                                if (!userDetailsAlreadyExistInSharedPreferences(userDetailsFromSharedPreferences)) {
                                    MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences,
                                            userDetailsFromSharedPreferences);

                                    MyCustomVariables.setUserDetails(userDetailsFromSharedPreferences);
                                }
                            }
                        })
                        .addOnFailureListener((final Exception e) ->
                                MyCustomMethods.showShortMessage(this, e.getMessage()))
                        .addOnProgressListener((final UploadTask.TaskSnapshot taskSnapshot) -> {
                            final long taskSnapshotBytesTransferred = taskSnapshot.getBytesTransferred();

                            final float taskSnapshotTotalByteCount = (float) taskSnapshot.getTotalByteCount();

                            final float progress = 100 * taskSnapshotBytesTransferred / taskSnapshotTotalByteCount;

                            binding.progressBar.setProgress((int) progress);
                        });
            }
        } else {
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.edit_photo_select_file));
        }
    }

    private boolean userDetailsAlreadyExistInSharedPreferences(final UserDetails details) {
        UserDetails userDetailsFromSharedPreferences = retrieveUserDetailsFromSharedPreferences();

        return details.equals(userDetailsFromSharedPreferences);
    }
}