package rs.edu.raf.rma.movies.domain

import kotlinx.serialization.Serializable

@Serializable
data class Cast(
    val imdbId: String,
    val name: String,
    val professions: String? = null,
    val department: String? = null,
    val profilePath: String? = null
)