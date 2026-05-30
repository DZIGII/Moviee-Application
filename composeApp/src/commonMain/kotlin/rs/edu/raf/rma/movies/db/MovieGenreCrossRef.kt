package rs.edu.raf.rma.movies.db

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "movie_genres",
    primaryKeys = ["imdbId", "genreId"],
)
data class MovieGenreCrossRef(
    val imdbId: String,
    @ColumnInfo(index = true) val genreId: Int,
)
