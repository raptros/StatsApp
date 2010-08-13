package com.roundarch.statsapp;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.roundarch.statsapp.DisplayActivityTest \
 * com.roundarch.statsapp.tests/android.test.InstrumentationTestRunner
 */
public class DisplayActivityTest extends ActivityInstrumentationTestCase2<DisplayActivity> {

    public DisplayActivityTest() {
        super("com.roundarch.statsapp", DisplayActivity.class);
    }

}
