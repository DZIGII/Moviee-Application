package rs.edu.raf.rma.movies.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.StructureKind

@Serializable
data class MovieDetail(
    val imdbId: String,
    val tmdbId: Int?,
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val tagline: String?,
    val releaseDate: String?,
    val year: Int?,
    val runtime: Int?,
    val budget: Long?,
    val revenue: Long?,
    val languageCode: String?,
    val popularity: Float?,
    val imdbRating: Float?,
    val imdbVotes: Int?,
    val tmdbRating: Float?,
    val tmdbVotes: Int?,
    val posterPath: String?,
    val backdropPath: String?,
    val homepage: String?,
    val genres: List<Genre>,
    val collection: MovieCollection? = null
)