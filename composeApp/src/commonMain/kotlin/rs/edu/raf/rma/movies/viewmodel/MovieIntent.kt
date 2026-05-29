package rs.edu.raf.rma.movies.viewmodel

import rs.edu.raf.rma.movies.domain.SortOption
import rs.edu.raf.rma.movies.screen.MovieFilterUiState

sealed interface MoviesIntent {
    data object LoadMovies : MoviesIntent
    data class OnMovieClicked(val imdbId: String) : MoviesIntent
    data class ChangeSortBy(val sortOption: SortOption) : MoviesIntent
    data class ApplyFilters(val filters: MovieFilterUiState) : MoviesIntent
}