package com.example.parstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.parstagram.databinding.ActivityTimelineBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

public class TimelineActivity extends AppCompatActivity {
    private ActivityTimelineBinding binding;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final String APP_TAG = TimelineActivity.class.getSimpleName();
    private File mPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logoutButton.setOnClickListener(v ->
                ParseUser.logOutInBackground(e -> {
                    if (e != null) {
                        Toast.makeText(this, "Couldn't log out", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    goLoginActivity();
                    finish();
                }));

        binding.submitImage.setOnClickListener(v -> {
            String description = binding.description
                    .getText()
                    .toString();
            if (description.isEmpty()) {
                Toast.makeText(this, "Description must not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mPhotoFile == null || binding.image.getDrawable() == null) {
                Toast.makeText(this, "You must upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            ParseUser user = ParseUser.getCurrentUser();

            Post post = new Post();
            post.setDescription(description);
            post.setImage(new ParseFile(mPhotoFile));
            post.setUser(user);

            post.saveInBackground(e -> {
                if (e != null) {
                    Toast.makeText(this, "Could not create post", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.description.setText("");
                binding.image.setImageResource(0);
                Toast.makeText(this, "Post created successfully", Toast.LENGTH_LONG).show();
            });
        });

        binding.uploadImage.setOnClickListener(v -> {
            launchCamera();
            Toast.makeText(this, "jala", Toast.LENGTH_SHORT).show();
        });
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri("photo.jpg");

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                binding.image.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}