package rs.edu.raf.rma.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.repository.MovieRepository


data class FavoritesState(
    val loading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String?=null
)

class FavoritesViewModel(
    private val repository: MovieRepository
): ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state

    private val _effect = Channel<MoviesEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeFavorites()
        fetchFavorites()
    }

    fun onIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadMovies -> fetchFavorites()
            is FavoritesIntent.OnMovieClicked -> navigateToDetails(intent.imdbId)
            is FavoritesIntent.RemoveFavorite -> removeFavorite(intent.imdbId)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.observeFavorites().collect { movies ->
                _state.value = _state.value.copy(movies = movies)
            }
        }
    }

    private fun fetchFavorites() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                repository.fetchFavorites()
                _state.value = _state.value.copy(loading = false, error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun removeFavorite(imdbId: String) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(imdbId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun navigateToDetails(imdbId: String) {
        viewModelScope.launch {
            _effect.send(MoviesEffect.NavigateToDetails(imdbId))
        }
    }
}