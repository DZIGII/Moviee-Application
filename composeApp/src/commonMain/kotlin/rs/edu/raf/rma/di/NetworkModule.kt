package rs.edu.raf.rma.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.SetupRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import rs.edu.raf.rma.core.auth.AuthStore
import rs.edu.raf.rma.core.auth.api.AuthApiService
import rs.edu.raf.rma.core.auth.di.Qualifiers
import rs.edu.raf.rma.core.auth.model.AuthState as ModelAuthState
import rs.edu.raf.rma.core.auth.repository.AuthRepository
import rs.edu.raf.rma.core.auth.repository.AuthRepositoryImpl
import rs.edu.raf.rma.core.auth.viewmodel.AuthViewModel
import rs.edu.raf.rma.movies.data.remote.MovieApiService
import rs.edu.raf.rma.movies.data.remote.NetworkConstants
import rs.edu.raf.rma.movies.data.remote.createHttpClient
import rs.edu.raf.rma.movies.repository.MovieRepository
import rs.edu.raf.rma.movies.repository.impl.MovieRepositoryImpl
import rs.edu.raf.rma.movies.quiz.QuizQuestionGenerator
import rs.edu.raf.rma.movies.viewmodel.FavoritesViewModel
import rs.edu.raf.rma.movies.viewmodel.MovieDetailsViewModel
import rs.edu.raf.rma.movies.viewmodel.MoviesViewModel
import rs.edu.raf.rma.movies.viewmodel.ProfileViewModel
import rs.edu.raf.rma.movies.viewmodel.QuizViewModel
import rs.edu.raf.rma.movies.viewmodel.WatchlistViewModel

val networkModule = module {

    // Obican HttpClient bez tokena - za login i register
    single<HttpClient>(Qualifiers.Unauthenticated) {
        createHttpClient()
    }

    // HttpClient sa auth pluginom - za sve ostale zahteve
    single<HttpClient>(Qualifiers.Authenticated) {
        val authStoreLazy: Lazy<AuthStore> = inject()
        createHttpClient {
            installAuthPlugin(authStoreLazy)
        }
    }

    // Ktorfit za auth (bez tokena)
    single<Ktorfit>(Qualifiers.Unauthenticated) {
        Ktorfit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .httpClient(get<HttpClient>(Qualifiers.Unauthenticated))
            .build()
    }

    // Ktorfit za autentikovane zahteve (sa tokenom)
    single<Ktorfit>(Qualifiers.Authenticated) {
        Ktorfit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .httpClient(get<HttpClient>(Qualifiers.Authenticated))
            .build()
    }

    // Auth API koristi unauthenticated klijent
    single<AuthApiService> {
        get<Ktorfit>(Qualifiers.Unauthenticated).create<AuthApiService>()
    }

    // Movie API koristi authenticated klijent
    single<MovieApiService> {
        get<Ktorfit>(Qualifiers.Authenticated).create<MovieApiService>()
    }

    single<AuthRepository> {
        AuthRepositoryImpl(authApi = get(), authStore = get())
    }

    single<MovieRepository> {
        MovieRepositoryImpl(appDatabase = get(), api = get())
    }

    single { QuizQuestionGenerator(appDatabase = get()) }

    viewModelOf(::AuthViewModel)
    viewModelOf(::MoviesViewModel)
    viewModelOf(::MovieDetailsViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::WatchlistViewModel)
    viewModelOf(::QuizViewModel)
    viewModelOf(::ProfileViewModel)
}

/**
 * Auth plugin koji:
 * 1. Dodaje Bearer token na svaki zahtev
 * 2. Na 401 odgovor radi force logout
 */
private fun HttpClientConfig<*>.installAuthPlugin(
    authStoreLazy: Lazy<AuthStore>,
) = install(createClientPlugin("AuthPlugin") {

    // Pre svakog zahteva - dodaj token u header
    on(SetupRequest) { request ->
        val authStore = authStoreLazy.value
        when (val authState = authStore.authState.value) {
            is ModelAuthState.Authenticated -> {
                request.header(
                    key = HttpHeaders.Authorization,
                    value = "Bearer ${authState.data.accessToken}",
                )
            }
            ModelAuthState.Unauthenticated -> Unit
        }
    }

    // Posle svakog odgovora - proveri da li je 401
    on(Send) { request ->
        val originalCall = proceed(request)

        originalCall.response.run {
            if (status != HttpStatusCode.Unauthorized) {
                return@run originalCall
            }

            // 401 - token je istekao, radimo force logout
            runBlocking {
                val authStore = authStoreLazy.value
                authStore.clearAuthData()
            }

            originalCall
        }
    }
})
