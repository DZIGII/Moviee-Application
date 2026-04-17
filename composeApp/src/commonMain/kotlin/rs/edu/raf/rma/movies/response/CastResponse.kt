package rs.edu.raf.rma.movies.response

import kotlinx.serialization.Serializable
import rs.edu.raf.rma.movies.domain.Cast

@Serializable
data class CastResponse(
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<Cast>
)