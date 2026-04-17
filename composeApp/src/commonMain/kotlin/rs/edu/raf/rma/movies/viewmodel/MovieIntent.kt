package rs.edu.raf.rma.movies.viewmodel

import rs.edu.raf.rma.movies.domain.SortOption

sealed interface MoviesIntent {
    data object LoadMovies : MoviesIntent
    data class OnMovieClicked(val imdbId: String) : MoviesIntent
    data class ChangeSortBy(val sortOption: SortOption) : MoviesIntent
}