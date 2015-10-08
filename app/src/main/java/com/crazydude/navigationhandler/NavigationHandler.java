package com.crazydude.navigationhandler;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.crazydude.navigationhandler.fragments.FirstFragment;
import com.crazydude.navigationhandler.fragments.SecondFragment;
import com.crazydude.navigationhandler.fragments.ThirdFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Crazy on 08.10.2015.
 */
public class NavigationHandler {

    private final static String TRANSACTION_PREFIX = "TRANS_";

    private WeakReference<AppCompatActivity> mActivity;
    private ArrayList<String> mNavigationList;

    public NavigationHandler(AppCompatActivity activity) {
        mNavigationList = new ArrayList<>();
        mActivity = new WeakReference<>(activity);
    }

    public void switchFragment(FragmentEnum fragment, boolean addToBack) {
        switchFragment(fragment, addToBack, false);
    }

    public void switchFragment(FragmentEnum fragment, boolean addToBack, boolean storePrevious) {
        FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment createdFragment = null;

        switch (fragment) {

            case FIRST:
                createdFragment = FirstFragment.newInstance("", "");
                break;
            case SECOND:
                createdFragment = SecondFragment.newInstance("", "");
                break;
            case THIRD:
                createdFragment = ThirdFragment.newInstance("", "");
                break;
        }

        if (addToBack) {
            transaction.addToBackStack(TRANSACTION_PREFIX + mNavigationList.size());
            mNavigationList.add(TRANSACTION_PREFIX + mNavigationList.size());
        }

        if (storePrevious) {
            transaction.add(R.id.content, createdFragment, null);
        } else {
            transaction.replace(R.id.content, createdFragment);
        }

        transaction.commit();
    }

    public void switchBack() {
        handleBackPress();
    }

    public void switchBack(int count) {
        if (count > mNavigationList.size()) {
            mActivity.get().finish();
        } else {
            for (int i = 0; i < count; i++) {
                mNavigationList.remove(mNavigationList.size() - 1);
            }
            mActivity.get().getSupportFragmentManager().popBackStack(mNavigationList.get(mNavigationList.size() - 1), 0);
        }
    }

    public void switchBack(boolean removeAll) {
        if (mNavigationList.size() > 0) {
            mActivity.get().getSupportFragmentManager().popBackStack(mNavigationList.get(0), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mNavigationList.clear();
        }
    }

    public void handleBackPress() {
        if (mNavigationList.size() > 1) {
            mNavigationList.remove(mNavigationList.size() - 1);
            mActivity.get().getSupportFragmentManager().popBackStack(mNavigationList.get(mNavigationList.size() - 1), 0);
        } else {
            mActivity.get().finish();
        }
    }
}
