package rs.edu.raf.rma.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.movies.db.QuizSessionEntity
import rs.edu.raf.rma.movies.quiz.QuizQuestionGenerator
import kotlin.math.min

class QuizViewModel(
    private val appDatabase: AppDatabase,
    private val questionGenerator: QuizQuestionGenerator,
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    private val _effect = Channel<QuizEffect>()
    val effect = _effect.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        checkCanStart()
    }

    fun onIntent(intent: QuizIntent) {
        when (intent) {
            is QuizIntent.StartQuiz -> startQuiz()
            is QuizIntent.SubmitAnswer -> submitAnswer(intent.answerIndex)
            is QuizIntent.NextQuestion -> nextQuestion()
            is QuizIntent.AbandonQuiz -> _state.value = _state.value.copy(showAbandonDialog = true)
            is QuizIntent.ConfirmAbandon -> confirmAbandon()
            is QuizIntent.DismissAbandon -> _state.value = _state.value.copy(showAbandonDialog = false)
            is QuizIntent.TimerTick -> timerTick()
        }
    }

    private fun checkCanStart() {
        viewModelScope.launch {
            val canStart = questionGenerator.canStartQuiz()
            _state.value = _state.value.copy(canStart = canStart)
        }
    }

    private fun startQuiz() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val questions = questionGenerator.generateQuestions()
                if (questions.size < 10) {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = "Not enough movies to generate quiz. Browse the catalog first."
                    )
                    return@launch
                }
                _state.value = _state.value.copy(
                    loading = false,
                    phase = QuizPhase.IN_PROGRESS,
                    questions = questions,
                    currentQuestionIndex = 0,
                    selectedAnswerIndex = null,
                    correctAnswers = 0,
                    timeRemainingSeconds = 60,
                )
                startTimer()
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.timeRemainingSeconds > 0 && _state.value.phase != QuizPhase.FINISHED) {
                delay(1000)
                onIntent(QuizIntent.TimerTick)
            }
        }
    }

    private fun timerTick() {
        val current = _state.value
        if (current.phase == QuizPhase.FINISHED) return

        val newTime = current.timeRemainingSeconds - 1
        if (newTime <= 0) {
            _state.value = current.copy(timeRemainingSeconds = 0)
            finishQuiz()
        } else {
            _state.value = current.copy(timeRemainingSeconds = newTime)
        }
    }

    private fun submitAnswer(answerIndex: Int) {
        val current = _state.value
        if (current.phase != QuizPhase.IN_PROGRESS) return
        if (current.selectedAnswerIndex != null) return

        val question = current.currentQuestion ?: return
        val isCorrect = answerIndex == question.correctAnswerIndex
        val newCorrect = if (isCorrect) current.correctAnswers + 1 else current.correctAnswers

        _state.value = current.copy(
            phase = QuizPhase.ANSWER_REVEALED,
            selectedAnswerIndex = answerIndex,
            correctAnswers = newCorrect,
        )

        viewModelScope.launch {
            delay(1500)
            onIntent(QuizIntent.NextQuestion)
        }
    }

    private fun nextQuestion() {
        val current = _state.value
        val nextIndex = current.currentQuestionIndex + 1

        if (nextIndex >= current.questions.size) {
            finishQuiz()
        } else {
            _state.value = current.copy(
                phase = QuizPhase.IN_PROGRESS,
                currentQuestionIndex = nextIndex,
                selectedAnswerIndex = null,
            )
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()
        val current = _state.value
        val score = calculateScore(current.correctAnswers, current.timeRemainingSeconds)

        _state.value = current.copy(
            phase = QuizPhase.FINISHED,
            score = score,
        )

        viewModelScope.launch {
            appDatabase.movieDao().insertQuizSession(
                QuizSessionEntity(
                    score = score,
                    correctAnswers = current.correctAnswers,
                    totalQuestions = current.questions.size,
                    timeUsedSeconds = current.timeUsedSeconds,
                    timestamp = 0L,
                )
            )
        }
    }

    private fun calculateScore(correctAnswers: Int, timeRemaining: Int): Double {
        if (correctAnswers == 0) return 0.0
        val raw = correctAnswers * (9.0 + timeRemaining.toDouble() / 60.0)
        return min(raw, 100.0)
    }

    private fun confirmAbandon() {
        timerJob?.cancel()
        _state.value = QuizState()
        checkCanStart()
        viewModelScope.launch {
            _effect.send(QuizEffect.NavigateBack)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
