package rs.edu.raf.rma.movies.db

import androidx.room.Entity

@Entity(
    tableName = "cast_members",
    primaryKeys = ["imdbId", "movieImdbId"],
)
data class CastEntity(
    val imdbId: String,
    val movieImdbId: String,
    val name: String,
    val professions: String?,
    val department: String?,
    val profilePath: String?,
)
