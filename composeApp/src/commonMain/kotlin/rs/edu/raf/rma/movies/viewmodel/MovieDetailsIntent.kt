package rs.edu.raf.rma.movies.viewmodel

sealed interface MovieDetailsIntent {
    data class LoadMovie(val imdbId: String) : MovieDetailsIntent
    data class ToggleFavorites(val imdbId: String): MovieDetailsIntent
    data class ToggleWatchlist(val imdbId: String): MovieDetailsIntent
}
