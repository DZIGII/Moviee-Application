package rs.edu.raf.rma.movies.viewmodel

data class ProfileState(
    val loading: Boolean = false,
    val error: String? = null,
    val fullName: String = "",
    val username: String = "",
    val bestScore: Double? = null,
    val quizCount: Int = 0,
    val favoritesCount: Int = 0,
    val watchlistCount: Int = 0,
)
