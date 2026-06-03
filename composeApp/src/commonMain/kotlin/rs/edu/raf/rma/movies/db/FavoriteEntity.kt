package rs.edu.raf.rma.movies.db

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["imdbId"],
)
data class FavoriteEntity(
    val imdbId: String
)