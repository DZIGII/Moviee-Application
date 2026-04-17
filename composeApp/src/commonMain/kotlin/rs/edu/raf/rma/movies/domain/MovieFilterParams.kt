package rs.edu.raf.rma.movies.domain

data class MovieFilterParams(
    val query: String? = null,
    val genres: List<String> = emptyList(),
    val fromYear: Int? = null,
    val toYear: Int? = null,
    val minRating: Float? = null
)