package rs.edu.raf.rma.movies.viewmodel

sealed interface QuizIntent {
    data object StartQuiz : QuizIntent
    data class SubmitAnswer(val answerIndex: Int) : QuizIntent
    data object NextQuestion : QuizIntent
    data object AbandonQuiz : QuizIntent
    data object ConfirmAbandon : QuizIntent
    data object DismissAbandon : QuizIntent
    data object TimerTick : QuizIntent
}
