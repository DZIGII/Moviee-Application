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

    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun observeAllGenres(): Flow<List<GenreEntity>>

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


    // ── Favorites ────────────────────────────────────────────

    @Transaction
    @Query("SELECT m.* FROM movies m INNER JOIN favorites f ON m.imdbId = f.imdbId")
    fun observeFavorites(): Flow<List<MovieWithGenres>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE imdbId = :imdbId)")
    fun isFavorite(imdbId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE imdbId = :imdbId")
    suspend fun removeFavorite(imdbId: String)

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()

    @Transaction
    suspend fun refreshFavorites(imdbIds: List<String>) {
        deleteAllFavorites()
        imdbIds.forEach { addFavorite(FavoriteEntity(it)) }
    }

    @Transaction
    @Query("SELECT m.* FROM movies m INNER JOIN watchlist w ON m.imdbId = w.imdbId")
    fun observeWatchlist(): Flow<List<MovieWithGenres>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE imdbId = :imdbId)")
    fun isInWatchlist(imdbId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(entry: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE imdbId = :imdbId")
    suspend fun removeFromWatchlist(imdbId: String)

    @Query("DELETE FROM watchlist")
    suspend fun deleteAllWatchlist()

    @Transaction
    suspend fun refreshWatchlist(imdbIds: List<String>) {
        deleteAllWatchlist()
        imdbIds.forEach { addToWatchlist(WatchlistEntity(it)) }
    }

    @Query("SELECT COUNT(*) FROM movies m INNER JOIN movie_images i ON m.imdbId = i.movieImdbId")
    suspend fun countMoviesWithImages(): Int

    @Query("""
        SELECT DISTINCT m.imdbId, m.title, m.year, m.imdbRating, m.imdbVotes, m.posterPath
        FROM movies m
        INNER JOIN movie_images i ON m.imdbId = i.movieImdbId
    """)
    suspend fun getMoviesWithImages(): List<MovieEntity>

    @Query("SELECT * FROM cast_members WHERE movieImdbId = :imdbId LIMIT 3")
    suspend fun getTopCastForMovie(imdbId: String): List<CastEntity>

    @Query("SELECT * FROM movie_images WHERE movieImdbId = :imdbId")
    suspend fun getImagesForMovie(imdbId: String): List<MovieImageEntity>

    @Query("SELECT DISTINCT name FROM cast_members ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomActorNames(limit: Int): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizSession(session: QuizSessionEntity)

    @Query("SELECT MAX(score) FROM quiz_sessions")
    fun observeBestScore(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM quiz_sessions")
    fun observeQuizCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM favorites")
    fun observeFavoritesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM watchlist")
    fun observeWatchlistCount(): Flow<Int>

    @Query("DELETE FROM quiz_sessions")
    suspend fun deleteAllQuizSessions()
}
