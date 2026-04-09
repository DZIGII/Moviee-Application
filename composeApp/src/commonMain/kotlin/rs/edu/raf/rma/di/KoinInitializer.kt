package rs.edu.raf.rma.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
//import rs.edu.raf.rma.passwords.details.PasswordDetailsViewModel
//import rs.edu.raf.rma.passwords.domain.PasswordRepository
//import rs.edu.raf.rma.passwords.list.PasswordsListViewModel
//import rs.edu.raf.rma.passwords.repository.InMemoryPasswordRepository
//
//val passwordsModule = module {
//    single { InMemoryPasswordRepository() } bind PasswordRepository::class
//    viewModelOf(::PasswordsListViewModel)
//    viewModelOf(::PasswordDetailsViewModel)
//}
//
//fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
//    return startKoin {
//        config?.invoke(this)
//        modules(
//            passwordsModule,
//        )
//    }
//}
