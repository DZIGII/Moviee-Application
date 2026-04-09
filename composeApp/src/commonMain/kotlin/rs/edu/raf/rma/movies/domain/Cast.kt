package rs.edu.raf.rma.movies.domain

data class Cast(
    val imdbId: String,
    val name: String,
    val professions: String?,
    val department: String?,
    val profilePath: String?
)