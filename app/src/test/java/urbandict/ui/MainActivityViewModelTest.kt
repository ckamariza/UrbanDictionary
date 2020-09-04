package urbandict.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import urbandict.api.Definition
import urbandict.repository.DefinitionsRepository
import urbandict.ui.MainActivityViewModel.SortOrder
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.CoroutineContext

class MainActivityViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val repo = mock<DefinitionsRepository>()
    private val savedStateHandle = mock<SavedStateHandle>()

    private val coroutineContextProvider = TestCoroutineContextProvider()

    private val response =
        listOf(
            Definition(
                "wat",
                "",
                1,
                2,
                1L,
            ),
            Definition(
                "wat",
                "",
                3,
                4,
                2L,
            ),
            Definition(
                "wat",
                "",
                5,
                3,
                3L,
            ),
            Definition(
                "wat",
                "",
                6,
                1,
                4L,
            )
        )

    @Test
    fun validTerm_searchThumpsUp_getDefinitionsSortedUp() {
        runBlocking {
            doReturn(flowOf(response)).`when`(repo).define(eq("wat"))

            val viewModel = MainActivityViewModel(
                savedStateHandle,
                repo,
                coroutineContextProvider,
            )

            viewModel.search("wat", SortOrder.THUMBS_UP)

            val value = viewModel.definitions.getOrAwaitValue()
            assertNull(value.error)
            assertEquals(false, value.isLoading)
            assertEquals(response.size, value.data?.size)
            assertEquals(4L, value.data?.get(0)?.id)

            verify(repo).define(eq("wat"))
        }
    }

    @Test
    fun validTerm_searchThumpsUp_getError() {
        runBlocking {
            doReturn(flow<List<Definition>> {
                throw IllegalStateException()
            }).`when`(repo).define(any())

            val viewModel = MainActivityViewModel(
                savedStateHandle,
                repo,
                coroutineContextProvider,
            )

            viewModel.search("wat", SortOrder.THUMBS_UP)

            val value = viewModel.definitions.getOrAwaitValue()
            assertNotNull(value.error)

            verify(repo).define(eq("wat"))
        }
    }

    @Test
    fun validTerm_search_getDefinitionsSorted(){
        runBlocking {
            doReturn(flowOf(response)).`when`(repo).define(eq("wat"))

            val viewModel = MainActivityViewModel(
                savedStateHandle,
                repo,
                coroutineContextProvider,
            )

            viewModel.search("wat", SortOrder.THUMBS_UP)

            var value = viewModel.definitions.getOrAwaitValue()
            assertNull(value.error)
            assertEquals(false, value.isLoading)
            assertEquals(4L, value.data?.get(0)?.id)

            viewModel.sort(SortOrder.THUMBS_DOWN)

            value = viewModel.definitions.getOrAwaitValue()
            assertNull(value.error)
            assertEquals(false, value.isLoading)
            assertEquals(2L, value.data?.get(0)?.id)

            verify(repo).define(eq("wat"))
        }
    }

    @Test
    fun viewModel_getRefreshedData() {
        runBlocking {
            doReturn(flowOf(response)).`when`(repo).define(eq("wat"))

            val viewModel = MainActivityViewModel(
                savedStateHandle,
                repo,
                coroutineContextProvider,
            )

            viewModel.search("wat", SortOrder.THUMBS_UP)

            val value = viewModel.definitions.getOrAwaitValue()
            assertNull(value.error)
            assertEquals(false, value.isLoading)
            assertEquals(4L, value.data?.get(0)?.id)

            viewModel.refresh(SortOrder.THUMBS_UP)
            viewModel.definitions.getOrAwaitValue()
            verify(repo, times(2)).define(eq("wat"))
        }
    }

    @Test
    fun validTerm_startWithLoading_getLoadedDefinitions() {
        runBlocking {
            doReturn(flow {
                delay(5000)
                emit(response)
            }).`when`(repo)
                .define(eq("wat"))

            val viewModel = MainActivityViewModel(
                savedStateHandle,
                repo,
                object : TestCoroutineContextProvider() {
                    override val IO: CoroutineContext
                        get() = Dispatchers.IO
                },
            )

            viewModel.search("wat", SortOrder.THUMBS_UP)

            val value = viewModel.definitions.getOrAwaitValue()
            assertNull(value.error)
            assertEquals(true, value.isLoading)
        }
    }

    @Test
    fun viewModel_checkConsumedError() {
        val error = MainActivityViewModel.LoadingError(NullPointerException())
        assertFalse(error.consumed)
        error.consume()
        assertTrue(error.consumed)
    }
}