package rs.edu.raf.rma.movies.repository.impl

import rs.edu.raf.rma.movies.data.remote.MovieApiService
import rs.edu.raf.rma.movies.domain.Cast
import rs.edu.raf.rma.movies.domain.Genre
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.domain.MovieDetail
import rs.edu.raf.rma.movies.domain.MovieImages
import rs.edu.raf.rma.movies.repository.MovieRepository

class MovieRepositoryImpl(
    private val api: MovieApiService
) : MovieRepository {

    override suspend fun getMovies(sortBy: String?): List<Movie> {
        return api.getMovies(sortBy = sortBy).items
    }

    override suspend fun getFilteredMovies(
        query: String?,
        genreId: Int?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?,
        sortBy: String?
    ): List<Movie> {
        return api.getMovies(
            query = query,
            genreId = genreId,
            minYear = minYear,
            maxYear = maxYear,
            minRating = minRating,
            sortBy = sortBy
        ).items
    }

    override suspend fun getGenres(): List<Genre> {
        return api.getGenres()
    }

    override suspend fun getMovieDetails(id: String): MovieDetail {
        return api.getMovieDetails(id)
    }

    override suspend fun getMovieCast(id: String): List<Cast> {
        return api.getMovieCast(id).items
    }

    override suspend fun getMovieImages(id: String): MovieImages {
        return api.getMovieImages(id)
    }
}