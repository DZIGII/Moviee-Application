package rs.edu.raf.rma.movies.navigation

import kotlinx.serialization.Serializable

@Serializable
data object MainDestination

@Serializable
data class MovieDetailsDestination(val imdbId: String)