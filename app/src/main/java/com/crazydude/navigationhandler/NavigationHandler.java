package com.crazydude.navigationhandler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Crazy on 08.10.2015.
 */
public class NavigationHandler {

    public class Transaction {

        private Fragment mCurrentFragment;

        public Transaction(Fragment fragment) {
            mCurrentFragment = fragment;
        }

        public void setCurrentFragment(Fragment fragment) {
            this.mCurrentFragment = fragment;
        }

        public Fragment getFragment() {
            return mCurrentFragment;
        }
    }

    public enum SwitchMethod {
        ADD, REPLACE;
    }

    private WeakReference<AppCompatActivity> mActivity;
    private ArrayList<Transaction> mNavigationList;

    public NavigationHandler(AppCompatActivity activity) {
        mNavigationList = new ArrayList<>();
        mActivity = new WeakReference<>(activity);
    }

    public void switchFragment(@NonNull Fragment fragment, SwitchMethod switchMethod, boolean addToEnd) {
        if (switchMethod == SwitchMethod.ADD && !addToEnd) {
            throw new RuntimeException("Navigation handler doesn't support ADD method without adding to the end right now!");
        }

        FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (switchMethod) {
            case ADD:
                transaction.add(R.id.content, fragment);
                break;
            case REPLACE:
                transaction.replace(R.id.content, fragment);
                break;
        }

        Transaction listTransaction = new Transaction(fragment);

        if (!addToEnd) {
            if (mNavigationList.size() == 0) {
                mNavigationList.add(listTransaction);
            } else {
                transaction.remove(mNavigationList.get(mNavigationList.size() - 1).getFragment());
                mNavigationList.get(mNavigationList.size() - 1).setCurrentFragment(fragment);
            }
        } else {
            mNavigationList.add(listTransaction);
        }

        transaction.commit();
    }

    public void removeAllFragmentsFromList() {
        FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (Transaction transaction1 : mNavigationList) {
            transaction.remove(transaction1.getFragment());
        }

        mNavigationList.clear();
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public void removeFragmentFromList(int index) {
        if (mNavigationList.size() > 0 && index < mNavigationList.size()) {
            FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.remove(mNavigationList.get(index).getFragment());
            mNavigationList.remove(index);
            transaction.commit();
        }
    }

    public void switchBack() {
        switchBack(1);
    }

    public void switchBack(int count) {
        if (mNavigationList.size() - 1 < count) {
            mActivity.get().finish();
        } else {
            FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (int i = 0; i < count; i++) {
                if (mNavigationList.size() > 0) {
                    transaction.remove(mNavigationList.get(mNavigationList.size() - 1 - i).getFragment());
                    mNavigationList.remove(mNavigationList.size() - 1);
                }
            }
            if (mNavigationList.size() > 0) {
                if (!mNavigationList.get(mNavigationList.size() - 1).getFragment().isAdded()) {
                    transaction.add(R.id.content, mNavigationList.get(mNavigationList.size() - 1).getFragment());
                }
            }
            transaction.commit();
        }
    }

    public void handleBackButtonPress() {
        if (mNavigationList.size() > 1) {
            FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(mNavigationList.get(mNavigationList.size() - 1).getFragment());
            mNavigationList.remove(mNavigationList.get(mNavigationList.size() - 1));
            if (mNavigationList.size() > 0) {
                Fragment fragment = mNavigationList.get(mNavigationList.size() - 1).getFragment();
                if (!fragment.isAdded()) {
                    transaction.add(R.id.content, fragment);
                }
            }
            transaction.commit();
        } else {
            mActivity.get().finish();
        }
    }

    public void saveState(Bundle state) {
        state.putSerializable("navigation_list", mNavigationList);
    }

    public void restoreState(Bundle state) {
        mNavigationList = (ArrayList<Transaction>) state.getSerializable("navigation_list");
    }
}
