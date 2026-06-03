package rs.edu.raf.rma.movies.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.movies.data.remote.MovieApiService
import rs.edu.raf.rma.movies.data.toDomain
import rs.edu.raf.rma.movies.data.toEntity
import rs.edu.raf.rma.movies.data.toGenreCrossRefs
import rs.edu.raf.rma.movies.db.FavoriteEntity
import rs.edu.raf.rma.movies.db.WatchlistEntity
import rs.edu.raf.rma.movies.domain.Cast
import rs.edu.raf.rma.movies.domain.Genre
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.domain.MovieDetail
import rs.edu.raf.rma.movies.domain.MovieImage
import rs.edu.raf.rma.movies.repository.MovieRepository

class MovieRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val api: MovieApiService
) : MovieRepository {

    override fun observeMovies(): Flow<List<Movie>> =
        appDatabase.movieDao()
            .observeAllMovies()
            .distinctUntilChanged()
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeMovieDetails(imdbId: String): Flow<MovieDetail?> =
        appDatabase.movieDao()
            .observeMovieDetails(imdbId)
            .map { entity -> entity?.toDomain() }

    override fun observeCast(imdbId: String): Flow<List<Cast>> =
        appDatabase.movieDao()
            .observeCast(imdbId)
            .map { rows -> rows.map { it.toDomain() } }

    override fun observeImages(imdbId: String): Flow<List<MovieImage>> =
        appDatabase.movieDao()
            .observeImages(imdbId)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun fetchMovies(sortBy: String?): List<String> {
        val response = api.getMovies(sortBy = sortBy)
        val movies = response.items
        val movieEntities = movies.map { it.toEntity() }
        val genreEntities = movies
            .flatMap { it.genres }
            .distinctBy { it.id }
            .map { it.toEntity() }
        val crossRefs = movies.flatMap { it.toGenreCrossRefs() }
        appDatabase.movieDao().refreshMoviesList(movieEntities, genreEntities, crossRefs)
        return movies.map { it.imdbId }
    }

    override suspend fun fetchFilteredMovies(
        query: String?,
        genreId: Int?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?,
        sortBy: String?,
    ): List<String> {
        val response = api.getMovies(
            query = query,
            genreId = genreId,
            minYear = minYear,
            maxYear = maxYear,
            minRating = minRating,
            sortBy = sortBy,
        )
        val movies = response.items
        val movieEntities = movies.map { it.toEntity() }
        val genreEntities = movies
            .flatMap { it.genres }
            .distinctBy { it.id }
            .map { it.toEntity() }
        val crossRefs = movies.flatMap { it.toGenreCrossRefs() }
        appDatabase.movieDao().refreshMoviesList(movieEntities, genreEntities, crossRefs)
        return movies.map { it.imdbId }
    }

    override suspend fun fetchMovieDetails(imdbId: String) {
        val detail = api.getMovieDetails(imdbId)
        val cast = api.getMovieCast(imdbId).items
        val images = api.getMovieImages(imdbId)

        val genreEntities = detail.genres.map { it.toEntity() }
        val crossRefs = detail.toGenreCrossRefs()
        val castEntities = cast.map { it.toEntity(imdbId) }
        val imageEntities = images.backdrops.map { it.toEntity(imdbId) }

        appDatabase.movieDao().refreshMovieDetails(
            details = detail.toEntity(),
            genres = genreEntities,
            crossRefs = crossRefs,
            cast = castEntities,
            images = imageEntities,
        )
    }

    override fun observeGenres(): Flow<List<Genre>> =
        appDatabase.movieDao()
            .observeAllGenres()
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun fetchGenres() {
        val genres = api.getGenres()
        val genreEntities = genres.map { it.toEntity() }
        appDatabase.movieDao().upsertGenres(genreEntities)
    }

    override fun observeFavorites(): Flow<List<Movie>> =
        appDatabase.movieDao()
            .observeFavorites()
            .distinctUntilChanged()
            .map { rows -> rows.map { it.toDomain() } }

    override fun isFavorite(imdbId: String): Flow<Boolean> =
        appDatabase.movieDao().isFavorite(imdbId)

    override suspend fun fetchFavorites() {
        val movies = api.getFavorites()
        val movieEntities = movies.map { it.toEntity() }
        val genreEntities = movies
            .flatMap { it.genres }
            .distinctBy { it.id }
            .map { it.toEntity() }
        val crossRefs = movies.flatMap { it.toGenreCrossRefs() }

        val dao = appDatabase.movieDao()
        dao.upsertGenres(genreEntities)
        dao.upsertMovies(movieEntities)
        dao.upsertMovieGenreCrossRefs(crossRefs)
        dao.refreshFavorites(movies.map { it.imdbId })
    }

    override suspend fun toggleFavorite(imdbId: String) {
        val dao = appDatabase.movieDao()
        val currentlyFavorite = dao.isFavorite(imdbId).first()

        if (currentlyFavorite) {
            dao.removeFavorite(imdbId)
            try {
                api.removeFavorite(imdbId)
            } catch (e: Exception) {
                dao.addFavorite(FavoriteEntity(imdbId))
                throw e
            }
        } else {
            dao.addFavorite(FavoriteEntity(imdbId))
            try {
                api.addFavorite(imdbId)
            } catch (e: Exception) {
                dao.removeFavorite(imdbId)
                throw e
            }
        }
    }

    // ── Watchlist ────────────────────────────────────────────

    override fun observeWatchlist(): Flow<List<Movie>> =
        appDatabase.movieDao()
            .observeWatchlist()
            .distinctUntilChanged()
            .map { rows -> rows.map { it.toDomain() } }

    override fun isInWatchlist(imdbId: String): Flow<Boolean> =
        appDatabase.movieDao().isInWatchlist(imdbId)

    override suspend fun fetchWatchlist() {
        val movies = api.getWatchlist()
        val movieEntities = movies.map { it.toEntity() }
        val genreEntities = movies
            .flatMap { it.genres }
            .distinctBy { it.id }
            .map { it.toEntity() }
        val crossRefs = movies.flatMap { it.toGenreCrossRefs() }

        val dao = appDatabase.movieDao()
        dao.upsertGenres(genreEntities)
        dao.upsertMovies(movieEntities)
        dao.upsertMovieGenreCrossRefs(crossRefs)
        dao.refreshWatchlist(movies.map { it.imdbId })
    }

    override suspend fun toggleWatchlist(imdbId: String) {
        val dao = appDatabase.movieDao()
        val currentlyInWatchlist = dao.isInWatchlist(imdbId).first()

        if (currentlyInWatchlist) {
            dao.removeFromWatchlist(imdbId)
            try {
                api.removeFromWatchlist(imdbId)
            } catch (e: Exception) {
                dao.addToWatchlist(WatchlistEntity(imdbId))
                throw e
            }
        } else {
            dao.addToWatchlist(WatchlistEntity(imdbId))
            try {
                api.addToWatchlist(imdbId)
            } catch (e: Exception) {
                dao.removeFromWatchlist(imdbId)
                throw e
            }
        }
    }
}
