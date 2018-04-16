package org.trustnote.wallet

import dagger.Component
import org.trustnote.wallet.data.network.NetworkModule
import org.trustnote.wallet.data.remote.ApiModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        ApplicationModule::class,
        NetworkModule::class,
        ApiModule::class
))

interface TApplicationComponent {

    // Injectors
    fun injectTo(app: TApp)


    // Submodule methods
    // Every screen is its own submodule of the graph and must be added here.
    // fun plus(module: ListModule): ListComponent
}

