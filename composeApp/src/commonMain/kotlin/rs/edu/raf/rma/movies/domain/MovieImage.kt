package rs.edu.raf.rma.movies.domain

import kotlinx.serialization.Serializable

@Serializable
data class MovieImage(
    val filePath: String,
    val width: Int? = null,
    val height: Int? = null,
    val voteAverage: Float? = null,
    val language: String? = null
)