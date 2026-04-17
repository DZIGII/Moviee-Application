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
        loadGenres()
    }

    fun onIntent(intent: MoviesIntent) {
        when (intent) {
            is MoviesIntent.LoadMovies -> loadMovies()
            is MoviesIntent.OnMovieClicked -> navigateToDetails(intent.imdbId)
            is MoviesIntent.ChangeSortBy -> changeSortBy(intent.sortOption)
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            try {
                val genres = repository.getGenres()
                genresMap = genres.associate { it.name to it.id }
                _state.value = _state.value.copy(genres = genres)
            } catch (e: Exception) {
                println("Failed to load genres: ${e.message}")
            }
        }
    }

    fun loadMovies() {
        currentFilters = null
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val movies = repository.getMovies(sortBy = _state.value.sortBy.apiValue)
                _state.value = _state.value.copy(loading = false, movies = movies, error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun applyFilters(filters: MovieFilterUiState) {
        currentFilters = filters
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val genreId = filters.selectedGenre?.let { genresMap[it] }
                val movies = repository.getFilteredMovies(
                    query = filters.searchQuery.takeIf { it.isNotBlank() },
                    genreId = genreId,
                    minYear = filters.fromYear.toIntOrNull(),
                    maxYear = filters.toYear.toIntOrNull(),
                    minRating = ((filters.minRating * 10).toInt() / 10f),
                    sortBy = _state.value.sortBy.apiValue
                )
                _state.value = _state.value.copy(loading = false, movies = movies, error = null)
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
            loadMovies()
        }
    }

    private fun navigateToDetails(imdbId: String) {
        viewModelScope.launch {
            _effect.send(MoviesEffect.NavigateToDetails(imdbId))
        }
    }
}