package rs.edu.raf.rma.movies.viewmodel

import rs.edu.raf.rma.movies.domain.QuizQuestion

enum class QuizPhase {
    NOT_STARTED,
    IN_PROGRESS,
    ANSWER_REVEALED,
    FINISHED,
}

data class QuizState(
    val phase: QuizPhase = QuizPhase.NOT_STARTED,
    val loading: Boolean = false,
    val error: String? = null,
    val canStart: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val correctAnswers: Int = 0,
    val timeRemainingSeconds: Int = 60,
    val showAbandonDialog: Boolean = false,
    val score: Double = 0.0,
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val timeUsedSeconds: Int
        get() = 60 - timeRemainingSeconds
}
