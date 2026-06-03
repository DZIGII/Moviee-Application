package rs.edu.raf.rma.movies.db

import androidx.room.Entity

@Entity(
    tableName = "watchlist",
    primaryKeys = ["imdbId"],
)
data class WatchlistEntity(
    val imdbId: String
)