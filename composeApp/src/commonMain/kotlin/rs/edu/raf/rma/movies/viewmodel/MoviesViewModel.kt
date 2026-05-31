package rs.edu.raf.rma.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import rs.edu.raf.rma.movies.domain.Genre
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.domain.SortOption
import rs.edu.raf.rma.movies.repository.MovieRepository
import rs.edu.raf.rma.movies.screen.MovieFilterUiState

data class MovieState(
    val loading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String? = null,
    val genres: List<Genre> = emptyList(),
    val sortBy: SortOption = SortOption.RATING
)

class MoviesViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state

    private val _effect = Channel<MoviesEffect>()
    val effect = _effect.receiveAsFlow()

    private var genresMap: Map<String, Int> = emptyMap()
    private var currentFilters: MovieFilterUiState? = null

    init {
        observeMovies()
        observeGenres()
        fetchGenres()
        fetchMovies()
    }

    fun onIntent(intent: MoviesIntent) {
        when (intent) {
            is MoviesIntent.LoadMovies -> fetchMovies()
            is MoviesIntent.OnMovieClicked -> navigateToDetails(intent.imdbId)
            is MoviesIntent.ChangeSortBy -> changeSortBy(intent.sortOption)
            is MoviesIntent.ApplyFilters -> applyFilters(intent.filters)
        }
    }

    private fun observeMovies() {
        viewModelScope.launch {
            repository.observeMovies().collect { movies ->
                _state.value = _state.value.copy(movies = movies)
            }
        }
    }

    private fun observeGenres() {
        viewModelScope.launch {
            repository.observeGenres().collect { genres ->
                genresMap = genres.associate { it.name to it.id }
                _state.value = _state.value.copy(genres = genres)
            }
        }
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                repository.fetchGenres()
            } catch (e: Exception) {
                println("Failed to fetch genres: ${e.message}")
            }
        }
    }

    private fun fetchMovies() {
        currentFilters = null
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val movieIds = repository.fetchMovies(sortBy = _state.value.sortBy.apiValue)
                _state.value = _state.value.copy(loading = false, error = null)
                fetchAllMovieDetails(movieIds)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun fetchAllMovieDetails(movieIds: List<String>) {
        viewModelScope.launch {
            for (imdbId in movieIds) {
                launch {
                    try {
                        repository.fetchMovieDetails(imdbId)
                    } catch (e: Exception) {
                        println("Failed to fetch details for $imdbId: ${e.message}")
                    }
                }
            }
        }
    }

    private fun applyFilters(filters: MovieFilterUiState) {
        currentFilters = filters
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val genreId = filters.selectedGenre?.let { genresMap[it] }
                repository.fetchFilteredMovies(
                    query = filters.searchQuery.takeIf { it.isNotBlank() },
                    genreId = genreId,
                    minYear = filters.fromYear.toIntOrNull(),
                    maxYear = filters.toYear.toIntOrNull(),
                    minRating = ((filters.minRating * 10).toInt() / 10f),
                    sortBy = _state.value.sortBy.apiValue
                )
                _state.value = _state.value.copy(loading = false, error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun changeSortBy(sortOption: SortOption) {
        _state.value = _state.value.copy(sortBy = sortOption)
        val filters = currentFilters
        if (filters != null) {
            applyFilters(filters)
        } else {
            fetchMovies()
        }
    }

    private fun navigateToDetails(imdbId: String) {
        viewModelScope.launch {
            _effect.send(MoviesEffect.NavigateToDetails(imdbId))
        }
    }
}
