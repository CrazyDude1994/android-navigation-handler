package com.crazydude.navigationhandlerdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.crazydude.navigationhandler.Feature;
import com.crazydude.navigationhandler.FeatureProvider;
import com.crazydude.navigationhandler.NavigationHandler;
import com.crazydude.navigationhandlerdemo.fragments.FirstFragment;
import com.crazydude.navigationhandlerdemo.fragments.SecondFragment;
import com.crazydude.navigationhandlerdemo.fragments.ThirdFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FeatureProvider {

    private NavigationHandler mNavigationHandler;

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationHandler = new NavigationHandler(this, R.id.content, this);

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        Button withBackstack = (Button) findViewById(R.id.with_backstack);
        Button withoutBackstack = (Button) findViewById(R.id.without_backstack);
        Button withBackstackAdd = (Button) findViewById(R.id.with_backstack_add);
        Button withoutBackstackAdd = (Button) findViewById(R.id.without_backstack_add);

        Button switchBack = (Button) findViewById(R.id.switch_back_one);
        Button switchBackFew = (Button) findViewById(R.id.switch_back_few);
        Button switchBackAll = (Button) findViewById(R.id.switch_back_all);

        final EditText switchCount = (EditText) findViewById(R.id.switch_count);

        withBackstack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchFragment(getCurrentSelectedFragment(),
                        NavigationHandler.SwitchMethod.REPLACE, true);
            }
        });

        withoutBackstack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchFragment(getCurrentSelectedFragment(), NavigationHandler.SwitchMethod.REPLACE, false);
//                mNavigationHandler.removeFragmentFromList(Integer.decode(switchCount.getText().toString()));
            }
        });

        withBackstackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchFragment(getCurrentSelectedFragment(), NavigationHandler.SwitchMethod.ADD, true);
            }
        });

        withoutBackstackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchFragment(getCurrentSelectedFragment(), NavigationHandler.SwitchMethod.ADD, false);
            }
        });

        switchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchBack();
            }
        });

        switchBackFew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchBack(Integer.decode(switchCount.getText().toString()));
            }
        });

        switchBackAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.removeAllFragmentsFromList();
            }
        });
    }

    private Fragment getCurrentSelectedFragment() {
        Fragment fragment;

        if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio_first) {
            fragment = FirstFragment.newInstance("", "");
        } else if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio_second) {
            fragment = SecondFragment.newInstance("", "");
        } else {
            fragment = ThirdFragment.newInstance("", "");
        }
        return fragment;
    }

    @Override
    public void onBackPressed() {
        mNavigationHandler.handleBackButtonPress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mNavigationHandler.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mNavigationHandler.restoreState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void provideFeatures(List<Feature> features) {
        for (Feature feature : features) {
            switch (feature.getName()) {
                case "toolbar":
                    Bundle arguments = feature.getArguments();
                    getSupportActionBar().setTitle(arguments.getString("title"));
                    break;
            }
        }
    }
}
