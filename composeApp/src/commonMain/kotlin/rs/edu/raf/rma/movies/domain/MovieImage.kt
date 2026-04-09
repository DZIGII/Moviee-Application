package rs.edu.raf.rma.movies.domain

data class MovieImage(
    val filePath: String,
    val width: Int?,
    val height: Int?,
    val voteAverage: Float?,
    val language: String?
)