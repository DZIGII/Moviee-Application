package rs.edu.raf.rma.movies.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

expect fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient
