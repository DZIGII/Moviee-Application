package rs.edu.raf.rma.movies.viewmodel

sealed interface MovieDetailsIntent {
    data class LoadMovie(val imdbId: String) : MovieDetailsIntent
}
