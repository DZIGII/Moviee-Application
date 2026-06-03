package rs.edu.raf.rma.movies.repository

import kotlinx.coroutines.flow.Flow
import rs.edu.raf.rma.movies.domain.Cast
import rs.edu.raf.rma.movies.domain.Genre
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.domain.MovieDetail
import rs.edu.raf.rma.movies.domain.MovieImage

interface MovieRepository {

    fun observeMovies(): Flow<List<Movie>>
    fun observeMovieDetails(imdbId: String): Flow<MovieDetail?>
    fun observeCast(imdbId: String): Flow<List<Cast>>
    fun observeImages(imdbId: String): Flow<List<MovieImage>>
    suspend fun fetchMovies(sortBy: String? = null): List<String>
    suspend fun fetchFilteredMovies(
        query: String? = null,
        genreId: Int? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null,
        sortBy: String? = null,
    ): List<String>
    suspend fun fetchMovieDetails(imdbId: String)
    fun observeGenres(): Flow<List<Genre>>
    suspend fun fetchGenres()
    fun observeFavorites(): Flow<List<Movie>>
    fun isFavorite(imdbId: String): Flow<Boolean>
    suspend fun fetchFavorites()
    suspend fun toggleFavorite(imdbId: String)
    fun observeWatchlist(): Flow<List<Movie>>
    fun isInWatchlist(imdbId: String): Flow<Boolean>
    suspend fun fetchWatchlist()
    suspend fun toggleWatchlist(imdbId: String)
}
