package rs.edu.raf.rma.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import rs.edu.raf.rma.core.auth.di.authModule
import rs.edu.raf.rma.core.db.di.databaseModule

val appModule = module {

}

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        config?.invoke(this)
        modules(
            appModule,
            networkModule,
            authModule,
            databaseModule(),
        )
    }
}