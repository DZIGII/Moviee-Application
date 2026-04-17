package rs.edu.raf.rma.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import rs.edu.raf.rma.movies.data.remote.MovieApiService
import rs.edu.raf.rma.movies.data.remote.NetworkConstants
import rs.edu.raf.rma.movies.data.remote.createHttpClient
import rs.edu.raf.rma.movies.repository.MovieRepository
import rs.edu.raf.rma.movies.repository.impl.MovieRepositoryImpl
import rs.edu.raf.rma.movies.viewmodel.MovieDetailsViewModel
import rs.edu.raf.rma.movies.viewmodel.MoviesViewModel

val networkModule = module {

    single<HttpClient> {
        createHttpClient()
    }

    single<Ktorfit> {
        Ktorfit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .httpClient(get<HttpClient>())
            .build()
    }

    single<MovieApiService> {
        get<Ktorfit>().create<MovieApiService>()
    }

    single<MovieRepository> {
        MovieRepositoryImpl(get())
    }

    viewModelOf(::MoviesViewModel)
    viewModelOf(::MovieDetailsViewModel)
}