package rs.edu.raf.rma.movies.db

import androidx.room.Entity

@Entity(
    tableName = "movie_images",
    primaryKeys = ["filePath", "movieImdbId"],
)
data class MovieImageEntity(
    val filePath: String,
    val movieImdbId: String,
    val width: Int?,
    val height: Int?,
    val voteAverage: Float?,
    val language: String?,
)
