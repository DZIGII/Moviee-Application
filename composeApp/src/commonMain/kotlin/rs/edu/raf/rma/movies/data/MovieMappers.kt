package rs.edu.raf.rma.movies.data

import rs.edu.raf.rma.movies.db.CastEntity
import rs.edu.raf.rma.movies.db.GenreEntity
import rs.edu.raf.rma.movies.db.MovieDetailsEntity
import rs.edu.raf.rma.movies.db.MovieEntity
import rs.edu.raf.rma.movies.db.MovieGenreCrossRef
import rs.edu.raf.rma.movies.db.MovieImageEntity
import rs.edu.raf.rma.movies.db.MovieWithGenres
import rs.edu.raf.rma.movies.domain.Cast
import rs.edu.raf.rma.movies.domain.Genre
import rs.edu.raf.rma.movies.domain.Movie
import rs.edu.raf.rma.movies.domain.MovieDetail
import rs.edu.raf.rma.movies.domain.MovieImage

fun Movie.toEntity(): MovieEntity = MovieEntity(
    imdbId = imdbId,
    title = title,
    year = year,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    posterPath = posterPath,
)

fun Genre.toEntity(): GenreEntity = GenreEntity(
    id = id,
    name = name,
)

fun Movie.toGenreCrossRefs(): List<MovieGenreCrossRef> =
    genres.map { genre ->
        MovieGenreCrossRef(imdbId = imdbId, genreId = genre.id)
    }

fun MovieDetail.toEntity(): MovieDetailsEntity = MovieDetailsEntity(
    imdbId = imdbId,
    tmdbId = tmdbId,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    tagline = tagline,
    releaseDate = releaseDate,
    year = year,
    runtime = runtime,
    budget = budget,
    revenue = revenue,
    languageCode = languageCode,
    popularity = popularity,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    tmdbRating = tmdbRating,
    tmdbVotes = tmdbVotes,
    posterPath = posterPath,
    backdropPath = backdropPath,
    homepage = homepage,
)

fun MovieDetail.toGenreCrossRefs(): List<MovieGenreCrossRef> =
    genres.map { genre ->
        MovieGenreCrossRef(imdbId = imdbId, genreId = genre.id)
    }

fun Cast.toEntity(movieImdbId: String): CastEntity = CastEntity(
    imdbId = imdbId,
    movieImdbId = movieImdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath,
)

fun MovieImage.toEntity(movieImdbId: String): MovieImageEntity =
    MovieImageEntity(
        filePath = filePath,
        movieImdbId = movieImdbId,
        width = width,
        height = height,
        voteAverage = voteAverage,
        language = language,
    )


fun GenreEntity.toDomain(): Genre = Genre(id = id, name = name)

fun MovieEntity.toDomain(genres: List<Genre>): Movie = Movie(
    imdbId = imdbId,
    title = title,
    year = year,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    posterPath = posterPath,
    genres = genres,
)

fun MovieWithGenres.toDomain(): Movie =
    movie.toDomain(genres = genres.map { it.toDomain() })

fun CastEntity.toDomain(): Cast = Cast(
    imdbId = imdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath,
)

fun MovieImageEntity.toDomain(): MovieImage = MovieImage(
    filePath = filePath,
    width = width,
    height = height,
    voteAverage = voteAverage,
    language = language,
)

fun MovieDetailsEntity.toDomain(): MovieDetail = MovieDetail(
    imdbId = imdbId,
    tmdbId = tmdbId,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    tagline = tagline,
    releaseDate = releaseDate,
    year = year,
    runtime = runtime,
    budget = budget,
    revenue = revenue,
    languageCode = languageCode,
    popularity = popularity,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    tmdbRating = tmdbRating,
    tmdbVotes = tmdbVotes,
    posterPath = posterPath,
    backdropPath = backdropPath,
    homepage = homepage,
    genres = emptyList(),
)