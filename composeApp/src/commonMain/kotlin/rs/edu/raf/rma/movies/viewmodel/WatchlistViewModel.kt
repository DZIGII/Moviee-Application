package rs.edu.raf.rma.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.repository.MovieRepository

data class WatchlistState(
    val loading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String ?= null
)

class WatchlistViewModel (
    private val repostiory: MovieRepository
): ViewModel() {

    private val _state = MutableStateFlow(WatchlistState())
    val state: StateFlow<WatchlistState> = _state

    private val _effect = Channel<MoviesEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeWatchlist()
        fethcWatchlist()
    }

    fun onIntent(intetn: WatchlistIntent) {
        when (intetn) {
            is WatchlistIntent.LoadMovies -> fethcWatchlist()
            is WatchlistIntent.OnMovieClicked -> navigateToDetails(intetn.imdbId)
            is WatchlistIntent.RemoveWatchlist -> removeWatchlist(intetn.imdbId)
        }
    }

    private fun observeWatchlist() {
        viewModelScope.launch {
            repostiory.observeWatchlist().collect { movies ->
                _state.value = _state.value.copy(loading = false, movies = movies)
            }
        }
    }

    private fun fethcWatchlist() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                repostiory.fetchWatchlist()
                _state.value = _state.value.copy(loading = false, error = null)
            } catch (e : Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun removeWatchlist(imdbId: String) {
        viewModelScope.launch {
            try {
                repostiory.toggleWatchlist(imdbId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun navigateToDetails(imdb: String) {
        viewModelScope.launch {
            _effect.send(MoviesEffect.NavigateToDetails(imdb))
        }
    }


}