package com.crazydude.navigationhandler;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    private int mContentId;
    private FeatureProvider mFeatureProvider;
    private int mFragmentIn;
    private int mFragmentOut;
    private HashSet<FragmentTransactionListener> mTransactionListeners = new HashSet<>();

    public NavigationHandler(AppCompatActivity activity,
                             @IdRes int contentId) {
        init(activity, contentId, null);
    }

    public NavigationHandler(AppCompatActivity activity,
                             @IdRes int contentId,
                             @Nullable FeatureProvider featureProvider) {
        init(activity, contentId, featureProvider);
        mFeatureProvider = featureProvider;
    }

    public NavigationHandler(AppCompatActivity activity,
                             @IdRes int contentId,
                             @Nullable FeatureProvider featureProvider,
                             int fragmentInAnim,
                             int fragmentOutAnim) {
        init(activity, contentId, featureProvider);
        mFeatureProvider = featureProvider;
        mFragmentIn = fragmentInAnim;
        mFragmentOut = fragmentOutAnim;
    }

    private void init(AppCompatActivity activity, @IdRes int contentId,
                      @Nullable FeatureProvider featureProvider) {
        mNavigationList = new ArrayList<>();
        mActivity = new WeakReference<>(activity);
        mContentId = contentId;
    }

    public void addTransactionListener(FragmentTransactionListener listener) {
        mTransactionListeners.add(listener);
    }

    private void notifyListeners(Transaction transaction, boolean isBack) {
        for (FragmentTransactionListener listener : mTransactionListeners) {
            listener.onTransaction(transaction, isBack);
        }
    }

    public void switchFragment(@NonNull Fragment fragment, SwitchMethod switchMethod, boolean addToEnd) {
        if (switchMethod == SwitchMethod.ADD && !addToEnd) {
            throw new RuntimeException("Navigation handler doesn't support ADD method without adding to the end right now!");
        }

        FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        setAnimation(transaction);

        switch (switchMethod) {
            case ADD:
                transaction.add(mContentId, fragment);
                break;
            case REPLACE:
                transaction.replace(mContentId, fragment);
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

        requestFeatureProvider(fragment);

        transaction.commit();
        notifyListeners(listTransaction, false);
    }

    private void setAnimation(FragmentTransaction transaction) {
        if (mFragmentOut != 0 && mFragmentIn != 0) {
            transaction.setCustomAnimations(mFragmentIn, mFragmentOut);
        }
    }

    public void removeAllFragmentsFromList() {
        FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        setAnimation(transaction);

        for (Transaction transaction1 : mNavigationList) {
            transaction.remove(transaction1.getFragment());
            notifyListeners(transaction1, true);
        }

        mNavigationList.clear();
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public void removeFragmentFromList(int index) {
        if (mNavigationList.size() > 0 && index < mNavigationList.size()) {
            FragmentManager fragmentManager = mActivity.get().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            setAnimation(transaction);

            transaction.remove(mNavigationList.get(index).getFragment());
            notifyListeners(mNavigationList.get(index), true);
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

            setAnimation(transaction);

            for (int i = 0; i < count; i++) {
                if (mNavigationList.size() > 0) {
                    transaction.remove(mNavigationList.get(mNavigationList.size() - 1).getFragment());
                    notifyListeners(mNavigationList.get(mNavigationList.size() - 1), true);
                    mNavigationList.remove(mNavigationList.size() - 1);
                }
            }
            if (mNavigationList.size() > 0) {
                if (!mNavigationList.get(mNavigationList.size() - 1).getFragment().isAdded()) {
                    Fragment fragment = mNavigationList.get(mNavigationList.size() - 1).getFragment();
                    requestFeatureProvider(fragment);
                    transaction.add(mContentId, fragment);
                }
            }
            transaction.commit();
        }
    }

    public void handleBackButtonPress() {
        switchBack();
    }

    public void saveState(Bundle state) {
        state.putSerializable("navigation_list", mNavigationList);
    }

    public void restoreState(Bundle state) {
        mNavigationList = (ArrayList<Transaction>) state.getSerializable("navigation_list");
        if (mNavigationList.size() > 0) {
            Transaction transaction = mNavigationList.get(mNavigationList.size() - 1);
            requestFeatureProvider(transaction.getFragment());
        }
    }

    private void requestFeatureProvider(Fragment fragment) {
        if (mFeatureProvider != null && fragment instanceof FeatureRequester) {
            List<Feature> features = ((FeatureRequester) fragment).requestFeature();
            mFeatureProvider.provideFeatures(features);
        }
    }

    public interface FragmentTransactionListener {

        void onTransaction(Transaction transaction, boolean isBack);
    }
}
