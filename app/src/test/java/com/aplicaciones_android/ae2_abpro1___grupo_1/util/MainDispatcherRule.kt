package com.aplicaciones_android.ae2_abpro1___grupo_1.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Rule que configura Dispatchers.Main con un TestDispatcher y lo resetea al terminar.
 * Usar en tests que ejecutan coroutines en viewModelScope o que necesitan Dispatchers.Main.
 *
 * Por defecto usa UnconfinedTestDispatcher para que las coroutines sobre Main se ejecuten
 * inmediatamente sin necesidad de avanzar manualmente schedulers en tests simples.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
