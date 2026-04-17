package rs.edu.raf.rma.movies.viewmodel

sealed interface MoviesEffect {
    data class NavigateToDetails(val imdbId: String) : MoviesEffect
}