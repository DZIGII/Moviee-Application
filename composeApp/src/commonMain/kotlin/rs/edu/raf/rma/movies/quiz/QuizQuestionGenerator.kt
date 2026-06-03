package rs.edu.raf.rma.movies.quiz

import rs.edu.raf.rma.core.db.AppDatabase
import rs.edu.raf.rma.movies.db.MovieEntity
import rs.edu.raf.rma.movies.domain.QuestionType
import rs.edu.raf.rma.movies.domain.QuizQuestion
import kotlin.random.Random

class QuizQuestionGenerator(
    private val appDatabase: AppDatabase,
) {

    suspend fun canStartQuiz(): Boolean {
        return appDatabase.movieDao().countMoviesWithImages() >= 10
    }

    suspend fun generateQuestions(): List<QuizQuestion> {
        val movies = appDatabase.movieDao().getMoviesWithImages().shuffled()
        val questions = mutableListOf<QuizQuestion>()
        val usedImages = mutableSetOf<String>()
        val typeCounts = mutableMapOf<QuestionType, Int>()
        val types = QuestionType.entries

        var movieIndex = 0

        while (questions.size < 10 && movieIndex < movies.size) {
            val availableTypes = types.filter { (typeCounts[it] ?: 0) < 4 }
            if (availableTypes.isEmpty()) break

            val type = availableTypes[Random.nextInt(availableTypes.size)]
            val movie = movies[movieIndex]
            movieIndex++

            val question = when (type) {
                QuestionType.GUESS_MOVIE -> generateGuessMovie(movie, movies, usedImages)
                QuestionType.GUESS_YEAR -> generateGuessYear(movie, usedImages)
                QuestionType.GUESS_ACTOR -> generateGuessActor(movie, movies, usedImages)
            }

            if (question != null) {
                questions.add(question)
                typeCounts[type] = (typeCounts[type] ?: 0) + 1
            }
        }

        return questions
    }

    private suspend fun generateGuessMovie(
        movie: MovieEntity,
        allMovies: List<MovieEntity>,
        usedImages: MutableSet<String>,
    ): QuizQuestion? {
        val image = pickUnusedImage(movie.imdbId, usedImages) ?: return null

        val wrongMovies = allMovies
            .filter { it.imdbId != movie.imdbId }
            .shuffled()
            .take(3)

        if (wrongMovies.size < 3) return null

        val answers = (wrongMovies.map { it.title } + movie.title).shuffled()
        val correctIndex = answers.indexOf(movie.title)

        return QuizQuestion(
            type = QuestionType.GUESS_MOVIE,
            imagePath = image,
            movieTitle = null,
            answers = answers,
            correctAnswerIndex = correctIndex,
        )
    }

    private suspend fun generateGuessYear(
        movie: MovieEntity,
        usedImages: MutableSet<String>,
    ): QuizQuestion? {
        val image = pickUnusedImage(movie.imdbId, usedImages) ?: return null
        val correctYear = movie.year.toIntOrNull() ?: return null

        val offsets = (-10..10).filter { it != 0 }.shuffled().take(3)
        val wrongYears = offsets.map { (correctYear + it).toString() }
        val answers = (wrongYears + correctYear.toString()).shuffled()
        val correctIndex = answers.indexOf(correctYear.toString())

        return QuizQuestion(
            type = QuestionType.GUESS_YEAR,
            imagePath = image,
            movieTitle = movie.title,
            answers = answers,
            correctAnswerIndex = correctIndex,
        )
    }

    private suspend fun generateGuessActor(
        movie: MovieEntity,
        allMovies: List<MovieEntity>,
        usedImages: MutableSet<String>,
    ): QuizQuestion? {
        val image = pickUnusedImage(movie.imdbId, usedImages) ?: return null

        val cast = appDatabase.movieDao().getTopCastForMovie(movie.imdbId)
        if (cast.isEmpty()) return null

        val correctActor = cast.first().name

        val wrongActors = appDatabase.movieDao().getRandomActorNames(20)
            .filter { name -> cast.none { it.name == name } }
            .shuffled()
            .take(3)

        if (wrongActors.size < 3) return null

        val answers = (wrongActors + correctActor).shuffled()
        val correctIndex = answers.indexOf(correctActor)

        return QuizQuestion(
            type = QuestionType.GUESS_ACTOR,
            imagePath = image,
            movieTitle = movie.title,
            answers = answers,
            correctAnswerIndex = correctIndex,
        )
    }

    private suspend fun pickUnusedImage(
        imdbId: String,
        usedImages: MutableSet<String>,
    ): String? {
        val images = appDatabase.movieDao().getImagesForMovie(imdbId)
        val available = images.firstOrNull { it.filePath !in usedImages }
        if (available != null) {
            usedImages.add(available.filePath)
            return available.filePath
        }
        return null
    }
}
