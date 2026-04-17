package rs.edu.raf.rma.movies.domain

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val imdbId: String,
    val title: String,
    val year: String,
    val imdbRating: Float,
    val imdbVotes: Int,
    val posterPath: String,
    val genres: List<Genre>
)