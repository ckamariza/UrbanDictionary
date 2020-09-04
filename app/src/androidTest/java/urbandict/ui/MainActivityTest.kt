package urbandict.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.TypeTextAction
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withResourceName
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import nike.urbandict.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainActivityTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun load_definitions_successfully() {
        val activity = activityTestRule.activity

        onView(withResourceName("search_src_text"))
            .perform(TypeTextAction("wat"), pressImeActionButton())

        waitUntilLoaded(activity)

        assertFalse(activity.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshView).isRefreshing)

        onView(withId(R.id.definitionsView)).check { view, noViewFoundException ->
            if (view == null) {
                throw noViewFoundException
            }
            assertTrue((view as RecyclerView).childCount > 2)
        }
    }

}