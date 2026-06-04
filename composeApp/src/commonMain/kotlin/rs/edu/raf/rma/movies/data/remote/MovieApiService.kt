package rs.edu.raf.rma.movies.data.remote

import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import rs.edu.raf.rma.core.auth.api.UserResponse
import rs.edu.raf.rma.movies.domain.Movie
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

    @GET("me/favorites")
    suspend fun getFavorites(): List<Movie>

    @POST("me/favorites/{id}")
    suspend fun addFavorite(@Path("id") imdbId: String): HttpResponse

    @DELETE("me/favorites/{id}")
    suspend fun removeFavorite(@Path("id") imdbId: String): HttpResponse

    @GET("me/watchlist")
    suspend fun getWatchlist(): List<Movie>

    @POST("me/watchlist/{id}")
    suspend fun addToWatchlist(@Path("id") imdbId: String): HttpResponse

    @DELETE("me/watchlist/{id}")
    suspend fun removeFromWatchlist(@Path("id") imdbId: String): HttpResponse

    @GET("me")
    suspend fun getMe(): UserResponse
}