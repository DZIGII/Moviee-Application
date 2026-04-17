package rs.edu.raf.rma.movies.data.remote

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import rs.edu.raf.rma.movies.domain.Cast
import rs.edu.raf.rma.movies.domain.Genre
import rs.edu.raf.rma.movies.domain.MovieDetail
import rs.edu.raf.rma.movies.domain.MovieImages
import rs.edu.raf.rma.movies.response.CastResponse
import rs.edu.raf.rma.movies.response.MoviesResponse

interface MovieApiService {

    @GET("movies")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("query") query: String? = null,
        @Query("genre_id") genreId: Int? = null,
        @Query("min_year") minYear: Int? = null,
        @Query("max_year") maxYear: Int? = null,
        @Query("min_rating") minRating: Float? = null,
        @Query("sort_by") sortBy: String? = null
    ): MoviesResponse

    @GET("movies/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: String
    ): MovieDetail

    @GET("genres")
    suspend fun getGenres(): List<Genre>

    @GET("movies/{id}/cast")
    suspend fun getMovieCast(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): CastResponse

    @GET("movies/{id}/images")
    suspend fun getMovieImages(
        @Path("id") id: String
    ): MovieImages
}