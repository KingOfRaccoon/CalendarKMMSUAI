package ru.castprograms.calendarkmmsuai.dispatchers

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual val ioDispatcher: CoroutineContext
    get() = Dispatchers.IO
actual val uiDispatcher: CoroutineContext
    get() = Dispatchers.Main
