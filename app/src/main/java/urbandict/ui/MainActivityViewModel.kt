package urbandict.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import urbandict.api.Definition
import urbandict.di.CoroutineContextProvider
import urbandict.repository.DefinitionsRepository

class MainActivityViewModel @ViewModelInject constructor(
    @Assisted private val stateHandle: SavedStateHandle,
    private val repository: DefinitionsRepository,
    private val coroutineContextProvider: CoroutineContextProvider,
) : ViewModel() {

    val definitions: LiveData<Result<List<Definition>>> = MutableLiveData()

    private var searchTerm: String? = null
    private var currentJob: Job? = null
    //restoring the state when the app restarts
    init {
        searchTerm = stateHandle[STATE_TERM]
        if (!searchTerm.isNullOrBlank()) {
            val sortOrder =
                if (stateHandle.get<Int>(STATE_SORT_ORDER) == SortOrder.THUMBS_DOWN.ordinal) {
                    SortOrder.THUMBS_DOWN
                } else {
                    SortOrder.THUMBS_UP
                }
            search(searchTerm.orEmpty(), sortOrder)
        }
    }
    //sorting the words based on the number of likes and dislikes
    fun sort(sortOrder: SortOrder) {
        stateHandle[STATE_SORT_ORDER] = sortOrder.ordinal
        val mutableLiveData = definitions as MutableLiveData
        currentJob = viewModelScope.launch(coroutineContextProvider.Main) {
            val currentValue = definitions.value
            if (currentValue?.data.isNullOrEmpty()) {
                mutableLiveData.value =
                    Result(
                        data = null,
                        error = null,
                        isLoading = false
                    )
            } else {
                mutableLiveData.value =
                    Result(
                        data = currentValue?.data,
                        error = currentValue?.error,
                        isLoading = true
                    )
                val sortedList = withContext(coroutineContextProvider.IO) {
                    ArrayList(currentValue?.data.orEmpty()).sortedByDescending {
                        if (sortOrder == SortOrder.THUMBS_UP) {
                            it.thumbsUp
                        } else {
                            it.thumbsDown
                        }
                    }
                }
                mutableLiveData.value =
                    Result(
                        data = sortedList,
                        error = null,
                        isLoading = false
                    )
            }
        }
    }

    //swipe refresh
    fun refresh(sort: SortOrder) {
        stateHandle[STATE_SORT_ORDER] = sort.ordinal
        search(searchTerm.orEmpty(), sort)
    }

    fun search(term: String, sortOrder: SortOrder) {
        this.searchTerm = term
        stateHandle[STATE_TERM] = term
        stateHandle[STATE_SORT_ORDER] = sortOrder.ordinal

        val mutableLiveData = definitions as MutableLiveData

        if (term.isBlank()) {
            mutableLiveData.value =
                Result(
                    data = emptyList(),
                    error = null,
                    isLoading = false
                )
            return
        }

        currentJob = viewModelScope.launch(coroutineContextProvider.Main) {
            try {
                repository
                    .define(term)
                    .flowOn(coroutineContextProvider.IO)
                    .map { list ->
                        list
                            .sortedByDescending {
                                if (sortOrder == SortOrder.THUMBS_UP) {
                                    it.thumbsUp
                                } else {
                                    it.thumbsDown
                                }
                            }
                    }
                    .flowOn(coroutineContextProvider.Main)
                    .onStart {
                        val currentValue = definitions.value
                        mutableLiveData.value =
                            Result(
                                data = currentValue?.data,
                                error = currentValue?.error,
                                isLoading = true
                            )
                    }
                    .onCompletion {
                        val currentValue = definitions.value
                        mutableLiveData.value =
                            Result(
                                data = currentValue?.data,
                                error = currentValue?.error,
                                isLoading = false
                            )
                    }
                    .collect { list ->
                        mutableLiveData.value =
                            Result(
                                data = list,
                                error = null,
                                isLoading = true
                            )
                    }
            } catch (ignore: CancellationException) {
                // Coroutine was canceled.
            } catch (t: Throwable) {
                mutableLiveData.value =
                    Result(
                        data = null,
                        error = LoadingError(
                            throwable = t
                        ),
                        isLoading = false
                    )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }
    companion object {
        private const val STATE_TERM = "term"
        private const val STATE_SORT_ORDER = "sort"
    }

    data class Result<out T>(
        val data: T?,
        val error: LoadingError?,
        val isLoading: Boolean = false
    )

    data class LoadingError(val throwable: Throwable, var consumed: Boolean = false) {
        fun consume(): Throwable {
            consumed = true
            return throwable
        }
    }

    enum class SortOrder {
        THUMBS_UP,
        THUMBS_DOWN
    }
}