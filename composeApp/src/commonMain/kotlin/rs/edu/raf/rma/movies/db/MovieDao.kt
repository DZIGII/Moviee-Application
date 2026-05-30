package rs.edu.raf.rma.movies.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Transaction
    @Query("SELECT * FROM movies")
    fun observeAllMovies(): Flow<List<MovieWithGenres>>

    @Transaction
    @Query("SELECT * FROM movies WHERE imdbId = :imdbId")
    fun observeMovieById(imdbId: String): Flow<MovieWithGenres?>

    @Query("SELECT * FROM movie_details WHERE imdbId = :imdbId")
    fun observeMovieDetails(imdbId: String): Flow<MovieDetailsEntity?>

    @Query("SELECT * FROM cast_members WHERE movieImdbId = :imdbId")
    fun observeCast(imdbId: String): Flow<List<CastEntity>>

    @Query("SELECT * FROM movie_images WHERE movieImdbId = :imdbId")
    fun observeImages(imdbId: String): Flow<List<MovieImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieGenreCrossRefs(crossRefs: List<MovieGenreCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieDetails(details: MovieDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCast(cast: List<CastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertImages(images: List<MovieImageEntity>)

    @Query("DELETE FROM movie_genres WHERE imdbId = :imdbId")
    suspend fun deleteGenresForMovie(imdbId: String)

    @Query("DELETE FROM cast_members WHERE movieImdbId = :imdbId")
    suspend fun deleteCastForMovie(imdbId: String)

    @Query("DELETE FROM movie_images WHERE movieImdbId = :imdbId")
    suspend fun deleteImagesForMovie(imdbId: String)

    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()

    @Transaction
    suspend fun refreshMoviesList(
        movies: List<MovieEntity>,
        genres: List<GenreEntity>,
        crossRefs: List<MovieGenreCrossRef>,
    ) {
        deleteAllMovies()
        upsertGenres(genres)
        upsertMovies(movies)
        upsertMovieGenreCrossRefs(crossRefs)
    }

    @Transaction
    suspend fun refreshMovieDetails(
        details: MovieDetailsEntity,
        genres: List<GenreEntity>,
        crossRefs: List<MovieGenreCrossRef>,
        cast: List<CastEntity>,
        images: List<MovieImageEntity>,
    ) {
        upsertMovieDetails(details)
        upsertGenres(genres)
        deleteGenresForMovie(details.imdbId)
        upsertMovieGenreCrossRefs(crossRefs)
        deleteCastForMovie(details.imdbId)
        upsertCast(cast)
        deleteImagesForMovie(details.imdbId)
        upsertImages(images)
    }
}
