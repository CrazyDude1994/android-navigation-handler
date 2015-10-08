package com.crazydude.navigationhandler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private NavigationHandler mNavigationHandler;

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationHandler = new NavigationHandler(this);

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        Button withBackstack = (Button) findViewById(R.id.with_backstack);
        Button withoutBackstack = (Button) findViewById(R.id.without_backstack);
        Button withBackstackAdd = (Button) findViewById(R.id.with_backstack_add);
        Button withoutBackstackAdd = (Button) findViewById(R.id.without_backstack_add);

        withBackstack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchFragment(getCurrentSelectedFragment(), true);
            }
        });

        withoutBackstack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchFragment(getCurrentSelectedFragment(), false);
            }
        });

        withBackstackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchFragment(getCurrentSelectedFragment(), true, true);
            }
        });

        withoutBackstackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationHandler.switchFragment(getCurrentSelectedFragment(), false, true);
            }
        });
    }

    private FragmentEnum getCurrentSelectedFragment() {
        FragmentEnum fragmentEnum;

        if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio_first) {
            fragmentEnum = FragmentEnum.FIRST;
        } else if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio_second) {
            fragmentEnum = FragmentEnum.SECOND;
        } else {
            fragmentEnum = FragmentEnum.THIRD;
        }
        return fragmentEnum;
    }

    @Override
    public void onBackPressed() {
        mNavigationHandler.handleBackPress();
    }
}
