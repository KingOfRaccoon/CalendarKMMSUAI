package ru.castprograms.calendarkmmsuai.android

import android.app.Application
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import ru.castprograms.calendarkmmsuai.TimeTableViewModel
import ru.castprograms.calendarkmmsuai.repository.TimeTableRepository
import ru.castprograms.calendarkmmsuai.repository.TimeTableService

class CalendarApplication: Application() {
    private val module = module {
        single { TimeTableService() }
        single { TimeTableRepository(get()) }
        single { TimeTableViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()
//        DynamicColors.applyToActivitiesIfAvailable(this)
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(applicationContext)
            modules(module)
        }
    }
}