package com.app.foodplace

import android.app.Application
import com.app.data.repository.LocationRepository
import com.app.data.repository.PlacesRepository
import com.app.domain.repository.ILocationRepository
import com.app.domain.repository.IPlacesRepository
import com.app.foodplace.viewmodel.LocationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class App : Application() {

    private val viewModelModule = module {
        viewModel { LocationViewModel(get(), get()) }
    }

    private val repositoryModule = module {
        factory { LocationRepository(get()) } bind ILocationRepository::class
        factory { PlacesRepository() } bind IPlacesRepository::class
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    viewModelModule,
                    repositoryModule
                )
            )
        }
    }
}