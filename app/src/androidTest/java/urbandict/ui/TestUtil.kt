package urbandict.ui

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import nike.urbandict.R

/**
 * Stop the test until RecyclerView's data gets loaded.
 *
 * Workaround for https://issuetracker.google.com/issues/123653014
 */
fun waitUntilLoaded(activity: Activity) {
    Espresso.onIdle()

    lateinit var recycleView: RecyclerView
    lateinit var refreshView: SwipeRefreshLayout

    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        recycleView = activity.findViewById(R.id.definitionsView)
        refreshView = activity.findViewById(R.id.swipeRefreshView)
    }

    while (recycleView.hasPendingAdapterUpdates() || refreshView.isRefreshing) {
        Thread.sleep(10)
    }
}

