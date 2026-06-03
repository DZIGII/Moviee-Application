package rs.edu.raf.rma.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rs.edu.raf.rma.movies.repository.MovieRepository

class MovieDetailsViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailsState())
    val state: StateFlow<MovieDetailsState> = _state

    fun onIntent(intent: MovieDetailsIntent) {
        when (intent) {
            is MovieDetailsIntent.LoadMovie -> loadMovie(intent.imdbId)
            is MovieDetailsIntent.ToggleFavorites -> toggleFavorites(intent.imdbId)
            is MovieDetailsIntent.ToggleWatchlist -> toggleWatchlist(intent.imdbId)
        }
    }

    private fun loadMovie(imdbId: String) {
        observeMovie(imdbId)
        fetchMovie(imdbId)
    }

    private fun observeMovie(imdbId: String) {
        viewModelScope.launch {
            launch {
                repository.observeMovieDetails(imdbId).collect { movie ->
                    _state.value = _state.value.copy(movie = movie)
                }
            }
            launch {
                repository.observeCast(imdbId).collect { cast ->
                    _state.value = _state.value.copy(cast = cast)
                }
            }
            launch {
                repository.observeImages(imdbId).collect { images ->
                    _state.value = _state.value.copy(images = images)
                }
            }
            launch {
                repository.isFavorite(imdbId).collect { isFav ->
                    _state.value = _state.value.copy(isFavorite = isFav)
                }
            }
            launch {
                repository.isInWatchlist(imdbId).collect { isWl ->
                    _state.value = _state.value.copy(isWatchlist = isWl)
                }
            }
        }
    }

    private fun fetchMovie(imdbId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                repository.fetchMovieDetails(imdbId)
                _state.value = _state.value.copy(loading = false, error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun toggleFavorites(imdbId: String) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(imdbId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun toggleWatchlist(imdbId: String) {
        viewModelScope.launch {
            try {
                repository.toggleWatchlist(imdbId)
            } catch (e : Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
