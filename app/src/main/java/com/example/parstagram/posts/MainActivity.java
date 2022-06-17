package com.example.parstagram.posts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.parstagram.R;
import com.example.parstagram.databinding.ActivityTimelineBinding;
import com.example.parstagram.users.ProfileFragment;


/**
 * Displays different Fragments depending on the option selected by the user in the navigation menu.
 */
public class MainActivity extends AppCompatActivity {
    private ActivityTimelineBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.compose_icon:
                    fragment = new ComposeFragment();
                    break;
                case R.id.profile_icon:
                    fragment = new ProfileFragment();
                    break;
                default:
                    fragment = new TimelineFragment();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return true;
        });

        mBinding.bottomNavigation.setSelectedItemId(R.id.home_icon);
    }
}