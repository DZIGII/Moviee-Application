package rs.edu.raf.rma.movies.viewmodel

sealed interface QuizEffect {
    data object NavigateBack : QuizEffect
}
