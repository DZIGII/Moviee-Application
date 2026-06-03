package rs.edu.raf.rma.movies.viewmodel

import rs.edu.raf.rma.movies.domain.Cast
import rs.edu.raf.rma.movies.domain.MovieDetail
import rs.edu.raf.rma.movies.domain.MovieImage

data class MovieDetailsState(
    val loading: Boolean = false,
    val movie: MovieDetail? = null,
    val cast: List<Cast> = emptyList(),
    val images: List<MovieImage> = emptyList(),
    val isFavorite: Boolean = false,
    val isWatchlist: Boolean = false,
    val error: String? = null
)