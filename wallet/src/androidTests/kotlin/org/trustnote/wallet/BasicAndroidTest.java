package org.trustnote.wallet;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.support.test.runner.AndroidJUnit4;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class BasicAndroidTest {

    @Before
    public void createLogHistory() {
    }

    @Test
    public void logHistory_ParcelableWriteRead() {
        // Set up the Parcelable object to send and receive.
        android.util.Log.e("Abc", "Abc");
    }
}