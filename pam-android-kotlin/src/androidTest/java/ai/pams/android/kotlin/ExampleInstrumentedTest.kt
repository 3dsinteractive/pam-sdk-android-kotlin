package ai.pams.android.kotlin

import ai.pams.android.kotlin.flex.parser.FlexParser
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("ai.pams.android.kotlin.test", appContext.packageName)
    }

    @Test
    fun testParseFlex(){
        val flex = """
    root(
            image(
                src="https://cdn.icon-icons.com/icons2/2248/PNG/512/cat_icon_138789.png"
                href="https://cdn.icon-icons.com/icons2/2248/PNG/512/cat_icon_138789.png"
            )
            text(
                text="How Much Wet Food Should I Feed My Cat?"
                size="title"
            )
            text(
                text="Knowing how much to feed your cat can help her maintain a healthy weight, but portion sizes can vary based on her age, weight, activity levels and more. Learn how to determine how much food your cat needs at each meal."
                size="body"
            )
        )
    """
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val flexView = FlexParser(appContext).parse(flex)
        val childNum = flexView?.childs?.count() ?: 0
        assertEquals(childNum, 3)
    }
}