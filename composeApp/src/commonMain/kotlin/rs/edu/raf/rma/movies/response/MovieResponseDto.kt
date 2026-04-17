package rs.edu.raf.rma.movies.response

import kotlinx.serialization.Serializable
import rs.edu.raf.rma.movies.domain.Movie

@Serializable
data class MoviesResponse(
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<Movie>
)