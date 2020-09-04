package urbandict.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import nike.urbandict.R


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    private val currentSortOrder: MainActivityViewModel.SortOrder
        get() {
            return if (navView.selectedItemId == R.id.sort_up) {
                MainActivityViewModel.SortOrder.THUMBS_UP
            } else {
                MainActivityViewModel.SortOrder.THUMBS_DOWN
            }
        }

    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarView)
        setUpAdapter()
        setUpSearchView()
        setUpActionBar()
    }

    private fun setUpAdapter() {
        val adapter = DefinitionsAdapter()
        val layoutManager = LinearLayoutManager(this)
        definitionsView.layoutManager = layoutManager
        definitionsView.adapter = adapter
        viewModel.definitions.observe(this, { result ->
            swipeRefreshView.isRefreshing = result.isLoading
            navView.isEnabled = !result.isLoading
            adapter.submitList(result.data.orEmpty())
            if (result.error?.consumed == false) {
                val throwable = result.error.consume()
                Log.e(TAG, "Failed to load definitions", throwable)

                Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setUpSearchView() {
        val inputMM = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        searchView = SearchView(
            ContextThemeWrapper(
                this,
                R.style.ThemeOverlay_MaterialComponents_Dark_ActionBar
            )
        ).apply { id = R.id.searchView }
        searchView.imeOptions = searchView.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setIconifiedByDefault(false)
        searchView.setOnSearchClickListener {
            viewModel.search(searchView.query?.toString().orEmpty(), currentSortOrder)
            inputMM.hideSoftInputFromWindow(searchView.applicationWindowToken, 0)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query.orEmpty(), currentSortOrder)
                inputMM.hideSoftInputFromWindow(searchView.applicationWindowToken, 0)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        swipeRefreshView.setOnRefreshListener {
            viewModel.refresh(currentSortOrder)
            inputMM.hideSoftInputFromWindow(searchView.applicationWindowToken, 0)
        }

        navView.setOnNavigationItemSelectedListener {
            inputMM.hideSoftInputFromWindow(searchView.applicationWindowToken, 0)
            navView.post { viewModel.sort(currentSortOrder) }
            true
        }

    }

    private fun setUpActionBar(){
        supportActionBar?.customView = searchView

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
    }


    companion object {
        const val TAG = "MainActivity"
    }
}
