package rs.edu.raf.rma.movies.response

import kotlinx.serialization.Serializable
import rs.edu.raf.rma.movies.domain.Genre

@Serializable
data class GenresResponse(
    val items: List<Genre>
)
