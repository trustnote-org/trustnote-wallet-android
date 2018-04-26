package org.trustnote.wallet

import android.content.Context
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.trustnote.wallet.util.Utils

@RunWith(MockitoJUnitRunner::class)
class BasicTest {
    @Mock
    lateinit var mMockContext: Context

    @Test
    fun testKotlinTryCode() {
        val isAllright = (6 > 5)
        assertTrue(isAllright)
    }

}
