package com.crazydude.navigationhandler;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by kartavtsev.s on 12.02.2016.
 */
public class Feature implements Serializable {

    private Bundle mArguments;
    private String mName;

    public Feature(String name, Bundle arguments) {
        mName = name;
        mArguments = arguments;
    }

    public Bundle getArguments() {
        return mArguments;
    }

    public String getName() {
        return mName;
    }
}
