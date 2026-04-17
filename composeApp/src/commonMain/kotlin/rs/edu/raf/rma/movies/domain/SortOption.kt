package rs.edu.raf.rma.movies.domain

enum class SortOption(val apiValue: String, val label: String) {
    RATING("imdb_rating", "Rating"),
    YEAR("year", "Year"),
    POPULARITY("popularity", "Popularity")
}
