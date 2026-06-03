package rs.edu.raf.rma.movies.viewmodel

sealed interface FavoritesIntent {
    data object LoadMovies : FavoritesIntent
    data class OnMovieClicked(val imdbId: String) : FavoritesIntent
    data class RemoveFavorite(val imdbId: String) : FavoritesIntent
}