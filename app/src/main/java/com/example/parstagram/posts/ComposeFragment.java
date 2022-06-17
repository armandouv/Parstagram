package com.example.parstagram.posts;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.parstagram.databinding.FragmentComposeBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

/**
 * Post creation screen.
 */
public class ComposeFragment extends Fragment {

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final String APP_TAG = ComposeFragment.class.getSimpleName();
    private FragmentComposeBinding mBinding;
    private File mPhotoFile;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentComposeBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.submitImage.setOnClickListener(v -> {
            String description = mBinding.description
                    .getText()
                    .toString();
            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Description must not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mPhotoFile == null || mBinding.image.getDrawable() == null) {
                Toast.makeText(getContext(), "You must upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            ParseUser user = ParseUser.getCurrentUser();

            Post post = new Post();
            post.setDescription(description);
            post.setImage(new ParseFile(mPhotoFile));
            post.setUser(user);

            post.saveInBackground(e -> {
                if (e != null) {
                    Toast.makeText(getContext(), "Could not create post", Toast.LENGTH_SHORT).show();
                    return;
                }

                mBinding.description.setText("");
                mBinding.image.setImageResource(0);
                Toast.makeText(getContext(), "Post created successfully", Toast.LENGTH_LONG).show();
            });
        });

        mBinding.uploadImage.setOnClickListener(v -> launchCamera());
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri("photo.jpg");

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

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
                mBinding.image.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}