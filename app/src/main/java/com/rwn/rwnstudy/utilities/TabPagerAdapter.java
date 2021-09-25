package com.rwn.rwnstudy.utilities;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rwn.rwnstudy.activities.ChatFragment;
import com.rwn.rwnstudy.activities.StatusFragment;
import com.rwn.rwnstudy.activities.UploadFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {


    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {

                return new StatusFragment();
            }
            case 1: {
                return new ChatFragment();
            }
            case 2: {
                return new UploadFragment();
            }

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Status";
            case 1:
                return  "Chats";

            case 2:
                return  "Upload";

            default:
                return null;
        }
    }
}
