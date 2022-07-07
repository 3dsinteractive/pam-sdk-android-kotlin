package ai.pams.android.kotlin

import ai.pams.android.kotlin.utils.DateUtils
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {
    @Test
    fun testParseDate() {
        val str = "2022-06-29 03:53:51"
        val date = DateUtils.localDateTimeFromString(str)
        assertEquals(date.hour, 10)
    }
}