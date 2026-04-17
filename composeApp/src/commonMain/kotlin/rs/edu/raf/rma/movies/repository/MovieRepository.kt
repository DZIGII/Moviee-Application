package rs.edu.raf.rma.movies.repository

import rs.edu.raf.rma.movies.domain.Cast
import rs.edu.raf.rma.movies.domain.Genre
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.domain.MovieDetail
import rs.edu.raf.rma.movies.domain.MovieImages

interface MovieRepository {

    suspend fun getMovies(sortBy: String? = null): List<Movie>

    suspend fun getFilteredMovies(
        query: String? = null,
        genreId: Int? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null,
        sortBy: String? = null
    ): List<Movie>

    suspend fun getMovieDetails(id: String): MovieDetail

    suspend fun getMovieCast(id: String): List<Cast>

    suspend fun getMovieImages(id: String): MovieImages

    suspend fun getGenres(): List<Genre>
}