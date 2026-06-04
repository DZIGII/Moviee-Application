package rs.edu.raf.rma.movies.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import rs.edu.raf.rma.movies.domain.QuestionType
import rs.edu.raf.rma.movies.domain.QuizQuestion
import rs.edu.raf.rma.movies.viewmodel.QuizIntent
import rs.edu.raf.rma.movies.viewmodel.QuizPhase
import rs.edu.raf.rma.movies.viewmodel.QuizState
import rs.edu.raf.rma.movies.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel = koinViewModel(),
    onAbandonConfirmed: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    if (state.showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(QuizIntent.DismissAbandon) },
            title = { Text("Abandon quiz?") },
            text = { Text("Your progress will be lost.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onIntent(QuizIntent.ConfirmAbandon)
                    onAbandonConfirmed()
                }) {
                    Text("Leave", color = Color(0xFFFF0A16))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(QuizIntent.DismissAbandon) }) {
                    Text("Stay", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1E2E),
            titleContentColor = Color.White,
            textContentColor = Color(0xFF999999),
        )
    }

    when (state.phase) {
        QuizPhase.NOT_STARTED -> QuizStartContent(
            state = state,
            onStartClick = { viewModel.onIntent(QuizIntent.StartQuiz) },
        )
        QuizPhase.IN_PROGRESS, QuizPhase.ANSWER_REVEALED -> QuizQuestionContent(
            state = state,
            onAnswerClick = { viewModel.onIntent(QuizIntent.SubmitAnswer(it)) },
            onQuitClick = { viewModel.onIntent(QuizIntent.AbandonQuiz) },
        )
        QuizPhase.FINISHED -> QuizResultContent(
            state = state,
            onPlayAgain = { viewModel.onIntent(QuizIntent.StartQuiz) },
        )
    }
}

@Composable
private fun QuizStartContent(
    state: QuizState,
    onStartClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "Movie Quiz",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "10 questions • 60 seconds",
                color = Color(0xFF999999),
                fontSize = 16.sp,
            )

            if (state.loading) {
                CircularProgressIndicator(color = Color(0xFFFF0A16))
            } else if (!state.canStart) {
                Text(
                    text = "Browse the catalog first to populate your quiz pool.",
                    color = Color(0xFFFF9800),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            } else {
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0A16)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.6f).height(50.dp),
                ) {
                    Text("Start Quiz", fontSize = 18.sp)
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = Color(0xFFFF5252),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun QuizQuestionContent(
    state: QuizState,
    onAnswerClick: (Int) -> Unit,
    onQuitClick: () -> Unit,
) {
    val question = state.currentQuestion ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onQuitClick) {
                Text("Quit", color = Color(0xFFFF0A16), fontSize = 14.sp)
            }
            Text(
                text = "${state.currentQuestionIndex + 1} / ${state.questions.size}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${state.timeRemainingSeconds}s",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { state.timeRemainingSeconds / 60f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = Color(0xFFFF0A16),
            trackColor = Color(0xFF333333),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (question.type) {
                QuestionType.GUESS_MOVIE -> "Guess the Movie"
                QuestionType.GUESS_YEAR -> "Guess the Movie Year"
                QuestionType.GUESS_ACTOR -> "Guess the Lead Actor"
            },
            color = Color(0xFFCCCCCC),
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (question.movieTitle != null) {
            Text(
                text = question.movieTitle,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${question.imagePath}",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            question.answers.forEachIndexed { index, answer ->
                val backgroundColor = when {
                    state.phase == QuizPhase.ANSWER_REVEALED && index == question.correctAnswerIndex -> Color(0xFF4CAF50)
                    state.phase == QuizPhase.ANSWER_REVEALED && index == state.selectedAnswerIndex -> Color(0xFFFF5252)
                    else -> Color(0xFF1E1E2E)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = state.phase == QuizPhase.IN_PROGRESS) {
                            onAnswerClick(index)
                        },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = answer,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizResultContent(
    state: QuizState,
    onPlayAgain: () -> Unit,
) {
    val incorrectAnswers = state.questions.size - state.correctAnswers

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "Quiz Complete!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = String.format("%.2f", state.score),
                color = Color(0xFFF5C518),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "out of 100",
                color = Color(0xFF999999),
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.correctAnswers}",
                        color = Color(0xFF4CAF50),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(text = "Correct", color = Color(0xFF999999), fontSize = 14.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$incorrectAnswers",
                        color = Color(0xFFFF5252),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(text = "Incorrect", color = Color(0xFF999999), fontSize = 14.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.timeUsedSeconds}s",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(text = "Time Used", color = Color(0xFF999999), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPlayAgain,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0A16)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.6f).height(50.dp),
            ) {
                Text("Play Again", fontSize = 18.sp)
            }
        }
    }
}
