package rs.edu.raf.rma.movies.viewmodel

sealed interface WatchlistIntent {
    data object LoadMovies: WatchlistIntent
    data class OnMovieClicked(val imdbId: String): WatchlistIntent
    data class RemoveWatchlist(val imdbId: String): WatchlistIntent
}