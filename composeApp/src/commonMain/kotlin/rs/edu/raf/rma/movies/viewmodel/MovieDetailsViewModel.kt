package rs.edu.raf.rma.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
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
        }
    }

    private fun loadMovie(imdbId: String) {
        viewModelScope.launch {
            _state.value = MovieDetailsState(loading = true)

            try {
                val movieDeferred = async { repository.getMovieDetails(imdbId) }
                val castDeferred = async { repository.getMovieCast(imdbId) }
                val imagesDeferred = async { repository.getMovieImages(imdbId) }

                val movie = movieDeferred.await()
                val cast = castDeferred.await()
                val images = imagesDeferred.await()

                _state.value = MovieDetailsState(
                    loading = false,
                    movie = movie,
                    cast = cast,
                    images = images.backdrops
                )
            } catch (e: Exception) {
                _state.value = MovieDetailsState(
                    loading = false,
                    error = e.message
                )
            }
        }
    }
}