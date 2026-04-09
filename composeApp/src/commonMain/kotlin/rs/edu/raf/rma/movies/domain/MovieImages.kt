package rs.edu.raf.rma.movies.domain

data class MovieImages(
    val posters: List<MovieImage>,
    val backdrops: List<MovieImage>,
    val logos: List<MovieImage>
)