package rs.edu.raf.rma.movies.domain

enum class QuestionType {
    GUESS_MOVIE,
    GUESS_YEAR,
    GUESS_ACTOR,
}

data class QuizQuestion(
    val type: QuestionType,
    val imagePath: String?,
    val movieTitle: String?,
    val answers: List<String>,
    val correctAnswerIndex: Int,
)
