package rs.edu.raf.rma.movies.domain

import kotlinx.serialization.Serializable

@Serializable
data class MovieCollection(
    val id: Int,
    val name: String,
    val posterPath: String? = null,
    val backdropPath: String? = null
)