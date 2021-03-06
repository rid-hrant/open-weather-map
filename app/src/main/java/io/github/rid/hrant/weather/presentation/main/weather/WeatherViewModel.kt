package io.github.rid.hrant.weather.presentation.main.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.rid.hrant.weather.domain.usecase.WeatherUseCases
import io.github.rid.hrant.weather.presentation.main.common.AbstractState
import io.github.rid.hrant.weather.presentation.main.common.DefaultState
import io.github.rid.hrant.weather.presentation.main.common.ErrorState
import io.github.rid.hrant.weather.presentation.main.common.LoadingState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.dsl.module

val viewModelModule = module {
    factory { WeatherViewModel(get()) }
}

class WeatherViewModel(private val weatherUseCases: WeatherUseCases) : ViewModel() {

    val weatherState = MutableLiveData<AbstractState>()

    fun getWeeklyWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherUseCases.getDailyWeatherByGeoData<List<DayViewModel>>(
                latitude, longitude
            )
                .onStart {
                    weatherState.value = LoadingState
                }
                .catch { e ->
                    weatherState.value = ErrorState(e.message.toString())
                }
                .collect { days ->
                    weatherState.value = DefaultState(days)
                }
        }
    }
}