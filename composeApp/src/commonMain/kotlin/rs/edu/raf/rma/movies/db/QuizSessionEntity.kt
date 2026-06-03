package rs.edu.raf.rma.movies.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_sessions")
data class QuizSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Double,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val timeUsedSeconds: Int,
    val timestamp: Long,
)
